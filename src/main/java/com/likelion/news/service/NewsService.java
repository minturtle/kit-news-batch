package com.likelion.news.service;

import com.likelion.news.crawler.NaverNewsCrawler;
import com.likelion.news.dto.CrawledNewsDto;
import com.likelion.news.dto.RefinedNewsDto;
import com.likelion.news.entity.CrawledNews;
import com.likelion.news.entity.RefinedNews;
import com.likelion.news.enums.ArticleCategory;
import com.likelion.news.repository.CrawledNewsRepository;
import com.likelion.news.repository.RefinedNewsRepository;
import com.likelion.news.utils.NanoIdProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NewsService {



    private final NaverNewsCrawler naverNewsCrawler;


    private final CrawledNewsRepository crawledNewsRepository;

    private final RefinedNewsRepository refinedNewsRepository;

    private final NanoIdProvider nanoIdProvider;

    @Value("${clova.summary.minimum-content-size}")
    private int minimumSize;


    @Value("${clova.summary.maximum-content-size}")
    private int maximumSize;

    /**
     * @param category 뉴스 카테고리
     * @param date       뉴스의 작성일
     * @param size  카테고리 기사 URL의 갯수
     * @return
     * @throws
     * @author minseok kim
     * @description 뉴스를 크롤링하는 메서드, 크롤링된 뉴스는 DB에 저장된다.
     */

    public List<String> getArticleUrls(ArticleCategory category, int size, LocalDate date){

        List<String> result = new ArrayList<>(size);

        int page = 1;

        loop : while(true){
            List<String> articleUrls = naverNewsCrawler.crawlArticleUrls(category, page, date);
            if(articleUrls.isEmpty()){
                break;
            }

            for(int i = 0; i < articleUrls.size(); i++){
                result.add(articleUrls.get(0));
                if(result.size() >= size){
                    break loop;
                }
            }

            result.addAll(articleUrls);

        }




        return result;
    }



    /**
     * @description URL에 해당하는 기사 정보를 조회하는 메서드
     * @author minseok kim
     * @param url 기사의 URL
     * @param category 기사의 카테고리
     * @throws
    */
    public Optional<CrawledNewsDto.CrawledInfo> getArticleDetail(String url, ArticleCategory category){
        return naverNewsCrawler.crawlArticleDetail(url, category);
    }



    /**
     * @description 크롤링한 기사 정보를 저장하는 메서드
     * @author minseok kim
     * @param article 기사 정보
    */
    @Transactional
    public void save(CrawledNewsDto.CrawledInfo article){

        CrawledNews entity = article.toEntity(nanoIdProvider.createNanoId());

        crawledNewsRepository.save(entity);
    }


    /**
     * @methodName getRandomNews
     * @author : Minseok Kim
     * @description articleTypes에 속한 뉴스중 size * length(articleTypes)개의 뉴스를 뽑아옴
     *
     * @param size 뽑아올 뉴스의 갯수
     * @param articleTypes 조회할 뉴스의 카테고리 리스트
     * @param articleDate 조회하려는 뉴스의 날짜
     * @return List<CrawledNews>
     */
    public List<CrawledNews> getRandomNews(Integer size, List<ArticleCategory> articleTypes, LocalDate articleDate){
        ArrayList<CrawledNews> result = new ArrayList<>();

        for(ArticleCategory articleType : articleTypes) {
            List<CrawledNews> randomNewsList = getRandomNews(size, articleType, articleDate);

            result.addAll(randomNewsList);
        }
        return result;
    }
    /**
     * @methodName getRandomNews
     * @author : Minseok Kim
     * @description articleType에 속한 뉴스중 size개의 뉴스를 뽑아옴. 해당하는 뉴스가 없으면 빈 리스트가 리턴됨.
     *
     * @param size 뽑아올 뉴스의 갯수
     * @param articleType 조회할 뉴스의 카테고리
     * @param articleDate : 조회하려는 뉴스의 Date
     * @return List<CrawledNews>
     */
    public List<CrawledNews> getRandomNews(Integer size, ArticleCategory articleType, LocalDate articleDate){


        // 날짜에 해당하는 모든 News를 가져 온다.
        LocalDateTime startDateTime = articleDate.atStartOfDay();
        LocalDateTime endDateTime = articleDate.plusDays(1).atStartOfDay();
        List<CrawledNews> newsList = crawledNewsRepository
                .findAllByArticleCategoryAndArticleDateIs(articleType.name(), startDateTime, endDateTime);


        if(newsList.isEmpty()){
            return List.of();
        }

        Set<CrawledNews> result = new HashSet<>();


        List<CrawledNews> satisfiedNewsList = newsList.stream().filter(n -> n.contentSizeIsIn(minimumSize, maximumSize)).toList();

        //Random Number을 뽑아온 후, 그에 해당하는 index의 뉴스의 content가 조건을 만족하는지 확인한다.
        // resultSize가 parameter로 받은 size에 만족할 때까지 반복한다.
        if(satisfiedNewsList.size() <= size){
            return satisfiedNewsList;
        }


        loop: while(true){
            Set<Integer> randomNumbers = getRandomNumbers(satisfiedNewsList.size(), size);

            for(Integer randomNumber : randomNumbers){
                if(result.size() >= size){
                    break loop;
                }
                result.add(satisfiedNewsList.get(randomNumber));
            }
        }



        return new ArrayList<>(result);
    }

    private void saveArticleInDB(List<CrawledNewsDto.CrawledInfo> articles) {
        List<CrawledNews> entities = articles.stream().map(c -> c.toEntity(nanoIdProvider.createNanoId())).toList();

        crawledNewsRepository.saveAll(entities);
    }



    /**
     * @methodName saveRefinedNewsList
     * @author : Minseok Kim
     * @description dto로 전달받은 refinedNews를 Entity로 변환해 저장하는 메서드
     *
     * @param  dtoList 저장하려는 refinedNews의 정보가 담긴 DTO List
     */
    @Transactional
    public void saveRefinedNewsList(List<RefinedNewsDto.Content> dtoList){
        List<RefinedNews> refinedNewsList = dtoList.stream().map(RefinedNewsDto.Content::toEntity).toList();

        refinedNewsRepository.saveAll(refinedNewsList);
    }


    /**
     * @methodName getRandomNumbers
     * @author : Minseok Kim
     * @description 중복되지 않은 임의의 숫자를 가져오는 함수
     *
     * @param  maxNumber 가장 최대 나올 수 있는숫자 -1, 즉, 0~maxNumber-1까지의 숫자가 랜덤으로 나온다
     * @param size randomNumber을 가져올 갯수
     */
    private Set<Integer> getRandomNumbers(int maxNumber, int size){
        Set<Integer> randomNumbers = new HashSet<>();

        while (randomNumbers.size() < size){
            int randomNumber = (int)(Math.random() * maxNumber);
            randomNumbers.add(randomNumber);
        }
        return randomNumbers;
    }
}
