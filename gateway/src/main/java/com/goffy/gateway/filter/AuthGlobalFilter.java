package com.goffy.gateway.filter;

import com.goffy.gateway.component.HandleException;
import com.goffy.gateway.config.IgnoreUrlsConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/13/14:42
 * @Description:
 */
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Resource
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private HandleException handleException;


    /**
     * Process the Web request and (optionally) delegate to the next {@code WebFilter}
     * through the given {@link GatewayFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 判断当前的请求是否在白名单中
        AntPathMatcher pathMatcher = new AntPathMatcher();
        boolean flag = false;
        String path = exchange.getRequest().getURI().getPath();
        for (String url : ignoreUrlsConfig.getUrls()) {
            if (pathMatcher.match(url, path)) {
                flag = true;
                break;
            }
        }
        // 白名单放行
        if (flag) {
            return chain.filter(exchange);
        }
        // 获取 access_token
        String access_token = exchange.getRequest().getQueryParams().getFirst("access_token");
        // 判断 access_token 是否为空
        if (StringUtils.isBlank(access_token)) {
            return handleException.writeError(exchange, "请登录");
        }
        // 校验 token 是否有效
        String checkTokenUrl = "http://oauth2-server/oauth/check_token?token=".concat(access_token);
        try {
            // 发送远程请求，验证 token
            ResponseEntity<String> entity = restTemplate.getForEntity(checkTokenUrl, String.class);
            // token 无效的业务逻辑处理
            if (entity.getStatusCode() != HttpStatus.OK) {
                return handleException.writeError(exchange,
                        "Token was not recognised, token: ".concat(access_token));
            }
            if (StringUtils.isBlank(entity.getBody())) {
                return handleException.writeError(exchange,
                        "This token is invalid: ".concat(access_token));
            }
        } catch (Exception e) {
            return handleException.writeError(exchange,
                    "Token was not recognised, token: ".concat(access_token));
        }
        // 放行
        return chain.filter(exchange);
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     * 网关过滤器的排序，数字越小优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
