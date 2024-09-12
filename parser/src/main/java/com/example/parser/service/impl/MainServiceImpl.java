package com.example.parser.service.impl;

import com.example.parser.dto.*;
import com.example.parser.entity.AppSeriesUrlDto;
import com.example.parser.entity.enums.UserState;
import com.example.parser.service.MainService;
import com.example.parser.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.parser.entity.enums.UserState.*;

@Log4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {

    private final ProducerService producerService;
    @Value("${url.searching_template.hdrezka}")
    private String urlSearchingTemplate;

    @Value("${secret.id}")
    private long secretId;

    @Value("${webdriver.path}")
    private String webDriverPath;

    @Value("${xpath_voice_act}")
    private String xPathVoiceAct;

    @Value("${xpath_season}")
    private String xPathSeason;

    @Value("${css_selector_for_translator}")
    private String cssSelectorForTranslator;


    @Override
    public void processCheckingReleases(TransferDataBetweenNodeAndParserDto dto) {
//        //TODO
//        UserState userState = dto.getUserState();
//        long chatId = dto.getChatId();
//
//            List<AppSeriesUrlDto> appSeriesUrlDtoList = dto.getAppSeriesUrlDtoList();
//            List<AppSeriesUrlDto> resultList = new ArrayList<>();
//
//            for(AppSeriesUrlDto appSeriesUrlDto: appSeriesUrlDtoList){
//                String url = appSeriesUrlDto.getUrl();
//                AppSeriesUrlDto actualAppSeriesUrlDto = parseFollowSeriesRelease(url, appSeriesUrlDto.getVoiceActingName());
//
//                if(appSeriesUrlDto.getLastEpisode() != actualAppSeriesUrlDto.getLastEpisode() || appSeriesUrlDto.getLastSeason() != actualAppSeriesUrlDto.getLastSeason()){
//                    appSeriesUrlDto.setNewUrl(actualAppSeriesUrlDto.getUrl());
//                    appSeriesUrlDto.setLastSeason(actualAppSeriesUrlDto.getLastSeason());
//                    appSeriesUrlDto.setLastEpisode(actualAppSeriesUrlDto.getLastEpisode());
//                    resultList.add(appSeriesUrlDto);
//                }
//
//            }
//
//            if(resultList.size() > 0){
//                TransferDataBetweenNodeAndParserDto resultDto = TransferDataBetweenNodeAndParserDto.builder()
//                        .chatId(secretId)
//                        .userState(BASIC_STATE)
//                        .appSeriesUrlDtoList(resultList)
//                        .build();

//                producerService.produceResultFindSeriesToSubscribe(resultDto);
//            }

    }

    @Override
    public void processFindSeries(FindSeriesToSubscribeDto findSeriesToSubscribeDto) {
        List<String> searchedSeriesList = parseSeriesToFollow(findSeriesToSubscribeDto.getTitle());
        FindSeriesToSubscribeResultDto findSeriesToSubscribeResultDto = FindSeriesToSubscribeResultDto.builder()
                .chatId(findSeriesToSubscribeDto.getChatId())
                .urlSeriesList(searchedSeriesList)
                .build();
        producerService.produceResultFindSeriesToSubscribe(findSeriesToSubscribeResultDto);
    }


    @Override
    public void processFindSeriesVoiceActs(FindSeriesVoiceActsDto findSeriesVoiceActsDto) {
        Map<Long, String> mapVoiceActing = getListVoiceActing(findSeriesVoiceActsDto.getUrl());
        FindSeriesVoiceActsResultDto findSeriesVoiceActsResultDto = FindSeriesVoiceActsResultDto.builder()
                .chatId(findSeriesVoiceActsDto.getChatId())
                .seriesUrlId(findSeriesVoiceActsDto.getUrlSeriesId())
                .mapVoiceActing(mapVoiceActing)
                .build();

        producerService.produceResultFindSeriesVoiceActs(findSeriesVoiceActsResultDto);
    }

    @Override
    public void processFindLastSeries(FindLastSeriesDto findLastSeriesDto) {
        AppSeriesUrlDto appSeriesUrlDto = parseFollowSeriesRelease(findLastSeriesDto.getUrl(), findLastSeriesDto.getVoiceActing());
        String urlResult = appSeriesUrlDto.getUrl();

        FindLastSeriesResultDto findLastSeriesResultDto = FindLastSeriesResultDto.builder()
                .chatId(findLastSeriesDto.getChatId())
                .telegramUserId(findLastSeriesDto.getTelegramUserId())
                .resultUrl(urlResult)
                .urlSeriesId(findLastSeriesDto.getUrlSeriesId())
                .appSeriesUrlDto(appSeriesUrlDto)
                .build();

        producerService.produceResultFindLastSeries(findLastSeriesResultDto);
    }


    private AppSeriesUrlDto parseFollowSeriesRelease(String url, String voiceActing) {

        try{
            System.setProperty("webdriver.chrome.driver", webDriverPath);
            WebDriver webDriver = new ChromeDriver();
            webDriver.get(url);

            Document document0 = Jsoup.connect(url).get();
            Elements listOfVoiceAct =  document0.select(cssSelectorForTranslator);
            String serialVoiceNum = "";
            for(int i = 0; i < listOfVoiceAct.size(); i++){
                if(listOfVoiceAct.get(i).toString().contains(voiceActing)){
                    serialVoiceNum = String.valueOf(i+1);
                    break;
                }
            }

            Document document1 = Jsoup.parse(getDocumentWithSelenium(webDriver, xPathVoiceAct, serialVoiceNum));
            String lastSeasonNum = String.valueOf(document1.select(".b-simple_season__item").size());

            Document document2 = Jsoup.parse(getDocumentWithSelenium(webDriver, xPathSeason, lastSeasonNum));
            String lastEpisodeNum = String.valueOf(document2.getElementsByAttributeValue("data-season_id", lastSeasonNum).size());
            String voiceActingValue = getVoiceActingValue(voiceActing, document2, cssSelectorForTranslator);

            String resultUrl = String.format("%s#t:%s-s:%s-e:%s", url, voiceActingValue, lastSeasonNum, lastEpisodeNum);
            webDriver.close();
            System.out.println(resultUrl);
            AppSeriesUrlDto appSeriesUrlDto = AppSeriesUrlDto.builder()
                    .url(resultUrl)
                    .voiceActingName(voiceActing)
                    .voiceActingValue(Integer.parseInt(voiceActingValue))
                    .lastSeason(Integer.parseInt(lastSeasonNum))
                    .lastEpisode(Integer.parseInt(lastEpisodeNum))
                    .build();
            return appSeriesUrlDto;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }


    private List<String> parseSeriesToFollow(String seriesTitle) {
        String regex = "https://hdrezka.ag/series/[a-z]{3,20}/([a-z0-9\\-]{10,100}).html";
        try{
            int seriesLimit = 3;
            Document document = Jsoup.connect(urlSearchingTemplate + seriesTitle).get();
            Elements elements = document.getElementsByAttribute("data-url");
            List<String> resultList = new ArrayList<>();

            for(Element element: elements){
                if(resultList.size() == seriesLimit) break;
                String elemToString = element.toString();
                int indexFrom = elemToString.indexOf("data-url=") + 10;
                int indexTo = elemToString.indexOf("\">");
                String res = elemToString.substring(indexFrom, indexTo);
                if(res.matches(regex)){
                    resultList.add(res);
                }
            }

            return resultList;
        }catch(Exception e){
            System.out.println(e);
        }

        return new ArrayList<>();
    }



    private String getDocumentWithSelenium(WebDriver webDriver, String xPath, String xPathValue) throws InterruptedException {
        WebElement btnToClick = webDriver.findElement(By.xpath(xPath.replace("%s", xPathValue)));
        btnToClick.click();

        Thread.sleep(5000);

        return webDriver.getPageSource();
    }



    private String getVoiceActingValue(String voiceActing, Document document, String cssQueryVoiceActing){
        Elements elementsOfVoices = document.select(cssQueryVoiceActing);
        String voiceActingStringValue = elementsOfVoices.stream()
                .map(Element::toString)
                .filter(str -> str.contains(voiceActing))
                .toList()
                .get(0);


        Pattern pattern = Pattern.compile("\"\\d+\"");
        Matcher matcher = pattern.matcher(voiceActingStringValue);
        String voiceActingNumValue = null;
        while (matcher.find()) {
            voiceActingNumValue = matcher.group();
        }
        assert voiceActingNumValue != null;
        return voiceActingNumValue.substring(1, voiceActingNumValue.length()-1);
    }

    private static Map<Long, String> getListVoiceActing(String url){
        try{
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select(".b-translator__item");
            Map<Long, String> resultMap = elements.stream()
                    .map(Node::toString)
        .collect(Collectors.toMap(str -> Long.valueOf(str.substring(str.indexOf("_id=")+5, str.indexOf("\">"))),
                str -> str.substring(str.indexOf("title=") + 7, str.indexOf("\" class"))
                ));

            return resultMap;
        }catch(Exception e){
            System.out.println(e);
        }

        return new HashMap<>();
    }


}

