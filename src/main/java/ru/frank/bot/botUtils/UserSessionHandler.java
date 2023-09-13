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

    public boolean sessionIsActive(Long userId){
        return userSessionDao.getUserById(userId) != null;
    }

    public void createUserSession(Long userId){
        LocalDateTime dateTime = LocalDateTime.now();
        userSessionDao.save(new UserSession(userId,dateTime.format(formatter)));
    }


    public void deleteUserSession(long userId){
        userSessionDao.delete(userSessionDao.getUserById(userId));
    }

    private String getDateFromSession(long userId){
        UserSession userSession = userSessionDao.getUserById(userId);
        return userSession.getStartTime();
    }

    /**
     * По истечению времени, текущая сессия должна быть удалена и пользователю отправляется сообщение об истечении
     * времени.
     * @param currentDate - время получения сообщения с ответом на вопрос от пользователя.
     * @param userId - id пользователя.
     * @return true/false.
     */
    public boolean validateDate(LocalDateTime currentDate, long userId) {
        LocalDateTime dateTimeFromSession = LocalDateTime.parse(getDateFromSession(userId), formatter);
        return currentDate.isBefore(dateTimeFromSession.plusSeconds(70));
    }





}
