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

    private LocalDateTime articleDatetime;

    private String articleCategory;

    private String media;

    private String articleTitle;

    @Column(name = "article_content", columnDefinition = "TEXT")
    private String articleContent;

    private String articleLink;

    private String uid;

    /**
     * @author minseok kim
     * @description contentSize가 범위 내에 속하는지를 체크하는 메서드
     * @param minimumSize content의 최소 크기
     * @param maximumSize content의 최대 크기
     * @return contentSize가 minimumSize ~ maximumSize에 속한다면 true 리턴
     */
    public boolean contentSizeIsIn(Integer minimumSize, Integer maximumSize) {
        return (minimumSize < articleContent.length()) && (maximumSize > articleContent.length());
    }
}

