package com.likelion.news.service;

import com.likelion.news.crawler.NaverNewsCrawler;
import com.likelion.news.entity.CrawledNews;
import com.likelion.news.enums.ArticleCategory;
import com.likelion.news.repository.CrawledNewsRepository;
import com.likelion.news.repository.RefinedNewsRepository;
import com.likelion.news.utils.NanoIdProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
class NewsServiceTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private CrawledNewsRepository crawledNewsRepository;


    @RepeatedTest(100)
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