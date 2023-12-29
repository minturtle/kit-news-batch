package com.likelion.news.service;


import com.likelion.news.dto.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ClovaApiCallService {

    private final ApiCallService apiCallService;
    private final Environment env;

    private final String CLIENT_ID_HEADER = "X-NCP-APIGW-API-KEY-ID";
    private final String CLIENT_SECRET_HEADER = "X-NCP-APIGW-API-KEY";


    protected ClovaApiCallService(ApiCallService apiCallService, Environment environment) {
        this.apiCallService = apiCallService;
        this.env = environment;
    }


    /**
     * @methodName getSummary
     * @author : Minseok Kim
     * @description req에 담긴 원문의 summary를 받아오는 메서드
     *
     * @param req ClovaSummary HTTP 요청에 필요한 데이터 필드
     * @return res summary 값
     * @exception
     */
    public String getSummary(ApiDto.ClovaSummaryRequest req){
        String clovaUrl = env.getProperty("clova.url");

        HashMap<String, String> reqHeaders = new HashMap<>();

        reqHeaders.put(CLIENT_ID_HEADER, env.getProperty("clova.client.id"));
        reqHeaders.put(CLIENT_SECRET_HEADER, env.getProperty("clova.client.secret"));
        reqHeaders.put("Content-Type", "application/json");

        ApiDto.ApiServiceRequest apiServiceRequest = ApiDto.ApiServiceRequest.builder()
                .url(clovaUrl)
                .headers(reqHeaders)
                .requestType(ApiDto.ApiServiceRequest.RequestType.POST)
                .body(req)
                .build();


        ApiDto.ApiServiceResponse<ApiDto.ClovaSummaryResponse> resp = apiCallService.callApi(apiServiceRequest, ApiDto.ClovaSummaryResponse.class);

        return resp.getBody().getSummary();
    }

    public ApiDto.ClovaSummaryRequest createDefaultNewsRequest(String title, String content) {
        ApiDto.ClovaSummaryRequest.ClovaRequestOption option = ApiDto.ClovaSummaryRequest.ClovaRequestOption.builder()
                .language(ApiDto.ClovaSummaryRequest.ClovaRequestOptionLanguage.KOREAN.getValue())
                .summaryCount(2)
                .tone(ApiDto.ClovaSummaryRequest.ClovaRequestOptionTone.원문_어투_유지.getValue())
                .model(ApiDto.ClovaSummaryRequest.ClovaRequestOptionModel.NEWS.getValue())
                .build();

        ApiDto.ClovaSummaryRequest.ClovaRequestDocument document = ApiDto.ClovaSummaryRequest.ClovaRequestDocument.builder()
                .title(title)
                .content(content)
                .build();

        ApiDto.ClovaSummaryRequest summaryReq = ApiDto.ClovaSummaryRequest.builder()
                .document(document)
                .option(option)
                .build();
        return summaryReq;
    }

}
