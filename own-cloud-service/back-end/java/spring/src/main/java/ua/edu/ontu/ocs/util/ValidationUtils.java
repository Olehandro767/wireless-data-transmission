package ua.edu.ontu.ocs.util;

public class ValidationUtils {

	private ValidationUtils() {
		// redundant
	}

	public static boolean entityIsNotNull(Object entity) {
		return entity != null;
	}

	public static boolean stringIsNotBlank(String data) {
		return entityIsNotNull(data) && !data.isBlank();
	}

	public static boolean isDiferend(Object value1, Object value2) {
		return (entityIsNotNull(value1) && !value1.equals(value2))
				|| (entityIsNotNull(value2) && !value2.equals(value1));
	}
}
