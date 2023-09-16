package ru.frank.config;

import org.springframework.stereotype.Component;
import ru.frank.service.BackgroundTimer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TimerManager {

	private final Map<Long, BackgroundTimer> activeTimers = new ConcurrentHashMap<>();

	public void startTimer(Long chatId) {
		BackgroundTimer timer = new BackgroundTimer();
		CompletableFuture<Void> future = CompletableFuture.runAsync(timer::start);
		timer.setStopFuture(future);
		activeTimers.put(chatId, timer);
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
}
