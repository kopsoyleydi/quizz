package ru.frank.dataBaseUtil.userScore;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;
import ru.frank.model.UserScore;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface UserScoreDao extends JpaRepository<UserScore, Long> {
	Optional<UserScore> findByChatId(Long id);

	@Query("SELECT s from UserScore s where s.chatId = :id order by s.score desc LIMIT 3")
	List<UserScore> findAllUserInCurrentChat(Long id);
}
