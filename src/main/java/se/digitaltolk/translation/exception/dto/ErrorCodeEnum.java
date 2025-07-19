/**
 * Author: Vinod Jagwani
 */
package se.digitaltolk.translation.exception.dto;

import org.springframework.http.HttpStatus;
import se.digitaltolk.translation.exception.ErrorPrinter;

public enum ErrorCodeEnum implements ErrorPrinter {

    NOT_FOUND(HttpStatus.BAD_REQUEST),
    INVALID_PARAM(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED);

    ErrorCodeEnum(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    private final HttpStatus httpStatus;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
