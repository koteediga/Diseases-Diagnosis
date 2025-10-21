package com.medicure.controllers;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medicure.config.custom.MyUserDetails;
import com.medicure.dto.request.LoginRequest;
import com.medicure.dto.request.RefreshTokenRequest;
import com.medicure.dto.request.UserPasswordRequest;
import com.medicure.dto.request.UserRequest;
import com.medicure.dto.response.RefreshTokenResponse;
import com.medicure.dto.response.UserResponse;
import com.medicure.entities.RefreshTokenEntity;
import com.medicure.entities.UserEntity;
import com.medicure.exceptions.UserExistsException;
import com.medicure.services.AuthService;
import com.medicure.services.JwtService;
import com.medicure.services.RefreshTokenService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
    name = "Authentication Controller",
    description = "This controller manages the authentication and authorization of User"
)
public class AuthController {

    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Valid @RequestBody LoginRequest loginRequest
    ) {
        System.out.println(loginRequest);
        UserEntity userEntity = authService.loginUser(loginRequest);

        if(userEntity == null)
            return null;

        UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);
        RefreshTokenEntity refreshToken = refreshTokenService.getByUserId(userEntity.getId());

        userResponse.setToken(jwtService.generateJwtToken(new MyUserDetails(userEntity)));
        if (refreshToken == null) {
            userResponse.setRefreshToken(refreshTokenService.buildRefreshToken(new MyUserDetails(userEntity)).getRefreshToken());
        }
        else {
            RefreshTokenEntity extendExperiarion = refreshTokenService.extendExperiarion(refreshToken);
            userResponse.setRefreshToken(extendExperiarion.getRefreshToken());
        }
        userResponse.setResponse("Login success");

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(userResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
        @Valid @RequestBody UserRequest userRequest
    ) throws UserExistsException {
        UserEntity userEntity = authService.registerUser(userRequest);

        if(userEntity == null)
            throw new UserExistsException();

        UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);
        userResponse.setToken(jwtService.generateJwtToken(new MyUserDetails(userEntity)));
        userResponse.setRefreshToken(refreshTokenService.buildRefreshToken(new MyUserDetails(userEntity)).getRefreshToken());
        userResponse.setResponse("register success");

        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(userResponse);
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
        @Valid @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
        RefreshTokenEntity validToken = refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());

        int userId = validToken.getUserId();
        String accessToken = jwtService.generateJwtToken(new MyUserDetails(authService.getUserById(userId)));
        
        refreshTokenResponse.setAccessToekn(accessToken);
        refreshTokenResponse.setRefreshToekn(refreshTokenRequest.getRefreshToken());

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(refreshTokenResponse);
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@Valid @RequestBody UserPasswordRequest request) {
        authService.setPassword(request);
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(Map.of("message", "password updated"));
    }

}
