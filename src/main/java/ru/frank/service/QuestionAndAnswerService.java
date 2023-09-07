package ru.frank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.frank.dataBaseUtil.QuestionAndAnswerDao;
import ru.frank.model.QuestionAndAnswer;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionAndAnswerService {

    private final QuestionAndAnswerDao questionAndAnswerDao;

    public Long getMaximumId() {
        List<QuestionAndAnswer> questionAndAnswersList = questionAndAnswerDao.findAll();
        return questionAndAnswersList.get(questionAndAnswersList.size() - 1).getId();
    }
}
