package com.example.node.service.impl;

import com.example.node.dao.AppSeriesUrlDAO;
import com.example.node.dao.AppUserDAO;
import com.example.node.dao.enums.UserState;
import com.example.node.dto.*;
import com.example.node.entity.AppSeriesUrl;
import com.example.node.entity.AppUser;
import com.example.node.service.FollowReleaseService;
import com.example.node.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.regex.Pattern;

import static com.example.node.dao.enums.UserState.*;

@Log4j
@RequiredArgsConstructor
@Service
public class FollowReleaseServiceImpl implements FollowReleaseService {

    private final AppUserDAO appUserDAO;
    private final ProducerService producerService;
    private final AppSeriesUrlDAO appSeriesUrlDAO;
    @Value("${secret.id}")
    private long secretId;

    @Override
    public SendMessage processFollowRelease(AppUser appUser, String text, long chatId) {
        String regex = "https://hdrezka.ag/series/[a-z]{3,20}/([a-z0-9\\-]{10,100}).html";
        boolean isMatched = Pattern.matches(regex, text);
        UserState userState = appUser.getState();
        SendMessage outputSendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Ошибка! Введите /cancel и начните заново!")
                .build();

        if(READY_FOR_INPUT_TITLE_STATE.equals(userState) && !text.contains("https://") && !text.contains("http://")){
            String outputText = findSeriesOnWebsite(appUser, text, chatId);
            outputSendMessage.setText(outputText);
        } else if (READY_FOR_INPUT_URL_STATE.equals(userState) || isMatched){

            if(isMatched){
                outputSendMessage = getVoicesActing(appUser, text, chatId);
            } else {
                String outputText = "Вы ввели некорректную ссылку на сериал. Попробуйте еще раз!";
                outputSendMessage.setText(outputText);
            }

        } else if (READY_FOR_INPUT_VOICE_STATE.equals(userState)) {
            String outputText = followRelease(appUser, text, chatId);
            outputSendMessage.setText(outputText);
        }

        return outputSendMessage;
    }


    public String followRelease(AppUser appUser, String urlIdAndVoice, long chatId){

        long urlId = Long.parseLong(urlIdAndVoice.substring(0, urlIdAndVoice.indexOf("/")));
        String voice = urlIdAndVoice.substring(urlIdAndVoice.indexOf("=") + 1);
        AppSeriesUrl appSeriesUrl = appSeriesUrlDAO.findById(urlId).orElseThrow();

        /////////Если appSeriesUrl уже существует в бд
        Optional<AppSeriesUrl> appSeriesUrlByUrlAndVoiceOptional = appSeriesUrlDAO.findByUrlAndVoiceActingName(appSeriesUrl.getUrl(), voice);

        if(appSeriesUrlByUrlAndVoiceOptional.isPresent()){
            AppSeriesUrl appSeriesUrlByUrlAndVoice = appSeriesUrlByUrlAndVoiceOptional.get();
            boolean isContainsAppSeriesUrl = false;

            for(AppSeriesUrl url: appUser.getUrlList()){
                if(url.getUrlId() == appSeriesUrlByUrlAndVoice.getUrlId()) {
                    isContainsAppSeriesUrl = true;
                    break;
                }
            }

            if(isContainsAppSeriesUrl){
                return "Вы уже подписаны на этот сериал в этой озвучке!";
            } else if(addPersistentAppSeriesUrlToListAppUser(appUser, appSeriesUrlByUrlAndVoice, appSeriesUrl)){
                return getSuccessFollowedMessage(appSeriesUrlByUrlAndVoice);
            }

        }/////////Если appSeriesUrl уже существует в бд

        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);

        FindLastSeriesDto findLastSeriesDto = FindLastSeriesDto.builder()
                .chatId(chatId)
                .telegramUserId(appUser.getTelegramUserId())
                .voiceActing(voice)
                .url(appSeriesUrl.getUrl())
                .urlSeriesId(urlId)
                .build();
        producerService.produceFindLastSeries(findLastSeriesDto);

