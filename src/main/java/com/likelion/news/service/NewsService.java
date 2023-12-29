package com.likelion.news.service;

import com.likelion.news.crawler.NaverNewsCrawler;
import com.likelion.news.dto.CrawledNewsDto;
import com.likelion.news.entity.CrawledNews;
import com.likelion.news.enums.ArticleCategory;
import com.likelion.news.repository.CrawledNewsRepository;
import com.likelion.news.utils.NanoIdProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {



    private final NaverNewsCrawler naverNewsCrawler;


    private final CrawledNewsRepository crawledNewsRepository;

    private final NanoIdProvider nanoIdProvider;
    /**
     * @author minseok kim
     * @description 뉴스를 크롤링하는 메서드, 크롤링된 뉴스는 DB에 저장된다.
     * @param categories 뉴스 카테고리
     * @param size 각 카테고리 별 크롤링할 뉴스의 개수
     * @param  date 뉴스의 작성일
     * @exception
    */

    @Transactional
    public void crawl(List<ArticleCategory> categories, int size, LocalDate date){

        for(ArticleCategory category : categories){
            List<CrawledNewsDto.CrawledInfo> articles = naverNewsCrawler.startCrawling(category, date, size);

            saveArticleInDB(articles);

        }



    }





    private void saveArticleInDB(List<CrawledNewsDto.CrawledInfo> articles) {
        List<CrawledNews> entities = articles.stream().map(c -> c.toEntity(nanoIdProvider.createNanoId())).toList();

        crawledNewsRepository.saveAll(entities);
    }

}
