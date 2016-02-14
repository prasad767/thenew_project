package com.visa.vts.certificationapp.utils;

import android.content.Context;

import com.visa.vts.certificationapp.R;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.UrlBase64;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Enumeration;

import javax.crypto.Cipher;

/**
 * This is a self-sustained utility for doing data & key encryption to be used along with Token (JWT/JWE) encryption.
 *
 * @author : pmungse
 * Date    : 8/7/14
 * @since  : 2.0.6
 */
public final class EncryptionUtility {


  public static final Charset utf8 = Charset.forName("UTF-8");

  public static final int CEK_SIZE_BYTES       = 16 ;
  public static final int AUTH_TAG_SIZE_BITS   = 128 ;
  public static final int AUTH_TAG_SIZE_BYTES  = AUTH_TAG_SIZE_BITS / 8 ;
  public static final int IV_SIZE_BITS         = 96 ;
  public static final int IV_SIZE_BYTES        = IV_SIZE_BITS / 8 ;

  protected static final boolean ENCRYPT_MODE     = true ;
  protected static final boolean DECRYPT_MODE     = false ;

  static {
    try {
      // load the bouncy castle provider if it's not already added in jdk security policy file.
      if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
        Security.addProvider(new BouncyCastleProvider());
    } catch (Exception e) {

    }
  }

  /**
   * Generate IV / salt with given byte length using SecureRandom.
   * @param numBytes
   * @return
   * @throws NoSuchAlgorithmException
   */

  public static final byte[] generateSalt(int numBytes) throws NoSuchAlgorithmException {
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    byte[] salt = new byte[numBytes];
    sr.nextBytes(salt);

      byte[] chars = Base64.encode(salt); // Ensure the bytes are converted to proper characters
      System.arraycopy(chars, 0, salt, 0, salt.length); // Discard extra characters
      clean(chars);
    return salt;
  }

  /**
   * Generate a ascii text key of given byte length using SecureRandom. BAse64 encoding (converting to ascii) will
   * increase byte length, so last additional part is ignored.
   * @param numBytes
   * @return
   * @throws NoSuchAlgorithmException
   */
  public static final byte[] generateKey(int numBytes) throws NoSuchAlgorithmException {
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    byte[] key = new byte[numBytes];
    sr.nextBytes(key);

    byte[] chars = Base64.encode(key); // Ensure the bytes are converted to proper characters
    System.arraycopy(chars, 0, key, 0, key.length); // Discard extra characters
    clean(chars);
    return key;
  }


  /**
   * Zeroes the contents of the provided byte array.
   * @param bytes Byte array to be zeroed out.
   */
  private static void clean(final byte[] bytes) {
    if (bytes != null) {
      Arrays.fill(bytes, (byte) 0);
    }
  }

  /**
   * Return the raw byte[] data as Base64 encoded UTF-8 character encoded string.
   * It encodes data in URL safe manner without padding.
   * @param data
   * @return
   */
  public static final String bs64Encode(byte[] data) {
    String encoded = new String(UrlBase64.encode(data), utf8) ;
    int strLen = encoded.length() ;
    if(strLen > 2) {
      int trim = 0;
      if (encoded.charAt(strLen - 1) == '.')
        trim++;

      if (encoded.charAt(strLen - 2) == '.')
        trim++;

      if (trim > 0)
        encoded = encoded.substring(0, strLen - trim);
    }

    return encoded ;
  }

  /**
   * Returns the Base64 Decoded byte[] from given String with UTF-8 character encoding.
   * @param data
   * @return
   */
  public static final byte[] bs64Decode(String data) {
    byte[] decoded ;
    if(data.length() == 0 ) {
      decoded = new byte[] {} ;
    }else {
      // add the trailing . to make padding compatible.
      int add = 4 - (data.length() % 4);
      if(add == 4)
        add = 0 ;
      String encoded = data + "...".substring(0, add);
      decoded =  UrlBase64.decode(encoded.getBytes(utf8)) ;
    }
    return decoded ;
  }


  /**
   * Generate a 256 bit key based on shared secret to be used with AES algorithm.
   * @param secret
   * @return
   * @throws NoSuchAlgorithmException
   */
  public static byte[] getKekBytes(String secret) throws NoSuchAlgorithmException {

    // Secret could be of different length and may not be exactly 256 bits. hence getting SHA2 to use it as key.
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(secret.getBytes(utf8));
    byte[] keyBytes = md.digest();
    assert keyBytes.length==32 ; // 32 byte == 256 bit size key
    return keyBytes ;
  }

