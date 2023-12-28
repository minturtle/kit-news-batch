package com.likelion.news.adapter;

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


@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
class NewsSearchAdapterTest {

    @Autowired
    private NaverNewsSearchAdapter naverNewsSearchAdapter;

    @Test
    @DisplayName("")
    public void testCrawling() throws Exception{
        //given

        //when
        naverNewsSearchAdapter.startCrawling();
        //then

    }


    @TestConfiguration
    public static class TestConfig{

        @Bean
        public NaverNewsSearchAdapter naverNewsSearchAdapter(){
            return new NaverNewsSearchAdapter();
        }

    }
}