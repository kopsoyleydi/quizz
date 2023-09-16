package ru.frank.dataBaseUtil;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.frank.model.QuestionAndAnswer;

@Repository
@Transactional
public interface QuestionsAndAnswerDao extends JpaRepository<QuestionAndAnswer, Long> {
	QuestionAndAnswer findByChatId(Long id);

	void deleteByChatId(Long chatId);
}
