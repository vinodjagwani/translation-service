/**
 * @author Vinod Jagwani
 */
package se.digitaltolk.translation.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import se.digitaltolk.translation.exception.dto.ErrorCodeEnum;
import se.digitaltolk.translation.exception.dto.ErrorResponse;
import se.digitaltolk.translation.exception.dto.ErrorResponse.ErrorInfo;

@Slf4j
@RestControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DefaultExceptionHandler {

    public static final int SERVICE_CODE = 1000;
    private static final String EMPTY_DOMAIN = "";
    private static final String EXCEPTION_OCCURRED_MSG = "Exception occurred: {}";

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleGenericException(final Throwable ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage(), Collections.emptyList()));
    }

    @ExceptionHandler({WebExchangeBindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidationExceptions(final Exception ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex.getMessage(), ex);
        final List<ErrorInfo> errors = ex instanceof WebExchangeBindException webEx ?
                webEx.getFieldErrors().stream()
                        .map(error -> buildErrorInfo(
                                error.getField(),
                                error.getDefaultMessage(),
                                ErrorCodeEnum.INVALID_PARAM.name()))
                        .toList() :
                ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().stream()
                        .map(error -> buildErrorInfo(
                                error.getObjectName(),
                                error.getDefaultMessage(),
                                ErrorCodeEnum.INVALID_PARAM.name()))
                        .toList();
        return ResponseEntity.badRequest().body(buildErrorResponse("Validation failed", errors));
    }

    @ExceptionHandler(BusinessServiceException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessServiceException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex.getMessage(), ex);
        final ErrorInfo errorInfo = buildErrorInfo(
                EMPTY_DOMAIN,
                ex.getMessage(),
                ex.getErrorEnum().toString());
        return ResponseEntity.status(ex.getHttpStatus())
                .body(buildErrorResponse(ex.getErrorEnum().toString(), List.of(errorInfo)));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(final ConstraintViolationException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex.getMessage(), ex);
        final ErrorInfo errorInfo = buildErrorInfo(EMPTY_DOMAIN, ex.getMessage(), ErrorCodeEnum.INVALID_PARAM.name());
        return ResponseEntity.badRequest()
                .body(buildErrorResponse("Constraint violation", List.of(errorInfo)));
    }

    @ExceptionHandler({IllegalArgumentException.class, ValidationException.class})
    public ResponseEntity<ErrorResponse> handleValidationRelatedExceptions(final Exception ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex.getMessage(), ex);
        if (ex instanceof ValidationException valEx) {
            final Throwable rootCause = ExceptionUtils.getRootCause(valEx);
            if (rootCause instanceof BusinessServiceException businessEx) {
                return handleBusinessException(businessEx);
            }
        }
        return ResponseEntity.badRequest()
                .body(buildErrorResponse(ex.getMessage(), Collections.emptyList()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, InvalidFormatException.class})
    public ResponseEntity<ErrorResponse> handleMessageParsingExceptions(final Exception ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex.getMessage(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        final Throwable rootCause = ExceptionUtils.getRootCause(ex);
        if (rootCause instanceof DateTimeParseException || rootCause instanceof InvalidFormatException) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
        }
        return ResponseEntity.status(status)
                .body(buildErrorResponse("Invalid request format", Collections.emptyList()));
    }

    private ErrorResponse buildErrorResponse(final String message, final List<ErrorInfo> errorInfos) {
        return ErrorResponse.builder()
                .message(message)
                .code(SERVICE_CODE)
                .errors(errorInfos)
                .build();
    }

    private ErrorInfo buildErrorInfo(final String domain, final String message, final String reason) {
        return ErrorInfo.builder()
                .domain(domain)
                .message(message)
                .reason(reason)
                .build();
    }
}