package com.likelion.news.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum ArticleCategory {





        POLITICS("100"), ECONOMY("101"),
        SOCIETY("102"), LIVING_CULTURE("103"), WORLD("104"),
        IT_SCIENCE("105"), OPINION("110");

        private final String sid;




}
