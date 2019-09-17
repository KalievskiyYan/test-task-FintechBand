package com.test.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PaymentRequest {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;

    Long routeId;

    Long clientId;

    Integer ticketId;

    ZonedDateTime departureDateTime;
    @Enumerated(EnumType.STRING)
    PaymentRequestStatus requestStatus;
}
