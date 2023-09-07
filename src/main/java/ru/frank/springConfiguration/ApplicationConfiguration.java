package ru.frank.springConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.TelegramBotsApi;
import ru.frank.bot.botUtils.QuestionAnswerGenerator;
import ru.frank.bot.botUtils.UserScoreHandler;
import ru.frank.bot.botUtils.UserSessionHandler;
import ru.frank.dataBaseUtil.QuestionAndAnswerDao;
import ru.frank.dataBaseUtil.UserSessionDao;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public UserSessionHandler userSessionHandler(){
        return new UserSessionHandler();
    }

    @Bean
    public QuestionAnswerGenerator questionAnswerGenerator(){
        return new QuestionAnswerGenerator();
    }

    @Bean
    public UserScoreHandler userScoreHandler(){
        return new UserScoreHandler();
    }



    @Bean
    public TelegramBotsApi telegramBotsApi(){
        return new TelegramBotsApi();
    }
}
