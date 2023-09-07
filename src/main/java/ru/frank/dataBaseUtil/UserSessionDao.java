package ru.frank.dataBaseUtil;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.frank.model.UserSession;

import java.util.List;

@Repository
@Transactional
public interface UserSessionDao extends JpaRepository<UserSession,  Long> {
    UserSession getUserById(Long id);

}
