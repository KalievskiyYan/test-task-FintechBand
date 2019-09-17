package com.test.rest;

import com.test.rest.dto.PaymentRequestStatusDTO;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentStatusGeneratorIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int serverPort;

    @Test
    public void shouldReturnRandomStatus() {
        ResponseEntity<PaymentRequestStatusDTO> entity = restTemplate.exchange("http://localhost:" + serverPort + "/payment-status-generator",
                HttpMethod.GET,
                null,
                PaymentRequestStatusDTO.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getBody(), Matchers.isIn(PaymentRequestStatusDTO.values()));
    }
}