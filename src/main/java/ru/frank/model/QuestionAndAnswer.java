package ru.frank.model;

import jakarta.persistence.*;

@Entity
public class QuestionAndAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String question;

	private String answer;

	@Column(name = "chat_id")
	private Long chatId;

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public QuestionAndAnswer(String question, String answer, Long chatId) {
		this.question = question;
		this.answer = answer;
		this.chatId = chatId;
	}

	public QuestionAndAnswer() {
	}


	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
