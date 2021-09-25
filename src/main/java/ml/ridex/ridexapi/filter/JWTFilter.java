package ml.ridex.ridexapi.filter;

import ml.ridex.ridexapi.config.JwtAuthenticationEntryPoint;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidJwtAuthenticationException;
import ml.ridex.ridexapi.service.JWTService;
import ml.ridex.ridexapi.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JWTFilter extends GenericFilterBean {
    private JWTService jwtService;
    private UserService userService;

    public JWTFilter(JWTService jwtTokenService, UserService userService) {
        this.jwtService = jwtTokenService;
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        String token = jwtService.resolveToken((HttpServletRequest) req);

        try {
            if (token != null && jwtService.validateToken(token)) {
                Authentication auth;
                if (token != null) {
                    String phone = jwtService.getUsername(token);
                    auth = userService.getAuthentication(phone);
                } else {
                    auth = null;
                }
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (InvalidJwtAuthenticationException e) {
            req.setAttribute(JwtAuthenticationEntryPoint.INVALID_TOKEN_MESSAGE, e.getMessage());
        } catch (EntityNotFoundException e) {
            req.setAttribute(JwtAuthenticationEntryPoint.INVALID_TOKEN_MESSAGE, "Invalid Token");
        }

        filterChain.doFilter(req, res);
    }
}
