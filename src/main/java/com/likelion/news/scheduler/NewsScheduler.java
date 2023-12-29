package com.likelion.news.scheduler;

import com.likelion.news.dto.ApiDto;
import com.likelion.news.dto.RefinedNewsDto;
import com.likelion.news.entity.CrawledNews;
import com.likelion.news.enums.ArticleCategory;
import com.likelion.news.service.ClovaApiCallService;
import com.likelion.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewsScheduler {


    private final ClovaApiCallService clovaService;
    private final NewsService newsService;

    @Value("${clova.summary.size}")
    private Integer summarizationSize;


    @Scheduled(cron = "0 00 18 * * *")
    public void runCrawling(){
        try{
            log.info("Crawler Scheduler Started");

            newsService.crawl(Arrays.stream(ArticleCategory.values()).toList(), 10, LocalDate.now());



            log.info("Crawler Scheduler Ended");
        }catch (Exception e){
            throw new RuntimeException("Summary API 호출중 오류가 발생했습니다.",e);
        }
    }

    @Scheduled(cron = "0 20 18 * * *")
    public void runSummary(){
        try{
            log.info("Summary Scheduler Started");


            // 모든 카테고리에서 Summarization 진행
            ArticleCategory[] articleTypes = ArticleCategory.values();

            List<CrawledNews> newsList
                    = newsService.getRandomNews(summarizationSize, List.of(articleTypes), LocalDate.now());


            List<RefinedNewsDto.Content> resultList = new ArrayList<>();

            // 구해온 모든 news에 대해 요약 진행
            for(CrawledNews news : newsList){
                ApiDto.ClovaSummaryRequest clovaSummaryRequest
                        = clovaService.createDefaultNewsRequest(news.getArticleTitle(), news.getArticleContent());

                try{
                    String summary = clovaService.getSummary(clovaSummaryRequest);

                    RefinedNewsDto.Content result = RefinedNewsDto.Content.builder()
                            .crawledNews(news)
                            .summary(summary)
                            .build();

                    resultList.add(result);

                    log.info("Summary Complete : ID : {}", news.getCrawledNewsId());
                }catch (RuntimeException e){
                    continue;
                }

            }

            // 요약 완료된 뉴스를 DB에 저장
            newsService.saveRefinedNewsList(resultList);
            log.info("Summary Scheduler Ended");


        }catch (Exception e){
            throw new RuntimeException("Summary API 호출중 오류가 발생했습니다.",e);
        }

    }

}


