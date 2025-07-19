/**
 * Author: Vinod Jagwani
 */
package se.digitaltolk.translation.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import se.digitaltolk.translation.validator.annotation.Enum;

@Schema(description = "Request payload for updating an existing translation entry")
public record TranslationUpdateRequest(
        @Schema(description = "Unique identifier key for the translation",
                example = "homepage.welcome",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "Key cannot be null or empty")
        @JsonProperty("key")
        String key,

        @Schema(description = "Updated translated text content",
                example = "Welcome back to our service!",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "Value cannot be null or empty")
        @JsonProperty("value")
        String value,

        @Schema(description = "Locale/language of the translation",
                example = "FR",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "Locale cannot be null or empty")
        @Enum(enumClass = TranslationLocale.class, message = "Invalid locale specified")
        @JsonProperty("locale")
        String locale,

        @Schema(description = "Categorization tag for the translation",
                example = "WEB",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "Tag cannot be null or empty")
        @Enum(enumClass = TranslationTag.class, message = "Invalid tag specified")
        @JsonProperty("tag")
        String tag) {

}
