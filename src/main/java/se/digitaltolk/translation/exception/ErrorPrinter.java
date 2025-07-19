/**
 * Author: Vinod Jagwani
 */
package se.digitaltolk.translation.exception;

import org.springframework.http.HttpStatus;

public interface ErrorPrinter {

    HttpStatus getHttpStatus();

}
