package ru.frank.dataBaseUtil.userScore;

import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.frank.model.UserScore;

import java.util.List;

@Repository
@Transactional
public interface UserScoreDao extends JpaRepository<UserScore, Long> {
    UserScore getAllById(Long id);

}
