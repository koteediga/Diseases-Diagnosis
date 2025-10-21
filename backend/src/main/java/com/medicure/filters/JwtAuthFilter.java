package com.medicure.filters;

import com.medicure.config.custom.MyUserDetails;
import com.medicure.entities.UserEntity;
import com.medicure.repositories.UserRepository;
import com.medicure.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

   private final HandlerExceptionResolver handlerExceptionResolver; 

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("Authorization header is missing");
                filterChain.doFilter(request, response);
                return;
            }
    
            String token = authHeader.substring(7);
            String email = jwtService.getUsername(token);
    
            if(SecurityContextHolder.getContext().getAuthentication() == null && email != null) {
                String userEmail = jwtService.getUsername(token);
                UserEntity user = userRepository.findByEmail(userEmail);
                MyUserDetails userDetails = new MyUserDetails(user);
                if(jwtService.isValidToken(email, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
    
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
    
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authenticationToken);
    
                }
            }
            filterChain.doFilter(request, response);
        } 
        catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
