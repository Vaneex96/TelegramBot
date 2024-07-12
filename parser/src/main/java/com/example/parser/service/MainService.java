package com.example.parser.service;

import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

public interface MainService {
    String parse(String url, String voiceActing);
    String getVoiceActingValue(String voiceActing, Document document, String cssQueryVoiceActing) throws IOException;
    String getDocumentWithSelenium(WebDriver webDriver, String xPath, String xPathValue) throws InterruptedException;
}
