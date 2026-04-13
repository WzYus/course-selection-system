package cn.detect.cs.security;

import cn.detect.cs.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // 从请求头中获取 Authorization
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 如果请求头存在且以 "Bearer " 开头，则提取 token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // 如果 token 解析失败（过期、签名错误等），仅记录日志，不中断请求
                logger.error("JWT token parsing error: " + e.getMessage());
            }
        }

        // 如果解析出用户名，并且当前 SecurityContext 中没有认证信息，则进行验证
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 加载用户详情（会调用你的 CustomUserDetailsService）
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 验证 token 是否有效
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // 创建认证令牌
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证信息设置到 SecurityContext 中
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 无论是否设置认证，都继续执行过滤器链
        // 这样公开接口（如 /api/auth/register/**）即使没有 token 也能被访问
        chain.doFilter(request, response);
    }
}