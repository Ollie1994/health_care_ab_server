package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.repository.LogRepository;
import healthcareab.project.healthcare_booking_app.utils.UserIdSalter;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserIdSalterTest {

    @Mock
    private LogRepository logRepository;

    @Test
    void testSalt_insertsSaltCorrectly() {
        String original = "123456";
        String expected = "12_x9_3456";

        String salted = UserIdSalter.salt(original);

        assertEquals(expected, salted);
        }

    @Test
    void testUnsalt_removesSalt() {
        String salted = "12_x9_3456";
        String expected = "123456";

        String unsalted = UserIdSalter.unsalt(salted);

        assertEquals(expected, unsalted);
    }

    @Test
    void testSalt_nullOrEmpty() {
        assertNull(UserIdSalter.salt(null));
        assertEquals("", UserIdSalter.salt(""));
    }

    @Test
    void testUnsalt_nullOrEmpty() {
        assertNull(UserIdSalter.unsalt(null));
        assertEquals("", UserIdSalter.unsalt(""));
    }
}
