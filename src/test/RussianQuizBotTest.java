import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.frank.bot.RussianQuizBot;
import ru.frank.bot.botUtils.UserScoreHandler;
import ru.frank.bot.botUtils.UserSessionHandler;
import ru.frank.service.*;


@RunWith(MockitoJUnitRunner.class)
public class RussianQuizBotTest {

	@Mock
	private UserSessionHandler userSessionHandler;

	@Mock
	private UserScoreHandler userScoreHandler;

	@Mock
	private TimerService timerService;

	@Mock
	private SessionService sessionService;

	@Mock
	private AmountService amountService;

	@Mock
	private QuestionService questionService;

	@Mock
	private QuestionAndAnswerService questionAndAnswerService;

	private RussianQuizBot russianQuizBot;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		russianQuizBot = new RussianQuizBot();
		russianQuizBot.userSessionHandler = userSessionHandler;
		russianQuizBot.userScoreHandler = userScoreHandler;
		russianQuizBot.timerService = timerService;
		russianQuizBot.sessionService = sessionService;
		russianQuizBot.amountService = amountService;
		russianQuizBot.questionService = questionService;
		russianQuizBot.questionAndAnswerService = questionAndAnswerService;
	}

	@Test
	public void testOnUpdateReceived_HelpCommand() throws TelegramApiException {

		Update update = createUpdateWithText("/help");

		russianQuizBot.onUpdateReceived(update);

	}


	private Update createUpdateWithText(String text) {
		Update update = new Update();
		Message message = new Message();
		message.setText(text);
		update.setMessage(message);
		return update;
	}
}
