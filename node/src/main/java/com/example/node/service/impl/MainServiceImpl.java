package com.example.node.service.impl;

import com.example.node.dao.AppUserDAO;
import com.example.node.dao.RawDataDAO;
import com.example.node.dao.enums.UserState;
import com.example.node.dto.SearchedSeriesDto;
import com.example.node.dto.SearchingSeriesToParseDto;
import com.example.node.entity.AppDocument;
import com.example.node.entity.AppPhoto;
import com.example.node.entity.AppUser;
import com.example.node.entity.RawData;
import com.example.node.exceptions.UploadFileException;
import com.example.node.service.*;
import com.example.node.service.enums.LinkType;
import com.example.node.service.enums.ServiceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;

import static com.example.node.dao.enums.UserState.*;
import static com.example.node.service.enums.ServiceCommand.*;

@Service
@RequiredArgsConstructor
@Log4j
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;

    private final FollowReleaseService followReleaseService;


    @Override
    public void processTextMessage(Update update) {
        AppUser appUser = findOrSaveAppUser(update);
        long chatId = update.getMessage().getChatId();
        UserState userState = appUser.getState();
        String text = update.getMessage().getText();
        String output = "";
        ServiceCommand serviceCommand = ServiceCommand.fromValue(text);

        if(CANCEL.equals(serviceCommand)){
            output = cancelProcess(appUser);
        } else if(FOLLOW_SERIES_RELEASE.equals(serviceCommand)){
            output = processServiceCommand(appUser, String.valueOf(serviceCommand));
        } else if(READY_FOR_INPUT_TITLE.equals(userState)){
            SearchingSeriesToParseDto searchingSeriesToParseDto = SearchingSeriesToParseDto.builder()
                    .title(text)
                    .chatId(chatId)
                    .build();
            output = followReleaseService.findSeriesOnWebsite(searchingSeriesToParseDto);
            AppUser appUserTemp = appUserDAO.findByTelegramUserId(appUser.getTelegramUserId()).get();
            appUserTemp.setState(BASIC_STATE);
            appUserDAO.save(appUserTemp);
        } else if (BASIC_STATE.equals(userState)){
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text,chatId);
        } else {
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова";
            log.debug("Unknown user state: " + userState);

        }

        sendAnswer(output, String.valueOf(chatId));

        saveRawData(update);
    }

    @Override
    public void processPhotoMessage(Update update) {
        AppUser appUser = findOrSaveAppUser(update);
        long chatId = update.getMessage().getChatId();

        if(isNotAllowToSendContent(chatId, appUser)){
            return;
        }

        try{
            AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
            String linkForDownload = fileService.generatedLink(appPhoto.getId(), LinkType.GET_PHOTO);
            sendAnswer("Фото загружено успешно! Ссылка для скачивание: \n" + linkForDownload, String.valueOf(chatId));
        } catch (UploadFileException e){
            log.error(e);
            sendAnswer("К сожалению загрузка файла не удалась! Повторите попытку позже", String.valueOf(chatId));
        }

    }

    @Override
    public void processDocMessage(Update update) {
        AppUser appUser = findOrSaveAppUser(update);
        long chatId = update.getMessage().getChatId();

        if(isNotAllowToSendContent(chatId, appUser)){
            return;
        }

        try{
            AppDocument doc = fileService.processDoc(update.getMessage());
            String linkForDownload = fileService.generatedLink(doc.getId(), LinkType.GET_DOC);
            sendAnswer("Документ загружен успешно! Ссылка для скачивание: " + linkForDownload, String.valueOf(chatId));
        }catch(UploadFileException e){
            log.error(e);
            String error = "К сожалению загрузка файла не удалась! Повторите попытку позже";
            sendAnswer(error, String.valueOf(chatId));
        }

    }


    public void processSearchedSeriesResponse(SearchedSeriesDto searchedSeriesDto) {
        sendSearchedSeriesUrlAnswer(searchedSeriesDto);
    }

    private boolean isNotAllowToSendContent(long chatId, AppUser appUser) {
        UserState userState = appUser.getState();
        if(!appUser.getIsActive()){
            String error = "Зарегистрируйтесь или активируйте свою учетную записать для загрузки контента!";
            sendAnswer(error, String.valueOf(chatId));
            return true;
        } else if(!BASIC_STATE.equals(userState)){
            String error = "Отмените текущую команду с помощью /cancel для отправки файлом!";
            sendAnswer(error, String.valueOf(chatId));
            return true;
        }

        return false;
    }

    public void sendAnswer(String output, String chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(output)
                .build();

        producerService.producerService(sendMessage);
    }

    public void sendSearchedSeriesUrlAnswer(SearchedSeriesDto searchedSeriesDto){
        List<String> listSearchedSeries = searchedSeriesDto.getUrlSeriesList();
        String chatId = String.valueOf(searchedSeriesDto.getChatId());
        for(String url: listSearchedSeries){
            sendAnswer(url, chatId);
        }

        String output = "Чтоб следить за выходом новых серий:\n" +
                "- Скопируйте и вставьте ссылку на сериал который Вы искали, и нажмите кнопку отправить.\n" +
                "Если нужного сериала нет в списке:\n" +
                "- Введите команду /follow_series_release и попробуйте ввести название еще раз!";
        sendAnswer(output, chatId);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        ServiceCommand serviceCommand = ServiceCommand.fromValue(cmd);
        if(REGISTRATION.equals(serviceCommand)){
            return appUserService.registerUser(appUser);
        } else if(FOLLOW_SERIES_RELEASE.equals(serviceCommand)){
            return followReleaseService.prepareUserToInputTitle(appUser);
        } else if(HELP.equals(serviceCommand)){
            return help();
        } else if(START.equals(serviceCommand)){
            return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n"
                + "/cancel - отмена выполнение текещей команды.\n"
                + "/registration - регистрация пользователя.\n"
                + "/follow_series_release - следите за выходом новых серий любимого сериала";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);

        return "Команда отменена!";
    }

    public AppUser findOrSaveAppUser(Update update) {
        Message textMessage = update.getMessage();
        User telegramUser = textMessage.getFrom();

        Optional<AppUser> persistentUser = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if(persistentUser.isEmpty()){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();

            return appUserDAO.save(transientAppUser);
        }

        return persistentUser.get();
    }

    private void saveRawData(Update update){
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }

}
