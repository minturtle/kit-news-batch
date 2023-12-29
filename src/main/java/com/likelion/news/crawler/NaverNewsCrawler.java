package com.likelion.news.crawler;

import com.likelion.news.dto.CrawledNewsDto;
import com.likelion.news.enums.ArticleCategory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class NaverNewsCrawler {

    public List<CrawledNewsDto.CrawledInfo> startCrawling(ArticleCategory category, LocalDate date, int resultSize) {
        List<CrawledNewsDto.CrawledInfo> result = new ArrayList<>(resultSize);

        try {

            int page = 1;

            loop : while(true){
                List<CrawledNewsDto.CrawledInfo> articleList = crawl(category, page, date);

                for(CrawledNewsDto.CrawledInfo article : articleList){
                    result.add(article);
                    // 파라미터로 넘어오는 갯수가 초과할 때까지 크롤링 및 삽입
                    if(resultSize <= result.size()){
                        break loop;
                    }
                }
                page++;
            }

        }catch (InterruptedException e){
            e.printStackTrace();
        }

        return result;
    }

    private List<CrawledNewsDto.CrawledInfo> crawl(ArticleCategory category, int page, LocalDate date) throws InterruptedException {

        List<CrawledNewsDto.CrawledInfo> result = new ArrayList<>();

        String url = createSearchUrl(category, page, date);
        List<String> articleUrls = getArticleUrls(url);

        for(String articleUrl : articleUrls){
            Thread.sleep(2000);
            Optional<CrawledNewsDto.CrawledInfo> article = crawlArticleDetail(articleUrl, category);

            if(article.isEmpty()){
                continue;
            }

            result.add(article.get());
        }

        return result;
    }

    private String createSearchUrl(ArticleCategory category, int page, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = date.format(formatter);
        String url = String.format("http://news.naver.com/main/list.nhn?mode=LSD&mid=sec&sid1=%s&page=%d&date=%s", category.getSid(), page, formattedDate);
        return url;
    }

    private Optional<CrawledNewsDto.CrawledInfo> crawlArticleDetail(String articleUrl, ArticleCategory category) {
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

    private List<String> getArticleUrls(String url) {
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


}


