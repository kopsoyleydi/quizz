package ru.frank.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_score")
public class UserScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    @Column
    private String userName;

    @Column(name = "score")
    private long score;

    @Column(name = "user_id")
    private Long userId;

    public UserScore() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public UserScore(Long chatId, String userName, long score, Long userId) {
        this.chatId = chatId;
        this.userName = userName;
        this.score = score;
        this.userId = userId;
    }
}
