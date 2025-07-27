package com.hureru.iam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hureru.iam.oauth.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * <p>
 * 用户认证 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2025-07-26
 */
@RestController
@RequestMapping("/api/v1")
public class OauthController {
    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
    * 获取授权码,启动授权码流程
     * @param responseType: code (必须)
     * @param clientId: 客户端ID (必须)
     * @param redirectUri: 回调URI (必须)
     * @param scope: 请求的权限范围，如 openid profile (必须)
     * @param codeChallenge: PKCE代码挑战 (必须)
     * @param codeChallengeMethod: S256 (必须)
     * @param state: 客户端生成的状态值，用于防止CSRF攻击 (推荐)
     * @return 重定向至登录页面或回调URI
     */
    @GetMapping("/oauth2/authorize")
    public RedirectView authorize(
        @RequestParam("response_type") String responseType,
        @RequestParam("client_id") String clientId,
        @RequestParam("redirect_uri") String redirectUri,
        @RequestParam(value = "scope", required = false) String scope,
        @RequestParam(value = "state", required = false) String state,
        @RequestParam("code_challenge") String codeChallenge,
        @RequestParam("code_challenge_method") String codeChallengeMethod
    ) {
        // TODO 实现授权逻辑
        return new RedirectView("/loginPage");
    }

    /**
    * 获取令牌
     * @param grantType: 授权类型，如 authorization_code (必须)
     * @param code: 从授权端点获取的授权码 (必须)
     * @param redirectUri: 与授权请求中相同的 回调URI (必须)
     * @param clientId: 客户端ID (必须)
     * @param codeVerifier: PKCE代码验证 (必须)
     * @return {@code 200 OK}:Token令牌示例：
     * <pre>
     * {
     *   "access_token": "...",
     *   "token_type": "Bearer",
     *   "expires_in": 900,
     *   "refresh_token": "...",
     *   "scope": "openid profile"
     * }
     * </pre>
     */
    @PostMapping("/oauth2/token")
    public ResponseEntity<TokenResponse> token(
        @RequestParam("grant_type") String grantType,
        @RequestParam("code") String code,
        @RequestParam("redirect_uri") String redirectUri,
        @RequestParam("client_id") String clientId,
        @RequestParam("code_verifier")String codeVerifier
    ) {
        // TODO 实现获取令牌逻辑
        return ResponseEntity.ok(null);
    }

}
