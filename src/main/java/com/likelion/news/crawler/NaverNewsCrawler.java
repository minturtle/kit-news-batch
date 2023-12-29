package com.likelion.news.crawler;

import com.likelion.news.enums.ArticleCategory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
public class NaverNewsCrawler {

    public void startCrawling() {
            try {
                crawl(ArticleCategory.Naver.IT_SCIENCE, 1, LocalDateTime.now());
            }catch (InterruptedException e){
                e.printStackTrace();
            }
    }

    private void crawl(ArticleCategory.Naver category, int page, LocalDateTime date) throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = date.format(formatter);

        String url = String.format("http://news.naver.com/main/list.nhn?mode=LSD&mid=sec&sid1=%s&page=%d&date=%s", category.getSid(), page, formattedDate);
        List<String> articleUrls = getArticleUrl(url);

        for(String articleUrl : articleUrls){
            Thread.sleep(1000);
            crawlArticleDetail(articleUrl);
        }

    }

    private void crawlArticleDetail(String articleUrl) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            Element articleHtml = Jsoup.connect(articleUrl).get();

            String articleTitle = articleHtml.select("h2.media_end_head_headline").text();
            String articleContent = articleHtml.select("article#dic_area").text();
            String author = articleHtml.select("meta[property=og:article:author]").attr("content");
            LocalDateTime dateTime = LocalDateTime.parse(articleHtml.select("span.media_end_head_info_datestamp_time._ARTICLE_DATE_TIME").attr("data-date-time"), formatter);

            System.out.println();



        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private List<String> getArticleUrl(String url) {
        try {

            List<String> postUrls = new ArrayList<>();
            // 각 페이지에 있는 기사들 가져오기
            Element body = Jsoup.connect(url).get().body();
            Elements tempPost = body.select(".newsflash_body .type06_headline li dl"); // Update your_selector to match your HTML structure

            tempPost.addAll(body.select(".newsflash_body .type06 li dl")) ;

            // 각 페이지에 있는 기사들의 url 저장
            for (Element element : tempPost) {
                Element link = element.selectFirst("a[href]");

                String href = link.attr("href");
                postUrls.add(href);

            }

            return postUrls;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


}


