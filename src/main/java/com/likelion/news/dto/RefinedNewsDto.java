package com.likelion.news.dto;

import com.likelion.news.entity.CrawledNews;
import com.likelion.news.entity.RefinedNews;
import lombok.Builder;
import lombok.Data;

public class RefinedNewsDto {

    @Data
    @Builder
    public static class Content {

        private CrawledNews crawledNews;
        private String summary;


        public RefinedNews toEntity(){
            return RefinedNews.builder()
                    .crawledNews(crawledNews)
                    .articleSummary(summary)
                    .build();
        }

    }

}
