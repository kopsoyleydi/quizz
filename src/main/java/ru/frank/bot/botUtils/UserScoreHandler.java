package ru.frank.bot.botUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.frank.dataBaseUtil.userScore.UserScoreDao;
import ru.frank.exceptions.UserScoreListIsEmptyException;
import ru.frank.model.UserScore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для обработки событий связанных с чтением, изменением, дополнением
 * счета пользователя в таблице базы данных.
 */
@Component
public class UserScoreHandler {

    @Autowired
    private UserScoreDao userScoreDao;

    /**
     * Метод проверяет наличие пользователя в таблице базы данных "user_score";
     * @param userId
     * @return true - если пользователь уже есть в таблице, false - если нет.
     */
    public boolean userAlreadyInChart(long userId){
        return userScoreDao.getAllById(userId) != null;
    }

    /**
     * Метод добавляет новую запись в таблицу
     * @param userId
     */
    public void addNewUserInChart(long userId, String userName){
        UserScore userScore = new UserScore(userId, userName, 0);
        userScoreDao.save(userScore);
    }

    public void incrementUserScore(long userId){
        UserScore userScore = userScoreDao.getAllById(userId);
        userScore.setScore(userScore.getScore() + 1);
        userScoreDao.save(userScore);
    }

    public long getUserScoreById(long userId){
        return userScoreDao.getAllById(userId).getScore();
    }
    
    /**
     * ArrayList include top 5 or less UserSco
     * @return
     */
    public List<UserScore> getTopFiveUserScore() {
        List<UserScore> allUserScore = userScoreDao.findAll();

        if(allUserScore.isEmpty()){
            throw new UserScoreListIsEmptyException("User score list is empty.");
        }

        List<UserScore> sortedList = allUserScore.stream()
                .sorted()
                .toList();

        int userScoreListSize = allUserScore.size();

        List<UserScore> topScoreList;

        if(userScoreListSize < 5){
            topScoreList = new ArrayList<>(allUserScore.subList(0, userScoreListSize));
        } else{
            topScoreList = new ArrayList<>(allUserScore.subList(0, 5));
        }

        return topScoreList;

    }

}
