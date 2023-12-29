package com.likelion.news.service;

import com.likelion.news.crawler.NaverNewsCrawler;
import com.likelion.news.dto.CrawledNewsDto;
import com.likelion.news.enums.ArticleCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NewsService {


    @Autowired
    private NaverNewsCrawler naverNewsCrawler;

    /**
     * @author minseok kim
     * @description 뉴스를 크롤링하는 메서드, 크롤링된 뉴스는 DB에 저장된다.
     * @param categories 뉴스 카테고리
     * @param size 각 카테고리 별 크롤링할 뉴스의 개수
     * @param  date 뉴스의 작성일
     * @exception
    */
    public void crawl(List<ArticleCategory> categories, int size, LocalDate date){

        for(ArticleCategory category : categories){
            List<CrawledNewsDto.CrawledInfo> articles = naverNewsCrawler.startCrawling(category, date, size);
        }


    }

}
