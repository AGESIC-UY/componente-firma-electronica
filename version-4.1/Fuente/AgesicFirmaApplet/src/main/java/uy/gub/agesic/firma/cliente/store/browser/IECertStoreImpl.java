/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.store.browser;

import es.mityc.javasign.pkstore.CertStoreException;
import es.mityc.javasign.pkstore.DefaultPassStoreKS;
import es.mityc.javasign.pkstore.mscapi.MSCAPIStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import uy.gub.agesic.firma.cliente.exception.DriverException;
import uy.gub.agesic.firma.cliente.exception.StoreException;
import uy.gub.agesic.firma.cliente.exception.StoreInvalidPasswordException;
import uy.gub.agesic.firma.cliente.store.CertStoreI;
import uy.gub.agesic.firma.cliente.store.SofisCertificate;

/**
 *
 * @author Sofis Solutions
 */
public class IECertStoreImpl implements CertStoreI {

    //IExplorerStore bstore = new IExplorerStore();
    MSCAPIStore bstore =null;


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

    public List<SofisCertificate> getSignCertificateFromStore() throws StoreException {
        System.out.println("IECertStoreImpl: getSignCertificateFromStore");
        List<SofisCertificate> toReturn = new ArrayList<SofisCertificate>();
        try {
            bstore = new MSCAPIStore(new DefaultPassStoreKS());
            List<X509Certificate> lista = bstore.getSignCertificates();
            //System.out.println("cant certs: "+lista.size());
            String[] aux;
            String cn = null;
            int pos = 0;
            SofisCertificate cert;
            for (X509Certificate c : lista) {
                //System.out.println("c.getSubjectDN(): "+c.getSubjectDN());
                //System.out.println("c.getSubjectDN().getName(): "+c.getSubjectDN().getName());
                cn = getCertificateIdentificacion(c.getSubjectDN().getName());
                //System.out.println("cn: "+cn);
                if (cn == null){
                    //System.out.println("cn es null");
                    try{
                        //Intentar obtener el cn del certificado
                        aux = c.getSubjectDN().getName().split(",");
                        
                        for (String n: aux){
                            if(n.trim().toLowerCase().contains("cn=")){
                               cn = (n.trim().split("="))[1];
                               break;
                            }
                        }
                        
                        
                    }catch(Exception w){
                        System.out.println("exception");
                        cn =  c.getSubjectDN().getName();
                    }
                }
                //System.out.println("cn: "+cn);
                cert = new SofisCertificate(cn, pos, c, this);
                toReturn.add(cert);
                pos++;
            }
            //System.out.println("toReturn.size: "+toReturn.size());
            return toReturn;
        } catch (CertStoreException ex) {
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
        //return new SunRsaSign ();
        return null;
        //return bstore.getProvider(certificate.getCertificate());
    }

    public List<SofisCertificate> getSignCertificateFromStore(String activeProvider) throws StoreException, DriverException, StoreInvalidPasswordException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
