package com.likelion.news.repository;

import com.likelion.news.entity.CrawledNews;
import org.springframework.data.repository.CrudRepository;

public interface CrawledNewsRepository extends CrudRepository<CrawledNews, Long> {
}
