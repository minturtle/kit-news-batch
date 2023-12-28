package com.likelion.news.adapter;

import com.likelion.news.dto.CrawledNewsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Array.get;


@Component
public class NaverNewsSearchAdapter {

    @Value("news.naver.api.url")
    private String NAVER_API_URL;

    @Value("news.naver.api.client-id")
    private String NAVER_API_CLIENT_ID;

    @Value("news.naver.api.client-secret")
    private String NAVER_API_CLIENT_SECRET;

    public List<CrawledNewsDto.CrawledInfo> search(){
        String text = null;
        try {
            text = URLEncoder.encode("그린팩토리", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }


        String apiURL = NAVER_API_URL + "?query=" + text;    // JSON 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // XML 결과


        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);


        System.out.println(responseBody);
    }

}
