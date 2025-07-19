/**
 * Author: Vinod Jagwani
 */
package se.digitaltolk.translation.security;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Either;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import se.digitaltolk.translation.security.util.JwtUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TokenIntrospect implements ReactiveOpaqueTokenIntrospector {

    JwtUtils jwtUtils;

    ObjectMapper objectMapper;

    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(final String token) {
        final Either<AuthenticationException, OAuth2AuthenticatedPrincipal> result = io.vavr.API.Match(token)
                .of(
                        Case($(t -> isFalse(jwtUtils.validateJwtToken(t))),
                                Either.left(new InvalidBearerTokenException("Invalid JWT token"))),
                        Case($(), Either.right(authenticate(token, decode(token))))
                );
        return result.fold(Mono::error, Mono::just);
    }

    private Map<String, Object> decode(final String token) {
        try {
            final DecodedJWT jwt = JWT.decode(token);
            final String base64EncodedBody = jwt.getPayload();
            final String body = new String(Base64.getUrlDecoder().decode(base64EncodedBody));
            final Map<String, Object> map = objectMapper.readValue(body, new TypeReference<>() {
            });
            map.put("token", token);
            return map;
        } catch (JWTDecodeException | JsonProcessingException e) {
            log.warn("Failed to decode JWT token", e);
            throw new InvalidBearerTokenException("Failed to decode JWT token", e);
        }
    }

    private OAuth2AuthenticatedPrincipal authenticate(final String token, final Map<String, Object> claims) {
        final String username = ofNullable((String) claims.get("sub"))
                .or(() -> ofNullable((String) claims.get("username")))
                .orElseThrow(() -> new InvalidBearerTokenException("Missing username in token"));
        final Instant expiration = ofNullable(claims.get("exp"))
                .map(Object::toString)
                .map(Long::parseLong)
                .map(Instant::ofEpochSecond)
                .orElse(Instant.EPOCH);
        return new DefaultOAuth2AuthenticatedPrincipal(
                username, Map.of("exp", expiration, "token", token), new ArrayList<>());
    }

}
