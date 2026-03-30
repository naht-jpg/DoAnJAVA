package ecourse.secutiry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecutiryConfig {

    @Autowired
    private CustomLoginFailureHandler loginFailureHandler;

    @Autowired
    private CustomLoginSuccessHandler loginSuccessHandler;

    @Bean
    public UserDetailsService getUserDetailsServices() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider getDaoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(getUserDetailsServices());
        provider.setPasswordEncoder(getPasswordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/admin/**").hasAuthority("admin")
                .requestMatchers("/user/**").hasAuthority("user")
                .requestMatchers("/**").permitAll()
        )
        .formLogin((form) -> form
                .loginPage("/home/signin")
                .loginProcessingUrl("/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
        )
        .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home/signin?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
        )
        // ===== REMEMBER ME =====
        .rememberMe((rm) -> rm
                .userDetailsService(getUserDetailsServices())
                .key("ecourse-remember-me-secret")
                .tokenValiditySeconds(86400) // 1 ngay
                .rememberMeParameter("remember-me")
        )
        // ===== SESSION MANAGEMENT =====
        .sessionManagement((session) -> session
                .maximumSessions(1)
                .expiredUrl("/home/signin?expired")
        )
        .csrf((csrf) -> csrf.disable());
        return http.build();
    }
}
