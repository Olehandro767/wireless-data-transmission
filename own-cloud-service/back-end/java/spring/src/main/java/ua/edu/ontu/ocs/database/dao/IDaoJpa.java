package ua.edu.ontu.ocs.database.dao;

import static ua.edu.ontu.ocs.util.ValidationUtils.isDiferend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IDaoJpa<T> {

	JpaRepository<T, Long> getRepository();

	default boolean canBeUpdated(T newEntity, T oldEntity) {
		return isDiferend(newEntity, oldEntity);
	}

	default T save(T entity) {
		return this.getRepository().save(entity);
	}

	default void delete(long id) {
		this.getRepository().deleteById(id);
	}
}
