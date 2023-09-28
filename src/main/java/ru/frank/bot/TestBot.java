package ru.frank.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.WebhookBot;

@Component
@Deprecated
public class TestBot implements WebhookBot {

	private static SetWebhook setWebhook;

	@Value("${bot.name}")
	String botName;
	@Value("${bot.token}")
	String token;

	@Override
	public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
		setWebhook.getSecretToken();
		return null;
	}

	@Override
	public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {

	}

	@Override
	public String getBotPath() {
		return null;
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
