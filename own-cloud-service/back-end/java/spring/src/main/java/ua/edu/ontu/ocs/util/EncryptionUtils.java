package ua.edu.ontu.ocs.util;

public class EncryptionUtils {
	
	private EncryptionUtils() {
		// redundant
	}
	
	public static String ADMIN_PRIVATE_KEY;
	public static String ADMIN_INIT_VECTOR;
	
	public static String encryptData(String data, String privateKey, byte[] initVector) {
		return null; // TODO
	}
	
	public static String decryptData(String encryptedData, String privateKey, byte[] initVector) {
		return null; // TODO
	}
}
