package com.likelion.news.adapter;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

class NewsSearchAdapterTest {




    @TestConfiguration
    public static class TestConfig{

        @Bean
        public NaverNewsSearchAdapter naverNewsSearchAdapter(){
            return new NaverNewsSearchAdapter();
        }

    }
}