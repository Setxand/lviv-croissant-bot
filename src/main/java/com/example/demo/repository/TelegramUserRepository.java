package com.example.demo.repository;

import com.example.demo.entity.peopleRegister.TUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TelegramUserRepository extends JpaRepository<TUser, Long> {
	@Query(nativeQuery = true,
			value = "select user_name from tuser where user_name is not NULL order by id desc limit 0, 20")
	public List<String> findTopw();

	public TUser findByChatId(Integer chatId);

	public TUser findByUserName(String userName);


}
