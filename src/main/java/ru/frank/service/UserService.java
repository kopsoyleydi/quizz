package ru.frank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.dataBaseUtil.userScore.UserScoreDao;
import ru.frank.model.UserScore;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserScoreDao userScoreDao;

    public List<UserScore> getTopUsersInChat(Long chatId){
        return userScoreDao.findAllUserInCurrentChat(chatId);
    }
}
