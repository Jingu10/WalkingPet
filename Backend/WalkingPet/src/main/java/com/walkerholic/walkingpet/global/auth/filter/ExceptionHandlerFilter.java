package com.walkerholic.walkingpet.global.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walkerholic.walkingpet.global.auth.error.TokenBaseException;
import com.walkerholic.walkingpet.global.auth.error.TokenErrorCode;
import com.walkerholic.walkingpet.global.error.response.ErrorResponseEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenBaseException tokenBaseException) {
            log.warn("tokenBaseException occur:");
            TokenErrorCode tokenErrorCode = tokenBaseException.getTokenErrorCode();
            setErrorResponse(tokenErrorCode,response);
//            setErrorResponse(tokenErrorCode.getStatus(), tokenErrorCode.getMessage() ,response);
        }
    }

    private void setErrorResponse(TokenErrorCode tokenErrorCode, HttpServletResponse response) {
        response.setStatus(tokenErrorCode.getStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String json = new ObjectMapper().writeValueAsString(ErrorResponseEntity.toTokenEntity(tokenErrorCode));
            response.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
