package ru.frank.dataBaseUtil.userScore;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.frank.model.UserScore;

import java.util.List;



@Repository
@Transactional
public interface UserScoreDao extends JpaRepository<UserScore, Long> {
	UserScore findByChatId(Long id);

	@Query("SELECT s from UserScore s where s.chatId = :id order by s.score desc LIMIT 3")
	List<UserScore> findAllUserInCurrentChat(Long id);

	@Query("select s from UserScore s where s.chatId = :chatId and s.userId = :userId")
	UserScore findByChatIdAndUserId(long userId, long chatId);

	@Modifying
	void deleteByChatId(long chatId);

}
