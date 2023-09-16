package ru.frank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.config.TimerManager;

import java.util.concurrent.CompletableFuture;

@Service
public class TimerService {
	@Autowired
	TimerManager timerManager;

	public void startTimer(Long chatId) {
		timerManager.startTimer(chatId);
	}

	public void stopTimer(Long chatId) {
		timerManager.stopTimer(chatId);
	}

	public CompletableFuture<Long> getCurrentSeconds(Long chatId) {
		return timerManager.getCurrentSeconds(chatId);
	}
}
