package com.example.parser.service.impl;

import com.example.parser.service.MainService;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
@Service
public class MainServiceImpl implements MainService {

    public static void main(String[] args){
        MainService mainService = new MainServiceImpl();
        mainService.parse("https://hdrezka.ag/series/melodrama/35328-postuchis-v-moyu-dver-2020.html", "turok1990");
    }

    @Override
    public String parse(String url, String voiceActing) {

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
            return resultUrl;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }


    @Override
    public String getDocumentWithSelenium(WebDriver webDriver, String xPath, String xPathValue) throws InterruptedException {
        WebElement btnToClick = webDriver.findElement(By.xpath(xPath.replace("%s", xPathValue)));
        btnToClick.click();

        Thread.sleep(5000);

        return webDriver.getPageSource();
    }


    @Override
    public String getVoiceActingValue(String voiceActing, Document document, String cssQueryVoiceActing){
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
}
