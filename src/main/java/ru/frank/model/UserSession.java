package ru.frank.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "sessions")
public class UserSession {



    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "startTime")
    private String startTime;

    @Column(name = "question")
    private String question;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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
    public UserSession(Long id, String startTime, String question, String answer) {
        this.id = id;
        this.startTime = startTime;
        this.question = question;
        this.answer = answer;
    }

    public UserSession() {
    }

    @Column(name = "answer")
    private String answer;


    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", startTime='" + startTime + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSession that = (UserSession) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (question != null ? !question.equals(that.question) : that.question != null) return false;
        return answer != null ? answer.equals(that.answer) : that.answer == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (question != null ? question.hashCode() : 0);
        result = 31 * result + (answer != null ? answer.hashCode() : 0);
        return result;
    }
}
