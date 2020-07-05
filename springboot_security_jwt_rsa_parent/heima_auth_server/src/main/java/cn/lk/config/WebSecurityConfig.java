package cn.lk.config;

import cn.lk.filter.JwtLoginFilter;
import cn.lk.filter.JwtVerifyFilter;
import cn.lk.sevice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private RsaKeyProperties prop;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //指定认证对象的来源
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    //SpringSecurity配置信息
    public void configure(HttpSecurity http) throws Exception {
        //关闭跨站请求防护
        http.csrf()
                .disable()
                //拥有ROLE_USER角色才可以访问产品
                .authorizeRequests().antMatchers("/product").hasAnyRole("USER")
                //其他的需要授权后访问
                .anyRequest().authenticated()
                .and()
                //增加自定义认证过滤器
                .addFilter(new JwtLoginFilter(super.authenticationManager(), prop))
                //增加自定义验证认证过滤器
                .addFilter(new JwtVerifyFilter(super.authenticationManager(), prop))
                //前后端分离是无状态的，不用session了，直接禁用。
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
