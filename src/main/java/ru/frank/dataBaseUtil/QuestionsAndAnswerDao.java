package ru.frank.dataBaseUtil;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.frank.model.QuestionAndAnswer;

@Repository
@Transactional
public interface QuestionsAndAnswerDao extends JpaRepository<QuestionAndAnswer, Long> {

	QuestionAndAnswer findByChatId(Long id);

	@Modifying
	void deleteAllByChatId(Long chatId);
}
