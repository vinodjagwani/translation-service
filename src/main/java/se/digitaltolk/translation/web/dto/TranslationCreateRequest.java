/**
 * @author Vinod Jagwani
 */
package se.digitaltolk.translation.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import se.digitaltolk.translation.validator.annotation.Enum;

@Schema(description = "Request payload for creating a new translation entry")
public record TranslationCreateRequest(
        @Schema(description = "Unique identifier key for the translation",
                example = "homepage.welcome",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "key can't be null or empty")
        @JsonProperty("key")
        String key,

        @Schema(description = "The translated text content",
                example = "Welcome to our service!",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "value can't be null or empty")
        @JsonProperty("value")
        String value,

        @Schema(description = "Locale/language of the translation",
                example = "EN",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "locale can't be null or empty")
        @Enum(enumClass = TranslationLocale.class, message = "Invalid locale")
        @JsonProperty("locale")
        String locale,

        @Schema(description = "Categorization tag for the translation",
                example = "WEB",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "tag can't be null or empty")
        @Enum(enumClass = TranslationTag.class, message = "Invalid tag")
        @JsonProperty("tag")
        String tag) {

}