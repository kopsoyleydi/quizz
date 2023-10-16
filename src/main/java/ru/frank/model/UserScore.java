package ru.frank.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_score")
public class UserScore {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column
    private String userName;

    @Column(name = "score")
    private long score;

    public Long getId() {
        return chatId;
    }

    public void setId(Long id) {
        this.chatId = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public UserScore(Long id, String userName, long score) {
        this.chatId = id;
        this.userName = userName;
        this.score = score;
    }

    public UserScore() {
    }
}