        return infoIsLoading();
    }


    private boolean addPersistentAppSeriesUrlToListAppUser(AppUser appUser, AppSeriesUrl appSeriesUrl, AppSeriesUrl appSeriesUrlForDelete){

        try{
            appUser.addAppSeriesUrl(appSeriesUrl);
            appUser.setState(BASIC_STATE);

            long urlIdForDelete = appSeriesUrlForDelete.getUrlId();
            appUser.removeAppSeriesUrlByUrlId(urlIdForDelete);

            appUserDAO.save(appUser);
            appSeriesUrlDAO.deleteById(urlIdForDelete);
        } catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private String getSuccessFollowedMessage(AppSeriesUrl appSeriesUrl){
        return "Подписка на уведомление о выходе новой серии оформлена! Перейдите по ссылкe для просмотра последней серии \n"
                + String.format("%s#t:%s-s:%s-e:%s", appSeriesUrl.getUrl(), appSeriesUrl.getVoiceActingValue(),
                appSeriesUrl.getLastSeason(), appSeriesUrl.getLastEpisode());
    }
    

    @Override
    public SendMessage getVoicesActing(AppUser appUser, String url, long chatId) {

        AppSeriesUrl transientAppSeriesUrl = AppSeriesUrl.builder()
                .url(url)
                .build();

        AppSeriesUrl persistentAppSeriesUrl = appSeriesUrlDAO.save(transientAppSeriesUrl);

        appUser.addAppSeriesUrl(persistentAppSeriesUrl);
        appUser.setState(READY_FOR_INPUT_VOICE_STATE);
        appUserDAO.save(appUser);

        FindSeriesVoiceActsDto findSeriesVoiceActsDto = FindSeriesVoiceActsDto.builder()
                .chatId(chatId)
                .url(url)
                .urlSeriesId(persistentAppSeriesUrl.getUrlId())
                .build();
        producerService.produceFindSeriesVoiceActs(findSeriesVoiceActsDto);

        return SendMessage.builder()
                .chatId(chatId)
                .text(infoIsLoading())
                .build();
    }

    @Override
    public String prepareUserToInputTitle(AppUser appUser) {
        appUser.setState(READY_FOR_INPUT_TITLE_STATE);
        appUserDAO.save(appUser);
        return "Введите, пожалуйста, название сериала: ";
    }

    @Override
    public String findSeriesOnWebsite(AppUser appUser, String title, long chatId){
        FindSeriesToSubscribeDto findSeriesToSubscribeDto = FindSeriesToSubscribeDto.builder()
                .chatId(chatId)
                .title(title)
                .build();

        AppUser appUserTemp = appUserDAO.findByTelegramUserId(appUser.getTelegramUserId()).get();
        appUserTemp.setState(READY_FOR_INPUT_URL_STATE);
        appUserDAO.save(appUserTemp);

        producerService.produceFindSeriesToSubscribe(findSeriesToSubscribeDto);
        return infoIsLoading();
    }

    @Override
    public void sendUrlsForCheckReleaseSeries() {
        List<AppSeriesUrlDto> appSeriesUrlDtoList = new ArrayList<>();
        appSeriesUrlDAO.findAll().forEach(item -> {
            String resultUrl = String.format("%s#t:%s-s:%s-e:%s", item.getUrl(), item.getVoiceActingValue(), item.getLastSeason(), item.getLastEpisode());
            AppSeriesUrlDto appSeriesUrlDto = AppSeriesUrlDto.builder()
                    .id(item.getUrlId())
                    .url(resultUrl)
                    .lastSeason(item.getLastSeason())
                    .lastEpisode(item.getLastEpisode())
                    .voiceActingName(item.getVoiceActingName())
                    .build();
            appSeriesUrlDtoList.add(appSeriesUrlDto);
        });

        if(appSeriesUrlDtoList.size() == 0) return;

        TransferDataBetweenNodeAndParserDto dto = TransferDataBetweenNodeAndParserDto.builder()
                .chatId(secretId)
                .userState(BASIC_STATE)
                .appSeriesUrlDtoList(appSeriesUrlDtoList)
                .build();

        //TODO
//        producerService.produceFindSeriesToSubscribe(dto);
    }

    @Override
    public List<AppUser> findAllFollowedUsers(long id) {
        AppSeriesUrl appSeriesUrl = appSeriesUrlDAO.findById(id).orElseThrow();
        return appSeriesUrl.getAppUsers();
    }

    @Override
    public void updateAppSeriesUrl(AppSeriesUrlDto appSeriesUrlDto) {
        AppSeriesUrl appSeriesUrl = appSeriesUrlDAO.findById(appSeriesUrlDto.getId()).get();
        appSeriesUrl.setUrl(appSeriesUrlDto.getNewUrl());
        appSeriesUrl.setLastSeason(appSeriesUrlDto.getLastSeason());
        appSeriesUrl.setLastEpisode(appSeriesUrlDto.getLastEpisode());
        appSeriesUrlDAO.save(appSeriesUrl);
    }

    @Override
    public SendMessage createSearchedSeriesVoiceButtonsAnswer(FindSeriesVoiceActsResultDto findSeriesVoiceActsResultDto){
        //TODO
        Map<Long, String> searchedVoiceActingMap = findSeriesVoiceActsResultDto.getMapVoiceActing();

        String chatId = String.valueOf(findSeriesVoiceActsResultDto.getChatId());

        if(searchedVoiceActingMap.size() > 0){

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            for(Map.Entry<Long, String> voice: searchedVoiceActingMap.entrySet()){
                InlineKeyboardButton button = InlineKeyboardButton.builder()
                        .text(voice.getValue())
                        .callbackData(findSeriesVoiceActsResultDto.getSeriesUrlId() + "/" + voice)
                        .build();
                List<InlineKeyboardButton> inlineKeyboardButtonList = List.of(button);
                rows.add(inlineKeyboardButtonList);
            }
            inlineKeyboardMarkup.setKeyboard(rows);
            SendMessage sendMessage = SendMessage.builder()
                    .text("Для завершения процесса подписки, выберите озвучку из списка: ")
                    .chatId(chatId)
                    .replyMarkup(inlineKeyboardMarkup)
                    .build();


            return sendMessage;
        } else {
            //TODO реализовать отправление ссылки на последнюю серии последнего сезона
            SendMessage sendMessage = SendMessage.builder()
                    .text("Вы успешно подписались на сериал")
                    .chatId(chatId)
                    .build();

            return sendMessage;
        }

    }

    private String infoIsLoading(){
        return "Идет обработка информации...";
    }
}
