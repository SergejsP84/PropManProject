package lv.emendatus.Destiny_PropMan.config;

import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.core.Authentication;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF configuration
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // Headers configuration
                .headers(headers -> headers
                        .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentSecurityPolicy(cps -> cps.policyDirectives("default-src 'self'; script-src 'self' https://trusted-scripts.com;"))
                )
                // Authorization configuration
                // Временные разрешения - по завершении работы те эндпойнты, которые
                // не несут пользовательских функций, будут закрыты .denyAll()
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/admin/toggle_manager_status/2").permitAll()
                        .requestMatchers("/tenants/getall").permitAll()
                        .requestMatchers("/auth/admin-login").permitAll() // Allow access to login endpoint
                        .requestMatchers("/tenants/getall").permitAll()
                        .requestMatchers("/property/getPropertyById/{id}").permitAll()
                        .requestMatchers("/property/update_country/{id}").permitAll()
                        .anyRequest().authenticated()
                )
                // Form login configuration
                .formLogin(formLogin ->
                        formLogin
                                .loginProcessingUrl("/auth/admin-login")
                                .permitAll()
                )
                // Logout configuration
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .permitAll()
                );
        return http.build();
    }

}
