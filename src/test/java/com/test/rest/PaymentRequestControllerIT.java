package com.test.rest;

import com.test.domain.PaymentRequestStatus;
import com.test.rest.dto.ErrorDTO;
import com.test.rest.dto.PaymentRequestDTO;
import com.test.rest.dto.PaymentRequestStatusDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentRequestControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @LocalServerPort
    private int serverPort;

    @Test
    @Sql("/clear.sql")
    public void shouldCreatePaymentRequest() {
        PaymentRequestDTO paymentRequestDTO = PaymentRequestDTO.builder()
                .clientId(1L)
                .routeId(121L)
                .departureDateTime(ZonedDateTime.now().toString())
                .build();
        ResponseEntity<Long> entity = restTemplate.exchange("http://localhost:" + serverPort + "/payment-request",
                HttpMethod.POST,
                new HttpEntity<>(paymentRequestDTO),
                Long.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(entity.getBody(), is(1L));
    }

    @Test
    @Sql("/clear.sql")
    public void shouldReturn409ForDuplicatePaymentRequest() {
        PaymentRequestDTO paymentRequestDTO = PaymentRequestDTO.builder()
                .clientId(1L)
                .routeId(121L)
                .departureDateTime(ZonedDateTime.now().toString())
                .build();
        ResponseEntity<Long> entity = restTemplate.exchange("http://localhost:" + serverPort + "/payment-request",
                HttpMethod.POST,
                new HttpEntity<>(paymentRequestDTO),
                Long.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(entity.getBody(), is(1L));
        ResponseEntity<ErrorDTO> duplicateEntity = restTemplate.exchange("http://localhost:" + serverPort + "/payment-request",
                HttpMethod.POST,
                new HttpEntity<>(paymentRequestDTO),
                ErrorDTO.class);
        assertThat(duplicateEntity.getStatusCode(), is(HttpStatus.CONFLICT));
        assertThat(duplicateEntity.getBody(), is(new ErrorDTO("Request Already Exist")));

    }

    @Test
    @Sql("/clear.sql")
    public void shouldReturnBadRequestForInvalidPaymentRequestCreation() {
        PaymentRequestDTO paymentRequestDTO = PaymentRequestDTO.builder()
                .routeId(228L)
                .departureDateTime("2020-01-03T10:11:30+02:00")
                .build();
        ResponseEntity<ErrorDTO> entity = restTemplate.exchange("http://localhost:" + serverPort + "/payment-request",
                HttpMethod.POST,
                new HttpEntity<>(paymentRequestDTO),
                ErrorDTO.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(entity.getBody(), is(new ErrorDTO("Client id can`t be null")));
    }

    @Test
    @Sql({"/clear.sql", "/importPaymentRequest.sql"})
    public void shouldReturnPaymentRequestStatus() {
        ResponseEntity<PaymentRequestStatus> entity = restTemplate.exchange("http://localhost:" + serverPort + "/payment-request/1/status",
                HttpMethod.GET,
                null,
                PaymentRequestStatus.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getBody(), is(PaymentRequestStatus.ERROR));
    }

    @Test
    @Sql("/clear.sql")
    public void shouldReturnNotFoundForInvalidPaymentRequestId() {
        ResponseEntity<ErrorDTO> entity = restTemplate.exchange("http://localhost:" + serverPort + "/payment-request/1/status",
                HttpMethod.GET,
                null,
                ErrorDTO.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(entity.getBody(), is(new ErrorDTO("Payment request with id {1} not found!")));
    }

    @Test
    @Sql({"/clear.sql", "/importPaymentRequest.sql"})
    public void shouldReturnPaymentRequestByClientId() {
        ResponseEntity<List<PaymentRequestDTO>> entity = restTemplate.exchange("http://localhost:" + serverPort + "/payment-request?clientId=2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PaymentRequestDTO>>() {

                });
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getBody(), is(Collections.singletonList(PaymentRequestDTO.builder()
                .id(1L)
                .clientId(2L)
                .routeId(1L)
                .departureDateTime("2020-01-03T10:11:30+02:00")
                .requestStatus(PaymentRequestStatusDTO.ERROR)
                .build())));
    }

}