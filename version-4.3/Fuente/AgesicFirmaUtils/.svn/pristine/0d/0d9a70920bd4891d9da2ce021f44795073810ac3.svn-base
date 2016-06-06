/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agesic.firma.validaciones.util;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfPKCS7;
import com.itextpdf.text.pdf.PdfReader;
import es.mityc.firmaJava.libreria.xades.DatosFirma;
import es.mityc.firmaJava.libreria.xades.ExtraValidators;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import org.agesic.firma.datatypes.ResultadoValidacion;
import es.mityc.firmaJava.libreria.xades.ValidarFirmaXML;
import es.mityc.firmaJava.libreria.xades.errores.FirmaXMLError;
import es.mityc.javasign.xml.xades.policy.IValidacionPolicy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import org.agesic.firma.config.ConfigurationUtil;
import org.agesic.firma.utils.Constantes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import uy.gub.agesic.firma.TipoDocumento;


/**
 *
 * @author usuario
 */
public class FirmaValidate {

    private static ConfigurationUtil cfgUtil = new ConfigurationUtil(null);
    public static String INVALID_CERTIFICATE_ERROR_CODE = "INVALID_CERTIFICATE";
    public static String EXPIRED_CERTIFICATE_ERROR_CODE = "EXPIRED_CERTIFICATE";
    public static String REVOKED_CERTIFICATE_ERROR_CODE = "REVOKED_CERTIFICATE";
    public static String CERTIFICATE_NOT_TRUSTED_ERROR_CODE = "CERTIFICATE_NOT_TRUSTED";
    public static String INVALID_DIGEST = "INVALID_DIGEST";
    public static String INVALID_SIGN_ERROR_CODE = "INVALID_SIGN";
    public static String INVALID_GENERAL = "INVALID_GENERAL";

