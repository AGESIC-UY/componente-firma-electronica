/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.hash;

import com.itextpdf.text.pdf.TSAClientBouncyCastle;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.exception.*;
import uy.gub.agesic.firma.cliente.store.SofisCertificate;

/**
 *
 * @author sofis-solutions
 */
public class HashSignatureImpl {

    public File signHash(SofisCertificate certificate, String certificatePassword, File hashToSign, String hashSignedPath, String user_data, HashMap<String,String> options) throws CertificateExpiredException, CertificateNotYetValidException, UserVerificationException, IOException, SignException, StoreException, CRLVerificationException, CARootVerificationException, StoreInvalidPasswordException {
        try {
            ConfigurationUtil cfgUtil = new ConfigurationUtil(options);
            String signAlg = cfgUtil.getValue("SIGN_ALG");
            PrivateKey privateKey = certificate.getStore().getPrivateKey(certificate, certificatePassword);
            Signature dsa = Signature.getInstance(signAlg);
            dsa.initSign(privateKey);
            FileUtils.readFileToByteArray(hashToSign);
            byte[] data = FileUtils.readFileToByteArray(hashToSign);
            dsa.update(data);
            byte[] signedData = dsa.sign();
            File toReturn = new File(hashSignedPath); 
            FileUtils.writeByteArrayToFile(toReturn, signedData);
            
            //Verifica que la firma de los bytes sea valida.
            Signature sigver = Signature.getInstance(signAlg);
            sigver.initVerify(certificate.getCertificate().getPublicKey());
            sigver.update(data);
            boolean verSig = sigver.verify(signedData);
            return toReturn;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new SignException(e);
        }

    }
}
