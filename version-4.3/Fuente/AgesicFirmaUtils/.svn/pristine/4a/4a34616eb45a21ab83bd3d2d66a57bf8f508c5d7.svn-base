/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agesic.firma.validaciones.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import org.agesic.firma.datatypes.ResultadoValidacion;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;

/**
 * Clase que verifica un certificado X509 contra un CRLS
 *
 * @author Sofis Solutions
 */
public class CRLValidation {

    /**
     * Extrae la url de la CRL del certificado, si este posee la extension
     * correspondiente y se fija el estado de revocacion del certificado contra
     * la CRL
     *
     * @param cert el certificado a validar
     * @throws CertificateVerificationException if the certificate fue revocado
     */
    public static ResultadoValidacion validateCertificate(X509Certificate cert, String CRL_DEFAULT_URL) throws Exception {
        //para esto el certificado debe tener la extension 2.5.29.31 - CRL Distribution Points.
        //En esta seccion está la url desde donde se descargan los certificados revocados
        List<String> crlDistPoints = new ArrayList();
        try {
            crlDistPoints = getCrlDistributionPoints(cert);
        } catch (Exception w) {
            throw w;
        }
        if (crlDistPoints == null || crlDistPoints.isEmpty()) {
            String crlDP = CRL_DEFAULT_URL;
            if (crlDP == null || crlDP.equalsIgnoreCase("")) {
                ResultadoValidacion resultadoValidacion = new ResultadoValidacion(FirmaValidate.REVOKED_CERTIFICATE_ERROR_CODE, "No se pudo obtener la URL de descarga de la lista de revocación: " + crlDP, null);
                return resultadoValidacion;
            }
        }

        if (crlDistPoints == null || crlDistPoints.isEmpty()) {
            String crlDP = CRL_DEFAULT_URL;
            if (crlDP != null && !crlDP.equalsIgnoreCase("")) {
                X509CRL crl = null;
                try {
                    crl = downloadCRL(crlDP);
                } catch (Exception w) {
                    ResultadoValidacion resultadoValidacion = new ResultadoValidacion(FirmaValidate.REVOKED_CERTIFICATE_ERROR_CODE, "No se pudo obtener la URL de descarga de la lista de revocación: " + crlDP, null);
                    return resultadoValidacion;
                }
                
                if (crl == null){
                    ResultadoValidacion resultadoValidacion = new ResultadoValidacion(FirmaValidate.REVOKED_CERTIFICATE_ERROR_CODE, "No se pudo obtener la URL de descarga de la lista de revocación: " + crlDP, null);
                    return resultadoValidacion;
                }
                if (crl != null && crl.isRevoked(cert)) {
                    ResultadoValidacion resultadoValidacion = new ResultadoValidacion(FirmaValidate.REVOKED_CERTIFICATE_CONF_ERROR_CODE, crlDP, null);
                    return resultadoValidacion;
                    //throw new Exception(crlDP);
                }
            }
        } else {
            for (String crlDP : crlDistPoints) {
                X509CRL crl = null;
                try {
                    crl = downloadCRL(crlDP);
                } catch (Exception w) {
                    w.printStackTrace();
                    ResultadoValidacion resultadoValidacion = new ResultadoValidacion(FirmaValidate.REVOKED_CERTIFICATE_ERROR_CODE, "No se pudo obtener la URL de descarga de la lista de revocación: " + crlDP, null);
                    return resultadoValidacion;
                }
                if (crl != null && crl.isRevoked(cert)) {
                    //throw new Exception(crlDP);
                    ResultadoValidacion resultadoValidacion = new ResultadoValidacion(FirmaValidate.REVOKED_CERTIFICATE_CONF_ERROR_CODE, crlDP, null);
                    return resultadoValidacion;
                }
            }
        }
        return null;
    }

    /**
     * Descarga la CRL a partir de la URL dada. Soporta http, https, ftp.
     */
    private static X509CRL downloadCRL(String crlURL) throws Exception {


        if (crlURL.startsWith("http://") || crlURL.startsWith("https://")
                || crlURL.startsWith("ftp://")) {
            X509CRL crl = downloadCRLFromWeb(crlURL);
            return crl;
        }
        return null;
    }

    /**
     * Descarga la CRL desde la URL dada (HTTP/HTTPS/FTP)
     */
    private static X509CRL downloadCRLFromWeb(String crlURL)
            throws MalformedURLException, IOException, CertificateException,
            CRLException {
        URL url = new URL(crlURL);
        InputStream crlStream = url.openStream();
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL crl = (X509CRL) cf.generateCRL(crlStream);
            return crl;
        } finally {
            crlStream.close();
        }
    }

    /**
     * Extrae del certificado todas las URL de las CRLs si el certificado posee
     * la extension 2.5.29.31 - CRL Distribution Points. Si no la posee retorna
     * la lista vacia
     */
    private static List<String> getCrlDistributionPoints(X509Certificate cert) throws CertificateParsingException, IOException {
        byte[] crldpExt = cert.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());
        if (crldpExt == null) {
            List<String> emptyList = new ArrayList<String>();
            return emptyList;
        }
        ASN1InputStream oAsnInStream = new ASN1InputStream(new ByteArrayInputStream(crldpExt));
        DERObject derObjCrlDP = oAsnInStream.readObject();
        DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
        byte[] crldpExtOctets = dosCrlDP.getOctets();
        ASN1InputStream oAsnInStream2 = new ASN1InputStream(new ByteArrayInputStream(crldpExtOctets));
        DERObject derObj2 = oAsnInStream2.readObject();
        CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);
        List<String> crlUrls = new ArrayList<String>();
        for (DistributionPoint dp : distPoint.getDistributionPoints()) {
            DistributionPointName dpn = dp.getDistributionPoint();
            // Look for URIs in fullName
            if (dpn != null) {
                if (dpn.getType() == DistributionPointName.FULL_NAME) {
                    GeneralName[] genNames = GeneralNames.getInstance(
                            dpn.getName()).getNames();
                    // Look for an URI
                    for (int j = 0; j < genNames.length; j++) {
                        if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                            String url = DERIA5String.getInstance(
                                    genNames[j].getName()).getString();
                            crlUrls.add(url);
                        }
                    }
                }
            }
        }
        return crlUrls;
    }
}
