package ru.frank.dataBaseUtil;

import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.frank.model.UserSession;


@Repository
@Transactional
public interface UserSessionDao extends JpaRepository<UserSession, Long> {
	UserSession getUserSessionById(Long id);

	@Modifying
	@Query("delete from UserSession p where p.id = :chatId")
	void deleteAllByChatId(long chatId);
}
