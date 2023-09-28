package ru.frank.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

	@Autowired
	private RussianQuizBot bot;

	@Autowired
	private MessageBot messageBot;

	@Autowired
	private TestBot testBot;

	private static SetWebhook setWebhook;

	@EventListener({ContextRefreshedEvent.class})
	public void init() throws TelegramApiException {
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		try {
			telegramBotsApi.registerBot(bot);
			telegramBotsApi.registerBot(messageBot);
			telegramBotsApi.registerBot(testBot, setWebhook);
		} catch (TelegramApiException e) {
			e.getStackTrace();
		}
	}
}
