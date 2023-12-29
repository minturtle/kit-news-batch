package com.likelion.news;

import com.likelion.news.dto.ApiDto;
import com.likelion.news.dto.RefinedNewsDto;
import com.likelion.news.entity.CrawledNews;
import com.likelion.news.enums.ArticleCategory;
import com.likelion.news.service.ClovaApiCallService;
import com.likelion.news.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class NewsBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsBatchApplication.class, args);
	}


}
