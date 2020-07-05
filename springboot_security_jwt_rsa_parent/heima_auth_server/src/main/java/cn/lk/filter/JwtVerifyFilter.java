package cn.lk.filter;

import cn.lk.config.RsaKeyProperties;
import cn.lk.domain.Payload;
import cn.lk.domain.SysUser;
import cn.lk.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class JwtVerifyFilter extends BasicAuthenticationFilter {

    private RsaKeyProperties prop;

    public JwtVerifyFilter(AuthenticationManager authenticationManager, RsaKeyProperties prop) {
        super(authenticationManager);
        this.prop = prop;
    }

    /**
     * 过滤请求
     */
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            //请求体的头中是否包含Authorization
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                //如果携带错误的token，则给用户提示请登录！
                chain.doFilter(request, response);
                response.setContentType("application/json;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter out = response.getWriter();
                Map resultMap = new HashMap();
                resultMap.put("code", HttpServletResponse.SC_FORBIDDEN);
                resultMap.put("msg", "请登录！");
                out.write(new ObjectMapper().writeValueAsString(resultMap));
                out.flush();
                out.close();
            } else {
                //如果携带了正确格式的token要先得到token
                String token = header.replace("Bearer ", "");
                //验证token是否正确
                Payload<SysUser> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), SysUser.class);
                SysUser user = payload.getUserInfo();
                if (user != null) {
                    //获取权限失败，会抛出异常
                    UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
                    //获取后，将Authentication写入SecurityContextHolder中供后序使用
                    SecurityContextHolder.getContext().setAuthentication(authResult);
                    chain.doFilter(request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
