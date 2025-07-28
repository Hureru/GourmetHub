package com.hureru.iam.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

/**
 * Spring Security 配置类
 * @author zheng
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * 配置 OAuth2.0 授权服务器的安全过滤链。
     * 这个过滤器链负责处理所有到授权端点（如 /oauth2/authorize, /oauth2/token）的请求。
     * @param http HttpSecurity 配置器
     * @return SecurityFilterChain
     * @throws Exception 抛出异常
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        // 启用 OpenID Connect 1.0
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());
        http
                // 当用户未认证时，重定向到登录页面
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // 配置资源服务器，用于接受和验证访问令牌
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * 配置保护应用API资源的安全过滤链。
     * 这个过滤器链负责保护你的业务API（例如 /api/**）。
     * @param http HttpSecurity 配置器
     * @return SecurityFilterChain
     * @throws Exception 抛出异常
     */
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 防护**
                // 对于 REST API，通常使用 token 进行认证，而不是 session，所以可以禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        // 公开访问注册、登录和错误页面
                        .requestMatchers("/api/v1/users/register", "/login", "/error").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                // 使用默认的表单登录页面
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    /**
     * 配置密码编码器，用于加密和验证用户密码。
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置 OAuth2 客户端信息。
     * 在生产环境中，您应该从数据库或其他持久化存储中加载客户端信息。
     * 这里为了演示，使用内存存储。
     * @return RegisteredClientRepository
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        // 定义 Token 的相关设置
        TokenSettings tokenSettings = TokenSettings.builder()
                // 设置 Access Token 的有效期为 7 天
                .accessTokenTimeToLive(Duration.ofDays(7))
                .build();

        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                // 将 tokenSettings 应用到这个客户端
                .tokenSettings(tokenSettings)
                // 客户端ID
                .clientId("gourmethub-client")
                // 客户端密钥
                .clientSecret(passwordEncoder().encode("secret"))
                // 客户端认证方式
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                // 授权码模式
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                // 刷新令牌
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // 回调地址
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/gourmethub-client")
                // 登出后重定向地址
                .postLogoutRedirectUri("http://127.0.0.1:8080/")
                // OIDC范围
                .scope(OidcScopes.OPENID)
                // OIDC范围
                .scope(OidcScopes.PROFILE)
                // 自定义范围
                .scope("read")
                // 自定义范围
                .scope("write")
                .clientSettings(ClientSettings.builder()
                        // 要求用户同意授权
                        .requireAuthorizationConsent(true)
                        // 启用 PKCE
                        .requireProofKey(true)
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(oidcClient);
    }

    /**
     * 配置 JWK (JSON Web Key) 源，用于签名JWT。
     * @return JWKSource
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 生成用于JWT签名的RSA密钥对。
     * @return KeyPair
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    /**
     * 配置 JWT 解码器。
     * @param jwkSource JWK源
     * @return JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * 配置授权服务器的设置，例如签发者URI。
     * @return AuthorizationServerSettings
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings(@Value("${spring.authorization-server.issuer}") String issuer) {
        // 这里的配置会从 application.yml 中读取 issuer-uri
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .build();
    }
}

