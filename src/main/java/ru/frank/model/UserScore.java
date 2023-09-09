package ru.frank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_score")
public class UserScore {

    @Id
    @Column(name = "id")
    private Long id;

    @Getter
    @Column
    private String userName;

    @Column(name = "score")
    private long score;

    @Override
    public String toString() {
        return "UserScore{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", score=" + score +
                '}';
    }

    public UserScore() {
    }

    public UserScore(Long id, String userName, long score) {
        this.id = id;
        this.userName = userName;
        this.score = score;
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

    public void setScore(long score) {
        this.score = score;
    }
}
