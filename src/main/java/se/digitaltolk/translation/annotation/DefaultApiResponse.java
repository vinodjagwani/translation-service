/**
 * Author: Vinod Jagwani
 */
package se.digitaltolk.translation.annotation;

import static se.digitaltolk.translation.constant.OpenApiConstant.FORBIDDEN_REQUEST_PHRASE;
import static se.digitaltolk.translation.constant.OpenApiConstant.GATEWAY_TIMEOUT_REQUEST_PHRASE;
import static se.digitaltolk.translation.constant.OpenApiConstant.INVALID_REQUEST;
import static se.digitaltolk.translation.constant.OpenApiConstant.SUCCESS_PHRASE;
import static se.digitaltolk.translation.constant.OpenApiConstant.UNAUTHORIZED_REQUEST_PHRASE;
import static se.digitaltolk.translation.constant.OpenApiConstant.UN_PROCESSABLE_REQUEST_PHRASE;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.MediaType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = SUCCESS_PHRASE),
        @ApiResponse(responseCode = "201", description = SUCCESS_PHRASE),
        @ApiResponse(responseCode = "400", description = INVALID_REQUEST, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "403", description = FORBIDDEN_REQUEST_PHRASE, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "401", description = UNAUTHORIZED_REQUEST_PHRASE, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = UN_PROCESSABLE_REQUEST_PHRASE, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "502", description = GATEWAY_TIMEOUT_REQUEST_PHRASE, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
})
public @interface DefaultApiResponse {

}
