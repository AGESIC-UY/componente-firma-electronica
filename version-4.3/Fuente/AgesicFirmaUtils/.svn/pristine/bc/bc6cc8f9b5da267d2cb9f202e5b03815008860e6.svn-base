package org.agesic.firma.validaciones.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.*;
import java.sql.Date;
import java.util.*;
import javax.security.auth.x500.X500Principal;
import org.agesic.firma.datatypes.ResultadoValidacion;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.CertPathValidatorUtilities;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.StoreException;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.x509.ExtendedPKIXBuilderParameters;
import org.bouncycastle.x509.X509CertStoreSelector;
import org.bouncycastle.x509.X509Store;
import org.w3c.dom.Document;
import sun.security.x509.X500Name;

public class CARootValidation {

    public static ResultadoValidacion validate(X509Certificate cert, String rutaJks2, char[] ksPass2) throws Exception {
        KeyStore trustStore = null;
        try {

            trustStore = cargarKeystore(rutaJks2, ksPass2);
        } catch (Exception w) {
            throw w;
        }

        //Si el certificado es autofirmado debe estar en el truststore
        if (isSelfSigned(cert)) {
            if (trustStore.getCertificateAlias(cert) == null) {
                ResultadoValidacion resultadoValidacion = new ResultadoValidacion(FirmaValidate.CERTIFICATE_NOT_TRUSTED_ERROR_CODE, "El certificado es autofirmado pero no es de confianza", null);
                return resultadoValidacion;
            }
        }

        //Validar la cadena de certificación
        Set<X509Certificate> additionalCerts = new HashSet<X509Certificate>();
        Set<X509Certificate> trustedRootCerts = new HashSet<X509Certificate>();
        Set<X509Certificate> intermediateCerts = new HashSet<X509Certificate>();

        PKIXParameters params = new PKIXParameters(trustStore);
        for (TrustAnchor ta : params.getTrustAnchors()) {
            X509Certificate trustCert = ta.getTrustedCert();
            additionalCerts.add(trustCert);
        }
        for (X509Certificate additionalCert : additionalCerts) {
            if (isSelfSigned(additionalCert)) {
                trustedRootCerts.add(additionalCert);
            } else {
                intermediateCerts.add(additionalCert);
            }
        }

        try {

            PKIXCertPathBuilderResult verifiedCertChain = verifyCertificate(cert, trustedRootCerts, intermediateCerts);
            return null;
        } catch (Exception w) {
            w.printStackTrace();
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(FirmaValidate.CERTIFICATE_NOT_TRUSTED_ERROR_CODE, w.getMessage(), null);
            return resultadoValidacion;

        }

    }

    public static boolean isSelfSigned(X509Certificate cert)
            throws CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException {
        try {
            // Try to verify certificate signature with its own public key
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (SignatureException sigEx) {
            // Invalid signature --> not self-signed
            return false;
        } catch (InvalidKeyException keyEx) {
            // Invalid key --> not self-signed
            return false;
        }
    }

    private static PKIXCertPathBuilderResult verifyCertificate(X509Certificate cert, Set<X509Certificate> trustedRootCerts,
            Set<X509Certificate> intermediateCerts) throws GeneralSecurityException {

        
        // Create the selector that specifies the starting certificate
        X509CertSelector selector = new X509CertSelector();
        selector.setCertificate(cert);

        // Create the trust anchors (set of root CA certificates)
        Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
        for (X509Certificate trustedRootCert : trustedRootCerts) {
            trustAnchors.add(new TrustAnchor(trustedRootCert, null));
        }

        // Configure the PKIX certificate builder algorithm parameters
        PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(trustAnchors, selector);

        // Disable CRL checks (this is done manually as additional step)
        pkixParams.setRevocationEnabled(false);


        // Specify a list of intermediate certificates
        CertStore intermediateCertStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(intermediateCerts), "BC");
        pkixParams.addCertStore(intermediateCertStore);

        /*try {
             ExtendedPKIXBuilderParameters pkixParamsExt = (ExtendedPKIXBuilderParameters) ExtendedPKIXBuilderParameters
                .getInstance((PKIXBuilderParameters) pkixParams);
            
             Selector certSelect = pkixParamsExt.getTargetConstraints();
        
            Collection certCol = findCertificates((X509CertStoreSelector)pkixParamsExt.getTargetConstraints(), pkixParamsExt.getCertStores());
            
            System.out.println("CERT COLL " + certCol);
        } catch (Exception w) {
            w.printStackTrace();
        }*/

        // Build and verify the certification chain
        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", "BC");
        PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder.build(pkixParams);
        return result;
    }

    protected static Collection findCertificates(X509CertStoreSelector certSelect,
            List certStores) throws Exception {
        Set certs = new HashSet();
        Iterator iter = certStores.iterator();

        while (iter.hasNext()) {
            Object obj = iter.next();

            if (obj instanceof X509Store) {
                X509Store certStore = (X509Store) obj;
                try {
                    certs.addAll(certStore.getMatches(certSelect));
                } catch (StoreException e) {
                    throw new Exception("Problem while picking certificates from X.509 store.", e);
                }
            } else {
                CertStore certStore = (CertStore) obj;

                try {
                    certs.addAll(certStore.getCertificates(certSelect));
                } catch (CertStoreException e) {
                    throw new Exception(
                            "Problem while picking certificates from certificate store.",
                            e);
                }
            }
        }
        return certs;
    }

    private static KeyStore cargarKeystore(String rutaJks, char[] ksPass) throws Exception {
        KeyStore keyStore = null;
        try {
            File jks = new File(rutaJks);
            if (jks == null) {
                throw new Exception("No se pudo encontrar el keystore [" + rutaJks + "]");
            }
            Security.addProvider(new BouncyCastleProvider());
            keyStore = KeyStore.getInstance("jks");
            InputStream input = new FileInputStream(jks);
            keyStore.load(input, ksPass);
        } catch (IOException ioEx) {
            if (ioEx.getCause() instanceof UnrecoverableKeyException) {
                throw new Exception("La contraseña proporcionada para el eToken no es correcta");
            }
            throw new Exception("Compruebe que el eToken se encuentra conectado al PC.");
        } catch (Exception ex) {
            throw new Exception("Error accediendo al eToken", ex);
        }
        return keyStore;
    }
}
