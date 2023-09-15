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

	private static LocalDateTime localDateTime;

	private static boolean checkGo = false;

	private static int amount = 0;
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

		String userMessageText = null;
		String userName = null;

		Long userId = null;
		Long chatId = null;

		if (update.hasMessage()) {
			if (!update.getMessage().hasText() & !update.hasCallbackQuery()) {
				executeSendMainMenu(update.getMessage().getChatId());
				return;
			}
			message = update.getMessage();
			userId = message.getFrom().getId();
			userName = message.getFrom().getUserName();
			chatId = message.getChatId();
			userMessageText = message.getText().toLowerCase();
		}
		if (update.hasCallbackQuery()) {
			message = update.getCallbackQuery().getMessage();
			userMessageText = update.getCallbackQuery().getData();
			chatId = message.getChatId();
			userId = update.getCallbackQuery().getFrom().getId();
		}
		if(userMessageText!=null){

			if (userMessageText.contains("/help")) {
				executeSendTextMessage(chatId, "Для начала новой викторины пришлите мне /go. " +
						"Для всего викторины без зависимости количество раундов будет дано 6 минут секунд, " +
						"по истечению этого времени, ответ не засчитывается. " +
						"За правильный ответ засчитывается 1 балл. " +
						"Для просмотра своего счета пришлите /score.");
			}

			if (userMessageText.contains("/top5")) {
				List<UserScore> topUsersScoreList = userScoreHandler.getTopFiveUserScore();
				String topUsersScoreString = topUsersScoreList.stream()
						.map(UserScore::getUserName)
						.collect(Collectors.joining(System.lineSeparator()));
				executeSendTextMessage(chatId, topUsersScoreString);
				executeSendTextMessage(chatId, topUsersScoreString);
			}

			if (userMessageText.contains("/go")) {

				if (!userScoreHandler.userAlreadyInChart(userId)) {
					userScoreHandler.addNewUserInChart(chatId, userName);
				}
				executeSelectMenu(chatId);

				backgroundTimer.start();

				userSessionHandler.createUserSession(chatId);
			}
			else if (userSessionHandler.sessionIsActive(chatId)){
				if(userSessionHandler.checkAmount(chatId)){
					String questionAndAnswer = questionAnswerGenerator.getNewQuestionAndAnswerForUser();

					String[] questionAndAnswerArray = questionAndAnswer.split("\\|");
					String question = questionAndAnswerArray[0];

					executeSendTextMessage(chatId, question);

					String rightAnswer = questionAndAnswerArray[1];

					if(rightAnswer.contains(userMessageText)){
						userScoreHandler.incrementUserScore(userId, 3);
						executeSendTextMessage(chatId, "true " + userName);
						userSessionHandler.minusAmountIter(chatId);
					}
				}
				else {
					executeSendTextMessage(chatId, "Введите количество раундов");
				}
			}
			else if(userSessionHandler.checkActiveAmount(chatId)){
				backgroundTimer.stop();
				userSessionHandler.deleteUserSession(chatId);
			}
			if(userMessageText.contains("/5")){
				amount = 5;
				userSessionHandler.setAmountInit(chatId, amount);
			}
			if(userMessageText.contains("/10")){
				amount = 10;
				userSessionHandler.setAmountInit(chatId, amount);
			}
			if(userMessageText.contains("/15")){
				amount = 15;
				userSessionHandler.setAmountInit(chatId, amount);
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

	private ReplyKeyboard getMainBotMarkup() {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>(); // Инициализируйте список
		keyboardMarkup.setKeyboard(keyboard); // Установите список в клавиатуре
		KeyboardRow row1 = new KeyboardRow();
		KeyboardRow row2 = new KeyboardRow();

		row1.add(new KeyboardButton("/go"));
		row1.add(new KeyboardButton("/score"));
		row2.add(new KeyboardButton("/top5"));
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
