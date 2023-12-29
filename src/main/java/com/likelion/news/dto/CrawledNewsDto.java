package com.likelion.news.dto;

import com.likelion.news.entity.CrawledNews;
import com.likelion.news.enums.ArticleCategory;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class CrawledNewsDto {


    @AllArgsConstructor
    @Builder
    @Data
    public static class CrawledInfo {
        private LocalDateTime articleDateTime;

        private ArticleCategory articleCategory;

        private String media;

        private String articleTitle;

        @Lob
        private String articleContent;

        private String articleLink;



        public CrawledNews toEntity(String uid){
            return CrawledNews.builder()
                    .articleTitle(articleTitle)
                    .articleContent(articleContent)
                    .media(media)
                    .articleLink(articleLink)
                    .articleDatetime(articleDateTime)
                    .articleCategory(articleCategory.name())
                    .uid(uid)
                    .build();
        }
    }

}
