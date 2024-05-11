package lv.emendatus.Destiny_PropMan.config;

import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserRole;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaLoginService;
import lv.emendatus.Destiny_PropMan.service.implementation.UserDetailsInnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.core.Authentication;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsInnerService userDetailsInnerService;
    @Autowired
    private DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("!!! - securityFilterChain method INVOKED!");
        http
                // CSRF configuration
//              HOW DO I GET THE THING IN WORKING ORDER AFTER I RE-ENABLE CSRF PROTECTION?
//                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .csrf(AbstractHttpConfigurer::disable)
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
                .httpBasic(Customizer.withDefaults())
                // Form login configuration
                .formLogin(formLogin ->
                        formLogin
                                .loginProcessingUrl("/auth/admin-login")
                                .permitAll()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginProcessingUrl("/auth/login")
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

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        System.out.println("!!! - configureGlobal method INVOKED!");
//        auth.jdbcAuthentication()
//            .dataSource(dataSource)
//            .usersByUsernameQuery("SELECT login, password, 1 as enabled FROM admins WHERE login = ? UNION ALL SELECT login, password, 1 as enabled FROM tenants WHERE login = ? UNION ALL SELECT login, password, 1 as enabled FROM managers WHERE login = ?")
//            .authoritiesByUsernameQuery("SELECT login, authority FROM admin_authorities WHERE admin_id = (SELECT id FROM admins WHERE login = ?) UNION ALL SELECT login, authority FROM tenant_authorities WHERE tenant_id = (SELECT id FROM tenants WHERE login = ?) UNION ALL SELECT login, authority FROM manager_authorities WHERE manager_id = (SELECT id FROM managers WHERE login = ?)")
//            .passwordEncoder(bCryptPasswordEncoder);
//    }

    // TEMPORARY!!


    // ATTEMPT
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User.builder()
//                .username("aamir")
//                .password("123")
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }

    //    @Bean
//    @Qualifier("userDetailsInnerService")
//    public AuthenticationProvider authenticationProvider() {
//        System.out.println("!!! - authenticationProvider method INVOKED!!!");
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsInnerService);
//        provider.setPasswordEncoder();
//        return provider;
//    }
// Сделать класс, который мог бы обрабатывать базовую идентификацию
    // интерфейс UserDetails
}
