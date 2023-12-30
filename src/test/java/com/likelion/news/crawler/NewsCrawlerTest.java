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
import java.time.LocalDateTime;
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

    @Test
    @DisplayName("Article의 URL을 통해 크롤링 후 뉴스의 상세 정보를 조회할 수 있다.")
    void testCrawlDetail(){
        // given
        String givenUrl = "http://localhost/mnews/article/029/0002846461?sid=105";

        String response = getMockArticleDetail();
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
                                .withBody(response)
                );

        // when
        CrawledNewsDto.CrawledInfo article =
                naverNewsCrawler.crawlArticleDetail(givenUrl, ArticleCategory.IT_SCIENCE)
                .orElseThrow(RuntimeException::new);

        // then
        assertThat(article.getArticleTitle()).isEqualTo("中에 털릴 대로 털린 日…사이버보안 인력 2배로 늘린다");
        assertThat(article.getArticleLink()).isEqualTo("http://localhost/mnews/article/029/0002846461?sid=105");
        assertThat(article.getArticleCategory()).isEqualTo(ArticleCategory.IT_SCIENCE);
        assertThat(article.getMedia()).isEqualTo("디지털타임스 | 네이버");
        assertThat(article.getArticleContent()).isEqualTo("국립사이버보안센터 인력 90명에서 175명으로 증원키로 중국 해커들, 센터 해킹해 9개월간 정보 빼갔는데 뒤늦게 알아 전세계가 중국과 북한 해커들의 공격으로 인해 사실상 1년 365일 사이버 전쟁을 방불케 하는 대결 상태에 놓인 가운데 중국 해커들에 털릴 대로 털린 일본 정부가 사이버보안 전문기관의 인력을 2배로 늘리기로 결정했다. 30일 교도통신에 따르면 일본 정부는 정부 기관을 노린 사이버 공격을 막고 부정한 접근을 차단하기 위해 국립사이버보안센터(NISC) 인력을 내년에 2배로 늘리고 조직을 확대하기로 했다. 현재 약 90명인 NISC 상근 인력을 175명으로 85명 늘리고, 이와 별도로 전문지식을 갖춘 민간 비상근 직원을 확충한다는 계획이다. 이와 함께 차관급 인사 1명과 국장급 인사 2명을 추가로 배치해 지휘 체계도 강화한다. 일본 정부는 지난해 12월 국가안전보장전략을 개정해 사이버 보안 분야 대응 능력을 미국이나 유럽 주요국과 동등한 수준으로 향상하기로 했다. 하지만 올해 여름에 나고야항이 사이버 공격을 받아 컨테이너 하역 작업이 중단됐고, 우주항공연구개발기구(JAXA)도 조직 내 네트워크를 일원화해 관리하는 서버에 부정한 접근이 있었다는 사실이 확인되는 등 잇따라 허점이 노출됐다. 여기에다 중국 해커로 의심되는 이들이 NISC를 작년 10월 해킹해 9개월 동안 각종 민감 데이터에 접근해온 정황이 밝혀져 파문이 일었다. 영국 파이낸셜타임스는 지난 8월, 정부와 민간 소식통을 인용해 중국과 연계된 해커들이 2022년 가을부터 올해 6월까지 일본의 NISC에 침입해 민감한 데이터에 접근했을 가능성이 있다고 보도했다. 파이낸셜타임스는 올초 일어난 나고야항 사이버 공격도 중국이 일본의 주요 인프라 방어체계에 대한 테스트의 일환으로 시도했을 가능성이 있다고 짚었다. 일본 정부가 NISC를 확대 개편한 것은 이처럼 사이버 공격 우려가 커지는 상황에서 감시 체제를 정비하고 방어 능력을 강화하는 조치의 일환이다. 교도통신에 따르면 일본 정부는 향후 사이버 보안 정책을 총괄하는 새로운 조직도 설립할 계획이다.");
        assertThat(article.getArticleDateTime()).isEqualTo(LocalDateTime.of(2023,12,30,22,30,10));

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


    @TestConfiguration
    public static class TestConfig{

        @Bean
        public NaverNewsCrawler naverNewsSearchAdapter(){
            return new NaverNewsCrawler();
        }

    }




}