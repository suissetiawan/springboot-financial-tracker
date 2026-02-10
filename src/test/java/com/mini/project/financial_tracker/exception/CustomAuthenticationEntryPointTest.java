package com.mini.project.financial_tracker.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CustomAuthenticationEntryPointTest {

    private CustomAuthenticationEntryPoint entryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entryPoint = new CustomAuthenticationEntryPoint();
    }

    @Test
    void commence_ShouldReturnCorrectResponse() throws IOException, ServletException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new StubServletOutputStream(outputStream));

        entryPoint.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String result = outputStream.toString();
        assertTrue(result.contains("401"));
        assertTrue(result.contains("Unauthorized: Login required"));
    }

    private static class StubServletOutputStream extends jakarta.servlet.ServletOutputStream {
        private final ByteArrayOutputStream baos;
        public StubServletOutputStream(ByteArrayOutputStream baos) { this.baos = baos; }
        @Override
        public void write(int b) throws IOException { baos.write(b); }
        @Override
        public boolean isReady() { return true; }
        @Override
        public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}
    }
}
