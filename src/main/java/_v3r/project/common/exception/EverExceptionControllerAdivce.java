package _v3r.project.common.exception;

import _v3r.project.common.apiResponse.CustomApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class EverExceptionControllerAdivce {
    @ExceptionHandler(EverException.class)
    public ResponseEntity<CustomApiResponse<?>> handlePlanearException(EverException e) {
        log.warn("ClassfitException", e);
        return ResponseEntity.status(e.getHttpStatusCode())
                .body(CustomApiResponse.fail(e.getErrorCode()));
    }

}
