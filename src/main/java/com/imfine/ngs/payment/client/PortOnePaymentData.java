package com.imfine.ngs.payment.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) // 알려지지 않은 속성은 무시
public class PortOnePaymentData {

    private String id; // 포트원 결제 ID (imp_uid)
    private String status;

    @JsonProperty("merchantId")
    private String merchantUid; // 가맹점 주문 ID

    private String orderName;
    private Amount amount;
    private Customer customer;
    private List<Cancellation> cancellations;
    private ZonedDateTime paidAt;
    private ZonedDateTime cancelledAt;

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        private long total; // 총 결제 금액
        private long taxFree; // 비과세 금액
        private long vat; // 부가세
        private long supply; // 공급가액
        private long discount; // 할인 금액
        private long paid; // 실제 결제된 금액
        private long cancelled; // 취소된 금액
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {
        private String id;
        private String name;
        private String email;
        private String phoneNumber;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cancellation {
        private String status;
        private long totalAmount;
        private String reason;
        private ZonedDateTime cancelledAt;
    }
}
