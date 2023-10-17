package ru.frank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.frank.dataBaseUtil.UserSessionDao;

@Service
public class AmountService {
	@Autowired
	private UserSessionDao userSessionDao;


	public boolean checkRound(Long chatId){
		int amount = userSessionDao.getUserSessionById(chatId).getAmountInit();
		return amount > 1;
	}
}
