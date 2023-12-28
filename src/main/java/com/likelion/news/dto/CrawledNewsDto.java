package com.likelion.news.dto;

import jakarta.persistence.Lob;

import java.time.LocalDateTime;

public class CrawledNewsDto {

    public static class CrawledInfo {
        private LocalDateTime articleDateTime;

        private String articleCategory;

        private String media;

        private String articleTitle;

        @Lob
        private String articleContent;

        private String articleLink;
    }

}
