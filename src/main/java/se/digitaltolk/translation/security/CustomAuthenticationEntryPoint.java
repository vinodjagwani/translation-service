/**
 * Author: Vinod Jagwani
 */
package se.digitaltolk.translation.security;

import static java.util.Collections.emptyList;
import static se.digitaltolk.translation.exception.DefaultExceptionHandler.SERVICE_CODE;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.digitaltolk.translation.exception.dto.ErrorResponse;

@Slf4j
public final class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SneakyThrows
    public Mono<Void> commence(final ServerWebExchange exchange, final AuthenticationException ex) {
        log.error("Unauthorized error: ", ex);
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code(SERVICE_CODE)
                .message("Full authentication is required to access this resource")
                .errors(emptyList())
                .build();
        final byte[] data = objectMapper.writeValueAsBytes(errorResponse);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return exchange.getResponse()
                .writeWith(Flux.just(exchange.getResponse().bufferFactory().wrap(data)))
                .doOnError(error -> log.error("Error writing response", error))
                .then();
    }

}
