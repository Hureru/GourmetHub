package com.hureru.iam.oauth.dto;

import lombok.Data;

/**
 * @author zheng
 */
@Data
public class TokenResponse {
    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    private String scope;

    public TokenResponse(String accessToken, String tokenType, int expiresIn, String refreshToken, String scope) {
        this.access_token = accessToken;
        this.token_type = tokenType;
        this.expires_in = expiresIn;
        this.refresh_token = refreshToken;
        this.scope = scope;
    }

}
