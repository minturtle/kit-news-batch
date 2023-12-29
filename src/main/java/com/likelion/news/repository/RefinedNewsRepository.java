package com.likelion.news.repository;


import com.likelion.news.entity.RefinedNews;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefinedNewsRepository extends CrudRepository<RefinedNews, Long> {
}
