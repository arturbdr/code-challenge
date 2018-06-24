package com.n26.challenge.gateway.controller.advice;

import com.n26.challenge.domain.exception.OldTransactionMetricException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class TransactionControllerAdvice {

    @ExceptionHandler(OldTransactionMetricException.class)
    public ResponseEntity catchException(final OldTransactionMetricException oldTransactionException) {
        log.error("failed to add metric {}", oldTransactionException);
        return ResponseEntity.noContent().build();
    }
}
