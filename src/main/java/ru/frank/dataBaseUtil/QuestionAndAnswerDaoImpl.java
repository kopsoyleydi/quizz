package ru.frank.dataBaseUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.frank.model.QuestionAndAnswer;


import java.util.List;

@Repository
@Transactional
public class QuestionAndAnswerDaoImpl implements QuestionAndAnswerDao{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public QuestionAndAnswer get(long id) {
        return entityManager.find(QuestionAndAnswer.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<QuestionAndAnswer> getAll() {
        return entityManager.createQuery("from QuestionAndAnswer ").getResultList();
    }

    @Override
    public long getRowCount() {
        return getAll().size();
    }

    /**
     * Метод возвращает максимальное значение ID из таблицы.
     * Таблица должна быть отсортированна по колонке ID.
     * @return long;
     */
    @Override
    public long getMaximumId() {
        List<QuestionAndAnswer> questionAndAnswersList = getAll();
        return questionAndAnswersList.get(questionAndAnswersList.size() - 1).getId();
    }
}
