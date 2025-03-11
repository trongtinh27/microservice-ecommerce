package com.ecommerce.event.dto;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class NotificationEvent {
    private String channel;
    private String recipient;
    private String templateCode;
    private Map<String, Object> data;
}
