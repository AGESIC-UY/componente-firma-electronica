/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.store.browser;

import es.mityc.javasign.pkstore.CertStoreException;
import es.mityc.javasign.pkstore.mozilla.MozillaStore;
import java.io.File;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import eu.bitwalker.useragentutils.Manufacturer;
import eu.bitwalker.useragentutils.UserAgent;
import org.ini4j.Wini;
import uy.gub.agesic.firma.cliente.exception.DriverException;
import uy.gub.agesic.firma.cliente.exception.StoreException;
import uy.gub.agesic.firma.cliente.exception.StoreInvalidPasswordException;
import uy.gub.agesic.firma.cliente.store.CertStoreI;
import uy.gub.agesic.firma.cliente.store.SofisCertificate;

/**
 *
 * @author Sofis Solutions
 */
public class MozillaCertStoreImpl implements CertStoreI {

    MozillaStore bstore = null;
    private String profile = "";

    public MozillaCertStoreImpl(String profileParam) throws StoreException {
        try {
            this.profile = profileParam;
        } catch (Exception ex) {
            throw new StoreException(ex);
        }

        //System.out.println("MOZILLA PROFILE PATH " + profile);

    }

    /**
     * Obtiene la identificación a partir del SubjectDN del certificado
     *
     * @param sdn String con el SubjectDN
     * @return String con la identificación
     */
    private static String getCertificateIdentificacion(String sdn) {
        StringTokenizer stk = new StringTokenizer(sdn, ",");
        String token;
        String respuesta = null;
        while (stk.hasMoreTokens()) {
            token = stk.nextToken();
            if (token.contains("SERIALNUMBER")) {
                String[] partes = token.split("=");
                respuesta = partes[1];
                break;
            }
        }
        return respuesta;
    }

    /**
     *
     * @author Sofis Solutions
     */
    public List<SofisCertificate> getSignCertificateFromStore() throws StoreException {
        List<SofisCertificate> toReturn = new ArrayList<SofisCertificate>();
        try {
            bstore = new MozillaStore(profile);
            List<X509Certificate> lista = bstore.getSignCertificates();
            String[] aux;
            String cn = null;
            int pos = 0;
            SofisCertificate cert;
            for (X509Certificate c : lista) {
                if (cn == null){
                    try{
                        //Intentar obtener el cn del certificado
                        aux = c.getSubjectDN().getName().split(",");
                        cn = (aux[0].trim().split("="))[1];
                    }catch(Exception w){
                        cn =  c.getSubjectDN().getName();
                    }
                }
                cert = new SofisCertificate(cn, pos, c, this);
                toReturn.add(cert);
                pos++;
            }
            System.out.println("TORETURN " + toReturn.size());
            return toReturn;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new StoreException(ex);
        }
    }

    public PrivateKey getPrivateKey(SofisCertificate certificate, String certificatePass) throws StoreException {
        try {
            return bstore.getPrivateKey(certificate.getCertificate());
        } catch (CertStoreException ex) {
            throw new StoreException(ex);
        }
    }

    public Provider getProvider(SofisCertificate certificate) {
        return bstore.getProvider(certificate.getCertificate());
    }

    public List<SofisCertificate> getSignCertificateFromStore(String activeProvider) throws StoreException, DriverException, StoreInvalidPasswordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
