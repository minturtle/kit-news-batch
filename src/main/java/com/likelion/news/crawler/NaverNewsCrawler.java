package com.likelion.news.crawler;

import com.likelion.news.dto.CrawledNewsDto;
import com.likelion.news.enums.ArticleCategory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class NaverNewsCrawler {



    @Value("${news.host.naver}")
    private String newsSiteHost;

    /**
     * @description 해당 Category에 맞는 기사의 link를 가져오는 메서드
     * @author minseok kim
     * @param category 검색하고자 하는 기사의 카테고리
     * @param page 리스트 페이지 네이션
     * @param date 검색하고자 하는 기사의 발행일
     * @throws
    */
    public List<String> crawlArticleUrls(ArticleCategory category, int page, LocalDate date){

        String url = createSearchUrl(category, page, date);

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
            throw new RuntimeException(e);
        }
    }

    public Optional<CrawledNewsDto.CrawledInfo> crawlArticleDetail(String articleUrl, ArticleCategory category) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            Element articleHtml = Jsoup.connect(articleUrl).get();

            String articleTitle = articleHtml.select("h2.media_end_head_headline").text();
            String articleContent = articleHtml.select("article#dic_area").text();
            String author = articleHtml.select("meta[property=og:article:author]").attr("content");
            LocalDateTime dateTime = LocalDateTime.parse(articleHtml.select("span.media_end_head_info_datestamp_time._ARTICLE_DATE_TIME").attr("data-date-time"), formatter);

            CrawledNewsDto.CrawledInfo article = CrawledNewsDto.CrawledInfo.builder()
                    .articleCategory(category)
                    .articleTitle(articleTitle)
                    .articleContent(articleContent)
                    .media(author)
                    .articleDateTime(dateTime)
                    .articleLink(articleUrl)
                    .build();

            return Optional.of(article);



        }catch (Exception e){
            return Optional.empty();
        }
    }


    private String createSearchUrl(ArticleCategory category, int page, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = date.format(formatter);
        String url = String.format("%s/main/list.nhn?mode=LSD&mid=sec&sid1=%s&page=%d&date=%s",newsSiteHost, category.getSid(), page, formattedDate);
        return url;
    }





}


