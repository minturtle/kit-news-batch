package com.likelion.news.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "crawled_news")
@Data
public class CrawledNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crawled_news_id", nullable = false)
    private Long crawledNewsId;

    private LocalDateTime articleDateTime;

    private String articleCategory;

    private String media;

    private String articleTitle;

    @Lob
    private String articleContent;

    private String articleLink;

    private String uid;
}

