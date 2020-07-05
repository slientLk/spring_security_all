package cn.lk.config;

import cn.lk.sevice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;


    @Bean
    public BCryptPasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //配置认证对象的来源
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      /*  auth.inMemoryAuthentication().withUser("user")
                .password("{noop}123456")
                .roles("USER");*/
        auth.userDetailsService(userService).passwordEncoder(getPasswordEncoder());
    }

    //SpringSecurity配置信息
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login.jsp","/failer.jsp","/css/**","/img/**","/plugins/**").permitAll()
                .antMatchers("/product").hasAnyRole("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.jsp")
                .loginProcessingUrl("/login")
                .successForwardUrl("/index.jsp")
                .failureForwardUrl("/failer.jsp")
                .and()
                .logout()
                .logoutSuccessUrl("/logout")
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/login.jsp")
                .and()
                .csrf()
                .disable();
    }
}
