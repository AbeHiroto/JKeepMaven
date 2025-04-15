package com.abehiroto.jkeep.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

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
        		.frameOptions(options -> options.sameOrigin()) // H2コンソール用に sameOrigin を許可 (推奨)
        )
    	.httpBasic(withDefaults());
        return http.build();
    }
    
    /**
     * パスワードエンコーダーの Bean 定義 (BCrypt を使用)
     * @return PasswordEncoder のインスタンス
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * UserDetailsService の Bean 定義 (インメモリ版)
     * アプリケーションで使用するユーザー情報を定義します。
     * @param passwordEncoder パスワードのハッシュ化に使用
     * @return UserDetailsService のインスタンス
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // --- メモリ上にユーザー情報を定義 ---
        // パスワード "password" を BCrypt でハッシュ化して設定します。
        // data.sql やテストで使うユーザー情報と合わせると良いでしょう。

        UserDetails user1 = User.builder()
                .username("user1")
                .password(passwordEncoder.encode("password")) // "password" をハッシュ化
                .roles("USER") // ロールを指定 (権限設定に使う)
                .build();

        UserDetails user2 = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();

        // NoteControllerTest で @WithMockUser(username = "testuser") を使っているので、
        // ローカル実行時の確認用に 'testuser' も定義しておくと便利です。
        UserDetails testuser = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password")) // テスト用パスワード
                .roles("USER")
                .build();

        // 作成した UserDetails を InMemoryUserDetailsManager に渡して Bean として登録
        return new InMemoryUserDetailsManager(user1, user2, testuser);
    }
}