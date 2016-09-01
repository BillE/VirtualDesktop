package com.dataclear;
import com.chilkatsoft.*;

public class ChilkatLibrary {
    private static final String PUBLIC_KEY_FILE = System.getProperty("user.home") 
            + "/.ssh/cPubKey.pem";

    private static final String PRIVATE_KEY_FILE = System.getProperty("user.home") 
            + "/.ssh/cPrivKey.pem";
    
    // TODO: unlock code
    private static final String CHILKAT_UNLOCK_CODE = "Anything for 30-day trial";
    
    static {
        try {
            System.loadLibrary("chilkat");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    static String encrypt(String stringToEncrypt) throws Exception {
        CkRsa rsa = new CkRsa();
        rsa.put_EncodingMode("hex");
        
        boolean success = rsa.UnlockComponent(CHILKAT_UNLOCK_CODE);
        if (success != true) {
            System.out.println(rsa.lastErrorText());
            // TODO: throw meaningful exception
            throw new Exception();
        }
        
        // load public key
        CkPublicKey pubKey = new CkPublicKey();
        success = pubKey.LoadOpenSslPemFile(PUBLIC_KEY_FILE);
        String pubKeyXml = pubKey.getXml();
        success = rsa.ImportPublicKey(pubKeyXml);
        if (success != true) {
            System.out.println(rsa.lastErrorText());
            // TODO: throw meaningful exception
            throw new Exception();
        }
        
        // encrypt the string
        boolean usePrivateKey = false;
        String encryptedString = rsa.encryptStringENC(stringToEncrypt, usePrivateKey);
        // System.out.println(encryptedString);
        return encryptedString;
    }
    
    static String decrypt(String stringToDecrypt) throws Exception {
        CkRsa rsa = new CkRsa();
        rsa.put_EncodingMode("hex");
        // load public key
        CkPrivateKey privateKey = new CkPrivateKey();
        boolean success = privateKey.LoadPemFile(PRIVATE_KEY_FILE);
        String privKeyXml = privateKey.getXml();
        success = rsa.ImportPublicKey(privKeyXml);
        if (success != true) {
            System.out.println(rsa.lastErrorText());
            // TODO: throw meaningful exception
            throw new Exception();
        }
        
        success = rsa.UnlockComponent(CHILKAT_UNLOCK_CODE);
        if (success != true) {
            System.out.println(rsa.lastErrorText());
            // TODO: throw meaningful exception
            throw new Exception();
        }
        
        boolean bUsePrivateKey = true;
        String decryptedString = rsa.decryptStringENC(stringToDecrypt, bUsePrivateKey);        
        return decryptedString;
    }
}
