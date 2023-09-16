package ru.frank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.bot.botUtils.QuestionAnswerGenerator;
import ru.frank.dataBaseUtil.QuestionsDao;
import ru.frank.model.QuestionAndAnswer;
import ru.frank.model.Questions;

@Service
public class QuestionService {

	@Autowired
	QuestionsDao questionsDao;

	@Autowired
	QuestionAndAnswerService questionAndAnswerService;

	@Autowired
	QuestionAnswerGenerator questionAnswerGenerator;

	public String getQuestion(Long chatId){
		Questions questions = questionAnswerGenerator.getRandomQuestionAndAnswer();
		QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(
				questions.getQuestion()
				, questions.getAnswer()
				, chatId);
		questionAndAnswerService.add(questionAndAnswer);

		return questions.getQuestion();
	}
}
