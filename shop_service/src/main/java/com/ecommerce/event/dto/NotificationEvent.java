package com.ecommerce.event.dto;


import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Objects;

@Data
@Builder
public class NotificationEvent {
    private String channel;
    private String recipient;
    private String templateCode;
    private Map<String, Object> data;

}
