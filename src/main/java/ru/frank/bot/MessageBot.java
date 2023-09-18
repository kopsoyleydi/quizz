package ru.frank.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Autowired
	QuestionAndAnswerService questionAndAnswerService;

	@Value("${bot.name}")
	String botName;
	@Value("${bot.token}")
	String token;

	private final Logger log = LoggerFactory.getLogger(Object.class);

	private volatile boolean running = false;

	public MessageBot() {

	}


	public void startMessageBot(Long chatId){

		if (scheduler.isShutdown()) {
			scheduler = Executors.newScheduledThreadPool(1); // Создаем новый пул потоков
		}

		Runnable messageTask = () -> sendHint(chatId);

		log.info("Starting the message sending scheduler.");

		scheduler.scheduleAtFixedRate(messageTask, 0, 15, TimeUnit.SECONDS);
		running = true;
	}
	private void sendHint(Long chatId) {

		String answer = questionAndAnswerService.getQuestionAndAnswerByChatId(chatId).getAnswer();
		char firstChar = answer.charAt(0);
		char lastChar = answer.charAt(answer.length() - 1);

		StringBuilder resultString = new StringBuilder(firstChar + " " + lastChar);

		int paddingLength = answer.length() - 2;
		for (int i = 0; i < paddingLength; i++) {
			resultString.append(" ");
		}

		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(String.valueOf(resultString));

		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public void stopSendingMessages() {
		if (running) {
			log.info("Stopping the message sending scheduler.");
			scheduler.shutdown();
			running = false;
		} else {
			log.info("Message sending scheduler is not running.");
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
