package ru.frank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "questions")
@NoArgsConstructor
@AllArgsConstructor
public class Questions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chat_id")
	private long id;


	@Column(name = "question")
	private String question;

	@Column(name = "answer")
	private String answer;

	public long getChat_id() {
		return id;
	}

	public void setChat_id(long chat_id) {
		this.id = chat_id;
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



	@Override
	public String toString() {
		return "QuestionAndAnswer{" +
				"chat_id=" + id +
				", question='" + question + '\'' +
				", answer='" + answer + '\'' +
				'}';
	}
}
