package com.visa.vts.certificationapp.utils;

import android.content.Context;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import static com.visa.vts.certificationapp.utils.EncryptionUtility.CEK_SIZE_BYTES;
import static com.visa.vts.certificationapp.utils.EncryptionUtility.IV_SIZE_BYTES;

import static com.visa.dmpd.encryption.GenericEncryptionUtility.generateKey;
import static com.visa.dmpd.encryption.GenericEncryptionUtility.generateSalt;
import static com.visa.vts.certificationapp.utils.EncryptionUtility.bs64Decode;
import static com.visa.vts.certificationapp.utils.EncryptionUtility.bs64Encode;
import static com.visa.vts.certificationapp.utils.EncryptionUtility.encryptData;
import static com.visa.vts.certificationapp.utils.EncryptionUtility.utf8;


/**
 * *** Note: This is not encryption framework related code. For lack of better place/artifact, it's placed inside encryption
 * project, so client applications will get it easily. At this point, creating a new artifact for single utility class is not justified.
 *
 * A Simple utility to parse and generate Java Web Token (JWT). This utility provides simple methods to create a token string
 * when a kid, claim and shared secrete is given. It only supports a bare-minimum set of options from JWT spec.
 *
 * @author : pmungse
 * Date    : 8/11/14
 * @since  : 2.0.6
 */
public final class JWTLocalUtility {

  private static final Logger LOG = LoggerFactory.getLogger(JWTLocalUtility.class) ;
   /**
   * Returns the JWE compact serialization formatted JWT for the given claim (plaintext) information using the given kid and
   * shared secret. It will do the encryption of claim (assertion) and convert all data into base64-url-safe encoding and return
   * the JWE compact serialization format, as:
   * BASE64URL(UTF8(JWE Protected Header)) + "." + BASE64URL(JWE Encrypted Key - CEK) + "." + BASE64URL(JWE Initialization Vector)
   * + "." + BASE64URL(JWE Ciphertext) + "." + BASE64URL(JWE Authentication Tag)
   *
   * @param plainText
   * @param kid
   * @return Base64 encoded JWE
   */
  public static final String createJwe(Context context,byte[] plainText, String kid) {
    LOG.trace("Creating JWE using kid: {}", kid) ;
    String jwe = null ;
    try {
      // create salt
      byte[] dataSalt = generateSalt(IV_SIZE_BYTES);
      byte[] rsk  =  generateKey(CEK_SIZE_BYTES) ;

      // wrap/encrypt key
      byte[] ersk = EncryptionUtility.encryptRandomWithIBK(context, rsk);
      byte[] headerBytes = buildJweHeaderBytes(kid);

      // encrypt data using key
      //byte[] dataSalt = generateSalt(IV_SIZE_BYTES) ;
      EncryptionUtility.CipherTextData encryptedData = encryptData(rsk, dataSalt, plainText,  headerBytes ) ;

      // combine all together in return string
      jwe = buildJwe(headerBytes, ersk, dataSalt, encryptedData) ;
      LOG.trace("The JWE created : {}" , jwe) ;

    } catch(Exception e) {
      LOG.error("Error in creating JWE", e);
    }

    return jwe ;
  }

  /**
   * Parses the given encrypted & encoded JWE into it's subparts. Also converts the encoded header into json structure to
   * access sub elements in it.
   * @param encyptedJwe
   * @return
   */
  public static final JWEData parseJwe(String encyptedJwe) {
    return new JWEData(encyptedJwe) ;
  }

  /**
   * Returns the api-key / kid from the encrypted JWE. This will parse the header from JWE and get kid information. It is
   * required for the clients to fetch the shared secret.
   * @param encryptedJwe
   * @return
   */
  public static final String getKidFromJwe(String encryptedJwe) {
    JWEData jwe = parseJwe(encryptedJwe) ;
    return jwe.getHeader().getKid() ;
  }


