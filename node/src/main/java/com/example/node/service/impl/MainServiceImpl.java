package com.example.node.service.impl;

import com.example.node.dao.AppSeriesUrlDAO;
import com.example.node.dao.AppUserDAO;
import com.example.node.dao.RawDataDAO;
import com.example.node.dao.VoiceActingDAO;
import com.example.node.dao.enums.UserState;
import com.example.node.dto.AppSeriesUrlDto;
import com.example.node.dto.FindLastSeriesResultDto;
import com.example.node.dto.FindSeriesToSubscribeResultDto;
import com.example.node.dto.FindSeriesVoiceActsResultDto;
import com.example.node.entity.*;
import com.example.node.exceptions.UploadFileException;
import com.example.node.service.AppUserService;
import com.example.node.service.FileService;
import com.example.node.service.MainService;
import com.example.node.service.ProducerService;
import com.example.node.service.enums.LinkType;
import com.example.node.service.enums.ServiceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Optional;

import static com.example.node.dao.enums.UserState.*;
import static com.example.node.service.enums.ServiceCommand.*;

@EnableScheduling
@Log4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final AppSeriesUrlDAO appSeriesUrlDAO;
    private final FileService fileService;
    private final AppUserService appUserService;

    private final FollowReleaseServiceImpl followReleaseService;
    private final VoiceActingDAO voiceActingDAO;

    @Value("${secret.id}")
    private long secretId;

    @Override
    public void processTextMessage(Update update) {

        AppUser appUser = findOrSaveAppUser(update);
        Message message;

        if(update.hasCallbackQuery()){
            message = update.getCallbackQuery().getMessage();
        }else{
            message = update.getMessage();
        }

        long chatId = message.getChatId();
        String text = message.getText();
        UserState userState = appUser.getState();
        List<UserState> userStatesForFollowing = List.of(READY_FOR_INPUT_TITLE_STATE, READY_FOR_INPUT_VOICE_STATE, READY_FOR_INPUT_URL_STATE);
        String output = "";
        ServiceCommand serviceCommand = ServiceCommand.fromValue(text);

        if(CANCEL.equals(serviceCommand)){
            output = cancelProcess(appUser);
        } else if(FOLLOW_SERIES_RELEASE.equals(serviceCommand)) {
            output = processServiceCommand(appUser, String.valueOf(serviceCommand));
        } else if (userStatesForFollowing.contains(userState)) {
            if(update.hasCallbackQuery()){
                if(update.getCallbackQuery().getMessage().getText().equals("Для завершения процесса подписки, выберите озвучку из списка:")){
                    text = update.getCallbackQuery().getData();
                } else {
                    text = update.getCallbackQuery().getMessage().getText();
                }
            }
            SendMessage sendMessage = followReleaseService.processFollowRelease(appUser, text, chatId);
            producerService.producerService(sendMessage);
            return;
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

    @Override
    public void processResultOfFindSeriesToSubscribe(FindSeriesToSubscribeResultDto findSeriesToSubscribeResultDto) {
        sendSearchedSeriesUrlAnswer(findSeriesToSubscribeResultDto);
    }

    @Override
    public void processResultFindSeriesVoiceActs(FindSeriesVoiceActsResultDto findSeriesVoiceActsResultDto) {
        try{
            SendMessage sendMessage = followReleaseService.createSearchedSeriesVoiceButtonsAnswer(findSeriesVoiceActsResultDto);
            producerService.producerService(sendMessage);
        }catch(Exception e){
            log.error(e);
        }
    }

    @Override
    public void processResultFindLastSeries(FindLastSeriesResultDto findLastSeriesResultDto) {
        AppSeriesUrl appSeriesUrl = appSeriesUrlDAO.findById(findLastSeriesResultDto.getUrlSeriesId()).orElseThrow();
        AppSeriesUrlDto appSeriesUrlDto = findLastSeriesResultDto.getAppSeriesUrlDto();

        appSeriesUrl.setVoiceActingName(appSeriesUrlDto.getVoiceActingName());
        appSeriesUrl.setVoiceActingValue(appSeriesUrlDto.getVoiceActingValue());
        appSeriesUrl.setLastSeason(appSeriesUrlDto.getLastSeason());
        appSeriesUrl.setLastEpisode(appSeriesUrlDto.getLastEpisode());

        appSeriesUrlDAO.save(appSeriesUrl);

        AppUser appUser = appUserDAO.findByTelegramUserId(findLastSeriesResultDto.getTelegramUserId()).orElseThrow();
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        sendAnswer("Подписка на уведомление о выходе новой серии оформлена! Перейдите по ссылкe для просмотра последней серии \n"
                + findLastSeriesResultDto.getResultUrl(), String.valueOf(findLastSeriesResultDto.getChatId()));
    }


    public void processCheckingSeriesReleases(FindSeriesToSubscribeResultDto findSeriesToSubscribeResultDto) {
        //TODO
//        UserState userState = dto.getUserState();
//
//        if (dto.getChatId() == secretId) {
//            List<AppSeriesUrlDto> appSeriesUrlDtoList = dto.getAppSeriesUrlDtoList();
//            if (appSeriesUrlDtoList.size() > 0) {
//
//                for (AppSeriesUrlDto appSeriesUrlDto : appSeriesUrlDtoList) {
//                    List<AppUser> allFollowedUsers = followReleaseService.findAllFollowedUsers(appSeriesUrlDto.getId());
//
//                    followReleaseService.updateAppSeriesUrl(appSeriesUrlDto);
//
//                    for (AppUser appUser : allFollowedUsers) {
//                        sendAnswer("Вышла новая серия сериала: \n" +
//                                appSeriesUrlDto.getNewUrl() + "\n" +
//                                "Приятного просмотра!", String.valueOf(appUser.getTelegramUserId()));
//                    }
//
//                }
//
//            }
//
//        }
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

    private void sendSearchedSeriesUrlAnswer(FindSeriesToSubscribeResultDto findSeriesToSubscribeResultDto){
        List<String> listSearchedSeries = findSeriesToSubscribeResultDto.getUrlSeriesList();
        String chatId = String.valueOf(findSeriesToSubscribeResultDto.getChatId());

        SendMessage sendMessage = new SendMessage();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Выбрать!");
        button.setCallbackData("url");

        List<InlineKeyboardButton> inlineKeyboardButtons = List.of(button);
        List<List<InlineKeyboardButton>> row = List.of(inlineKeyboardButtons);
        inlineKeyboardMarkup.setKeyboard(row);

        for(String url: listSearchedSeries){
            sendMessage.setText(String.format("%s", url));
            sendMessage.setChatId(findSeriesToSubscribeResultDto.getChatId());
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            producerService.producerService(sendMessage);
        }

        String output =
                "Если нужного сериала нет в списке:\n" +
                "- Введите команду /cancel. \n" +
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
            System.out.println(cmd);
            System.out.println(appUser.getState());
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
        User telegramUser;
        if(update.hasCallbackQuery()){
            telegramUser = update.getCallbackQuery().getFrom();
            System.out.println("telegramUserId: " + telegramUser.getId());
        }else{
            telegramUser = update.getMessage().getFrom();
        }


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

//    @Scheduled(fixedDelay = 3600000)
//    private void notifyUsersReleaseOfSeries(){
//        System.out.println("Планированая проверка выхода новых серий запущена!");
//        followReleaseService.sendUrlsForCheckReleaseSeries();
//    }
}
