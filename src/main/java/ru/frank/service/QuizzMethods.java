package ru.frank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.dataBaseUtil.QuestionsAndAnswerDao;
import ru.frank.dataBaseUtil.UserSessionDao;
import ru.frank.dataBaseUtil.userScore.UserScoreDao;
import ru.frank.model.QuestionAndAnswer;

import java.util.Objects;

@Service
public class QuizzMethods {

	@Autowired
	private QuestionsAndAnswerDao questionsAndAnswerDao;

	@Autowired
	private UserSessionDao userSessionDao;

	@Autowired
	private UserScoreDao userScoreDao;

	@Deprecated
	public boolean  checkAnswer(String answer, Long chatId){
		QuestionAndAnswer questionAndAnswer = questionsAndAnswerDao.findByChatId(chatId);
		return Objects.equals(questionAndAnswer.getAnswer(), answer);
	}

	public String deleteGameAtChatID(long chatId){
			questionsAndAnswerDao.deleteAllByChatId(chatId);
			userSessionDao.deleteAllByChatId(chatId);
			userScoreDao.deleteByChatId(chatId);
			return "Success";
	}
}
