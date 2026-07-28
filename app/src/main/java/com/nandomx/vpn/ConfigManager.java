package com.nandomx.vpn;
import javax.crypto.Cipher; import javax.crypto.spec.SecretKeySpec; import android.util.Base64;
public class ConfigManager {
 public static String encrypt(String p){try{SecretKeySpec s=new SecretKeySpec("NANDOMX-AGUILA-NMX-2024!".substring(0,16).getBytes(),"AES");Cipher c=Cipher.getInstance("AES/ECB/PKCS5Padding");c.init(Cipher.ENCRYPT_MODE,s);return Base64.encodeToString(c.doFinal(p.getBytes()),Base64.NO_WRAP);}catch(Exception e){return null;}}
 public static String decrypt(String enc){try{SecretKeySpec s=new SecretKeySpec("NANDOMX-AGUILA-NMX-2024!".substring(0,16).getBytes(),"AES");Cipher c=Cipher.getInstance("AES/ECB/PKCS5Padding");c.init(Cipher.DECRYPT_MODE,s);return new String(c.doFinal(Base64.decode(enc,Base64.NO_WRAP)));}catch(Exception e){return null;}}
 // IMPORTAR: lee.nmx.enc -> decrypt -> JSON sni/host/payload/ssh/udpgw7300
 // EXPORTAR: JSON -> encrypt -> guarda.nmx.enc compartible
}
