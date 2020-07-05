package cn.lk.filter;

import cn.lk.config.RsaKeyProperties;
import cn.lk.domain.SysRole;
import cn.lk.domain.SysUser;
import cn.lk.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private RsaKeyProperties prop;

    public JwtLoginFilter(AuthenticationManager authenticationManager, RsaKeyProperties prop) {
        this.authenticationManager = authenticationManager;
        this.prop = prop;
    }

    /**
     * 接收并解析用户凭证，出現错误时，返回json数据前端
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //将json格式请求体转成javaBean对象
            SysUser sysUser = new ObjectMapper().readValue(request.getInputStream(), SysUser.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            sysUser.getUsername(),
                            sysUser.getPassword()
                    ));
        } catch (IOException e) {
            try {
                //如果认证失败，提供自定义json格式异常
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter ps = response.getWriter();
                HashMap<String, Object> map = new HashMap<>();
                map.put("code", HttpServletResponse.SC_UNAUTHORIZED);
                map.put("message", "账号或密码错误！");
                ps.write(new ObjectMapper().writeValueAsString(map));
                ps.flush();
                ps.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 用户登录成功后，生成token,并且返回json数据给前端
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //得到当前认证的用户对象
        SysUser sysUser = new SysUser();
        sysUser.setUsername(authResult.getName());
        sysUser.setRoles((List<SysRole>) authResult.getAuthorities());
        //json web token创建
        String token = JwtUtils.generateTokenExpireInMinutes(sysUser, prop.getPrivateKey(), 24 * 60);

        //返回token
        response.addHeader("Authorization", "Bearer " + token);


        try {
            //登录成功时，返回json格式进行提示
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter ps = response.getWriter();
            HashMap<String, Object> map = new HashMap<>();
            map.put("code", HttpServletResponse.SC_OK);
            map.put("message", "登录成功！");
            ps.write(new ObjectMapper().writeValueAsString(map));
            ps.flush();
            ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
