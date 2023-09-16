package ru.frank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.dataBaseUtil.UserSessionDao;
import ru.frank.model.UserSession;

@Service
public class SessionService {

	@Autowired
	private UserSessionDao userSessionDao;

	public boolean checkSession(Long chatID){
		UserSession userSession = userSessionDao.getUserSessionById(chatID);
		if (userSession!=null){
			return true;
		}
		return false;
	}

	public void plusAmount(Long chatId, int amount){
		UserSession userSession = userSessionDao.getUserSessionById(chatId);
		userSession.setAmountInit(amount);
		userSessionDao.save(userSession);
	}
}
