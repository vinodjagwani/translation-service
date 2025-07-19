package se.digitaltolk.translation.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.digitaltolk.translation.annotation.DefaultApiResponse;
import se.digitaltolk.translation.repository.entity.TranslationDocument;
import se.digitaltolk.translation.service.TranslationService;
import se.digitaltolk.translation.web.dto.TranslationCreateRequest;
import se.digitaltolk.translation.web.dto.TranslationUpdateRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/translation")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Translation", description = "Translation Service APIs")
public class TranslationController {

    TranslationService translationService;

    @DefaultApiResponse
    @Operation(summary = "Create a new translation", description = "Creates a translation document with key, locale, tag and value")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<TranslationDocument>> createTranslation(
            @RequestBody @Valid final TranslationCreateRequest request) {
        return new ResponseEntity<>(translationService.createTranslation(request), HttpStatus.CREATED);
    }

    @DefaultApiResponse
    @Operation(summary = "Update an existing translation", description = "Updates a translation document identified by translationId")
    @PutMapping(path = "/{translationId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TranslationDocument> updateTranslation(
            @Parameter(description = "translationId of the translation to update", required = true)
            @PathVariable final String translationId,
            @RequestBody @Valid final TranslationUpdateRequest request) {
        return translationService.updateTranslation(translationId, request);
    }

    @DefaultApiResponse
    @Operation(summary = "Get translation by translationId", description = "Retrieves a translation document by its translationId")
    @GetMapping(path = "/{translationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TranslationDocument> getTranslationById(
            @Parameter(description = "translationId of the translation to retrieve", required = true)
            @PathVariable final String translationId) {
        return translationService.findByTranslationId(translationId);
    }

    @DefaultApiResponse
    @Operation(summary = "Search translations", description = "Search translations by optional filters: key, tag, locale, value")
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TranslationDocument> searchTranslations(
            @Parameter(description = "Filter by key") @RequestParam(required = false) final String key,
            @Parameter(description = "Filter by tag") @RequestParam(required = false) final String tag,
            @Parameter(description = "Filter by locale") @RequestParam(required = false) final String locale,
            @Parameter(description = "Filter by value") @RequestParam(required = false) final String value,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size) {
        return translationService.searchTranslations(key, tag, locale, value, page, size);
    }

    @DefaultApiResponse
    @Operation(summary = "Delete translation by translationId", description = "Deletes a translation document identified by translationId")
    @DeleteMapping(path = "/{translationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTranslation(
            @Parameter(description = "translationId of the translation to delete", required = true)
            @PathVariable final String translationId) {
        return translationService.deleteTranslation(translationId);
    }
}
