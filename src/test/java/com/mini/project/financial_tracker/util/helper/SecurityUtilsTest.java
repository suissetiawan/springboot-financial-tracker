package com.mini.project.financial_tracker.util.helper;

import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.util.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {

    private final SecurityUtils securityUtils = new SecurityUtils();

    @Test
    void getCurrentUsername_WhenAuthenticated_ShouldReturnUsername() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        assertEquals("testuser", SecurityUtils.getCurrentUsername());
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsername_WhenPrincipalIsNotUserDetails_ShouldReturnNull() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("just-a-string", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        assertNull(SecurityUtils.getCurrentUsername());
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsername_WhenNotAuthenticated_ShouldReturnNull() {
        SecurityContextHolder.clearContext();
        assertNull(SecurityUtils.getCurrentUsername());
    }

    @Test
    void convertToUserDetails_ShouldSucceed() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        UserDetails userDetails = securityUtils.convertToUserDetails(user);

        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
