package se.digitaltolk.translation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import se.digitaltolk.translation.mapper.TranslationMapper;
import se.digitaltolk.translation.repository.TranslationRepository;
import se.digitaltolk.translation.repository.entity.TranslationDocument;
import se.digitaltolk.translation.service.impl.TranslationServiceImpl;
import se.digitaltolk.translation.web.dto.TranslationCreateRequest;
import se.digitaltolk.translation.web.dto.TranslationLocale;
import se.digitaltolk.translation.web.dto.TranslationTag;
import se.digitaltolk.translation.web.dto.TranslationUpdateRequest;

@FieldDefaults(level = AccessLevel.PRIVATE)
class TranslationServiceTest {

    static final String TEST_ID = "t1";
    static final String TEST_KEY = "welcome.message";
    static final String TEST_VALUE = "Welcome!";
    static final String TEST_LOCALE = TranslationLocale.EN.name();
    static final String TEST_TAG = TranslationTag.WEB.name();
    static final String UPDATED_VALUE = "Welcome again!";
    static final String UPDATED_TAG = TranslationTag.MOBILE.name();

    @Mock
    TranslationRepository translationRepository;

    @Mock
    ReactiveElasticsearchTemplate elasticsearchTemplate;

    @InjectMocks
    TranslationServiceImpl translationService;

    AutoCloseable mocksCloseable;

    @BeforeEach
    void setUp() {
        mocksCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocksCloseable.close();
    }

    @Test
    @DisplayName("Test createTranslation")
    void testCreateTranslation() {
        final TranslationCreateRequest request = new TranslationCreateRequest(
                TEST_KEY, TEST_VALUE, TEST_LOCALE, TEST_TAG);
        when(translationRepository.save(any(TranslationDocument.class)))
                .thenReturn(Mono.just(buildTestDocument()));
        StepVerifier.create(translationService.createTranslation(request))
                .expectNextMatches(translation ->
                        TEST_ID.equals(translation.getId()) &&
                                TEST_KEY.equals(translation.getKey())).verifyComplete();
        verify(translationRepository).save(any(TranslationDocument.class));
    }

    @Test
    @DisplayName("Test bulkCreateTranslations")
    void testBulkCreateTranslations() {
        final TranslationCreateRequest request1 = new TranslationCreateRequest("key1", "value1", "EN", "DESKTOP");
        final TranslationCreateRequest request2 = new TranslationCreateRequest("key2", "value2", "FR", "MOBILE");
        final List<TranslationCreateRequest> requests = List.of(request1, request2);
        final TranslationDocument doc1 = buildTestDocument("key1", "value1", "EN", "DESKTOP");
        final TranslationDocument doc2 = buildTestDocument("key2", "value2", "FR", "MOBILE");
        try (MockedStatic<TranslationMapper> mockedMapper = Mockito.mockStatic(TranslationMapper.class)) {
            mockedMapper.when(() -> TranslationMapper.toEntity(request1)).thenReturn(doc1);
            mockedMapper.when(() -> TranslationMapper.toEntity(request2)).thenReturn(doc2);
            when(translationRepository.saveAll(List.of(doc1, doc2))).thenReturn(Flux.just(doc1, doc2));
            StepVerifier.create(translationService.bulkCreateTranslations(requests)).verifyComplete();
            verify(translationRepository).saveAll(List.of(doc1, doc2));
        }
    }

    @Test
    @DisplayName("Test updateTranslation")
    void testUpdateTranslation() {
        final TranslationUpdateRequest updateRequest = new TranslationUpdateRequest(
                TEST_KEY, UPDATED_VALUE, TEST_LOCALE, UPDATED_TAG);
        when(translationRepository.findById(TEST_ID))
                .thenReturn(Mono.just(buildTestDocument()));
        when(translationRepository.save(any(TranslationDocument.class)))
                .thenReturn(Mono.just(buildUpdatedDocument()));
        StepVerifier.create(translationService.updateTranslation(TEST_ID, updateRequest))
                .expectNextMatches(translation ->
                        UPDATED_VALUE.equals(translation.getValue()) &&
                                UPDATED_TAG.equals(translation.getTag())).verifyComplete();
        verify(translationRepository).findById(TEST_ID);
        verify(translationRepository).save(any(TranslationDocument.class));
    }

    @Test
    @DisplayName("Test searchTranslations")
    void testSearchTranslations() {
        final TranslationDocument doc = buildTestDocument();
        SearchHit<TranslationDocument> searchHit = new SearchHit<>(
                null, doc.getId(), null, 1.0f, null, null, null, null, null, null, doc);
        when(elasticsearchTemplate.search(any(CriteriaQuery.class), eq(TranslationDocument.class)))
                .thenReturn(Flux.just(searchHit));
        StepVerifier.create(translationService.searchTranslations(
                        TEST_KEY, TEST_TAG, TEST_LOCALE, TEST_VALUE, 0, 10))
                .expectNextMatches(translation ->
                        TEST_KEY.equals(translation.getKey())).verifyComplete();
        verify(elasticsearchTemplate).search(any(CriteriaQuery.class), eq(TranslationDocument.class));
    }

    @Test
    @DisplayName("Test deleteTranslation")
    void testDeleteTranslation() {
        when(translationRepository.deleteById(TEST_ID)).thenReturn(Mono.empty());
        StepVerifier.create(translationService.deleteTranslation(TEST_ID)).verifyComplete();
        verify(translationRepository).deleteById(TEST_ID);
    }

    private TranslationDocument buildTestDocument() {
        return buildTestDocument(TEST_KEY, TEST_VALUE, TEST_LOCALE, TEST_TAG);
    }

    private TranslationDocument buildUpdatedDocument() {
        return buildTestDocument(TEST_KEY, UPDATED_VALUE, TEST_LOCALE, UPDATED_TAG);
    }

    private TranslationDocument buildTestDocument(
            final String key,
            final String value,
            final String locale,
            final String tag
    ) {
        final TranslationDocument doc = new TranslationDocument();
        doc.setId(TEST_ID);
        doc.setKey(key);
        doc.setValue(value);
        doc.setLocale(locale);
        doc.setTag(tag);
        doc.setCreatedAt(Instant.now().toEpochMilli());
        doc.setUpdatedAt(Instant.now().toEpochMilli());
        return doc;
    }
}