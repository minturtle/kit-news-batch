package com.likelion.news.adapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


@Component
public class NaverNewsSearchAdapter {

        private List<String> selectedCategories;
        private LocalDateTime startDate;
        private LocalDateTime endDate;


        public void setCategory(String... categories) {
            // Validation and setting categories
        }

        public void setDateRange(String startDateStr, String endDateStr) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.startDate = LocalDateTime.parse(startDateStr, formatter);
            this.endDate = LocalDateTime.parse(endDateStr, formatter);
        }

    public void startCrawling() {
        crawl();
    }

    private void crawl() {
        String url = "http://news.naver.com/main/list.nhn?mode=LSD&mid=sec&sid1=100&page=1";
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        private String getUrlData(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            // Implement the HTTP request and response handling
            return ""; // Return the response content
        }

        private void insertArticleData(Connection conn, String... articleData) {
            // Implement database insert logic
        }

    }


