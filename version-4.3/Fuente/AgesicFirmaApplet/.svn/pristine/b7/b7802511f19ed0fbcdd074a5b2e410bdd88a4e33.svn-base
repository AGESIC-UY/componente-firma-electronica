/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.store.pkcs11;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.FailedLoginException;
import eu.bitwalker.useragentutils.UserAgent;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.config.MessagesUtil;
import uy.gub.agesic.firma.cliente.exception.DriverException;
import uy.gub.agesic.firma.cliente.exception.StoreException;
import uy.gub.agesic.firma.cliente.exception.StoreInvalidPasswordException;
import uy.gub.agesic.firma.cliente.store.CertStorePasswordI;
import uy.gub.agesic.firma.cliente.store.SofisCertificate;

/**
 *
 * @author Sofis Solutions
 */
public class PKCS11CertStoreImpl implements CertStorePasswordI {
    //el enumerado con los tipos de almacenes (etoken , file)

    public enum PKCS11CertStoreType {

        ETOKEN, FILE
    };
    //el tipo de almacen
    private PKCS11CertStoreType type;
    //el path al file del Keystore JKS o al archivo de configuración del Keystore PKCS11
    private String path;
    //el password del almacen
    private String password;
    //el keystore
    private KeyStore keyStore;
    //los datos del usuario qeu está intentando acceder al etoken, como el sistema operativo y el browser que está utilizando
    private UserAgent userAgent;
    private MessagesUtil msgUtil;
    private ConfigurationUtil cfgUtil;

    public PKCS11CertStoreImpl(PKCS11CertStoreImpl.PKCS11CertStoreType type, UserAgent userAgent, HashMap<String,String> opciones) {
        this.type = type;
        this.userAgent = userAgent;
        this.msgUtil = new MessagesUtil(opciones);
        this.cfgUtil = new ConfigurationUtil(opciones);
    }

     public List<SofisCertificate> getSignCertificateFromStore() throws StoreException, DriverException, StoreInvalidPasswordException{
     return null;
     }
    
    public List<SofisCertificate> getSignCertificateFromStore(String activeProvider) throws StoreException, DriverException, StoreInvalidPasswordException {
        List<SofisCertificate> toReturn = new ArrayList<SofisCertificate>();
        try {
            keyStore = null;
            PKCS11Util util = new PKCS11Util(cfgUtil,msgUtil);
            if (type.equals(PKCS11CertStoreType.ETOKEN)) {
                keyStore = util.loadTokenKeyStore(password, userAgent,activeProvider);
            } else if (type.equals(PKCS11CertStoreType.FILE)) {
               keyStore = util.loadJKSKeyStore(password, path);
            }


            if (keyStore != null) {
                Enumeration<String> as = keyStore.aliases();
                while (as.hasMoreElements()) {
                    String alias = as.nextElement();
                    try {
                        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
                        
                        //Intentar obtener el cn del certificado
                        String[] aux = cert.getSubjectDN().getName().split(",");
                        String cn = (aux[0].trim().split("="))[1];
                        //Agregar el certificador a la lista
                        toReturn.add(new SofisCertificate(alias, cn, cert, this));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        SofisCertificate cert = new SofisCertificate(alias, "", null, this);
                        cert.setOk(false);
                        toReturn.add(cert);
                    }
                }
                return toReturn;
            } else {
                return toReturn;
            }
        } catch (StoreException e) {
            throw e;
        } catch (StoreInvalidPasswordException e) {
            throw e;
        } catch (DriverException e) {
            throw e;
        } catch (Exception e) {
            throw new StoreException(e);
        }


    }

    public PrivateKey getPrivateKey(SofisCertificate certificate, String certificatePass) throws StoreException, StoreInvalidPasswordException {
        try {
            PrivateKey key = (PrivateKey) keyStore.getKey(certificate.getAlias(), certificatePass.toCharArray());
            return key;
        } catch (Exception ioEx) {
            if (ioEx.getCause() instanceof UnrecoverableKeyException || ioEx.getCause() instanceof FailedLoginException) {
                throw new StoreInvalidPasswordException(msgUtil.getValue("MSG_CONTRASENIA_INVALIDA"));
            }
            throw new StoreException(ioEx);
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Provider getProvider(SofisCertificate certificate) {
        return null;
    }

    public PKCS11CertStoreType getType() {
        return type;
    }
   

}
