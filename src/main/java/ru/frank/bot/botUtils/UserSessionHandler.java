package ru.frank.bot.botUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.dataBaseUtil.UserSessionDao;
import ru.frank.model.UserSession;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class UserSessionHandler {

    @Autowired
    private UserSessionDao userSessionDao;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd HH:mm:ss");

    public boolean sessionIsActive(Long chatId){
        return userSessionDao.getUserSessionById(chatId) != null;
    }

    public void createUserSession(Long chatId){
        LocalDateTime dateTime = LocalDateTime.now();
        userSessionDao.save(new UserSession(chatId,dateTime.format(formatter), 1));
    }

    public boolean checkActiveAmount(Long chatId){
        return userSessionDao.getUserSessionById(chatId).getAmountInit() == 1;
    }
    public boolean checkAmount(Long chatId){
        return userSessionDao.getUserSessionById(chatId).getAmountInit() != 0;
    }

    public void deleteUserSession(long userId){
        userSessionDao.delete(userSessionDao.getUserSessionById(userId));
    }

    public String getDateFromSession(long chatId){
        UserSession userSession = userSessionDao.getUserSessionById(chatId);
        return userSession.getStartTime();
    }

    public void setAmountInit(Long chatId, int amountInit){
        UserSession userSession = userSessionDao.getUserSessionById(chatId);
        userSession.setAmountInit(amountInit);
        userSessionDao.save(userSession);
    }

    public void minusAmountIter(Long chatId){
        UserSession userSession = userSessionDao.getUserSessionById(chatId);
        int i = userSession.getAmountInit() - 1;
        userSession.setAmountInit(i);
        userSessionDao.save(userSession);
    }

    public boolean validateDate(LocalDateTime currentDate, long userId) {
        LocalDateTime dateTimeFromSession = LocalDateTime.parse(getDateFromSession(userId), formatter);
        return currentDate.isBefore(dateTimeFromSession.plusSeconds(61));
    }





}
