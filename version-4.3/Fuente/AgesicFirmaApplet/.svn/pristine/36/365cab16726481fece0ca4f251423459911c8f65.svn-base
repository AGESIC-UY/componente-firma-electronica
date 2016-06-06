/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.store.pkcs11;

import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.ProviderException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.config.MessagesUtil;
/**
 *
 * @author satella
 */
public class KeystoreUtil {
    MessagesUtil msgUtil;
    ConfigurationUtil cfgUtil;
    
    public KeystoreUtil(ConfigurationUtil cfgUtil, MessagesUtil msgUtil) {
        this.msgUtil = msgUtil;
        this.cfgUtil = cfgUtil;
    }
    
     /**
     * Carga en memoria el Keystore con el path del certificado
     *
     * @param ksPass
     * @param pathKeystore
     * @return
     * @throws java.lang.Exception
     */
    public KeyStore loadJKSKeyStore(String ksPass, String pathKeystore) throws Exception {
        File f = new File(pathKeystore);
        InputStream inStream = new FileInputStream(f);
        KeyStore keyStorePKCS12 = KeyStore.getInstance("PKCS12");
        keyStorePKCS12.load(inStream, ksPass.toCharArray());
        return keyStorePKCS12;
    }

    
}
