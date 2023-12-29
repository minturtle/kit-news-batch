package com.likelion.news.service;


import com.likelion.news.dto.ApiServiceRequest;
import com.likelion.news.dto.ApiServiceResponse;
import com.likelion.news.dto.ClovaSummaryRequest;
import com.likelion.news.dto.ClovaSummaryResponse;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
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
    public String getSummary(ClovaSummaryRequest req){
        String clovaUrl = env.getProperty("clova.url");

        HashMap<String, String> reqHeaders = new HashMap<>();

        reqHeaders.put(CLIENT_ID_HEADER, env.getProperty("clova.client.id"));
        reqHeaders.put(CLIENT_SECRET_HEADER, env.getProperty("clova.client.secret"));
        reqHeaders.put("Content-Type", "application/json");

        ApiServiceRequest apiServiceRequest = ApiServiceRequest.builder()
                .url(clovaUrl)
                .headers(reqHeaders)
                .requestType(ApiServiceRequest.RequestType.POST)
                .body(req)
                .build();


        ApiServiceResponse<ClovaSummaryResponse> resp = apiCallService.callApi(apiServiceRequest, ClovaSummaryResponse.class);

        return resp.getBody().getSummary();
    }

    public ClovaSummaryRequest createDefaultNewsRequest(String title, String content) {
        ClovaSummaryRequest.ClovaRequestOption option = ClovaSummaryRequest.ClovaRequestOption.builder()
                .language(ClovaSummaryRequest.ClovaRequestOptionLanguage.KOREAN.getValue())
                .summaryCount(2)
                .tone(ClovaSummaryRequest.ClovaRequestOptionTone.원문_어투_유지.getValue())
                .model(ClovaSummaryRequest.ClovaRequestOptionModel.NEWS.getValue())
                .build();

        ClovaSummaryRequest.ClovaRequestDocument document = ClovaSummaryRequest.ClovaRequestDocument.builder()
                .title(title)
                .content(content)
                .build();

        ClovaSummaryRequest summaryReq = ClovaSummaryRequest.builder()
                .document(document)
                .option(option)
                .build();
        return summaryReq;
    }

}
