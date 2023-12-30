package com.likelion.news.integration;

import com.likelion.news.dto.CrawledNewsDto;
import com.likelion.news.entity.CrawledNews;
import com.likelion.news.enums.ArticleCategory;
import com.likelion.news.repository.CrawledNewsRepository;
import com.likelion.news.service.NewsService;
import org.junit.jupiter.api.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


@ActiveProfiles("test")
@SpringBootTest
public class NewsIntegrationTest {

    @Autowired
    private NewsService newsService;

    private static final Logger log = Logger.getLogger(NewsIntegrationTest.class.getName());


    @Autowired
    private CrawledNewsRepository crawledNewsRepository;
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
    @DisplayName("카테고리별로 뉴스들을 조회해서 크롤링 한 후 이를 데이터 베이스에 저장할 수 있다.")
    void testCrawling() throws InterruptedException {
        // given
        String listResponse = getMockArticleUrl();
        String detailResponse = getMockArticleDetail();

        new MockServerClient("localhost", PORT)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/main/list.nhn")
                                .withSchemaQueryStringParameter("sid1","{ \"type\": \"string\", \"pattern\": \"1**\" }" )
                                .withQueryStringParameters(List.of(
                                        Parameter.param("mode", "LSD"),
                                        Parameter.param("mid", "sec"),
                                        Parameter.param("page", "1"),
                                        Parameter.param("date", "20231230")
                                ))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(listResponse)
                );

        new MockServerClient("localhost", PORT)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/mnews/article/029/0002846461")
                                .withQueryStringParameters(List.of(
                                        Parameter.param("sid", "105")
                                ))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(detailResponse)
                );



        // when
        long startTime = System.nanoTime();
        log.info("Crawling Integration Test Start");
        for(ArticleCategory category : Arrays.stream(ArticleCategory.values()).toList()){
            List<String> articleUrls = newsService.getArticleUrls(category, 2, LocalDate.of(2023,12,30));


            for(String articleUrl : articleUrls){
                Optional<CrawledNewsDto.CrawledInfo> articleDetail =
                        newsService.getArticleDetail(articleUrl, category);

                if(articleDetail.isEmpty()){
                    continue;
                }
                newsService.save(articleDetail.get());

            }
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        log.info("Crawling Integration Test End");
        log.info("run time : "  + duration/1000000000);

        // then
        List<CrawledNews> list = StreamSupport.stream(
                crawledNewsRepository.findAll().spliterator(), false).toList();

        assertThat(list).hasSize(2 * ArticleCategory.values().length);
    }


    private String getMockArticleDetail() {
        String filePath = "src/main/resources/test/article_detail_example.html"; // 파일 경로
        try {
            String content = Files.readString(Paths.get(filePath));
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
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
