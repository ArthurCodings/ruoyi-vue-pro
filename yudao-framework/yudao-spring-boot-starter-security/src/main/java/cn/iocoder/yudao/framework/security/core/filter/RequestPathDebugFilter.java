package cn.iocoder.yudao.framework.security.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 临时调试过滤器：打印 /admin-api/system/tenant 请求的实际路径，用于排查 permitAll 不匹配问题。
 * 排查完成后可删除此类及对应 Bean 注册。
 */
@Slf4j
public class RequestPathDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri != null && uri.contains("/tenant/")) {
            log.warn("[RequestPathDebug] URI={}, contextPath={}, servletPath={}, pathInfo={}",
                    uri, request.getContextPath(), request.getServletPath(), request.getPathInfo());
        }
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 仅对 /tenant/ 相关请求打日志，无论路径是否含 /admin-api/（外部代理可能改写路径）
        String uri = request.getRequestURI();
        return uri == null || !uri.contains("/tenant/");
    }
}