  /**
   * Returns the decrypted data from JWE using the given secret and CEK in JWE.
   * @param jwe
   * @return
   */
  public static final String decryptJwe(Context context,JWEData jwe, int keyId) {

    // get header
    // get key salt, encrypted key
    // decrypt key

    byte[] ersk = bs64Decode(jwe.ersk);
    byte[] rskBytes = EncryptionUtility.decryptRandomWithIBK(context,ersk, keyId);

    // decode data salt & tag
    // decrypt data
    byte[] data = EncryptionUtility.decryptData(rskBytes, bs64Decode(jwe.getDataSalt()), bs64Decode(jwe.getCipherText()),
            bs64Decode(jwe.getDataAuthTag()), null) ;

      //Extracting the plain text leaving behind the first byte, which is tag value
      byte[] sensitiveData = Arrays.copyOfRange(data, 1, data.length) ;

      // get plain text from the sensitiveData bytes using UTF-8 charset
      try {
          String plainText =  new String(sensitiveData, "UTF-8") ;
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      return new String(data, utf8) ;
  }

  /**
   * Returns the string information in the claim-set of JWE. This could be either for JWT or PAN encryption. It deconstructs the parts
   * of JWE and decrypts the payload using given shared secret. This implements only subset of JWE functionality (i.e algo=dir)
   *
   * @param encryptedJwe
   * @param secret
   * @return JSON String represented in the claim.
   */
//  public static final String decryptJwe(String encryptedJwe, String secret) {
//
//    return decryptJwe(new JWEData(encryptedJwe), secret) ;
//  }


  /**
   * Create the json format from given JWE header object.
   * @param header
   * @return
   */
  private static String getJson(JWEHeader header) {
    String json ;
    try {
      json = new Gson().toJson(header);
      LOG.trace("JSON Created from header object: {}", json) ;
    }catch(Exception e) {
      LOG.error("Error in creating json for JWT header", e) ;
      throw new RuntimeException(e) ;
    }
    return json ;

  }


  private static byte[] buildJweHeaderBytes(String kid) {
    // create jwe header
    JWEHeader header = new JWEHeader() ;
    header.setKid(kid) ;

    String headerJson = getJson(header) ;
    LOG.trace("headerJson: {}", headerJson) ;
    return headerJson.getBytes(utf8);
  }

  private static String buildJwe(byte[] header, byte[] ersk, byte[] dataSalt, EncryptionUtility.CipherTextData encryptedData) {
    StringBuilder jwe = new StringBuilder() ;

    jwe.append(bs64Encode(header) )
        .append(".")
        .append(bs64Encode(ersk))
        .append(".")
        .append(bs64Encode(dataSalt))
        .append(".")
        .append(encryptedData.getCipherText())
        .append(".")
        .append(encryptedData.getAuthTag()) ;

    LOG.trace("The created JWE: {}" , jwe.toString() );

    return jwe.toString();
  }


  /**
   * Class to hold the JWE header information. It defaults the key encryption and data encryption to AES/GCM/256 bit key.
   * the JWE Type is default to 'JOSE'. Rest information is taken from the actual jwt.
   */
  public static class JWEHeader {
    private String alg = "RSA1_5" ;
    private String enc = "AGCM256" ;
    private String typ = "JOSE" ;
    private String kid = "1234";
    private String channelSecurityContext = "RSA_PKI";
    private String iat =  ""+ System.currentTimeMillis();

    public JWEHeader() { }

    public String getAlg() { return  alg ;}
    public String getChannelSecurityContext() { return  channelSecurityContext ;}
    public String getEnc(){ return  enc ;}
    public String getTyp(){ return typ ;}
    public String getKid(){ return kid ;  }
    public String getIat(){ return iat ;  }

      public void setKid(String kid) {
          this.kid = kid;
      }

  }


  /**
   * Class to hold the encoded & encrypted JWE data. It parses the JWE assuming it's compact serialization and breaks
   * it into 5 parts : header, encrypted key, salt, encrypted payload and authentication tag. Header is further decoded
   * into JWEHeader as per above class.
   *
   *  **NOTE** Any header elements not given in JWEHeader will be ignored - potentially failing auth tag check.
   */
  public static class JWEData {
    private String headerJson ;
    private JWEHeader header ;
    private String ersk ;
    private String dataSalt ;
    private String cipherText ;
    private String dataAuthTag ;

    public JWEData(String encryptedJwe) {
      assert encryptedJwe != null ;
      try {
        String[] parts = encryptedJwe.split("\\.", -1);
        ersk = parts[1];
        dataSalt = parts[2];
        cipherText = parts[3];
        dataAuthTag = parts[4];
        String headerEncoded = parts[0];
        byte[] hdBytes = bs64Decode(headerEncoded);
        headerJson = new String(hdBytes, utf8) ;
          Gson gson = new Gson();

          try {
              header = gson.fromJson(headerJson, JWEHeader.class);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }catch(Exception e) {
        LOG.error("Error in parsing jwe into sub-parts, jwe:" + encryptedJwe, e);
        throw new RuntimeException(e) ;
      }
    }

    public JWEHeader getHeader() { return header ;}
    public String getHeaderJson() { return headerJson ; }
    public String getErsk() { return ersk; }
    public String getDataSalt() { return dataSalt; }
    public String getCipherText() { return cipherText; }
    public String getDataAuthTag() { return dataAuthTag; }

  }

}
