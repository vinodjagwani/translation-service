package se.digitaltolk.translation.mapper;

import java.time.Instant;
import java.util.UUID;
import se.digitaltolk.translation.repository.entity.TranslationDocument;
import se.digitaltolk.translation.web.dto.TranslationCreateRequest;
import se.digitaltolk.translation.web.dto.TranslationUpdateRequest;

public final class TranslationMapper {

    private TranslationMapper() {
    }

    public static TranslationDocument toEntity(final TranslationCreateRequest request) {
        var doc = new TranslationDocument();
        doc.setId(UUID.randomUUID().toString());
        doc.setKey(request.key());
        doc.setValue(request.value());
        doc.setLocale(request.locale());
        doc.setTag(request.tag());
        long now = Instant.now().toEpochMilli();
        doc.setCreatedAt(now);
        doc.setUpdatedAt(now);
        return doc;
    }

    public static void updateEntityFromRequest(final TranslationDocument doc, final TranslationUpdateRequest request) {
        doc.setKey(request.key());
        doc.setValue(request.value());
        doc.setLocale(request.locale());
        doc.setTag(request.tag());
        doc.setUpdatedAt(Instant.now().toEpochMilli());
    }
}
