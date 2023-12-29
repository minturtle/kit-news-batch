package com.likelion.news;

import com.likelion.news.enums.ArticleCategory;
import com.likelion.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class NewsBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsBatchApplication.class, args);
	}



}
