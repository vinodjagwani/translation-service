package se.digitaltolk.translation;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.digitaltolk.translation.service.TranslationService;
import se.digitaltolk.translation.web.dto.TranslationCreateRequest;
import se.digitaltolk.translation.web.dto.TranslationLocale;
import se.digitaltolk.translation.web.dto.TranslationTag;

@Slf4j
@Component
@Profile({"test", "dev"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TranslationTestDataLoader implements CommandLineRunner {

    TranslationService translationService;

    @Override
    public void run(String... args) {
        final Random random = new Random();
        final TranslationLocale[] locales = TranslationLocale.values();
        final TranslationTag[] tags = TranslationTag.values();
        final List<TranslationCreateRequest> dummyRequests = IntStream.rangeClosed(1, 100_000)
                .mapToObj(i -> {
                    String randomLocale = locales[random.nextInt(locales.length)].name();
                    String randomTag = tags[random.nextInt(tags.length)].name();
                    return new TranslationCreateRequest(
                            "test.key." + i,
                            "value " + i,
                            randomLocale,
                            randomTag);
                })
                .toList();

        int batchSize = 1000;
        for (int i = 0; i < dummyRequests.size(); i += batchSize) {
            int end = Math.min(i + batchSize, dummyRequests.size());
            List<TranslationCreateRequest> batch = dummyRequests.subList(i, end);
            translationService.bulkCreateTranslations(batch).block();
            log.info("Inserted batch: {} to {}", i, end);
        }
    }
}
