package com.likelion.news.crawler;

import com.likelion.news.dto.CrawledNewsDto;
import com.likelion.news.enums.ArticleCategory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
class NewsCrawlerTest {

    @Autowired
    private NaverNewsCrawler naverNewsCrawler;

    private static ClientAndServer mockServer;

    private static final int PORT = 80;
    @BeforeAll
    static void beforeAll() {
        mockServer = ClientAndServer.startClientAndServer(PORT);
    }


    @BeforeEach
    void setUp() {
        mockServer.reset();
    }

    @AfterAll
    static void afterAll() {
        mockServer.stop();
    }

    @Test
    @DisplayName("카테고리에 해당하는 뉴스의 URL을 크롤링해 올 수 있다.")
    void testGetArticleUrls(){
        // given
        String response = getMockArticleUrl();
        new MockServerClient("localhost", PORT)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/main/list.nhn")
                                .withQueryStringParameters(List.of(
                                        Parameter.param("mode", "LSD"),
                                        Parameter.param("mid", "sec"),
                                        Parameter.param("sid1", "105"),
                                        Parameter.param("page", "1"),
                                        Parameter.param("date", "20231230")
                                        ))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(response)
                );



        // when
        List<String> actuals = naverNewsCrawler
                .crawlArticleUrls(ArticleCategory.IT_SCIENCE, 1, LocalDate.of(2023, 12, 30));

        // then
        assertThat(actuals).isNotEmpty();

    }



    @TestConfiguration
    public static class TestConfig{

        @Bean
        public NaverNewsCrawler naverNewsSearchAdapter(){
            return new NaverNewsCrawler();
        }

    }



    private String getMockArticleUrl() {
        String filePath = "src/main/resources/test/article_list_example.html"; // 파일 경로
        try {
            String content = Files.readString(Paths.get(filePath));
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}