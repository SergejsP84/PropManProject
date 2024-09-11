package lv.emendatus.Destiny_PropMan.config;

import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserRole;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaLoginService;
import lv.emendatus.Destiny_PropMan.service.implementation.UserDetailsInnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.core.Authentication;

import javax.sql.DataSource;
import java.io.Serializable;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
    public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return expressionHandler;
    }

    private static final String[] SWAGGER_WHITELIST = {
            // -- Swagger UI v3 (OpenAPI)
            "/v2/API-docs",
            "/v3/API-docs",
            "/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui/**",

    };

//    @Bean
//    public HttpFirewall allowSemicolonHttpFirewall() {
//        StrictHttpFirewall firewall = new StrictHttpFirewall();
//        firewall.setAllowSemicolon(true); // Allow semicolon in URL
//        return firewall;
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        System.out.println("!!! - securityFilterChain method INVOKED!");
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
                        .requestMatchers("/auth/admin-login").permitAll() // Allow access to login endpoints
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/property/getPropertyById/{id}").permitAll()
                        .requestMatchers("/property/update_country/{id}").permitAll()
                        .requestMatchers("/admins-control/create").hasAuthority("ADMIN")
                        .requestMatchers("/admins-control/delete").hasAuthority("ADMIN")
                        .requestMatchers("/admin/toggle_tenant_status/{tenant_id}").hasAuthority("ADMIN")
                        .requestMatchers("/admin/toggle_manager_status/{id}").hasAuthority("ADMIN")
                        .requestMatchers("/admin/register_tenant").hasAuthority("ADMIN")
                        .requestMatchers("/admin/register_manager").hasAuthority("ADMIN")
                        .requestMatchers("/admin/update_tenant/{tenantId}").hasAuthority("ADMIN")
                        .requestMatchers("/admin/update_manager/{managerId}").hasAuthority("ADMIN")
                        .requestMatchers("/admin/delete_tenant/{tenantId}").hasAuthority("ADMIN")
                        .requestMatchers("/admin/delete_manager/{managerId}").hasAuthority("ADMIN")
                        .requestMatchers("/admin/add_property").hasAuthority("ADMIN")
                        .requestMatchers("/admin/delete_property/{propertyId}").hasAuthority("ADMIN")
                        .requestMatchers("/admin/resolve_claim/{claimId}").hasAuthority("ADMIN")
                        .requestMatchers("/admin/pending_refunds").hasAuthority("ADMIN")
                        .requestMatchers("/admin/settle_refund").hasAuthority("ADMIN")
                        .requestMatchers("/admin/pending_payouts").hasAuthority("ADMIN")
                        .requestMatchers("/admin/settle_payout").hasAuthority("ADMIN")
                        .requestMatchers("/admin/new_payout").hasAuthority("ADMIN")
                        .requestMatchers("/admin/new_refund").hasAuthority("ADMIN")
                        .requestMatchers("/admin/add_currency").hasAuthority("ADMIN")
                        .requestMatchers("/admin/set_numerical_configs").hasAuthority("ADMIN")
                        .requestMatchers("/admin/add_amenity").hasAuthority("ADMIN")
                        .requestMatchers("/admin/remove_amenity/{amenityId}").hasAuthority("ADMIN")
                        .requestMatchers("/admin/delete-review/{reviewId}").hasAuthority("ADMIN")
                        .requestMatchers("/admin/view-claims").hasAuthority("ADMIN")
                        .requestMatchers("/admin/view-claims").hasAuthority("ADMIN")
                        .requestMatchers("/admin/change-rate/**").hasAuthority("ADMIN")


                        .requestMatchers("/managerial/getProfile/{managerId}").permitAll()
                        .requestMatchers("/managerial/updateProfile/{managerId}").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/getPropertyPortfolio/{managerId}").permitAll()
                        .requestMatchers("/managerial/property/{propertyId}/bookings").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/manager/{managerId}/bookings").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/properties/{propertyId}/unpaid-bills").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/managers/{managerId}/unpaid-bills").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/properties/{propertyId}/unpaid-bills").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/managers/{managerId}/financial-statement").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/addProperty").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/discount").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/discount/reset").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/bookings/pending-approval").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/bookings/approve").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/bookings/decline").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/delete_property/{propertyId}").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/addPhotos/{propertyId}").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/removePhoto/{propertyId}").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/early-termination/accept").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/early-termination/decline").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/property/unavailable").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/unlock_property/{propertyId}").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/property/{propertyId}/addBill").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/property/{propertyId}/remove_bill/{billId}").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/rate_tenant/{tenant_id}/{manager_id}/{booking_id}/{rating}").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/amenities/{property_id}").hasAuthority("MANAGER")
                        .requestMatchers("/managerial/tenant-profile/{booking_id}}").hasAuthority("MANAGER")
                        .requestMatchers("/tenant/property/details/{propertyId}").permitAll()
                        .requestMatchers("/tenant/getProfile/{tenantId}").hasAuthority("TENANT")
                        .requestMatchers("/ten/confirm-email-change").permitAll()
                        .requestMatchers("/man/confirm-email-change").permitAll()
                        .requestMatchers("/tenant/updateProfile/{tenantId}").hasAuthority("TENANT")
                        .requestMatchers("/tenant/getHistory/{tenantId}").hasAuthority("TENANT")
                        .requestMatchers("/tenant/saveFavoriteProperty").hasAuthority("TENANT")
                        .requestMatchers("/tenant/getFavoriteProperties/{tenantId}").hasAuthority("TENANT")
                        .requestMatchers("/tenant/removeFavoriteProperty/{tenantId}/{propertyId}").hasAuthority("TENANT")
                        .requestMatchers("/tenant/completed-payments").hasAuthority("TENANT")
                        .requestMatchers("/tenant/outstanding-payments").hasAuthority("TENANT")
                        .requestMatchers("/tenant/current-bookings").hasAuthority("TENANT")
                        .requestMatchers("/tenant/requestEarlyTermination").hasAuthority("TENANT")
                        .requestMatchers("/tenant/processPayment/payment_id").hasAuthority("TENANT")
                        .requestMatchers("/tenant/rate_property").hasAuthority("TENANT")
                        .requestMatchers("/tenant/submit_claim").hasAuthority("TENANT")
                        .requestMatchers("/amenities/**").denyAll()
                        .requestMatchers("/registration/tenants/**").denyAll()
                        .requestMatchers("/registration/manager/**").denyAll()
                        .requestMatchers("/auth/register/tenant").permitAll()
                        .requestMatchers("/confirm-registration").permitAll()
                        .requestMatchers("/auth/register/manager").permitAll()
                        .requestMatchers("/auth/verify-otp-tenant").permitAll()
                        .requestMatchers("/auth/verify-otp-manager").permitAll()
                        .requestMatchers("/auth/verify-otp-admin").permitAll()
                        .requestMatchers("/communication/**").authenticated()
                        .requestMatchers("/password/change").authenticated()
                        .requestMatchers("/password/request-reset").permitAll()
                        .requestMatchers("/password/complete-reset").permitAll()
                        .requestMatchers("/review/post").hasAuthority("TENANT")
                        .requestMatchers("/review/getByProperty/{propertyId}").permitAll()
                        .requestMatchers("/reservation/book").hasAuthority("TENANT")
                        .requestMatchers("/reservation/cancel").hasAuthority("TENANT")
                        .requestMatchers("/search/properties").permitAll()





                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                // Form login configuration
//                .formLogin(formLogin ->
//                        formLogin
//                                .loginProcessingUrl("/auth/admin-login")
//                                .permitAll()
//                )
//                .formLogin(formLogin ->
//                        formLogin
//                                .loginProcessingUrl("/auth/login")
//                                .permitAll()
//                )
                .formLogin(AbstractHttpConfigurer::disable)
                // Logout configuration
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .permitAll()
                )

                ;

        return http.build();
    }


    public class CustomPermissionEvaluator implements PermissionEvaluator {

        private static final Logger logger = LoggerFactory.getLogger(CustomPermissionEvaluator.class);

        @Override
        public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
            return hasPermission(authentication, null, null, permission);
        }

        @Override
        public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
            logger.info("hasPermission called with targetId: {}, targetType: {}, permission: {}", targetId, targetType, permission);

            if (authentication == null || permission == null) {
                return false;
            }

            if (permission.equals("DefaultAdmin")) {
                boolean isDefaultAdmin = authentication.getName().equals("DefaultAdmin");
                logger.info("isDefaultAdmin: {}", isDefaultAdmin);
                return isDefaultAdmin;
            }

            return false;
        }
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
