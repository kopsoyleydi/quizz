package ru.frank.dataBaseUtil;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.frank.model.Questions;


@Repository
@Transactional
public interface QuestionsDao extends JpaRepository<Questions, Long> {
	 Questions getQuestionById(Long id);

}
