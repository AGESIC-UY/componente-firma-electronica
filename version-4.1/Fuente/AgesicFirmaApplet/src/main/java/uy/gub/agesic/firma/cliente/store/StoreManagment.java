/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import uy.gub.agesic.firma.cliente.exception.DriverException;
import uy.gub.agesic.firma.cliente.exception.StoreInvalidPasswordException;
import uy.gub.agesic.firma.cliente.store.browser.IECertStoreImpl;
import uy.gub.agesic.firma.cliente.store.browser.MozillaCertStoreImpl;
import uy.gub.agesic.firma.cliente.store.pkcs11.PKCS11CertStoreImpl;
import uy.gub.agesic.firma.cliente.store.pkcs11.PKCS11CertStoreImpl.PKCS11CertStoreType;

/**
 *
 * @author Sofis Solutions
 */
public class StoreManagment {

    //los tipos de store soportados
    public enum store_types {

        PKCS11, PKCS12, MOZILLA, IE;
    }
    //booleno que indica si no se pudo detectar un token del estilo PKCS11_TOKEN
    private boolean PKCS11_TOKEN_DETECTED = false;
    List<CertStoreI> certStoreList = new ArrayList();

    public void addCertStore(CertStoreI store) {
        this.certStoreList.add(store);
    }

    /**
     * Para cada store configurado de tipo browser se cargan los certificados
     *
     * @return
     */
    public List<SofisCertificate> getSignCertificateFromBrowser() throws StoreInvalidPasswordException {
        System.out.println("StoreManagment:getSignCertificateFromBrowser");
        List<SofisCertificate> toReturn = new ArrayList();
        HashMap alias = new HashMap();
        for (CertStoreI csI : certStoreList) {
            try {
                if (csI instanceof MozillaCertStoreImpl || csI instanceof IECertStoreImpl) {
                    List<SofisCertificate> x509certs = csI.getSignCertificateFromStore();
                    for (SofisCertificate cert : x509certs) {
                        //System.out.println("cert.getAlias(): "+cert.getAlias());
                        if (alias.containsKey(cert.getAlias())) {
                            //ingore
                        } else {
                            //System.out.println("lo agrega");
                            alias.put(cert.getAlias(), cert.getAlias());
                            toReturn.add(cert);
                        }
                    }
                } else {
                    continue;
                }
            } catch (StoreInvalidPasswordException w) {
                //ignoramos los errores al cargar los certificados, dado qeu se pueden 
                //estar cargando de distintas fuentes como keystore, archivo jks, internet explorer y firefox, excepto el error
                //por password incorrecto
                throw w;
            } catch (Exception w) {

                //ignoramos los errores al cargar los certificados, dado qeu se pueden 
                //estar cargando de distintas fuentes como keystore, archivo jks, internet explorer y firefox.
                w.printStackTrace();
            }
        }
        return toReturn;
    }

    /**
     * Se cargan los certificados desde el store PKCS11, el parametro type define si es un token o 
     * un archivo con el certificado.
     * El path es la ruta al archivo de configuración del etoken o el path al archivo con el certificado , segun el tipo.
     *
     * @return
     */
    public List<SofisCertificate> getSignCertificateFromPKCS11(String password,String path, PKCS11CertStoreType type,String activeProvider) throws StoreInvalidPasswordException, DriverException {
        List<SofisCertificate> toReturn = new ArrayList();
        HashMap alias = new HashMap();
        for (CertStoreI csI : certStoreList) {
            try {
                if (csI instanceof PKCS11CertStoreImpl && ((PKCS11CertStoreImpl)csI).getType().equals(type)) {
                    if (csI instanceof CertStorePasswordI) {
                        CertStorePasswordI csPasssI = (CertStorePasswordI) csI;
                        csPasssI.setPassword(password);
                    }
                    ((PKCS11CertStoreImpl)csI).setPath(path);
                    PKCS11_TOKEN_DETECTED = true;
                    List<SofisCertificate> x509certs = csI.getSignCertificateFromStore(activeProvider);
                    for (SofisCertificate cert : x509certs) {
                        if (alias.containsKey(cert.getAlias())) {
                            //ingore
                        } else {
                            alias.put(cert.getAlias(), cert.getAlias());
                            toReturn.add(cert);
                        }
                    }
                } else {
                    continue;
                }
            } catch (DriverException w) {
                throw w;
            } catch (StoreInvalidPasswordException w) {
                if (csI instanceof PKCS11CertStoreImpl) {
                    PKCS11_TOKEN_DETECTED = false;
                }
                //ignoramos los errores al cargar los certificados, dado qeu se pueden 
                //estar cargando de distintas fuentes como keystore, archivo jks, internet explorer y firefox, excepto el error
                //por password incorrecto
                throw w;
            } catch (Exception w) {
                //ignoramos los errores al cargar los certificados, dado qeu se pueden 
                //estar cargando de distintas fuentes como keystore, archivo jks, internet explorer y firefox.
                w.printStackTrace();
            }
        }
        return toReturn;
    }

    /**
     * Para cada store configurado se cargan los certificados
     *
     * @return
     */
    public List<SofisCertificate> getSignCertificateFromStores(String password) throws StoreInvalidPasswordException, DriverException {

        List<SofisCertificate> toReturn = new ArrayList();
        //para no incluir los certificados repetidos, 
        //en el caso de los etoken es comun que estos instalen plugin en los exploradores
        //por lo tanto puede ocurrir que se liste un alias dos veces una vez por el uso de la api PKCS11 directamente
        //y otra vez por acceder al repositorio del navegador y tener instalado el plugin del token.
        HashMap alias = new HashMap();
        for (CertStoreI csI : certStoreList) {
            try {
                if (csI instanceof CertStorePasswordI) {
                    CertStorePasswordI csPasssI = (CertStorePasswordI) csI;
                    csPasssI.setPassword(password);
                }
                List<SofisCertificate> x509certs = csI.getSignCertificateFromStore();
                for (SofisCertificate cert : x509certs) {
                    if (alias.containsKey(cert.getAlias())) {
                        //ingore
                    } else {
                        alias.put(cert.getAlias(), cert.getAlias());
                        toReturn.add(cert);
                    }

                }

                if (csI instanceof PKCS11CertStoreImpl) {
                    PKCS11_TOKEN_DETECTED = true;
                }

            } catch(DriverException w){
                throw w;
            } catch (StoreInvalidPasswordException w) {
                //ignoramos los errores al cargar los certificados, dado qeu se pueden 
                //estar cargando de distintas fuentes como keystore, archivo jks, internet explorer y firefox, excepto el error
                //por password incorrecto
                throw w;
            } catch (Exception w) {
                if (csI instanceof PKCS11CertStoreImpl) {
                    PKCS11_TOKEN_DETECTED = false;
                }
                //ignoramos los errores al cargar los certificados, dado qeu se pueden 
                //estar cargando de distintas fuentes como keystore, archivo jks, internet explorer y firefox.
                w.printStackTrace();
            }
        }
        return toReturn;
    }

    public boolean isPKCS11_TOKEN_DETECTED() {
        return PKCS11_TOKEN_DETECTED;
    }

    public void setPKCS11_TOKEN_DETECTED(boolean PKCS11_TOKEN_DETECTED) {
        this.PKCS11_TOKEN_DETECTED = PKCS11_TOKEN_DETECTED;
    }
}
