package softnecessary.furgonok.utils;


import android.util.Base64;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public final class AESCipher {

  private static final String CIPHER_NAME = "AES/CBC/PKCS5PADDING";
  private static final int CIPHER_KEY_LEN = 16; //128 bits

  /**
   * Encrypt data using AES Cipher (CBC) with 128 bit key
   *
   * @param key  - key to use should be 16 bytes long (128 bits)
   * @param iv   - initialization vector
   * @param data - data to encrypt
   * @return encryptedData data in base64 encoding with iv attached at end after a :
   */
  public static String encrypt(String key, String iv, String data) {
    try {
      if (key.length() < AESCipher.CIPHER_KEY_LEN) {
        int numPad = AESCipher.CIPHER_KEY_LEN - key.length();

        for (int i = 0; i < numPad; i++) {
          key = key.concat("0"); //0 pad to len 16 bytes
        }

      } else if (key.length() > AESCipher.CIPHER_KEY_LEN) {
        key = key.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
      }
      if (iv.length() < AESCipher.CIPHER_KEY_LEN) {
        int numPad = AESCipher.CIPHER_KEY_LEN - iv.length();

        for (int i = 0; i < numPad; i++) {
          iv = iv.concat("0"); //0 pad to len 16 bytes
        }

      } else if (iv.length() > AESCipher.CIPHER_KEY_LEN) {
        iv = iv.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
      }
      IvParameterSpec initVector = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
      SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

      Cipher cipher = Cipher.getInstance(AESCipher.CIPHER_NAME);
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, initVector);

      byte[] encryptedData = cipher.doFinal((data.getBytes()));

      String base64_EncryptedData = Base64.encodeToString(encryptedData, Base64.DEFAULT);
      String base64_IV = Base64.encodeToString(iv.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
      String result = base64_EncryptedData + ":" + base64_IV;

      return result.replace("\n", "");

    } catch (Exception ignored) {

    }

    return "";
  }

}