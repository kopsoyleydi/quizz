package ru.frank.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Async
public class BackgroundTimer {
	private long startTime;
	private boolean isRunning;
	private boolean stopRequested;

	private CompletableFuture<Void> stopFuture;

	public void setStopFuture(CompletableFuture<Void> stopFuture) {
		this.stopFuture = stopFuture;
	}

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

		stop();
		if (stopFuture != null) {
			stopFuture.complete(null);
		}
	}

	public void stop() {
		stopRequested = true;
	}



	public CompletableFuture<Long> getCurrentSeconds() {
		if (isRunning) {
			long elapsedTime = System.currentTimeMillis() - startTime;
			return CompletableFuture.completedFuture(elapsedTime / 1000);
		} else {
			return CompletableFuture.completedFuture(0L);
		}
	}
}
