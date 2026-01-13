package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.exceptions.ConflictException;
import healthcareab.project.healthcare_booking_app.exceptions.GlobalExceptionHandler;
import healthcareab.project.healthcare_booking_app.exceptions.ResourceNotFoundException;
import healthcareab.project.healthcare_booking_app.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequest() {
        // --- ARRANGE ---
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        // --- ACT ---
        ResponseEntity<String> response = exceptionHandler.handleIllegalArgument(ex);

        // --- ASSERT ---
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody());
    }

    @Test
    void handleResourceNotFound_shouldReturnNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource missing");

        ResponseEntity<String> response = exceptionHandler.handleResourceNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource missing", response.getBody());
    }

    @Test
    void handleUnauthorizedException_shouldReturnUnauthorized() {
        UnauthorizedException ex = new UnauthorizedException("Not allowed");

        ResponseEntity<String> response = exceptionHandler.handleUnauthorizedException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Not allowed", response.getBody());
    }

    @Test
    void handleConflictException_shouldReturnConflict() {
        ConflictException ex = new ConflictException("Conflict happened");

        ResponseEntity<String> response = exceptionHandler.handleConflictException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict happened", response.getBody());
    }

    @Test
    void handleGeneral_shouldReturnInternalServerError() {
        Exception ex = new Exception("Something went wrong");

        ResponseEntity<String> response = exceptionHandler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred.", response.getBody());
    }

    @Test
    void handleAccessDeniedException_shouldReturnUnauthorized() {
        AccessDeniedException ex = mock(AccessDeniedException.class);

        ResponseEntity<String> response = exceptionHandler.handleAccessDeniedException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void handleNullPointerException_shouldReturnBadRequest() {
        NullPointerException ex = new NullPointerException();

        ResponseEntity<String> response = exceptionHandler.handleNullPointerException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("An unexpected NullPointerException has occurred.", response.getBody());
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturnBadRequestWithMessages() {
        // --- ARRANGE ---
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        var bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(
                Collections.singletonList(
                        new org.springframework.validation.FieldError(
                                "object", "field", "must not be empty"
                        )
                )
        );

        // --- ACT ---
        ResponseEntity<String> response = exceptionHandler.handleMethodArgumentNotValidException(ex);

        // --- ASSERT ---
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("field: must not be empty", response.getBody());
    }

    @Test
    void handleHttpMessageNotReadableException_shouldReturnBadRequest() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<String> response = exceptionHandler.handleHttpMessageNotReadableException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The request body is invalid.", response.getBody());
    }

    @Test
    void handleMethodArgumentTypeMismatchException_shouldReturnBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);

        ResponseEntity<String> response = exceptionHandler.handleMethodArgumentTypeMismatchException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The request body is invalid.", response.getBody());
    }
}