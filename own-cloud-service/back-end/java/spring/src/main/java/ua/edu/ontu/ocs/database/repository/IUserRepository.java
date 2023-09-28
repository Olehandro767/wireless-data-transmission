package ua.edu.ontu.ocs.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ua.edu.ontu.ocs.database.model.UserModel;

public interface IUserRepository extends JpaRepository<UserModel, Long> {
}
