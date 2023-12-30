package com.likelion.news.service;

import com.likelion.news.crawler.NaverNewsCrawler;
import com.likelion.news.dto.CrawledNewsDto;
import com.likelion.news.entity.CrawledNews;
import com.likelion.news.enums.ArticleCategory;
import com.likelion.news.repository.CrawledNewsRepository;
import com.likelion.news.repository.RefinedNewsRepository;
import com.likelion.news.utils.NanoIdProvider;
import org.junit.jupiter.api.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


@DataJpaTest
@ActiveProfiles("test")
class NewsServiceTest {

    @Autowired
    private NewsService newsService;

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
        List<String> actuals = newsService
                .getArticleUrls(ArticleCategory.IT_SCIENCE, 1, LocalDate.of(2023, 12, 30));

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
                newsService.getArticleDetail(givenUrl, ArticleCategory.IT_SCIENCE)
                        .orElseThrow(RuntimeException::new);

        // then
        assertThat(article.getArticleTitle()).isEqualTo("test-title");
        assertThat(article.getArticleLink()).isEqualTo("http://localhost/mnews/article/029/0002846461?sid=105");
        assertThat(article.getArticleCategory()).isEqualTo(ArticleCategory.IT_SCIENCE);
        assertThat(article.getMedia()).isEqualTo("testAuthor");
        assertThat(article.getArticleContent()).isEqualTo("test-content");
        assertThat(article.getArticleDateTime()).isEqualTo(LocalDateTime.of(2023,12,30,22,30,10));

    }

    @Test
    @DisplayName("뉴스 상세 크롤링시, 서버가 200아닌 코드를 리턴한다면 Option.empty를 리턴한다.")
    void testCrawlDetailReturnsEmpty(){
        // given
        String givenUrl = "http://localhost/mnews/article/029/0002846461?sid=105";

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
                                .withStatusCode(404)
                );
        // when
        Optional<CrawledNewsDto.CrawledInfo> articleOptional = newsService.getArticleDetail(givenUrl, ArticleCategory.IT_SCIENCE);

        // then
        assertThat(articleOptional.isEmpty()).isTrue();
    }


    @RepeatedTest(10)
    @DisplayName("본문 크기가 10~20인 뉴스를 랜덤하게 가져올 수 있다.")
    public void t1() throws Exception{
        //given
        CrawledNews test1 = CrawledNews.builder()
                .articleTitle("테스트 뉴스 1")
                .media("금오일보")
                .articleCategory(ArticleCategory.POLITICS.name())
                .articleContent("테스트 입니다. 10자 넘습니다.")
                .articleDatetime(LocalDateTime.of(2023, 8, 8, 12, 0, 0))
                .build();
        CrawledNews test2 = CrawledNews.builder()
                .articleTitle("테스트 뉴스 2")
                .media("금오일보")
                .articleCategory(ArticleCategory.POLITICS.name())
                .articleContent("테스트 입니다. 10자 넘습니다.")
                .articleDatetime(LocalDateTime.of(2023, 8, 8, 12, 0, 0))
                .build();
        CrawledNews test3 = CrawledNews.builder()
                .articleTitle("테스트 뉴스 3")
                .media("금오일보")
                .articleCategory(ArticleCategory.POLITICS.name())
                .articleContent("테스트 입니다. 10자 넘습니다.")
                .articleDatetime(LocalDateTime.of(2023, 8, 8, 12, 0, 0))
                .build();
        CrawledNews test4 = CrawledNews.builder()
                .articleTitle("테스트 뉴스 1")
                .media("금오일보")
                .articleCategory(ArticleCategory.POLITICS.name())
                .articleContent("10자 안넘습니다")
                .articleDatetime(LocalDateTime.of(2023, 8, 8, 12, 0, 0))
                .build();
        CrawledNews test5 = CrawledNews.builder()
                .articleTitle("테스트 뉴스 1")
                .media("금오일보")
                .articleCategory(ArticleCategory.POLITICS.name())
                .articleContent("안녕하세요, 테스트용 뉴스입니다. 20자가 넘습니다.......................")
                .articleDatetime(LocalDateTime.of(2023, 8, 8, 12, 0, 0))
                .build();

        crawledNewsRepository.saveAll(List.of(test1, test2, test3, test4, test5));


        int testSize = 2;
        //when
        List<CrawledNews> randomNewsList = newsService.getRandomNews(testSize, ArticleCategory.POLITICS, LocalDate.of(2023, 8, 8));
        //then
        assertThat(randomNewsList).isNotIn(test4, test5);
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


        @Bean
        public NanoIdProvider nanoIdProvider(Environment environment){
            return new NanoIdProvider(environment);
        }


        @Bean
        public NewsService newsService(NanoIdProvider nanoIdProvider, NaverNewsCrawler naverNewsCrawler, CrawledNewsRepository crawledNewsRepository, RefinedNewsRepository refinedNewsRepository){
            return new NewsService(naverNewsCrawler, crawledNewsRepository, refinedNewsRepository, nanoIdProvider);
        }
    }
}