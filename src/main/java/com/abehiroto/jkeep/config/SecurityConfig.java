package com.abehiroto.jkeep.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @SuppressWarnings("removal")
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/login", "/static/**").permitAll()  // 認証不要パス
//                .anyRequest().authenticated()  // それ以外は要認証
//            )
//            .formLogin(form -> form
//                .loginPage("/login")  // カスタムログインページ
//                .defaultSuccessUrl("/notes")  // ログイン成功時の遷移先
//            );
    	http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll() // H2コンソールを認証除外
            .anyRequest().authenticated()
        )
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**") // CSRF無効化
        )
        .headers(headers -> headers
            .frameOptions().disable() // iframe許可（H2コンソール用）
        );
        return http.build();
    }
}