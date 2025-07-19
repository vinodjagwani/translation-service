package se.digitaltolk.translation.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.digitaltolk.translation.exception.BusinessServiceException;
import se.digitaltolk.translation.exception.dto.ErrorCodeEnum;
import se.digitaltolk.translation.mapper.TranslationMapper;
import se.digitaltolk.translation.repository.TranslationRepository;
import se.digitaltolk.translation.repository.entity.TranslationDocument;
import se.digitaltolk.translation.service.TranslationService;
import se.digitaltolk.translation.web.dto.TranslationCreateRequest;
import se.digitaltolk.translation.web.dto.TranslationUpdateRequest;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TranslationServiceImpl implements TranslationService {

    TranslationRepository translationRepository;

    ReactiveElasticsearchTemplate elasticsearchTemplate;

    public Mono<TranslationDocument> createTranslation(final TranslationCreateRequest request) {
        log.info("Start create translation with request [{}]", request);
        final TranslationDocument translationDocument = TranslationMapper.toEntity(request);
        return translationRepository.save(translationDocument)
                .doOnSuccess(
                        t -> log.info("Successfully created translation [{}] with key [{}]", t.getId(), t.getKey()))
                .doOnError(e -> log.error("Error creating translation with key [{}]", request.key(), e));
    }

    public Mono<TranslationDocument> updateTranslation(
            final String translationId,
            final TranslationUpdateRequest request
    ) {
        log.info("Start update translation for translationId [{}] with request [{}]", translationId, request);
        return translationRepository.findById(translationId)
                .switchIfEmpty(Mono.error(new BusinessServiceException(ErrorCodeEnum.NOT_FOUND,
                        "Translation not found: " + translationId)))
                .flatMap(existing -> {
                    TranslationMapper.updateEntityFromRequest(existing, request);
                    return translationRepository.save(existing);
                })
                .doOnSuccess(
                        t -> log.info("Successfully updated translation [{}] with key [{}]", t.getId(), t.getKey()))
                .doOnError(e -> log.error("Error updating translation [{}]", translationId, e));
    }

    public Flux<TranslationDocument> searchTranslations(final String key,
            final String tag,
            final String locale,
            final String value,
            final int page,
            final int size) {
        log.info("Start search translations with filters key=[{}], tag=[{}], locale=[{}], value=[{}]",
                key, tag, locale, value);
        final CriteriaQuery query = buildCriteriaQuery(key, tag, locale, value);
        query.setPageable(PageRequest.of(page, size));
        log.debug("Executing search with query: {}", query);
        return elasticsearchTemplate.search(query, TranslationDocument.class)
                .map(SearchHit::getContent)
                .doOnComplete(() -> log.info("Completed searchTranslations"))
                .doOnError(e -> log.error("Error during search translations", e));
    }


    public Mono<TranslationDocument> findByTranslationId(final String translationId) {
        log.info("Start query translation for translationId [{}]", translationId);
        return translationRepository.findById(translationId)
                .switchIfEmpty(Mono.error(new BusinessServiceException(ErrorCodeEnum.NOT_FOUND,
                        "Translation not found: " + translationId)))
                .doOnSuccess(t -> {
                    if (t != null) {
                        log.info("Found translation [{}] with key [{}]", t.getId(), t.getKey());
                    }
                })
                .doOnError(e -> log.error("Error fetching translation [{}]", translationId, e));
    }

    public Mono<Void> deleteTranslation(final String translationId) {
        log.info("Start delete translation for translationId [{}]", translationId);
        return translationRepository.deleteById(translationId)
                .doOnSuccess(v -> log.info("Deleted translation [{}]", translationId))
                .doOnError(e -> log.error("Error deleting translation [{}]", translationId, e));
    }

    private CriteriaQuery buildCriteriaQuery(
            final String key,
            final String tag,
            final String locale,
            final String value
    ) {
        final List<Criteria> criteriaList = new ArrayList<>();
        if (StringUtils.isNotBlank(key)) {
            criteriaList.add(Criteria.where("key").is(key));
        }
        if (StringUtils.isNotBlank(tag)) {
            criteriaList.add(Criteria.where("tag").is(tag));
        }
        if (StringUtils.isNotBlank(locale)) {
            criteriaList.add(Criteria.where("locale").is(locale));
        }
        if (StringUtils.isNotBlank(value)) {
            criteriaList.add(Criteria.where("value").matches(value));
        }
        final Criteria combined = criteriaList.stream().reduce(Criteria::and).orElse(new Criteria());
        return new CriteriaQuery(combined);
    }

    public Mono<Void> bulkCreateTranslations(final List<TranslationCreateRequest> requests) {
        log.debug("Start bulk insert translation with requests [{}]", requests);
        final List<TranslationDocument> docs = requests.stream().map(TranslationMapper::toEntity).toList();
        return translationRepository.saveAll(docs).then();
    }
}
