package ru.frank.bot.botUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.dataBaseUtil.QuestionsDao;
import ru.frank.model.Questions;
import ru.frank.service.QuestionAndAnswerService;

@Service
public class QuestionAnswerGenerator {

	@Autowired
	private QuestionsDao questionsDao;

	@Autowired
	private QuestionAndAnswerService questionAndAnswerService;

	/**
	 * Генерирует случайное число от 1 до Maximum ID из БД
	 *
	 * @return (long) [1 ; max ID]
	 */
	private long getRandomNumber() {
		return (long) (Math.random() * questionAndAnswerService.getMaximumId() + 1);
	}

	/**
	 * Метод для получения случайной записи класса QuestionAndAnswer из таблицы БД
	 *
	 * @return QuestionAndAnswer object
	 */
	public Questions getRandomQuestionAndAnswer() {
		Questions questions = null;
		while (questions == null) {
			questions = this.questionsDao.getQuestionById(getRandomNumber());
		}
		return questions;
	}

	/**
	 * Метод получает случайный объект класса QuestionAndAnswer с помощью
	 * метода getRandomQuestionAndAnswer() и формирует из полей объекта QuestionAndAnswer
	 * строку содержащую вопрос и ответ разделенные символом '|'.
	 * Например: "В каком году началась Первая мировая война?|1914"
	 *
	 * @return String вопрос и ответ разделенные символом '|'.
	 */

}
