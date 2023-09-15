package ru.frank.model;

import jakarta.persistence.*;


@Entity
@Table(name = "sessions")
public class UserSession {

	@Id
	@Column(name = "chat_id")
	private Long id;

	@Column(name = "startTime")
	private String startTime;

	@Column(name = "amountInit")
	private int amountInit;

	public UserSession(Long chat_id, String startTime, int amountInit) {
		this.id = chat_id;
		this.startTime = startTime;
		this.amountInit = amountInit;
	}

	public UserSession(Long chat_id, String startTime) {
		this.id = chat_id;
		this.startTime = startTime;
	}

	public UserSession() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long chat_id) {
		this.id = chat_id;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public int getAmountInit() {
		return amountInit;
	}

	public void setAmountInit(int amountInit) {
		this.amountInit = amountInit;
	}


	@Override
	public String toString() {
		return "UserSession{" +
				"chat_id=" + id +
				", startTime='" + startTime + '\'' +
				", amountInit=" + amountInit +
				'}';
	}
}
