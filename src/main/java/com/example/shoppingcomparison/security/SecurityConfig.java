package com.example.shoppingcomparison.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static com.example.shoppingcomparison.security.UserRole.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails annaSmithUser = User.builder()
                .username("annasmith")
                .password(passwordEncoder.encode("password"))
                .roles(USER.name())
                .build();

        UserDetails lindaUser = User.builder()
                .username("linda")
                .password(passwordEncoder.encode("password123"))
                .roles(ADMIN.name())
                .build();

        return new InMemoryUserDetailsManager(annaSmithUser,
                lindaUser);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //BasicAuth
        http
                .authorizeRequests()
                .antMatchers("/", "/index", "/allProducts", "/products").permitAll()
                .antMatchers("/console").hasRole(ADMIN.name())
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
//                .formLogin();
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/font/**", "/js/**", "/img/**", "/icon/**");
    }
}
