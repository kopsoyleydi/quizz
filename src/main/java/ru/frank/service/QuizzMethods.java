package ru.frank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.dataBaseUtil.QuestionsAndAnswerDao;
import ru.frank.model.QuestionAndAnswer;

import java.util.Objects;

@Service
public class QuizzMethods {

	@Autowired
	public QuestionsAndAnswerDao questionsAndAnswerDao;

	public boolean  checkAnswer(String answer, Long chatId){
		QuestionAndAnswer questionAndAnswer = questionsAndAnswerDao.findByChatId(chatId);
		return Objects.equals(questionAndAnswer.getAnswer(), answer);
	}
}
