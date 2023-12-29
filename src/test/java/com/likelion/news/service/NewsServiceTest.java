package com.likelion.news.service;

import com.likelion.news.crawler.NaverNewsCrawler;
import com.likelion.news.entity.CrawledNews;
import com.likelion.news.enums.ArticleCategory;
import com.likelion.news.repository.CrawledNewsRepository;
import com.likelion.news.utils.NanoIdProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
class NewsServiceTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private CrawledNewsRepository newsRepository;

    @Test
    @DisplayName("뉴스를 크롤링하고 이를 저장할 수 있다")
    public void testNewsCrawl() throws Exception{
        //given

        //when
        newsService.crawl(List.of(ArticleCategory.IT_SCIENCE, ArticleCategory.ECONOMY), 5, LocalDate.now());
        //then
        List<CrawledNews> actual = StreamSupport.stream(
                newsRepository.findAll().spliterator(), false).toList();

        assertThat(actual).hasSize(10);
    }



    @TestConfiguration
    public static class TestConfig{

        @Autowired
        private CrawledNewsRepository crawledNewsRepository;

        @Bean
        public NaverNewsCrawler naverNewsSearchAdapter(){
            return new NaverNewsCrawler();
        }


        @Bean
        public NanoIdProvider nanoIdProvider(Environment environment){
            return new NanoIdProvider(environment);
        }


        @Bean
        public NewsService newsService(NanoIdProvider nanoIdProvider, NaverNewsCrawler naverNewsCrawler, CrawledNewsRepository crawledNewsRepository){
            return new NewsService(naverNewsCrawler, crawledNewsRepository, nanoIdProvider);
        }
    }
}