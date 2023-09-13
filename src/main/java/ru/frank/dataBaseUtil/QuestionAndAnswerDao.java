package ru.frank.dataBaseUtil;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.frank.model.QuestionAndAnswer;


@Repository
@Transactional
public interface QuestionAndAnswerDao extends JpaRepository<QuestionAndAnswer, Long> {
	QuestionAndAnswer getQuestionById(Long id);

}
