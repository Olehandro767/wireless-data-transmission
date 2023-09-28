package ua.edu.ontu.ocs.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ua.edu.ontu.ocs.database.model.UserModel;
import ua.edu.ontu.ocs.database.repository.IUserRepository;

@Service
@RequiredArgsConstructor
public class UserDao implements IDaoJpa<UserModel> {

	private final IUserRepository userRepository;
	
	@Override
	public JpaRepository<UserModel, Long> getRepository() {
		return this.userRepository;
	}
}