    public static List<ResultadoValidacion> validarFirma(byte[] docs,byte[] sign, String tipoFirma, X509Certificate cert, HashMap<String, String> opciones) {
        cfgUtil = new ConfigurationUtil(opciones);
        List<ResultadoValidacion> toReturn = new ArrayList();
        try {
            if (Constantes.TIPO_FIRMA_PDF.equals(tipoFirma)) {

                List resultadoValidacionPDF = validarFirmaPDF(docs);
                if (!resultadoValidacionPDF.isEmpty()) {
                    return resultadoValidacionPDF;
                }
            }
            if (Constantes.TIPO_FIRMA_XML.equals(tipoFirma)) {

                List resultadoValidacionXML = validarFirmaXML(docs);
                if (!resultadoValidacionXML.isEmpty()) {
                    return resultadoValidacionXML;
                }

            }
            if (Constantes.TIPO_FIRMA_HASH.equals(tipoFirma)) {

                List resultadoValidacionHASH = validarFirmaHASH(docs, sign,cert);
                if (!resultadoValidacionHASH.isEmpty()) {
                    return resultadoValidacionHASH;
                }

            }
        } catch (Exception ex) {
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_GENERAL, ex.getLocalizedMessage(), null);
            toReturn.add(resultadoValidacion);
            return toReturn;
        }
        return toReturn;

    }

    public static List<ResultadoValidacion> validarFirma(byte[] docs, byte[] sign,String tipoFirma, byte[] cert) {
        List<ResultadoValidacion> toReturn = new ArrayList();
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate x509Certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert));
            return validarFirma(docs,sign, tipoFirma, x509Certificate, null);
        } catch (CertificateException ex) {
            Logger.getLogger(FirmaValidate.class.getName()).log(Level.SEVERE, null, ex);
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_CERTIFICATE_ERROR_CODE, ex.getLocalizedMessage(), null);
            toReturn.add(resultadoValidacion);
            return toReturn;
        }
    }

    public static List<ResultadoValidacion> validarFirma(List<DataHandler> docs, String tipoFirma, byte[] cert, byte[] hashSign) {
        
        
        
        List<ResultadoValidacion> toReturn = new ArrayList();
        try {
            if (Constantes.TIPO_FIRMA_PDF.equals(tipoFirma)) {
                for (DataHandler d : docs) {
                    List resultadoValidacionPDF = validarFirmaPDF(IOUtils.toByteArray(d.getInputStream()));
                    if (!resultadoValidacionPDF.isEmpty()) {
                        return resultadoValidacionPDF;
                    }

                }
            }
            if (Constantes.TIPO_FIRMA_XML.equals(tipoFirma)) {
                for (DataHandler d : docs) {
                    List resultadoValidacionXML = validarFirmaXML(IOUtils.toByteArray(d.getInputStream()));
                    if (!resultadoValidacionXML.isEmpty()) {
                        return resultadoValidacionXML;
                    }
                }
            }
            if (Constantes.TIPO_FIRMA_HASH.equals(tipoFirma)) {
                for (DataHandler d : docs) {
                    List resultadoValidacionHASH = validarFirmaHASH(IOUtils.toByteArray(d.getInputStream()),hashSign, cert);
                    if (!resultadoValidacionHASH.isEmpty()) {
                        return resultadoValidacionHASH;
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(FirmaValidate.class.getName()).log(Level.SEVERE, null, ex);
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_GENERAL, ex.getLocalizedMessage(), null);
            toReturn.add(resultadoValidacion);
            return toReturn;
        } catch (Exception ex) {
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_GENERAL, ex.getLocalizedMessage(), null);
            toReturn.add(resultadoValidacion);
            return toReturn;
        }
        return toReturn;
    }

    public static List<ResultadoValidacion> validarFirmaPDF(byte[] data) {
        List<ResultadoValidacion> toReturn = new ArrayList();

        try {
            PdfReader reader = new PdfReader(data);
            AcroFields af = reader.getAcroFields();

            List<String> names = af.getSignatureNames();
            if (names == null || names.isEmpty()) {
                ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_SIGN_ERROR_CODE, "No se encuentra una firma en el documento.", null);
                toReturn.add(resultadoValidacion);
                return toReturn;
            }

            for (int k = 0; k < names.size(); ++k) {
                String name = names.get(k);
                PdfPKCS7 pk = af.verifySignature(name);
                Calendar cal = pk.getSignDate();
                Certificate pkc[] = pk.getCertificates();
                if (!pk.verify()) {
                    ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_DIGEST, "El documento ha sido modificado luego de firmado.", null);
                    toReturn.add(resultadoValidacion);
                    return toReturn;
                }
                for (Certificate x509Certificate : pkc) {
                    //se valida si el certificado expiro
                    try {
                        ResultadoValidacion resultadoValidacion = ExpiredValidation.validate((X509Certificate) x509Certificate, cal);
                        if (resultadoValidacion != null) {
                            toReturn.add(resultadoValidacion);
                            //return toReturn;
                        }
                    } catch (Exception w) {
                        w.printStackTrace();
                    }

                    boolean validarConfianza = cfgUtil.getBooleanValue("CA_ROOT_VALIDATION_ENABLE");
                    if (validarConfianza) {
                        //se valida la confianza
                        try {
                            String pathTrustore = cfgUtil.getValue("KEYSTORE_TRUSTORE_PATH");
                            String passTrustore = cfgUtil.getValue("KEYSTORE_TRUSTORE_PASS");
                            if (pathTrustore != null && !pathTrustore.equalsIgnoreCase("") && passTrustore != null && !passTrustore.equalsIgnoreCase("")) {
                                ResultadoValidacion resultadoValidacion = CARootValidation.validate((X509Certificate) x509Certificate, pathTrustore, passTrustore.toCharArray());
                                if (resultadoValidacion != null) {
                                    toReturn.add(resultadoValidacion);
                                    //return toReturn;
                                }
                            }
                        } catch (Exception w) {
                            w.printStackTrace();

                        }

                    }

                    boolean validarCrl = cfgUtil.getBooleanValue("CRL_ENABLED");
                    if (validarCrl) {
                        String CRL_DEFAULT_URL = null;
                        if (cfgUtil.contains("CRL_DEFAULT_URL")) {
                            CRL_DEFAULT_URL = cfgUtil.getValue("CRL_DEFAULT_URL");
                        }
                        //se valida el CRL
                        try {
                            ResultadoValidacion resultadoValidacion = CRLValidation.validateCertificate((X509Certificate) x509Certificate, CRL_DEFAULT_URL);
                            if (resultadoValidacion != null) {
                                toReturn.add(resultadoValidacion);
                                //return toReturn;
                            }
                        } catch (Exception w) {
                            w.printStackTrace();
                        }

                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(FirmaValidate.class.getName()).log(Level.SEVERE, null, ex);
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_GENERAL, ex.getLocalizedMessage(), null);
            toReturn.add(resultadoValidacion);
            return toReturn;
        }
        return toReturn;
    }

    public static List<ResultadoValidacion> validarFirmaHASH(byte[] doc,byte[]sign ,X509Certificate x509Certificate) {
        List<ResultadoValidacion> toReturn = new ArrayList();
        try {
            
            //Verifica que la firma de los bytes sea valida.
            String signAlg = cfgUtil.getValue("SIGN_ALG");
            Signature sigver = Signature.getInstance(signAlg);
            sigver.initVerify(x509Certificate.getPublicKey());
            sigver.update(doc);

            //se valida si el certificado expiro
            try {
                ResultadoValidacion resultadoValidacion = ExpiredValidation.validate(x509Certificate, null);
                if (resultadoValidacion != null) {
                    toReturn.add(resultadoValidacion);
                    //return toReturn;
                }

            } catch (Exception w) {
                w.printStackTrace();
            }

            boolean validarConfianza = cfgUtil.getBooleanValue("CA_ROOT_VALIDATION_ENABLE");
            if (validarConfianza) {
                //se valida la confianza
                try {
                    String pathTrustore = cfgUtil.getValue("KEYSTORE_TRUSTORE_PATH");
                    String passTrustore = cfgUtil.getValue("KEYSTORE_TRUSTORE_PASS");
                    if (pathTrustore != null && !pathTrustore.equalsIgnoreCase("") && passTrustore != null && !passTrustore.equalsIgnoreCase("")) {
                        ResultadoValidacion resultadoValidacion = CARootValidation.validate(x509Certificate, pathTrustore, passTrustore.toCharArray());
                        if (resultadoValidacion != null) {
                            toReturn.add(resultadoValidacion);
                            //return toReturn;
                        }
                    }
                } catch (Exception w) {
                    w.printStackTrace();

                }

            }

            boolean validarCrl = cfgUtil.getBooleanValue("CRL_ENABLED");
            if (validarCrl) {
                //se valida el CRL
                String CRL_DEFAULT_URL = null;
                if (cfgUtil.contains("CRL_DEFAULT_URL")) {
                    CRL_DEFAULT_URL = cfgUtil.getValue("CRL_DEFAULT_URL");
                }
                try {
                    ResultadoValidacion resultadoValidacion = CRLValidation.validateCertificate(x509Certificate, CRL_DEFAULT_URL);
                    if (resultadoValidacion != null) {
                        toReturn.add(resultadoValidacion);
                        //return toReturn;
                    }
                } catch (Exception w) {
                    w.printStackTrace();
                }

            }



            boolean verify = sigver.verify(sign);
            if (!verify) {
                ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_SIGN_ERROR_CODE, "Firma no valida", null);
                toReturn.add(resultadoValidacion);
            }

            return toReturn;

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FirmaValidate.class.getName()).log(Level.SEVERE, null, ex);
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_GENERAL, ex.getLocalizedMessage(), null);
            toReturn.add(resultadoValidacion);
            return toReturn;

        } catch (SignatureException ex) {
            Logger.getLogger(FirmaValidate.class.getName()).log(Level.SEVERE, null, ex);
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_SIGN_ERROR_CODE, ex.getLocalizedMessage(), null);
            toReturn.add(resultadoValidacion);
            return toReturn;
        } catch (InvalidKeyException ex) {
            Logger.getLogger(FirmaValidate.class.getName()).log(Level.SEVERE, null, ex);
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_GENERAL, ex.getLocalizedMessage(), null);
            toReturn.add(resultadoValidacion);
            return toReturn;
        }
    }

    public static List<ResultadoValidacion> validarFirmaHASH(byte[] doc, byte[] sign, byte[] cert) {
        List<ResultadoValidacion> toReturn = new ArrayList();
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate x509Certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert));
            return validarFirmaHASH(doc,sign, x509Certificate);
        } catch (CertificateException ex) {
            Logger.getLogger(FirmaValidate.class.getName()).log(Level.SEVERE, null, ex);
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_CERTIFICATE_ERROR_CODE, ex.getLocalizedMessage(), null);
            toReturn.add(resultadoValidacion);
            return toReturn;
        }


    }

    public static List<ResultadoValidacion> validarFirmaXML(byte[] data) {


        ArrayList<IValidacionPolicy> arrayPolicies = new ArrayList<IValidacionPolicy>(1);
        IValidacionPolicy policy = new CertPolicy();
        arrayPolicies.add(policy);
        // Validadores extra
        ExtraValidators validator = new ExtraValidators(arrayPolicies, null, null);
        List<es.mityc.firmaJava.libreria.xades.ResultadoValidacion> results = null;
        List<ResultadoValidacion> toReturn = new ArrayList();
        try {

            ValidarFirmaXML vXml = new ValidarFirmaXML();
            results = vXml.validar(data, validator);
        } catch (FirmaXMLError e) {
            e.printStackTrace();
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_GENERAL, e.getLocalizedMessage(), null);
            toReturn.add(resultadoValidacion);
            return toReturn;
        }

        // Se muestra por consola el resultado de la validación
        es.mityc.firmaJava.libreria.xades.ResultadoValidacion result = null;
        Iterator<es.mityc.firmaJava.libreria.xades.ResultadoValidacion> it = results.iterator();
        while (it.hasNext()) {
            result = it.next();
            boolean isValid = result.isValidate();
            if (isValid) {
            } else {

                CertPath cp = result.getDatosFirma().getCadenaFirma();
                X509Certificate x509Certificate = (X509Certificate) cp.getCertificates().get(0);


                //se valida si el certificado expiro
                try {
                    ResultadoValidacion resultadoValidacion = ExpiredValidation.validate(x509Certificate, null);
                    if (resultadoValidacion != null) {
                        toReturn.add(resultadoValidacion);
                        //return toReturn;
                    }

                } catch (Exception w) {
                    w.printStackTrace();
                }

                boolean validarConfianza = cfgUtil.getBooleanValue("CA_ROOT_VALIDATION_ENABLE");
                if (validarConfianza) {
                    //se valida la confianza
                    try {
                        String pathTrustore = cfgUtil.getValue("KEYSTORE_TRUSTORE_PATH");
                        String passTrustore = cfgUtil.getValue("KEYSTORE_TRUSTORE_PASS");
                        if (pathTrustore != null && !pathTrustore.equalsIgnoreCase("") && passTrustore != null && !passTrustore.equalsIgnoreCase("")) {
                            ResultadoValidacion resultadoValidacion = CARootValidation.validate(x509Certificate, pathTrustore, passTrustore.toCharArray());
                            if (resultadoValidacion != null) {
                                toReturn.add(resultadoValidacion);
                                //return toReturn;
                            }
                        }
                    } catch (Exception w) {
                        w.printStackTrace();

                    }

                }
                boolean validarCrl = cfgUtil.getBooleanValue("CRL_ENABLED");
                if (validarCrl) {
                    //se valida el CRL
                    String CRL_DEFAULT_URL = null;
                    if (cfgUtil.contains("CRL_DEFAULT_URL")) {
                        CRL_DEFAULT_URL = cfgUtil.getValue("CRL_DEFAULT_URL");
                    }
                    try {
                        ResultadoValidacion resultadoValidacion = CRLValidation.validateCertificate(x509Certificate, CRL_DEFAULT_URL);
                        if (resultadoValidacion != null) {
                            toReturn.add(resultadoValidacion);
                            //return toReturn;
                        }
                    } catch (Exception w) {
                        w.printStackTrace();
                    }

                }

                ResultadoValidacion resultadoValidacion = new ResultadoValidacion(INVALID_GENERAL, result.getLog(), null);
                toReturn.add(resultadoValidacion);
            }
        }

        return toReturn;
    }

    public static void main(String[] args) {

        String file = "C:\\Users\\sofis\\Documents\\hello_signed - copia.pdf";
        File fileF = new File(file);
        try {
            List map = FirmaValidate.validarFirmaPDF(FileUtils.readFileToByteArray(fileF));
            System.out.println("MAP " + map);
        } catch (IOException ex) {
            Logger.getLogger(FirmaValidate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
