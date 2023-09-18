package ru.frank.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

	@Autowired
	private RussianQuizBot bot;

	@Autowired
	private MessageBot messageBot;

	@EventListener({ContextRefreshedEvent.class})
	public void init() throws TelegramApiException {
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		try {
			telegramBotsApi.registerBot(bot);
			telegramBotsApi.registerBot(messageBot);
		} catch (TelegramApiException e) {
			e.getStackTrace();
		}
	}
}
