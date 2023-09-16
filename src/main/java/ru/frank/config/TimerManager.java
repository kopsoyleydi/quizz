package ru.frank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.frank.bot.botUtils.UserSessionHandler;
import ru.frank.service.BackgroundTimer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TimerManager {

	@Autowired
	UserSessionHandler userSessionHandler;

	private final Map<Long, BackgroundTimer> activeTimers = new ConcurrentHashMap<>();

	public void startTimer(Long chatId) {
		if (userSessionHandler.sessionIsActive(chatId)) {
			if (!activeTimers.containsKey(chatId)) {
				BackgroundTimer timer = new BackgroundTimer();
				CompletableFuture<Void> future = CompletableFuture.runAsync(timer::start);
				timer.setStopFuture(future);
				activeTimers.put(chatId, timer);
			} else {
				System.out.println("Таймер для chatId " + chatId + " уже активен!");
			}
		} else {
			// Сессия не найдена в базе данных
			System.out.println("Сессия для chatId " + chatId + " не существует!");
		}
	}

	public void stopTimer(Long chatId) {
		BackgroundTimer timer = activeTimers.get(chatId);
		if (timer != null) {
			timer.stop();
			activeTimers.remove(chatId);
		}
	}

	public CompletableFuture<Long> getCurrentSeconds(Long chatId) {
		BackgroundTimer timer = activeTimers.get(chatId);
		if (timer != null) {
			return timer.getCurrentSeconds();
		} else {
			return CompletableFuture.completedFuture(0L);
		}
	}

	public boolean isTimerActive(Long chatId) {
		return activeTimers.containsKey(chatId);
	}
}
