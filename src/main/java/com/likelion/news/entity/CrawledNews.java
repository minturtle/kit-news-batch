package com.likelion.news.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "crawled_news")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
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

