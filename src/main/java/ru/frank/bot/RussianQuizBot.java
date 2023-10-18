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
import ru.frank.bot.botUtils.UserScoreHandler;
import ru.frank.bot.botUtils.UserSessionHandler;
import ru.frank.model.UserScore;
import ru.frank.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class RussianQuizBot extends TelegramLongPollingBot {

	private final Logger log = LoggerFactory.getLogger(Object.class);

	static final String ERROR_TEXT = "Error occurred:  ";


	@Value("${bot.name}")
	String botName;
	@Value("${bot.token}")
	String token;

	@Autowired
	public
	UserSessionHandler userSessionHandler;

	@Autowired
	public
	UserScoreHandler userScoreHandler;

	@Autowired
	public
	TimerService timerService;

	@Autowired
	public
	SessionService sessionService;

	@Autowired
	public
	AmountService amountService;

	@Autowired
	public
	QuestionService questionService;

	@Autowired
	public
	QuestionAndAnswerService questionAndAnswerService;

	@Autowired
	QuizzMethods quizzMethods;

	@Autowired
	UserService userService;


	@Override
	public void onUpdateReceived(Update update) {


		Message message = update.getMessage();

		String userMessageText = message.getText().toLowerCase();

		long userId = message.getFrom().getId();
		String userName = message.getFrom().getUserName();
		Long chatId = message.getChatId();

		if(userMessageText!=null){

			if (userMessageText.contains("/help")) {
				executeSendTextMessage(chatId, "Для начала новой викторины пришлите мне /go. " +
						"Для всего викторины без зависимости количество раундов будет дано 6 минут секунд, " +
						"по истечению этого времени, ответ не засчитывается. " +
						"За правильный ответ засчитывается 1 балл. " +
						"Для просмотра своего счета пришлите /score.");
			}

			if(userMessageText.contains("/stop")){
				timerService.stopTimer(chatId);

				executeSendTextMessage(chatId, deleteGameAtTheChat(chatId));
			}

			if(userMessageText.contains("/go")){
				if (!userScoreHandler.userAlreadyInChart(userId, chatId)) {
					userScoreHandler.addNewUserInChart(chatId, userName, userId);
				}

				executeSelectMenu(chatId);

				userSessionHandler.createUserSession(chatId);

				timerService.startTimer(chatId);

			}
			if(userMessageText.contains("/5")){
				userSessionHandler.setAmountInit(chatId, 5);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				executeSendTextMessage(chatId, questionService.getQuestion(chatId) + "?");
			}
			if(userMessageText.contains("/10")){
				userSessionHandler.setAmountInit(chatId, 10);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				executeSendTextMessage(chatId, questionService.getQuestion(chatId)  + "?");
			}
			if(userMessageText.contains("/15")){
				userSessionHandler.setAmountInit(chatId, 15);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				executeSendTextMessage(chatId, questionService.getQuestion(chatId)  + "?");
			}
		}
		if(sessionService.checkSession(chatId)){


			if (amountService.checkRound(chatId)){


				Long seconds = 0L;
				try {
					seconds = timerService.getCurrentSeconds(chatId).get();
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException(e);
				}

				if(questionAndAnswerService.getQuestionAndAnswerByChatId(chatId) != null) {
					if (userMessageText.contains(questionAndAnswerService.getQuestionAndAnswerByChatId(chatId).getAnswer())) {
						timerService.stopTimer(chatId);
						if (seconds >= 0 && seconds <= 15) {
							userScoreHandler.incrementUserScore(userId, 3, chatId);
							userSessionHandler.minusAmountIter(chatId);
							questionAndAnswerService.deleteQuestionByChatID(chatId);
							timerStarting(chatId);
						} else if (seconds >= 16 && seconds <= 40) {
							userScoreHandler.incrementUserScore(userId, 2, chatId);
							userSessionHandler.minusAmountIter(chatId);
							questionAndAnswerService.deleteQuestionByChatID(chatId);
							timerStarting(chatId);
						} else if (seconds >= 41 && seconds <= 59) {
							userScoreHandler.incrementUserScore(userId, 1, chatId);
							questionAndAnswerService.deleteQuestionByChatID(chatId);
							userSessionHandler.minusAmountIter(chatId);
							timerStarting(chatId);
						}
					} else if (!userMessageText.contains(questionAndAnswerService.getQuestionAndAnswerByChatId(chatId).getAnswer())) {
						executeSendTextMessage(chatId, "Неправильный ответ");
					}
				}
			}

		}

	}

	private void timerStarting(Long chatId) {
		timerService.startTimer(chatId);
		executeSendTextMessage(chatId, "Правильный ответ, дальше");
		if(amountService.checkRound(chatId)){
			executeSendTextMessage(chatId, questionService.getQuestion(chatId));
		}
		else {
			executeSendTextMessage(chatId, "Игра окончена");
			timerService.stopTimer(chatId);
			executeSendTextMessage(chatId, sendTopThreeUsersScoreInChat(chatId));
			deleteGameAtTheChat(chatId);
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
		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboardMarkup.setKeyboard(keyboard);
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
		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboardMarkup.setKeyboard(keyboard);
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

	private String sendTopThreeUsersScoreInChat(long chatId){
		List<UserScore> topList = new ArrayList<>(userService.getTopUsersInChat(chatId));
		StringBuilder top = new StringBuilder();
		int numberMembers = 1;
		for(int i = 0;i< topList.size();i++){
			top.append(numberMembers++).append(" ").append("Участник ").append(topList.get(i).getUserName()).append(" с ").append(topList.get(i).getScore()).append("\n");
		}
		return top.toString();
	}
	private void executeMessage(SendMessage message) {
		try {
			execute(message);
		} catch (TelegramApiException e) {
			log.error(ERROR_TEXT + e.getMessage());
		}
	}

	private String deleteGameAtTheChat(long chatId){
		return quizzMethods.deleteGameAtChatID(chatId);
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
