package com.abiodunyekeen.hotelbooking.request;

import lombok.Data;

import java.util.Map;

@Data
public class SessionRequest {

    private String userId;
    private String sessionUrl;
    private String message;
    private Map<String,String> data;
}
