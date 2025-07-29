package com.hureru.iam.config;

import com.hureru.iam.exception.RestAccessDeniedHandler;
import com.hureru.iam.exception.RestAuthenticationEntryPoint;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
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
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

/**
 * Spring Security 配置类
 *
 * @author zheng
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(SecurityConfig.JwkProperties.class)
public class SecurityConfig {
    // 注入自定义处理器
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    /**
     * 配置 OAuth2.0 授权服务器的安全过滤链。
     * 这个过滤器链负责处理所有到授权端点（如 /oauth2/authorize, /oauth2/token）的请求。
     *
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
     *
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
                        .requestMatchers("/api/v1/register/*",
                                "/api/v1/users/pending",
                                "/login",
                                "/error").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(restAuthenticationEntryPoint) // 指定 401 错误处理器
                        .accessDeniedHandler(restAccessDeniedHandler))
                // 使用默认的表单登录页面
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    /**
     * 配置密码编码器，用于加密和验证用户密码。
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置基于 JDBC 的 OAuth2 客户端信息仓库。
     *
     * @param jdbcTemplate JDBC 模板
     * @return RegisteredClientRepository
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    /**
     * 配置基于 JDBC 的 OAuth2 授权服务。
     *
     * @param jdbcTemplate               JDBC 模板
     * @param registeredClientRepository 客户端仓库
     * @return OAuth2AuthorizationService
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * 配置基于 JDBC 的 OAuth2 授权同意服务。
     *
     * @param jdbcTemplate               JDBC 模板
     * @param registeredClientRepository 客户端仓库
     * @return OAuth2AuthorizationConsentService
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * 定义一个配置类来接收 application.yml 中的 jwt.keystore 配置
     */
    @ConfigurationProperties(prefix = "jwt.keystore")
    public record JwkProperties(
            Resource location,
            String storePassword,
            String alias,
            String keyPassword,
            String keyId
    ) {
    }

    /**
     * 配置 JWK (JSON Web Key) 源，用于签名JWT。
     * 现在它从 JKS 密钥库文件中加载持久化的密钥。
     *
     * @param jwkProperties 密钥库配置属性
     * @return JWKSource
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource(JwkProperties jwkProperties) {
        try {
            // 加载 JKS 文件
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(jwkProperties.location().getInputStream(), jwkProperties.storePassword().toCharArray());

            // 从密钥库中获取密钥对
            KeyPair keyPair = new KeyPair(
                    keyStore.getCertificate(jwkProperties.alias()).getPublicKey(),
                    (RSAPrivateKey) keyStore.getKey(jwkProperties.alias(), jwkProperties.keyPassword().toCharArray())
            );

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            // 创建 RSAKey 对象
            RSAKey rsaKey = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(jwkProperties.keyId())
                    .build();

            JWKSet jwkSet = new JWKSet(rsaKey);
            System.out.println("🔐 JKS loaded successfully: " + jwkSet);
            return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
        } catch (Exception e) {
            throw new IllegalStateException("无法加载密钥库", e);
        }
    }

    /**
     * 配置 JWT 解码器。
     *
     * @param jwkSource JWK源
     * @return JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * 配置授权服务器的设置，例如签发者URI。
     *
     * @return AuthorizationServerSettings
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings(@Value("${spring.authorization-server.issuer}") String issuer) {
        // 这里的配置会从 application.yml 中读取 issuer-uri
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .build();
    }

    @Bean
    public CommandLineRunner dataLoader(RegisteredClientRepository registeredClientRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String clientId = "gourmethub-client";
            // 检查客户端是否已存在
            if (registeredClientRepository.findByClientId(clientId) == null) {
                TokenSettings tokenSettings = TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofDays(7))
                        .build();

                RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId(clientId)
                        .clientSecret(passwordEncoder.encode("secret"))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("http://127.0.0.1:8080/login/oauth2/code/gourmethub-client")
                        .postLogoutRedirectUri("http://127.0.0.1:8080/")
                        .scope(OidcScopes.OPENID)
                        .scope(OidcScopes.PROFILE)
                        .scope("read")
                        .scope("write")
                        .tokenSettings(tokenSettings)
                        .clientSettings(ClientSettings.builder()
                                .requireAuthorizationConsent(true)
                                .requireProofKey(true)
                                .build())
                        .build();

                registeredClientRepository.save(oidcClient);
            }
        };
    }
}

