package com.likelion.news.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.news.dto.ApiDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

// TODO : CLOVA에서 400, 500 오류가 발생할 때에 대한 테스트 케이스 작성 필요
@SpringBootTest(classes = {ClovaApiCallService.class, ApiCallService.class, ObjectMapper.class})
@ExtendWith(SpringExtension.class)
class ClovaApiCallServiceTest {
    private ClientAndServer mockServer;

    @Autowired
    private ClovaApiCallService clovaSummaryApiCallService;

    @MockBean
    private Environment environment;


    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws JsonProcessingException {
        mockServer = ClientAndServer.startClientAndServer(8888);

    }


    @AfterEach
    void tearDown() {
        mockServer.stop();
    }


    @Test
    @DisplayName("테스트에 필요한 의존관계를 모두 주입받고 사용할 수 있다.")
    void t1() throws Exception {
        //given
        Object injectedBean1 = ReflectionTestUtils.getField(clovaSummaryApiCallService, "apiCallService");
        Object injectedBean2 = ReflectionTestUtils.getField(clovaSummaryApiCallService, "env");

        //then
        assertThat(clovaSummaryApiCallService).isNotNull();
        assertThat(injectedBean1).isNotNull();
        assertThat(injectedBean2).isNotNull();
    }


    @Test
    @DisplayName("Clova로 부터 요약하고자 하는 문서를 Http Request에 받아서 전송하고, 요약문을 응답 받을 수 있다.")
    void t2() throws Exception {
        //given
        mockServer.when(
                        request()
                                .withMethod("POST")
                                .withPath("/text-summary/v1/summarize")
                                .withHeaders(
                                        Header.header("X-NCP-APIGW-API-KEY-ID", "test-id"),
                                        Header.header("X-NCP-APIGW-API-KEY", "test-secret"),
                                        Header.header("Content-Type", "application/json")
                                )
                                .withBody(objectMapper.writeValueAsString(createSummaryRequest()))

                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(new Header("Content-Type", "application/json;charset=utf-8"))
                                .withBody("{ \"summary\" : \"good\"}")
                );


        when(environment.getProperty("clova.url")).thenReturn("http://localhost:8888/text-summary/v1/summarize");
        when(environment.getProperty("clova.client.id")).thenReturn("test-id");
        when(environment.getProperty("clova.client.secret")).thenReturn("test-secret");

        ApiDto.ClovaSummaryRequest summaryReq = createSummaryRequest();

        //when

        String actual = clovaSummaryApiCallService.getSummary(summaryReq);

        //then
        assertThat(actual).isEqualTo("good");


    }

    private ApiDto.ClovaSummaryRequest createSummaryRequest() {
        ApiDto.ClovaSummaryRequest.ClovaRequestOption option = ApiDto.ClovaSummaryRequest.ClovaRequestOption.builder()
                .language(ApiDto.ClovaSummaryRequest.ClovaRequestOptionLanguage.KOREAN.getValue())
                .summaryCount(2)
                .tone(ApiDto.ClovaSummaryRequest.ClovaRequestOptionTone.원문_어투_유지.getValue())
                .model(ApiDto.ClovaSummaryRequest.ClovaRequestOptionModel.NEWS.getValue())
                .build();

        ApiDto.ClovaSummaryRequest.ClovaRequestDocument document = ApiDto.ClovaSummaryRequest.ClovaRequestDocument.builder()
                .title("hello")
                .content("test")
                .build();

        ApiDto.ClovaSummaryRequest summaryReq = ApiDto.ClovaSummaryRequest.builder()
                .document(document)
                .option(option)
                .build();
        return summaryReq;
    }
}
