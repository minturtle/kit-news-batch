package com.likelion.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ApiServiceRequest{
    private RequestType requestType;
    private Map<String, String> headers;
    private String url;
    private Object body;

    public static enum RequestType{
        GET, POST, PUT, PATCH, DELETE
    }
}
