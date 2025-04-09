package com.overflow.laundry.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.overflow.laundry.util.LogUtils.logWarn;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
                     AccessDeniedException accessDeniedException) throws IOException {

    String message = (accessDeniedException != null && accessDeniedException.getMessage() != null)
        ? accessDeniedException.getMessage() : "Authorization failed";

    logWarn(message, accessDeniedException);

    String jsonResponse = String.format("{\"success\": %b, \"message\": \"%s\", "
            + "\"data\": {\"details\": \"%s\", \"path\": \"%s\"}, \"timestamp\": \"%s\"}",
        false, HttpStatus.FORBIDDEN.getReasonPhrase(), message, request.getRequestURI(), LocalDateTime.now());

    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(jsonResponse);
  }

}
