package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.service.PasswordService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final SuccessUserHandler successUserHandler;
    private final PasswordService passwordService;

    @Autowired
    public WebSecurityConfig(@Lazy UserDetailsService userDetailsService,
                             SuccessUserHandler successUserHandler,
                             PasswordService passwordService) {
        this.userDetailsService = userDetailsService;
        this.successUserHandler = successUserHandler;
        this.passwordService = passwordService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/login", "/register").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successUserHandler)
                .failureUrl("/login?error=true").permitAll()
                .and()
                .logout().logoutUrl("/logout")
                .logoutSuccessUrl("/").permitAll()
                .and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordService.getPasswordEncoder());
    }
}
