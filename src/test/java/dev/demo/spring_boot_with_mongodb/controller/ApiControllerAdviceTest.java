package dev.demo.spring_boot_with_mongodb.controller;

import dev.demo.spring_boot_with_mongodb.exception.ResourceNotFoundException;
import dev.demo.spring_boot_with_mongodb.payload.ErrorResponse;
import dev.demo.spring_boot_with_mongodb.payload.FieldValidationError;
import dev.demo.spring_boot_with_mongodb.payload.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ApiControllerAdviceTest {
    private ApiControllerAdvice advice;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        advice = new ApiControllerAdvice();
        request = mock(HttpServletRequest.class);
        given(request.getRequestURI()).willReturn("/api/v1/students/123");
    }

    @Test
    void handleNotFound_returns404AndErrorResponse() {
        // given
        ResourceNotFoundException ex = new ResourceNotFoundException("Student", "id", "123");
        // when
        ResponseEntity<ErrorResponse> resp = advice.handleNotFound(ex, request);
        // then
        assertThat(resp.getStatusCode().value()).isEqualTo(404);
        ErrorResponse body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(404);
        assertThat(body.error()).isEqualTo("Not Found");
        assertThat(body.message()).contains("Student not found");
        assertThat(body.path()).isEqualTo("/api/v1/students/123");
        assertThat(body.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void handleMalformedJson_returns400WithGenericMessage() {
        // given
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed");
        // when
        ResponseEntity<ErrorResponse> resp = advice.handleMalformedJson(ex, request);
        // then
        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        ErrorResponse body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.error()).isEqualTo("Bad Request");
        assertThat(body.message()).isEqualTo("Malformed JSON request");
    }

    @Test
    void handleValidation_returns400WithFieldErrors() {
        // given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        FieldError fe1 = new FieldError("student", "email", "must be valid");
        FieldError fe2 = new FieldError("student", "firstName", "must not be blank");
        given(ex.getBindingResult()).willReturn(br);
        given(br.getFieldErrors()).willReturn(List.of(fe1, fe2));
        // when
        ResponseEntity<ValidationErrorResponse> resp = advice.handleValidation(ex, request);
        // then
        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        ValidationErrorResponse body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.error()).isEqualTo("Bad Request");
        assertThat(body.fieldErrors())
                .extracting(FieldValidationError::field, FieldValidationError::message)
                .containsExactlyInAnyOrder(
                        tuple("email", "must be valid"),
                        tuple("firstName", "must not be blank")
                );
    }

    @Test
    void handleAll_returns500OnGenericException() {
        // given
        Exception ex = new RuntimeException("oops");
        // when
        ResponseEntity<ErrorResponse> resp = advice.handleAll(ex, request);
        // then
        assertThat(resp.getStatusCode().value()).isEqualTo(500);
        ErrorResponse body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.error()).isEqualTo("Internal Server Error");
        assertThat(body.message()).isEqualTo("An unexpected error occurred");
    }
}
