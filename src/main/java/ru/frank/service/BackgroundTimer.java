package ru.frank.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BackgroundTimer {
	private long startTime;
	private boolean isRunning;
	private boolean stopRequested;

	public BackgroundTimer() {
		isRunning = false;
		stopRequested = false;
	}

	@Async
	public void start() {
		startTime = System.currentTimeMillis();
		isRunning = true;
		stopRequested = false;

		while (isRunning && !stopRequested) {
			long elapsedTime = System.currentTimeMillis() - startTime;
			System.out.println("Прошло секунд: " + (elapsedTime / 1000));
			try {
				Thread.sleep(1000); // Подождать 1 секунду
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (elapsedTime >= 60000) { // Остановить после 60 секунд
				isRunning = false;
			}
		}
	}

	public void stop() {
		stopRequested = true;
	}

	public long getCurrentSeconds() {
		if (isRunning) {
			long elapsedTime = System.currentTimeMillis() - startTime;
			return elapsedTime / 1000;
		} else {
			return 0;
		}
	}
}
