package uy.gub.agesic.firma.cliente.validaciones;

import java.io.InputStream;
import java.security.*;
import java.security.cert.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.config.MessagesUtil;
import uy.gub.agesic.firma.cliente.exception.CARootVerificationException;
import uy.gub.agesic.firma.cliente.exception.CertificateVerificationException;

public class CARootValidation {

    static MessagesUtil msgUtil = new MessagesUtil(null);
    
    /**
     * @param cert - certificado a validar
     * @param additionalCerts - conjunto de certificados trusted root CA que
     * ser�n usados como "trust anchors" y certificados CA intermediadios que
     * ser�n usados como parte de la cadena de certificados. Todos los
     * certificados self-signed son considerados como certificados trusted root
     * CA. El resto son considerados como certificados CA intermedios.
     * @return la cadena de certificados (si la verificaci�n fue satisfactoria)
     * @throws CertificateVerificationException - si la certificaci�n no es
     * existosa (p.e. algun certificado en cadena expiro)
     */
    public static PKIXCertPathBuilderResult validateCertificate(X509Certificate cert, Set<X509Certificate> additionalCerts) throws CARootVerificationException {
        try {

            // Se fija si el certificado a validar es self-signed
            if (isSelfSigned(cert)) {
                throw new CARootVerificationException(msgUtil.getValue("MSG_CARootValidation_SELF_SIGNED"));
            }

            // Prepara un conjunto de certificados trusted root CA
            // y un conjunto de certificados intermedios
            Set<X509Certificate> trustedRootCerts = new HashSet<X509Certificate>();
            Set<X509Certificate> intermediateCerts = new HashSet<X509Certificate>();
            for (X509Certificate additionalCert : additionalCerts) {
                if (isSelfSigned(additionalCert)) {
                    trustedRootCerts.add(additionalCert);
                } else {
                    intermediateCerts.add(additionalCert);
                }
            }

            // Verifica el certificado
            PKIXCertPathBuilderResult verifiedCertChain = verifyCertificate(cert, trustedRootCerts, intermediateCerts);

            return verifiedCertChain;

        } catch (CertPathBuilderException certPathEx) {
            throw new CARootVerificationException(msgUtil.getValue("MSG_CARootValidation_ERROR_PATH"), certPathEx);
        } catch (Exception ex) {
            throw new CARootVerificationException(msgUtil.getValue("MSG_CARootValidation_ERROR_GENERAL"), ex);
        }
    }

    /**
     * Se fija si el certificado X.509 dado es self-signed.
     */
    public static boolean isSelfSigned(X509Certificate cert) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            // Verifica la firma con su propia clave publica
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (SignatureException sigEx) {
            // firma invalida --> no es self-signed
            return false;
        } catch (InvalidKeyException keyEx) {
            // clave invalida --> no es self-signed
            return false;
        }
    }

    private static PKIXCertPathBuilderResult verifyCertificate(X509Certificate cert, Set<X509Certificate> trustedRootCerts, Set<X509Certificate> intermediateCerts) throws GeneralSecurityException {
        try {

            // Creamos el selector que especifica el certificado con el que comienza la cadena de certificados
            X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(cert);
            // Creamos los trust anchors (conjunto de certificados root CA )
            Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
            for (X509Certificate trustedRootCert : trustedRootCerts) {
                trustAnchors.add(new TrustAnchor(trustedRootCert, null));
            }
            // Se configuran los parametros con los que se va a contruir la cedena de certificados.
            PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(trustAnchors, selector);
            // Disabilitamos el chequeo CRL (se hace mas adelante)
            pkixParams.setRevocationEnabled(false);


            // Especificamos la lista de certificados intermedios
            CertStore intermediateCertStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(intermediateCerts), "BC");
            pkixParams.addCertStore(intermediateCertStore);


            //Se contruye la cedena de certificados verificando que el certificado que queremos validar esta firmado por la root CA
            CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
            PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder.build(pkixParams);
            System.out.println("result: " + result.getCertPath());
             
            return result;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CARootValidation.class.getName()).log(Level.SEVERE, null, ex);

        }catch(Exception e){
        e.printStackTrace();
        }

        return null;
    }
}
