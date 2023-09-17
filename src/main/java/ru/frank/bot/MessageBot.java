package ru.frank.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.frank.config.TimerManager;
import ru.frank.service.QuestionAndAnswerService;

import java.util.concurrent.*;

@Component
public class MessageBot extends TelegramLongPollingBot {

	@Autowired
	TimerManager timerManager;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private Runnable messageTask;

	@Autowired
	QuestionAndAnswerService questionAndAnswerService;

	@Value("${bot.name}")
	String botName;
	@Value("${bot.token}")
	String token;

	public MessageBot() {
	}

	public MessageBot(Long chatId) {

		messageTask = () -> sendHint(chatId);

		scheduler.scheduleAtFixedRate(messageTask, 0, 15, TimeUnit.SECONDS);
	}

	private void sendHint(Long chatId) {

		String answer = questionAndAnswerService.getQuestionAndAnswerByChatId(chatId).getAnswer();
		char firstChar = answer.charAt(0);
		char lastChar = answer.charAt(answer.length() - 1);

		StringBuilder resultString = new StringBuilder(firstChar + " " + lastChar);

		int paddingLength = answer.length() - 2; // Вычисляем сколько пространства нужно добавить
		for (int i = 0; i < paddingLength; i++) {
			resultString.append(" ");
		}

		SendMessage message = new SendMessage();
		message.setChatId(chatId); // Укажите ID чата, в котором вы хотите отправлять подсказки
		message.setText(String.valueOf(resultString));

		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public void stopSendingMessages() {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
	}

	@Override
	public void onUpdateReceived(Update update) {

	}

	@Override
	public String getBotUsername() {
		return null;
	}

	@Override
	public String getBotToken() {
		return token;
	}
}
