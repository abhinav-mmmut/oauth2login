package com.example.oauth2impl;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/login_page").permitAll()
			.anyRequest().authenticated()
			.and()
			.oauth2Login()
			.loginPage("/login_page").defaultSuccessUrl("/",true)
			.and()
			.logout().logoutUrl("/logout_url").logoutSuccessUrl("/login_page");
	}

}
