package ru.frank.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.dataBaseUtil.QuestionAndAnswerDao;
import ru.frank.model.QuestionAndAnswer;

import java.util.List;

@Service
public class QuestionAndAnswerService {

	@Autowired
	private QuestionAndAnswerDao questionAndAnswerDao;

	public Long getMaximumId() {
		List<QuestionAndAnswer> questionAndAnswersList = questionAndAnswerDao.findAll();
		return questionAndAnswersList.get(questionAndAnswersList.size() - 1).getId();
	}
}
