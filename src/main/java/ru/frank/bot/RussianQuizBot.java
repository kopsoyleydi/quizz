package ru.frank.bot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.frank.bot.botUtils.QuestionAnswerGenerator;
import ru.frank.bot.botUtils.UserScoreHandler;
import ru.frank.bot.botUtils.UserSessionHandler;
import ru.frank.model.UserScore;
import ru.frank.service.BackgroundTimer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RussianQuizBot extends TelegramLongPollingBot {
	private final String BOT_USER_NAME = "QuizzBeksBot";
	private final String TOKEN = "6574685806:AAE5S_6snNhj0vUq818y660YyfbypzEnHU8";

	private final Logger log = LoggerFactory.getLogger(Object.class);

	static final String ERROR_TEXT = "Error occurred:  ";

	@Autowired
	QuestionAnswerGenerator questionAnswerGenerator;

	@Autowired
	UserSessionHandler userSessionHandler;

	@Autowired
	UserScoreHandler userScoreHandler;

	@Autowired
	BackgroundTimer backgroundTimer;

	// TODO Сделать красиво (это уродливо), рефакторнуть на разные методы.
	// TODO Меньше вложенных if if if.
	@Override
	public void onUpdateReceived(Update update) {


		Message message = null;

		//Текст сообщения от пользователя
		String userMessageText = null;
		String userName = null;

		Long userId = null;
		Long chatId = null;

		// Get text message from user.
		if (update.hasMessage()) {
			// Answer for empty user's message.
			if (!update.getMessage().hasText() & !update.hasCallbackQuery()) {
				executeSendMainMenu(update.getMessage().getChatId());
				return;
			}
			message = update.getMessage();
			userId = message.getFrom().getId();
			userName = message.getFrom().getUserName();
			chatId = message.getChatId();
			userMessageText = message.getText().toLowerCase();
//            // TODO Заменить на отдельный метод
//            sendMessage.setChatId(chatId);

			if (!userSessionHandler.sessionIsActive(userId)) {
				executeSendMainMenu(chatId);
			}
		}

		// Get pressed button from user.
		if (update.hasCallbackQuery()) {
			message = update.getCallbackQuery().getMessage();
			userMessageText = update.getCallbackQuery().getData();
			chatId = message.getChatId();
			userId = update.getCallbackQuery().getFrom().getId();
		}

		// Сессия с написавшем пользователем не активна (нет заданного вопроса викторины).
		if (userMessageText != null) {

			if (userMessageText.contains("/help")) {
				executeSendTextMessage(chatId, "Для начала новой выкторины пришлите мне /go. " +
						"Для ответа на один вопрос викторины отведено 20 секунд, " +
						"по истечению этого времени, ответ не засчитывается. " +
						"За правильный ответ засчитывается 1 балл. " +
						"Для просмотра своего счета пришлите /score.");
			}

			if (userMessageText.contains("/score")) {

				// Проверяем наличие текущего пользователя в таблице БД "score",
				if (userScoreHandler.userAlreadyInChart(userId)) {
					executeSendTextMessage(chatId, "Ваш счет: " + String.valueOf(userScoreHandler.getUserScoreById(userId)));
				} else {
					executeSendTextMessage(chatId, "Запись во вашему счету отсутствует, " +
							"вероятно вы еще не играли в викторину. " +
							"Для начала пришлите /go.");
				}
			}
			if (userMessageText.contains("/top10")) {
				List<UserScore> topUsersScoreList = userScoreHandler.getTopFiveUserScore();
				String topUsersScoreString = topUsersScoreList.stream()
						.map(UserScore::getUserName)
						.collect(Collectors.joining(System.lineSeparator()));
				executeSendTextMessage(chatId, topUsersScoreString);
				executeSendTextMessage(chatId, topUsersScoreString);
			}

			// Начало новой викторины.
			if (userMessageText.contains("/go")) {

				// Получаем новый вопрос + ответ из генератора в виде одной строки.
				String questionAndAnswer = questionAnswerGenerator.getNewQuestionAndAnswerForUser();

				String[] questionAndAnswerArray = questionAndAnswer.split("\\|");
				String question = questionAndAnswerArray[0];

				// Создаем сессию с текущим пользователем
				userSessionHandler.createUserSession(chatId, questionAndAnswer);

				// Проверяем наличие текущего пользователя в таблице БД "score",
				// при отсутствии - добавляем пользователя в таблицу со счетом 0.
				if (!userScoreHandler.userAlreadyInChart(userId)) {
					userScoreHandler.addNewUserInChart(userId, userName);
				}

				executeSendTextMessage(chatId, question);

				backgroundTimer.start();


				//I don't think you need to do anything for your particular problem

				////sendMessage(message, question);
				// Отвечаем пользователю, если сообщение не содержит явных указаний для бота (default bot's answer)
			}
			if (userSessionHandler.sessionIsActive(chatId) && userMessageText != null) {

				LocalDateTime currentDate = LocalDateTime.now();

				if (userSessionHandler.validateDate(currentDate, chatId)) {

					String rightAnswer = userSessionHandler.getAnswerFromSession(chatId);

					if (rightAnswer.contains(userMessageText)) {
						if (backgroundTimer.getCurrentSeconds() >= 0 && backgroundTimer.getCurrentSeconds() < 15) {
							userScoreHandler.incrementUserScore(userId, 3);
							executeSendTextMessage(chatId, userName + "3");
							backgroundTimer.stop();
							userSessionHandler.deleteUserSession(chatId);
						}
						if (backgroundTimer.getCurrentSeconds() >= 15 && backgroundTimer.getCurrentSeconds() < 45) {
							userScoreHandler.incrementUserScore(userId, 2);
							executeSendTextMessage(chatId, userName + "2");
							backgroundTimer.stop();
							userSessionHandler.deleteUserSession(chatId);
						}
					} else {
						executeSendTextMessage(chatId, "Неправильный ответ");
					}
				} else {
					executeSendTextMessage(chatId, "Время на ответ вышло.");
//                sendMessage(message, "Время на ответ вышло.");
					userSessionHandler.deleteUserSession(userId);
				}
			}

		}
		//else{
		//    executeSendMainMenu(chatId);
		//}

	}

	private void executeSendMainMenu(Long chatId) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText("Выбери команду");
		sendMessage.setReplyMarkup(getMainBotMarkup());
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void executeSendTextMessage(Long chatId, String text) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(text);
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method builds main bot menu buttons that contains basic bot commands.
	 *
	 * @return InlineKeyboardMarkup object with build menu.
	 */
	private ReplyKeyboard getMainBotMarkup() {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>(); // Инициализируйте список
		keyboardMarkup.setKeyboard(keyboard); // Установите список в клавиатуре
		KeyboardRow row1 = new KeyboardRow();
		row1.add(new KeyboardButton("/go"));
		row1.add(new KeyboardButton("/score"));
		row1.add(new KeyboardButton("/top10"));
		row1.add(new KeyboardButton("/help"));

		keyboard.add(row1);

		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
	}

	/**
	 * Send text constructed by Bot to user who's asking.
	 *
	 * @param
	 * @param
	 */
	@Deprecated
	private void sendMessage(long chatId, String textToSend) {
		SendMessage message = new SendMessage();
		message.setChatId(String.valueOf(chatId));
		message.setText(textToSend);
		executeMessage(message);
	}

	private void executeMessage(SendMessage message) {
		try {
			execute(message);
		} catch (TelegramApiException e) {
			log.error(ERROR_TEXT + e.getMessage());
		}
	}


	@Override
	public String getBotUsername() {
		return this.BOT_USER_NAME;
	}

	@Override
	public String getBotToken() {
		return this.TOKEN;
	}
}