  /**
   * Encrypt the randomly generated CEK using shared secret (SHA2 hash) and salt (IV). It internally uses encryptData, which
   * uses AES/GCM/256 bit key to encrypt data.
   *
   * @param secret
   * @param salt
   * @param keyToWrap
   * @return CipherTextData which contains the encrypted key and associated authentication tag information in base64 encoded format.
   */
  public static final CipherTextData encryptKey(final String secret, final byte[] salt, final byte[] keyToWrap) {
    CipherTextData cipherTextData = null ;

    try {
      byte[] kekBytes = getKekBytes(secret) ;
      cipherTextData = encryptData(kekBytes, salt, keyToWrap, null) ;
    } catch(Exception e) {

      throw new RuntimeException(e) ;
    }
    return cipherTextData ;
  }

    public static byte[] encryptRandomWithIBK(Context context, byte[] clearRandomKey){

        byte[] publicBytes = readKey(context, R.raw.vts_ibk_pub_key, true);
        byte[] encRandomKey = null;

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicBytes);
            PublicKey ibkPublicKey = keyFactory.generatePublic(x509KeySpec);
            Cipher encCipher = Cipher.getInstance("RSA/NONE/PKCS1Padding", "BC");
            encCipher.init(Cipher.ENCRYPT_MODE, ibkPublicKey);
            encRandomKey = encCipher.doFinal(clearRandomKey);
        } catch (Exception e){

        }
        return encRandomKey;
    }

    public static byte[] decryptRandomWithIBK(Context context, byte[] encryptedRandomKey, int keyId){

        byte[] private_bytes = readKey(context, keyId, false);
        byte[] plainRandomKey = null;
        try {
            PrivateKey ibkPrivateKey = getPrivateKey(context,keyId);
            Cipher encCipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
            encCipher.init(Cipher.DECRYPT_MODE, ibkPrivateKey);
            plainRandomKey = encCipher.doFinal(encryptedRandomKey);
        } catch (Exception e){

            e.printStackTrace();
        }
        return plainRandomKey;
    }


    public static byte[] readKey(Context context,int certId, boolean isPub) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream is = context.getResources().openRawResource(certId);
        readStream(is, byteArrayOutputStream);
        String certinpem = new String(byteArrayOutputStream.toByteArray());
        if(isPub) {
            certinpem = certinpem.replace("-----BEGIN PUBLIC KEY-----", "");
            certinpem = certinpem.replace("-----END PUBLIC KEY-----", "");
        } else {
            certinpem = certinpem.replace("-----BEGIN RSA PRIVATE KEY-----", "");
            certinpem = certinpem.replace("-----END RSA PRIVATE KEY-----", "");
        }
        byte[] buf_cert = Base64.decode(certinpem);
        return buf_cert;
    }

    public static void readStream(InputStream stream, ByteArrayOutputStream baos) {
        try {
            int bt;
            while ((bt = stream.read()) != -1) {
                baos.write(bt);
            }
        } catch (IOException e) {

        }
    }


  /**
   * Encrypt the payload / data using given key, salt and authentication data. for key encryption, additional authentication data
   * is null. for data encryption in JWT, it's the header in json format.
   *
   * @param key
   * @param salt
   * @param data
   * @param aad
   * @return CipherTextData which contains the encrypted key and associated authentication tag information in base64 encoded format.
   */
  public static final CipherTextData encryptData(final byte[] key, final byte[] salt, final byte[] data, final byte[] aad) {
    CipherTextData cipherTextData = null ;


    try {
      GCMBlockCipher gcm = new GCMBlockCipher(new AESFastEngine());
      gcm.init(ENCRYPT_MODE, new AEADParameters(new KeyParameter(key), AUTH_TAG_SIZE_BITS, salt, aad)); // no AAD for key encryption.
      int outsize = gcm.getOutputSize(data.length);
      byte[] out = new byte[outsize] ;
      int offOut = gcm.processBytes(data, 0, data.length, out, 0) ;
      gcm.doFinal(out, offOut);
      cipherTextData = getCipherTextData(out, AUTH_TAG_SIZE_BYTES);
    } catch(Exception e) {

      throw new RuntimeException(e) ;
    }

    return cipherTextData ;
  }


  /**
   * Decrypt the encrypted key using shared secret (hash), salt and authentication tag.
   * @param secret
   * @param salt
   * @param encryptedKey
   * @param authTag
   * @return CEK in byte[] format.
   */
  public static final byte[] decryptKey(final String secret, final byte[] salt, final byte[] encryptedKey, final byte[] authTag )  {
    byte[] returnData ;

    try{
      byte[] ibkBytes = getKekBytes(secret) ;
      returnData = decryptData(ibkBytes, salt, encryptedKey, authTag, null) ;
    }catch(Exception e) {

      throw new RuntimeException(e) ;
    }

    return returnData ;
  }

  /**
   * Decrypt the encrypted data using CEK, salt and authentication tag. It also authenticates the auth tag to ensure encrypted data and
   * associated authentication data is not tempered with.
   * @param key
   * @param salt
   * @param encrypted
   * @param authTag
   * @param aad
   * @return The decrypted data in byte[] format.
   */
  public static final byte[] decryptData(final byte[] key, final byte[] salt, final byte[] encrypted, final byte[] authTag, final byte[] aad) {
    byte[] returnData = null ;
      GCMBlockCipher gcm = null;

    try{
       gcm = new GCMBlockCipher(new AESFastEngine());
      gcm.init(DECRYPT_MODE, new AEADParameters(new KeyParameter(key), AUTH_TAG_SIZE_BITS, salt, aad)); // no AAD for key encryption.
      byte[] finalData = new byte[encrypted.length+authTag.length] ;
      System.arraycopy(encrypted, 0, finalData, 0, encrypted.length);
      System.arraycopy(authTag, 0, finalData, encrypted.length, authTag.length);
      int outSize =  gcm.getOutputSize(finalData.length) ;
      returnData = new byte[outSize] ;
      int offset = gcm.processBytes(finalData,0,finalData.length, returnData, 0) ;
      gcm.doFinal(returnData,offset) ;
    }catch(Exception e) {

      //throw new RuntimeException(e) ;
    }
    return returnData ;
  }

  /**
   * Creates the CipherTextData object containing base64 encoded cipher text and authentication tag data.
   * @param out
   * @param authTagSize
   * @return
   */
  private static CipherTextData getCipherTextData(byte[] out, int authTagSize) {
    int ctSize = out.length - authTagSize ;
    byte[] cipherTextBytes = new byte[ctSize] ;
    byte[] mac = new byte[authTagSize] ;
    System.arraycopy(out, 0, cipherTextBytes, 0, ctSize) ;
    System.arraycopy(out, ctSize, mac, 0, authTagSize) ;
    CipherTextData cipherTextData = new CipherTextData(cipherTextBytes, mac) ;
    return cipherTextData;
  }


  /**
   * Inner class to hold the Cipher Encryption operation result. Holds the cipher text and Auth tag details in Base64 encoded format.
   */
  public static class CipherTextData {
    private String cipherText ;
    private String authTag ;

    public CipherTextData(final byte[] cipherOutput, final byte[] macData) {

      cipherText = bs64Encode(cipherOutput) ;
      authTag    = bs64Encode(macData) ;

    }

    public String getCipherText() {
      return cipherText ;
    }
    public String getAuthTag() {
      return authTag ;
    }

  }

    public static PrivateKey getPrivateKey(Context context, int id) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream is = context.getResources().openRawResource(id);
        EncryptionUtility.readStream(is, byteArrayOutputStream);
        String certinpem = new String(byteArrayOutputStream.toByteArray());
        String privKeyPEM = certinpem.replace(
                "-----BEGIN RSA PRIVATE KEY-----\n", "")
                .replace("-----END RSA PRIVATE KEY-----", "");

        byte[] encodedPrivateKey = Base64.decode(privKeyPEM);

        try {
            ASN1Sequence primitive = (ASN1Sequence) ASN1Sequence
                    .fromByteArray(encodedPrivateKey);
            Enumeration<?> e = primitive.getObjects();
            BigInteger v = ((DERInteger) e.nextElement()).getValue();

            int version = v.intValue();
            if (version != 0 && version != 1) {
                throw new IllegalArgumentException("wrong version for RSA private key");
            }
            /**
             * In fact only modulus and private exponent are in use.
             */
            BigInteger modulus = ((DERInteger) e.nextElement()).getValue();
            BigInteger publicExponent = ((DERInteger) e.nextElement()).getValue();
            BigInteger privateExponent = ((DERInteger) e.nextElement()).getValue();
            BigInteger prime1 = ((DERInteger) e.nextElement()).getValue();
            BigInteger prime2 = ((DERInteger) e.nextElement()).getValue();
            BigInteger exponent1 = ((DERInteger) e.nextElement()).getValue();
            BigInteger exponent2 = ((DERInteger) e.nextElement()).getValue();
            BigInteger coefficient = ((DERInteger) e.nextElement()).getValue();

            RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, privateExponent);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey pk = kf.generatePrivate(spec);
            return pk;
        } catch (IOException e2) {
            throw new IllegalStateException();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }

}
