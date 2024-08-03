package com.example.parser.service.impl;

import com.example.parser.dto.TransferDataBetweenNodeAndParserDto;
import com.example.parser.entity.AppSeriesUrlDto;
import com.example.parser.entity.enums.UserState;
import com.example.parser.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.parser.entity.enums.UserState.*;

@Log4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {

    private final ProducerService producerService;


    @Override
    public void processFollowingToSeries(TransferDataBetweenNodeAndParserDto dto) {
        UserState userState = dto.getUserState();
        long chatId = dto.getChatId();
        System.out.println("chatId: " + chatId);

        if(userState.equals(READY_FOR_INPUT_TITLE_STATE)){
            List<String> searchedSeriesList = parseSeriesToFollow(dto.getTitle());
            TransferDataBetweenNodeAndParserDto newDto = TransferDataBetweenNodeAndParserDto.builder()
                    .chatId(chatId)
                    .userState(READY_FOR_INPUT_TITLE_STATE)
                    .urlSeriesList(searchedSeriesList)
                    .build();
            producerService.produceSearchedSeriesResponse(newDto);
        } else if (userState.equals(READY_FOR_INPUT_URL_STATE)) {
            List<String> voicesList = getListVoiceActing(dto.getUrl());
            TransferDataBetweenNodeAndParserDto newDto = TransferDataBetweenNodeAndParserDto.builder()
                    .chatId(chatId)
                    .userState(READY_FOR_INPUT_URL_STATE)
                    .voiceActing(voicesList)
                    .urlSeriesId(dto.getUrlSeriesId())
                    .build();
            producerService.produceSearchedSeriesResponse(newDto);
        } else if (userState.equals(READY_FOR_INPUT_VOICE_STATE)) {
            AppSeriesUrlDto appSeriesUrlDto = parseFollowSeriesRelease(dto.getUrl(), dto.getVoiceActing().get(0));
            String urlResult = appSeriesUrlDto.getUrl();
            TransferDataBetweenNodeAndParserDto newDto = TransferDataBetweenNodeAndParserDto.builder()
                    .chatId(chatId)
                    .telegramUserId(dto.getTelegramUserId())
                    .userState(READY_FOR_INPUT_VOICE_STATE)
                    .resultUrl(urlResult)
                    .urlSeriesId(dto.getUrlSeriesId())
                    .appSeriesUrlDto(appSeriesUrlDto)
                    .build();
            producerService.produceSearchedSeriesResponse(newDto);
        } else if(chatId == -777){
            List<AppSeriesUrlDto> appSeriesUrlDtoList = dto.getAppSeriesUrlDtoList();
            List<AppSeriesUrlDto> resultList = new ArrayList<>();

            for(AppSeriesUrlDto appSeriesUrlDto: appSeriesUrlDtoList){
                int endIndex = appSeriesUrlDto.getUrl().indexOf("html") + 4;
                String url = appSeriesUrlDto.getUrl().substring(0, endIndex);
                AppSeriesUrlDto actualAppSeriesUrlDto = parseFollowSeriesRelease(url, appSeriesUrlDto.getVoiceActingName());

                if(appSeriesUrlDto.getLastEpisode() != actualAppSeriesUrlDto.getLastEpisode() || appSeriesUrlDto.getLastSeason() != actualAppSeriesUrlDto.getLastSeason()){
                    appSeriesUrlDto.setNewUrl(actualAppSeriesUrlDto.getUrl());
                    appSeriesUrlDto.setLastSeason(actualAppSeriesUrlDto.getLastSeason());
                    appSeriesUrlDto.setLastEpisode(actualAppSeriesUrlDto.getLastEpisode());
                    resultList.add(appSeriesUrlDto);
                }

            }

            System.out.println(appSeriesUrlDtoList);

            if(resultList.size() > 0){
                TransferDataBetweenNodeAndParserDto resultDto = TransferDataBetweenNodeAndParserDto.builder()
                        .chatId(-777)
                        .appSeriesUrlDtoList(resultList)
                        .build();

                producerService.produceSearchedSeriesResponse(resultDto);
            }

        }
    }


    @Override
    public AppSeriesUrlDto parseFollowSeriesRelease(String url, String voiceActing) {

        String xPathVoiceAct = "//*[@id=\"translators-list\"]/li[%s]";
        String xPathSeason = "//*[@id=\"simple-seasons-tabs\"]/li[%s]";

        try{
            System.setProperty("webdriver.chrome.driver", "D:\\myProjectsCopy\\IdeaProjects\\MyBot\\MyBot\\parser\\selenium\\chromedriver.exe");
            WebDriver webDriver = new ChromeDriver();
            webDriver.get(url);

            Document document0 = Jsoup.connect(url).get();

            Elements listOfVoiceAct =  document0.select(".b-translator__item");
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
            String voiceActingValue = getVoiceActingValue(voiceActing, document2, ".b-translator__item");

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

    @Override
    public List<String> parseSeriesToFollow(String seriesTitle) {
        String urlSearchingTemplate = "https://hdrezka.ag/search/?do=search&subaction=search&q=";

        try{
            Document document = Jsoup.connect(urlSearchingTemplate + seriesTitle).get();
            Elements elements = document.getElementsByAttribute("data-url");
            List<String> resultList = elements.stream().map(element -> {
                String elemToString = element.toString();
                int indexFrom = elemToString.indexOf("data-url=") + 10;
                int indexTo = elemToString.indexOf("\">");

                return elemToString.substring(indexFrom, indexTo);
            }).toList();

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

    private static List<String> getListVoiceActing(String url){
        try{
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select(".b-translator__item");
            List<String> resultList = elements.stream().map(element -> {
                String elemToString = element.toString();
                int indexFrom = elemToString.indexOf("title=") + 7;
                int indexTo = elemToString.indexOf("\" class");

                return elemToString.substring(indexFrom, indexTo);
            }).toList();

            return resultList;
        }catch(Exception e){
            System.out.println(e);
        }

        return new ArrayList<>();
    }


}
