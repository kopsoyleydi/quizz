package ru.frank.bot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	private final Logger log = LoggerFactory.getLogger(Object.class);

	static final String ERROR_TEXT = "Error occurred:  ";

	private static int amountIter = 0;

	private static String rightAnswer;

	@Value("${bot.name}")
	String botName;
	@Value("${bot.token}")
	String token;

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

			if (userMessageText.contains("/go")) {

				if (!userScoreHandler.userAlreadyInChart(userId)) {
					userScoreHandler.addNewUserInChart(userId, userName);
				}

				userSessionHandler.createUserSession(chatId);

				executeSelectMenu(chatId);

				backgroundTimer.start();

			}
		}
		if(userSessionHandler.sessionIsActive(chatId)){
			executeSelectMenu(chatId);
		}

		if(userMessageText.equals("/5")){
			amountIter = 5;
		}
		else if(userMessageText.equals("/10")){
			amountIter = 10;
		}
		else if(userMessageText.equals("/15")){
			amountIter = 15;
		}
		else {
			executeSendTextMessage(chatId, "выберите количество или перезагрузите игру");
		}

		if((amountIter == 5 || amountIter == 10 || amountIter == 15) && userSessionHandler.sessionIsActive(chatId)){

			int iter = 0;

			while (amountIter != iter){

				String questionAndAnswer = questionAnswerGenerator.getNewQuestionAndAnswerForUser();

				String[] questionAndAnswerArray = questionAndAnswer.split("\\|");
				String question = questionAndAnswerArray[0];

				executeSendTextMessage(chatId, question);

				rightAnswer = questionAndAnswerArray[1];

				if (update.hasCallbackQuery()) {
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
				}

				executeSendTextMessage(chatId, rightAnswer + " true");

				LocalDateTime currentDate = LocalDateTime.now();

				if (userSessionHandler.validateDate(currentDate, chatId)) {


					if (rightAnswer.contains(userMessageText)) {

						if (backgroundTimer.getCurrentSeconds() >= 0 && backgroundTimer.getCurrentSeconds() < 15) {
							userScoreHandler.incrementUserScore(userId, 3);
							executeSendTextMessage(chatId, userName + " 3");
							iter++;
							backgroundTimer.stop();
							userSessionHandler.deleteUserSession(chatId);
						}
						if (backgroundTimer.getCurrentSeconds() >= 15 && backgroundTimer.getCurrentSeconds() < 45) {
							userScoreHandler.incrementUserScore(userId, 2);
							executeSendTextMessage(chatId, userName + " 2");
							iter++;
							backgroundTimer.stop();
							userSessionHandler.deleteUserSession(chatId);
						}
						if (backgroundTimer.getCurrentSeconds() >= 1 && backgroundTimer.getCurrentSeconds() < 15) {
							userScoreHandler.incrementUserScore(userId, 1);
							executeSendTextMessage(chatId, userName + " 1");
							iter++;
							backgroundTimer.stop();
							userSessionHandler.deleteUserSession(chatId);
						}
					} else {
						executeSendTextMessage(chatId, "Неправильный ответ");
					}
				} else {
					userSessionHandler.deleteUserSession(userId);
					executeSendTextMessage(chatId, "Время на ответ вышло.");
				}
			}
		}
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

	private void executeSelectMenu(Long chatId) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText("Выберите раунды");
		sendMessage.setReplyMarkup(getSelectMenu());
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
		KeyboardRow row2 = new KeyboardRow();

		row1.add(new KeyboardButton("/go"));
		row1.add(new KeyboardButton("/score"));
		row2.add(new KeyboardButton("/top10"));
		row2.add(new KeyboardButton("/help"));

		keyboard.add(row1);
		keyboard.add(row2);

		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
	}

	private ReplyKeyboard getSelectMenu() {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>(); // Инициализируйте список
		keyboardMarkup.setKeyboard(keyboard); // Установите список в клавиатуре
		KeyboardRow row1 = new KeyboardRow();
		KeyboardRow row2 = new KeyboardRow();

		row1.add(new KeyboardButton("/5"));
		row1.add(new KeyboardButton("/10"));
		row2.add(new KeyboardButton("/15"));

		keyboard.add(row1);
		keyboard.add(row2);

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
		return botName;
	}

	@Override
	public String getBotToken() {
		return token;
	}
}
