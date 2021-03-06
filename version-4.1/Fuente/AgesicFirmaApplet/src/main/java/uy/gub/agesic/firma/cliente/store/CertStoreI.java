/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uy.gub.agesic.firma.cliente.store;



import java.security.PrivateKey;
import java.security.Provider;
import java.util.List;
import uy.gub.agesic.firma.cliente.exception.DriverException;
import uy.gub.agesic.firma.cliente.exception.StoreException;
import uy.gub.agesic.firma.cliente.exception.StoreInvalidPasswordException;

/**
 *
 * @author Sofis Solutions
 */
public interface  CertStoreI{
    /**
     * La lista de certificados del store
     * @return
     * @throws StoreException
     * @throws StoreInvalidPasswordException 
     */
    public List<SofisCertificate> getSignCertificateFromStore() throws StoreException, DriverException, StoreInvalidPasswordException;
    
    /**
     * La lista de certificados del store
     * @return
     * @throws StoreException
     * @throws StoreInvalidPasswordException 
     */
    public List<SofisCertificate> getSignCertificateFromStore(String activeProvider) throws StoreException, DriverException, StoreInvalidPasswordException;
    
    /**
     * Dado un certificado del Store obtiene el Private Key
     * @param certificate
     * @param certificatePass
     * @return
     * @throws StoreException
     * @throws StoreInvalidPasswordException 
     */
    public PrivateKey getPrivateKey(SofisCertificate certificate, String certificatePass) throws StoreException,StoreInvalidPasswordException;
    
    /**
     * El provider que debe de utilizar el store para la firma
     * @param certificate
     * @return 
     */
    public Provider getProvider(SofisCertificate certificate);
}
