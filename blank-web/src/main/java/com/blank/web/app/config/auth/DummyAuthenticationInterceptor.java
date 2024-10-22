package com.blank.web.app.config.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ymsl.solid.base.json.JsonUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

public class DummyAuthenticationInterceptor extends OncePerRequestFilter {

    /**
     * Dummy username for login simulation
     */
    @Value("${solid.pj.login.dummy.username:dummy}")
    private String username;

    private UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    /**
     * Login simulation.
     * For accessing APIs with user contexts
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getPrincipal())) {

            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("username", username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(JsonUtils.toString(userInfo));
            ((PJUserDetails)userDetails).setAppLocale(request.getLocale());

            UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                    userDetails, "", userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(result);
        }

        filterChain.doFilter(request, response);
    }
}

