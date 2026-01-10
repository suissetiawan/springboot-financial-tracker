package com.mini.project.financial_tracker.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mini.project.financial_tracker.repository.UserRepository;
import com.mini.project.financial_tracker.entity.User;
import java.util.UUID;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter{

    private final JwtUtils jwtUtil;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
        HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = null;
        String userId = null;

        // Ambil token dari header & extract user id
        token = authHeader.substring(7);
        userId = jwtUtil.extractUserIdFromAccessToken(token);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User existUser = userRepository.findById(UUID.fromString(userId)).orElse(null);

            if (jwtUtil.validateAccessToken(token, existUser)) {
                UserDetails userDetails = securityUtils.convertToUserDetails(existUser);

                // set context auth context nya
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); 
    }

}
