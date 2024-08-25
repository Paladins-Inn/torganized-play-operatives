package de.paladinsinn.tp.dcis.stormknights.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableWebSecurity(debug = false)
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class ActuatorSecurityConfiguration {
    @Bean
    public SecurityFilterChain observabilitySecurity(HttpSecurity http) throws Exception {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher("/actuator/**");
        AntPathRequestMatcher healthCheck = new AntPathRequestMatcher("/actuator/health/**");

        http
            .securityMatcher(matcher)
            .authorizeHttpRequests(r -> r
                .requestMatchers(healthCheck).anonymous()
                .requestMatchers(matcher).authenticated()
                .anyRequest().hasRole("OBSERVER")
                
            )
            .httpBasic(h -> h.realmName("Observability"))
            .cors(c -> c.disable())
            .csrf(c -> c.disable())
            .sessionManagement(s -> s.disable())
            ;

        return http.build();
    }

    @Bean
	public AuthenticationManager authenticationManager(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		ProviderManager providerManager = new ProviderManager(authenticationProvider);
		providerManager.setEraseCredentialsAfterAuthentication(false);

		return providerManager;
	}

    @Value("${spring.security.user.name}")
    private String username;
    @Value("${spring.security.user.password}")
    private String password;
    @Value("${spring.security.user.roles}")
    private String roles;

	@Bean
	public UserDetailsService userDetailsService() {
        log.debug("Observability user. name={}, password={}, roles=[{}]", username, password, roles);

		@SuppressWarnings("deprecation")
        UserDetails userDetails = User
            .withDefaultPasswordEncoder()
            .username(username)
			.password(password)
			.roles(roles.split(","))
			.build();

		return new InMemoryUserDetailsManager(userDetails);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
