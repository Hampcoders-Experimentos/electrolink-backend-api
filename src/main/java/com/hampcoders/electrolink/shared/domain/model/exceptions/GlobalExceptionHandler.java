package com.hampcoders.electrolink.shared.domain.model.exceptions;

import com.hampcoders.electrolink.shared.interfaces.rest.resources.MessageResource;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler that maps domain and persistence exceptions to clean HTTP
 * statuses with a {@link MessageResource} body.
 *
 * <p>Without this advice every uncaught service exception surfaced as HTTP 500. The
 * mappings below give callers (and integration tests) meaningful 4xx responses.</p>
 *
 * <p>Note: there is intentionally <strong>no</strong> {@code @ExceptionHandler(Exception.class)}
 * catch-all. A broad handler would also intercept {@code ResponseStatusException} (which
 * several controllers throw with a deliberate status) and override it, so framework
 * exceptions and genuinely unexpected failures are left to Spring's default handling.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Maps not-found domain lookups to 404 Not Found.
   *
   * @param ex the thrown exception
   * @return a 404 response carrying the exception message
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<MessageResource> handleEntityNotFound(final EntityNotFoundException ex) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  /**
   * Maps invalid arguments and unresolved references to 400 Bad Request.
   *
   * @param ex the thrown exception
   * @return a 400 response carrying the exception message
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<MessageResource> handleIllegalArgument(final IllegalArgumentException ex) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  /**
   * Maps business-rule violations (e.g. duplicate resource, wrong state) to 409 Conflict.
   *
   * @param ex the thrown exception
   * @return a 409 response carrying the exception message
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<MessageResource> handleIllegalState(final IllegalStateException ex) {
    return build(HttpStatus.CONFLICT, ex.getMessage());
  }

  /**
   * Maps database integrity violations (foreign key / unique constraint) to 409 Conflict.
   *
   * @param ex the thrown exception
   * @return a 409 response
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<MessageResource> handleDataIntegrity(
      final DataIntegrityViolationException ex) {
    return build(HttpStatus.CONFLICT, "Operation violates a data integrity constraint.");
  }

  /**
   * Maps ambiguous single-result lookups (more than one row) to 409 Conflict.
   *
   * @param ex the thrown exception
   * @return a 409 response
   */
  @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
  public ResponseEntity<MessageResource> handleIncorrectResultSize(
      final IncorrectResultSizeDataAccessException ex) {
    return build(HttpStatus.CONFLICT, "The query matched more than one result.");
  }

  /**
   * Maps bean-validation failures to 400 Bad Request.
   *
   * @param ex the thrown exception
   * @return a 400 response
   */
  @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
  public ResponseEntity<MessageResource> handleValidation(final Exception ex) {
    return build(HttpStatus.BAD_REQUEST, "Validation failed: " + ex.getMessage());
  }

  private ResponseEntity<MessageResource> build(final HttpStatus status, final String message) {
    return ResponseEntity.status(status).body(new MessageResource(message));
  }
}
