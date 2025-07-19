package se.digitaltolk.translation.web.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.digitaltolk.translation.repository.entity.TranslationDocument;
import se.digitaltolk.translation.service.TranslationService;
import se.digitaltolk.translation.web.dto.TranslationCreateRequest;
import se.digitaltolk.translation.web.dto.TranslationUpdateRequest;

@WebFluxTest(TranslationController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Import(TranslationControllerTest.MockConfig.class)
class TranslationControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    TranslationService translationService;

    @Test
    @WithMockUser
    @DisplayName("Test createTranslation - success")
    void testCreateTranslation_Success() {
        final TranslationCreateRequest request = new TranslationCreateRequest(
                "greeting", "Hello", "EN", "MOBILE"
        );
        when(translationService.createTranslation(any())).thenReturn(Mono.just(buildDocument()));
        webTestClient.post()
                .uri("/v1/translation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("t1")
                .jsonPath("$.key").isEqualTo("greeting")
                .jsonPath("$.value").isEqualTo("Hello")
                .jsonPath("$.locale").isEqualTo("EN")
                .jsonPath("$.tag").isEqualTo("MOBILE");
    }

    @Test
    @WithMockUser
    @DisplayName("Test updateTranslation - success")
    void testUpdateTranslation_Success() {
        final TranslationUpdateRequest request = new TranslationUpdateRequest(
                "farewell", "Goodbye", "EN", "DESKTOP"
        );
        when(translationService.updateTranslation(eq("t1"), any())).thenReturn(Mono.just(buildDocument()));
        webTestClient.put()
                .uri("/v1/translation/t1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("t1");
    }

    @Test
    @WithMockUser
    @DisplayName("Test getTranslationById")
    void testGetTranslationById() {
        when(translationService.findByTranslationId("t1")).thenReturn(Mono.just(buildDocument()));
        webTestClient.get()
                .uri("/v1/translation/t1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("t1");
    }

    @Test
    @WithMockUser
    @DisplayName("Test searchTranslations")
    void testSearchTranslations() {
        when(translationService.searchTranslations(any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(
                Flux.just(buildDocument()));
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/translation/search")
                        .queryParam("key", "greeting")
                        .queryParam("value", "Hello")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("t1");
    }

    @Test
    @WithMockUser
    @DisplayName("Test deleteTranslation")
    void testDeleteTranslation() {
        when(translationService.deleteTranslation("t1")).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/v1/translation/t1")
                .exchange()
                .expectStatus().isNoContent();
    }

    private TranslationDocument buildDocument() {
        final TranslationDocument doc = new TranslationDocument();
        doc.setId("t1");
        doc.setKey("greeting");
        doc.setValue("Hello");
        doc.setLocale("EN");
        doc.setTag("MOBILE");
        doc.setCreatedAt(Instant.now().toEpochMilli());
        doc.setUpdatedAt(Instant.now().toEpochMilli());
        return doc;
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                    .build();
        }
    }
}
