package site.mygumi.goodbite.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class SameSiteCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {
        filterChain.doFilter(request, response);

        if (response.getHeader("Set-Cookie") != null) {
            String header = response.getHeader("Set-Cookie");
            header = header + "; SameSite=None; Secure";
            response.setHeader("Set-Cookie", header);
        }
    }
}