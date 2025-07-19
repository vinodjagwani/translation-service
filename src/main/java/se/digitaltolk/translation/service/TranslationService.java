package se.digitaltolk.translation.service;


import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.digitaltolk.translation.repository.entity.TranslationDocument;
import se.digitaltolk.translation.web.dto.TranslationCreateRequest;
import se.digitaltolk.translation.web.dto.TranslationUpdateRequest;

public interface TranslationService {

    Mono<TranslationDocument> findByTranslationId(final String translationId);

    Mono<TranslationDocument> createTranslation(final TranslationCreateRequest request);

    Mono<TranslationDocument> updateTranslation(final String translationId, final TranslationUpdateRequest request);

    Flux<TranslationDocument> searchTranslations(
            final String key,
            final String tag,
            final String locale,
            final String value,
            final int page,
            final int size);

    Mono<Void> deleteTranslation(final String translationId);

    Mono<Void> bulkCreateTranslations(final List<TranslationCreateRequest> requests);

}
