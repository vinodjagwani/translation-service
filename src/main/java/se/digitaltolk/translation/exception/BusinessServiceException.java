/**
 * Author: Vinod Jagwani
 */
package se.digitaltolk.translation.exception;

import java.io.Serial;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class BusinessServiceException extends RuntimeException implements Serializable {

    @Serial private static final long serialVersionUID = 1905122041950251207L;

    final HttpStatus httpStatus;

    final transient ErrorPrinter errorEnum;

    public BusinessServiceException(final ErrorPrinter errorEnum, final String message) {
        super(message);
        this.errorEnum = errorEnum;
        this.httpStatus = errorEnum.getHttpStatus();
    }
}

