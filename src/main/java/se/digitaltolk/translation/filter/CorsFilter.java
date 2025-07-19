/**
 * @author Vinod Jagwani
 */
package se.digitaltolk.translation.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CorsFilter implements WebFilter {

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    private static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    private static final String ALLOWED_ORIGINS = "*";
    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, PATCH";
    private static final String ALLOWED_HEADERS = "Content-Type, Authorization, Origin, Accept";
    private static final String EXPOSED_HEADERS = "Content-Length, Authorization, Origin";
    private static final String MAX_AGE = "3600";

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull final ServerWebExchange exchange, @NonNull final WebFilterChain chain) {
        var response = exchange.getResponse();
        var headers = response.getHeaders();
        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGINS);
        headers.add(ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);
        headers.add(ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS);
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, EXPOSED_HEADERS);
        headers.add(ACCESS_CONTROL_MAX_AGE, MAX_AGE);
        headers.add(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        if (isPreflightRequest(exchange)) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }
        return chain.filter(exchange);
    }

    private boolean isPreflightRequest(final ServerWebExchange exchange) {
        return exchange.getRequest().getMethod() == HttpMethod.OPTIONS &&
                exchange.getRequest().getHeaders().containsKey("Origin") &&
                exchange.getRequest().getHeaders().containsKey("Access-Control-Request-Method");
    }
}