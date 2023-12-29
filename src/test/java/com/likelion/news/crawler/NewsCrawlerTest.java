package com.likelion.news.crawler;

import com.likelion.news.dto.CrawledNewsDto;
import com.likelion.news.enums.ArticleCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
class NewsCrawlerTest {

    @Autowired
    private NaverNewsCrawler naverNewsCrawler;


    @Test
    @DisplayName("한페이지 이하의 갯수의 뉴스를 크롤링할 수 있다.")
    public void testCrawlUnder1Page() throws Exception{
        //given
        int givenSize = 8;
        ArticleCategory givenCategory = ArticleCategory.IT_SCIENCE;
        LocalDate givenDate = LocalDate.now();

        //when
        List<CrawledNewsDto.CrawledInfo> actual = naverNewsCrawler.startCrawling(givenCategory, givenDate, givenSize);
        //then
        assertThat(actual).hasSize(givenSize);

    }
    @Test
    @DisplayName("한 페이지 이상의 갯수인 뉴스를 크롤링할 수 있다.")
    public void testCrawlingAbove1Page() throws Exception{
        //given
        int givenSize = 30;
        ArticleCategory givenCategory = ArticleCategory.IT_SCIENCE;
        LocalDate givenDate = LocalDate.now();

        //when
        List<CrawledNewsDto.CrawledInfo> actual = naverNewsCrawler.startCrawling(givenCategory, givenDate, givenSize);
        //then
        assertThat(actual).hasSize(givenSize);
    }


    @TestConfiguration
    public static class TestConfig{

        @Bean
        public NaverNewsCrawler naverNewsSearchAdapter(){
            return new NaverNewsCrawler();
        }

    }
}