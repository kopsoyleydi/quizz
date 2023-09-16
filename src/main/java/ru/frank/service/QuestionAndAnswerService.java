package ru.frank.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.dataBaseUtil.QuestionsAndAnswerDao;
import ru.frank.dataBaseUtil.QuestionsDao;
import ru.frank.model.QuestionAndAnswer;

import java.util.List;

@Service
public class QuestionAndAnswerService {

	@Autowired
	private QuestionsDao questionsDao;

	@Autowired
	private QuestionsAndAnswerDao questionsAndAnswerDao;

	public Long getMaximumId() {
		List<ru.frank.model.Questions> questionAndAnswersList = questionsDao.findAll();
		return questionAndAnswersList.get(questionAndAnswersList.size() - 1).getChat_id();
	}

	public void add(QuestionAndAnswer questionAndAnswer){
		questionsAndAnswerDao.save(questionAndAnswer);
	}

	public QuestionAndAnswer getQuestionAndAnswerByChatId(Long chatId){
		return questionsAndAnswerDao.findByChatId(chatId);
	}

	public void deleteQuestionByChatID(Long chatId){
		questionsAndAnswerDao.deleteByChatId(chatId);
	}
}
