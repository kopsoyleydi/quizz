package ru.frank.bot.botUtils;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.frank.dataBaseUtil.userScore.UserScoreDao;

import ru.frank.model.UserScore;


/**
 * Класс для обработки событий связанных с чтением, изменением, дополнением
 * счета пользователя в таблице базы данных.
 */
@Component
@Transactional
public class UserScoreHandler {

	@Autowired
	private UserScoreDao userScoreDao;

	/**
	 * Метод проверяет наличие пользователя в таблице базы данных "user_score";
	 *
	 * @param userId
	 * @return true - если пользователь уже есть в таблице, false - если нет.
	 */
	public boolean userAlreadyInChart(long userId, long chatId) {
		return userScoreDao.findByChatIdAndUserId(userId, chatId) != null;
	}

	/**
	 * Метод добавляет новую запись в таблицу
	 *
	 * @param
	 */
	public void addNewUserInChart(long chatId, String userName, long userId) {
		UserScore userScore = new UserScore(chatId, userName, 2, userId);
		userScoreDao.save(userScore);
	}

	public void incrementUserScore(long userId, int score, long chatId) {
		UserScore userScore = userScoreDao.findByChatIdAndUserId(userId, chatId);
		userScore.setScore(userScore.getScore() + score);
		userScoreDao.save(userScore);
	}

	public long getUserScoreById(long userId, long chatId) {
		return userScoreDao.findByChatIdAndUserId(userId, chatId).getScore();
	}



}
