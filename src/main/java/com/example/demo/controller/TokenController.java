package com.example.demo.controller;

import com.example.demo.configuration.OpenApiConfig;
import com.example.demo.dto.TokenResponse;
import com.example.demo.enums.APIExceptionCode;
import com.example.demo.exception.APIException;
import com.example.demo.security.AccessTokenService;
import com.example.demo.security.RefreshTokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    /**
     * 登录：Basic Auth 校验通过后，返回短期 Access Token，Refresh Token 写入 HttpOnly Cookie。
     */
    @PostMapping("/token")
    @SecurityRequirements({
            @SecurityRequirement(name = OpenApiConfig.BASIC_SCHEME)
    })
    public TokenResponse login(Authentication authentication, HttpServletResponse response) {
        String refreshToken = refreshTokenService.create(authentication.getName());
        refreshTokenService.writeCookie(response, refreshToken);
        return buildTokenResponse(accessTokenService.create(authentication));
    }

    /**
     * 刷新：浏览器自动携带 HttpOnly Cookie，换取新的 Access Token（同时轮换 Refresh Token）。
     */
    @PostMapping("/token/refresh")
    public TokenResponse refresh(
            @CookieValue(name = "${jwt.refresh-token-cookie-name:refresh_token}", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new APIException(APIExceptionCode.REFRESH_TOKEN_MISSING.getErrorCode(),
                    APIExceptionCode.REFRESH_TOKEN_MISSING.getErrorMessage());
        }

        RefreshTokenService.RotateResult result = refreshTokenService.rotate(refreshToken);
        refreshTokenService.writeCookie(response, result.getRefreshToken());

        UserDetails user = userDetailsService.loadUserByUsername(result.getUsername());
        String scope = user.getAuthorities().stream()
                .map(Object::toString)
                .reduce((a, b) -> a + " " + b)
                .orElse("");
        return buildTokenResponse(accessTokenService.create(result.getUsername(), scope));
    }

    /**
     * 登出：吊销 Refresh Token 并清除 Cookie。
     */
    @PostMapping("/token/logout")
    public void logout(
            @CookieValue(name = "${jwt.refresh-token-cookie-name:refresh_token}", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenService.revoke(refreshToken);
        }
        refreshTokenService.clearCookie(response);
    }

    private TokenResponse buildTokenResponse(String accessToken) {
        return new TokenResponse(accessToken, "Bearer", accessTokenService.getExpiresIn());
    }

}
