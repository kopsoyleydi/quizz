package ru.frank.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "user_score")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserScore {

    @Id
    @Column(name = "id")
    private long id;

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
}
