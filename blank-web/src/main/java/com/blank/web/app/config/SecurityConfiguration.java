package com.blank.web.app.config;

import com.blank.web.app.config.auth.DummyAuthenticationInterceptor;
import com.blank.web.app.config.auth.PJUserDetailsServiceLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Basic security configuration
 */
@Configuration
public class SecurityConfiguration {

    @Value("${solid.pj.login.dummy.allow-origins:http://localhost:[*]}")
    private String[] dummyAllowOrigins;

    /**
     * declaring no-authentication resources and x-frame-option settings
     */

    @Bean("commonWebSecurityConfig")
    @Order(SecurityProperties.IGNORED_ORDER + 100)
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/**/webjars/**","/**/resources/**","/**/ynawebjars/**","/**/public/**", "/**/static-res/**", "/monitor/**");
    }

    /**
     * No authentication. Just for testing interfaces.
     */
    @Bean
    @Profile("no-auth")
    @Order(SecurityProperties.BASIC_AUTH_ORDER + 1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
                .authorizeHttpRequests(authz ->
                    authz.anyRequest().authenticated()
                );

        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.cors(cors -> cors.configurationSource(urlBasedCorsConfigurationSource()));

        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    /**
     * Dummy authentication. Used for testing interfaces which require user information.
     */
    @Configuration
    @Profile("dummy-auth")
    @Order(SecurityProperties.BASIC_AUTH_ORDER + 2)
    protected class DummySecurity implements WebMvcConfigurer {

        @Bean
        @Order(SecurityProperties.BASIC_AUTH_ORDER + 1)
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.securityMatcher("/**")
                    .authorizeHttpRequests(authz ->
                            authz.anyRequest().permitAll()
                    );

            http.addFilterBefore(dummyAuthenticationInterceptor(), UsernamePasswordAuthenticationFilter.class);
            http.csrf(AbstractHttpConfigurer::disable);
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
            http.cors(cors -> cors.configurationSource(urlBasedCorsConfigurationSource()));

            return http.build();
        }

        @Bean
        public PJUserDetailsServiceLoader userDetailsService() {
          return new PJUserDetailsServiceLoader();
        }

        @Bean
        public DummyAuthenticationInterceptor dummyAuthenticationInterceptor() {
            return new DummyAuthenticationInterceptor();
        }

        @Bean
        public CorsFilter corsFilter() {
            return new CorsFilter(urlBasedCorsConfigurationSource());
        }

    }

    public UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        for(String allowOrigins: dummyAllowOrigins) {
            config.addAllowedOriginPattern(allowOrigins);
        }
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);
        source.registerCorsConfiguration("/**", config); // api to get access token
        return source;
    }

}
