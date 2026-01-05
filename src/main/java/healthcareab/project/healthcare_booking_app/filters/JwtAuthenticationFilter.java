package healthcareab.project.healthcare_booking_app.filters;

import healthcareab.project.healthcare_booking_app.services.CustomUserDetailsService;
import healthcareab.project.healthcare_booking_app.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;

        // try to get the jwt from Authorization header
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            jwt = bearerToken.substring(7);
        }

        // if jwt is not in header, try to get it from cookie
        if(jwt == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        // if we have a jwt and the user is not authenticated
        if(jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // extract username from token
                String username = jwtUtil.extractUsername(jwt);

                // get user details from db
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // validate token and create valid authentication if token is valid
                if(jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    // add request details for extra security
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // set authentication back into security context
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (JwtException e) {
                logger.error("JWT validation failed.", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // continue to next filter chain
        filterChain.doFilter(request, response);
    }
}
