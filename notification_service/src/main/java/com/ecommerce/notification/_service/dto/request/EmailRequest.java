package com.ecommerce.notification._service.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class EmailRequest {

    private Sender sender;
    private List<Recipient> to;
    private String subject;
    private String htmlContent;


    @Data
    @Builder
    static
    public class Sender {
        private String name;
        private String email;
    }

    @Data
    @Builder
    static
    public class Recipient {
        private String name;
        private String email;
    }
}
