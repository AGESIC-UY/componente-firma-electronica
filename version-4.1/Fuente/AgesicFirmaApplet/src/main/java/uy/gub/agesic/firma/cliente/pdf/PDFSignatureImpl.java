package uy.gub.agesic.firma.cliente.pdf;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfLiteral;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPKCS7;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSigGenericPKCS;
import com.itextpdf.text.pdf.PdfSignature;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.TSAClientBouncyCastle;
import eu.bitwalker.useragentutils.UserAgent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import uy.gub.agesic.firma.cliente.applet.SignPanel;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.config.MessagesUtil;
import uy.gub.agesic.firma.cliente.exception.CARootVerificationException;
import uy.gub.agesic.firma.cliente.exception.CRLVerificationException;
import uy.gub.agesic.firma.cliente.exception.SignException;
import uy.gub.agesic.firma.cliente.exception.StoreException;
import uy.gub.agesic.firma.cliente.exception.UserVerificationException;
import uy.gub.agesic.firma.cliente.store.SofisCertificate;
import uy.gub.agesic.firma.cliente.store.StoreManagment;
import uy.gub.agesic.firma.cliente.store.pkcs11.PKCS11CertStoreImpl;
import uy.gub.agesic.firma.cliente.store.pkcs11.PKCS11Util;
import uy.gub.agesic.firma.cliente.validaciones.CARootValidation;
import uy.gub.agesic.firma.cliente.validaciones.CRLValidation;
import uy.gub.agesic.firma.cliente.validaciones.UserCertificateValidation;

/**
 *
 * @author sofis solutions
 */
public class PDFSignatureImpl {

    private String typeToSign = "";

    public PDFSignatureImpl(String typeProvider) {
        typeToSign = typeProvider;
    }

    /**
     * Firma el documento PDF
     *
     * @param certificate certificado para utilizar en la firma
     * @param certificatePassword el password del certificado.
     * @param pdfToSign el documento pdf a firmar
     * @param pdfSignedPath el path en donde se crea el documento pdf firmado
     * @param user_data el usuario que está realizando la firma
     * @return el archivo pdf firmado.
     * @throws CertificateExpiredException si el certifiado está revocado
     * @throws CertificateNotYetValidException si el certificado no es valido
     * @throws InvalidCertificateForUserException si el certificado no
     * corresponde al usuario que está realizando la misma
     * @throws IOException si no se puede acceder al PDF a firmar
     * @throws SignException si se detecta una exception general al firmar.
     * @throws SignException si se detecta una exception al acceder al store del
     * certificado.
     */
    public File signPDF(SofisCertificate certificate, String certificatePassword, File pdfToSign, String pdfSignedPath, String user_data, HashMap<String, String> options) throws CertificateExpiredException, CertificateNotYetValidException, UserVerificationException, IOException, SignException, StoreException, CRLVerificationException, CARootVerificationException {
        ConfigurationUtil cfgUtil = new ConfigurationUtil(options);
        MessagesUtil msgUtil = new MessagesUtil(options);
        //obtenemos los vaores de la configuración 
        boolean pdfApp = cfgUtil.getBooleanValue("PDFSIGNATURE_APPEARANCE_ENABLED");
        String pdfApp_Location = msgUtil.getValue("PDFSIGNATURE_APPEARANCE_LOCATION");
        String pdfApp_Reason = msgUtil.getValue("PDFSIGNATURE_APPEARANCE_REASON");
        String pdfApp_Text = msgUtil.getValue("PDFSIGNATURE_APPEARANCE_TEXT");
        com.itextpdf.text.Image pdfApp_image = msgUtil.getIItextmageValue("PDFSIGNATURE_APPEARANCE_IMAGE");
        boolean tsa = cfgUtil.getBooleanValue("TSA_ENABLED");

        //si se debe validar el certificado en el cliente
        boolean ocsp = cfgUtil.getBooleanValue("OCSP_ENABLED");
        String ocspDefaultUrl = cfgUtil.getValue("OCSP_DEFAULT_URL");
        boolean crl = cfgUtil.getBooleanValue("CRL_ENABLED");
        boolean CA_ROOT_VALIDATION = cfgUtil.getBooleanValue("CA_ROOT_VALIDATION_ENABLE");
        boolean USER_VALIDATION = cfgUtil.getBooleanValue("USER_VALIDATION_ENABLE");

        String digestAlg = cfgUtil.getValue("DIGEST_ALG");
        String signAlg = cfgUtil.getValue("SIGN_ALG");

        //seteamos el provider del Store si el mismo no es nulo
        if (certificate.getStore().getProvider(certificate) != null) {
            Security.addProvider(certificate.getStore().getProvider(certificate));

        }

        Security.addProvider(new BouncyCastleProvider());

        X509Certificate X509cert = (X509Certificate) certificate.getCertificate();
        KeyStore keystore = null;

        //validamos el certificado que se está utilizando para firmar.
        if (USER_VALIDATION) {
            UserCertificateValidation.validateCertificate(X509cert, user_data);
        }

        if (crl) {
            CRLValidation.validateCertificate(X509cert);
        }

        if (CA_ROOT_VALIDATION) {

            Set<X509Certificate> additionalCerts = null;
            try {
                //dado el Keystore con los certificados de confianza, se genera el conjunto de los mismos.
                String password = cfgUtil.getValue("KEYSTORE_TRUSTORE_PASS");
                additionalCerts = new HashSet<X509Certificate>();
                keystore = KeyStore.getInstance(KeyStore.getDefaultType());
                InputStream is = cfgUtil.getInputStream("KEYSTORE_TRUSTORE_NAME");
                if (is != null) {
                    keystore.load(is, password.toCharArray());
                    PKIXParameters params = new PKIXParameters(keystore);
                    for (TrustAnchor ta : params.getTrustAnchors()) {
                        X509Certificate trustCert = ta.getTrustedCert();
                        additionalCerts.add(trustCert);
                    }
                }

            } catch (Exception w) {
                throw new CARootVerificationException(msgUtil.getValue("MSG_CARootValidation_ERROR_GENERAL"));
            }

            if (additionalCerts != null) {
                PKIXCertPathBuilderResult res = CARootValidation.validateCertificate(X509cert, additionalCerts);
                if (res == null) {

                }

            } else {
                throw new CARootVerificationException(msgUtil.getValue("MSG_CARootValidation_ERROR_GENERAL"));
            }

        }

        //crea el documento PDF con la firma
        Certificate[] chain = new Certificate[]{X509cert};
        PdfReader reader = new PdfReader(pdfToSign.getPath());
        FileOutputStream os = new FileOutputStream(pdfSignedPath);
        // Get all the signatures if existing
        AcroFields acroFields = reader.getAcroFields();
        List<String> signatureNames = acroFields.getSignatureNames();
        PdfStamper stamper = null;
        PdfSignatureAppearance appearance = null;
        PrivateKey privateKey = null;
        try {
            privateKey = certificate.getStore().getPrivateKey(certificate, certificatePassword);
            // Choose to append or not the signature        
            if (signatureNames.isEmpty()) {
                stamper = PdfStamper.createSignature(reader, os, '\0');
            } else {
                stamper = PdfStamper.createSignature(reader, os, '\0', null, true);
            }
            appearance = stamper.getSignatureAppearance();
            if (pdfApp_image != null) {
                appearance.setImage(pdfApp_image);
            }
            if (pdfApp) {
                //en caso que la firma no se visualice, se debe modificar las dimensiones del rectangulo verficar "/CropBox del pdf
                appearance.setVisibleSignature(new Rectangle(20, 20, 80, 80), 1, null);
                appearance.setLayer2Text(pdfApp_Text);
            }

        } catch (Throwable e) {
            e.printStackTrace();
            throw new SignException(e);
        }
        if (digestAlg.equalsIgnoreCase("SHA-1")) {
            try {
                appearance.setCrypto(privateKey, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
                if (certificate.getStore().getProvider(certificate) != null) {
                    appearance.setProvider(certificate.getStore().getProvider(certificate).getName());
                }
                TSAClientBouncyCastle tsaClient = null;
                if (tsa) {
                    String TSA_URL = cfgUtil.getValue("TSA_URL");
                    String TSA_USER = cfgUtil.getValue("TSA_USER");
                    String TSA_PASSWORD = cfgUtil.getValue("TSA_PASSWORD");
                    //peticion a la TSA
                    tsaClient = new TSAClientBouncyCastle(TSA_URL, TSA_USER, TSA_PASSWORD, 6500, "SHA256");

                }
                byte[] ocspBytes = null;
                if (ocsp) {
                    String url = PdfPKCS7.getOCSPURL((X509Certificate) chain[0]);
                    if (url == null || url.equalsIgnoreCase("")) {
                        url = ocspDefaultUrl;
                    }
                    CertificateFactory cf = CertificateFactory.getInstance("X509");
                    //FileInputStream is = new FileInputStream(properties.getProperty("ROOTCERT"));
                    //X509Certificate root = (X509Certificate) cf.generateCertificate(is);
                    //ocspBytes = new OcspClientBouncyCastle().getEncoded((X509Certificate) chain[0], root, url);
                }

                PdfSigGenericPKCS sig = null;
                if (certificate.getStore().getProvider(certificate) != null) {
                    sig = new PdfSigGenericPKCS.PPKMS(certificate.getStore().getProvider(certificate).getName());
                } else {
                    sig = new PdfSigGenericPKCS.PPKMS();
                }
                final HashMap<PdfName, Integer> exclusionSizes = new HashMap<PdfName, Integer>();

                if (pdfApp_Location != null && !pdfApp_Location.equalsIgnoreCase("")) {
                    sig.setLocation(pdfApp_Location);
                }
                if (pdfApp_Reason != null && !pdfApp_Reason.equalsIgnoreCase("")) {
                    sig.setReason(pdfApp_Reason);
                }
                sig.setName(PdfPKCS7.getSubjectFields(X509cert).getField("CN"));
                sig.setDate(new PdfDate(Calendar.getInstance()));
                // signing stuff
                final byte[] digest = new byte[256];
                final byte[] rsaData = new byte[20];
                sig.setExternalDigest(digest, rsaData, "RSA");
                sig.setSignInfo(privateKey, chain, null);
                final PdfString contents = (PdfString) sig.get(PdfName.CONTENTS);
                // *2 to get hex size, +2 for delimiters
                final int timestampSize = 4400;
                PdfLiteral contentsLit = new PdfLiteral((contents.toString().length() + timestampSize) * 2 + 2);
                exclusionSizes.put(PdfName.CONTENTS, new Integer(contentsLit.getPosLength()));
                sig.put(PdfName.CONTENTS, contentsLit);

                //appearance.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);
                appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);

                // process all the information set above
                appearance.setCryptoDictionary(sig);
                appearance.preClose(exclusionSizes);

                // calculate digest (hash)
                final MessageDigest messageDigest = MessageDigest.getInstance(digestAlg);

                final byte[] buf = new byte[8192];
                int n;
                final java.io.InputStream inp = appearance.getRangeStream();
                while ((n = inp.read(buf)) != -1) {
                    messageDigest.update(buf, 0, n);
                }
                final byte[] hash = messageDigest.digest();
                final Signature sign = Signature.getInstance(signAlg);

                sign.initSign(privateKey);
                sign.update(hash);
                final byte[] signature = sign.sign();

                contentsLit = (PdfLiteral) sig.get(PdfName.CONTENTS);
                final byte[] outc = new byte[(contentsLit.getPosLength() - 2) / 2];
                final PdfPKCS7 pkcs7 = sig.getSigner();
                pkcs7.setExternalDigest(signature, hash, "RSA");
                final PdfDictionary dic = new PdfDictionary();

                final byte[] ssig = pkcs7.getEncodedPKCS7(null, null, tsaClient, ocspBytes);
                System.arraycopy(ssig, 0, outc, 0, ssig.length);

                dic.put(PdfName.CONTENTS, new PdfString(outc).setHexWriting(true));

                appearance.close(dic);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new SignException(e);
            }

            return new File(pdfSignedPath);
        } else {
            //SHA-256
            try {

                PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
                appearance.setSignDate(new GregorianCalendar());
                dic.setDate(new PdfDate(appearance.getSignDate()));
                dic.setName(PdfPKCS7.getSubjectFields((X509Certificate) chain[0]).getField("CN"));
                if (pdfApp_Location != null && !pdfApp_Location.equalsIgnoreCase("")) {
                    dic.setLocation(pdfApp_Location);
                }
                if (pdfApp_Reason != null && !pdfApp_Reason.equalsIgnoreCase("")) {
                    dic.setReason(pdfApp_Reason);
                }
                appearance.setCryptoDictionary(dic);

                int csize = 4000;
                HashMap exc = new HashMap();
                exc.put(PdfName.CONTENTS, new Integer(csize * 2 + 2));
                appearance.preClose(exc);
                ArrayList list = new ArrayList();
                for (int i = 0; i < chain.length; i++) {
                    list.add(chain[i]);
                }
                CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
                CMSProcessable content = new CMSProcessableRange(appearance);

                 //added by mike
                /*
                 CertStore chainStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(list));
                 generator.addCertificatesAndCRLs(chainStore);
                 CMSSignedData signedData = generator.generate(content,Security.getProvider("Sun
               
                 eToken1").getName());
                 */
                String provider = "BC";
                PKCS11Util util = new PKCS11Util(cfgUtil, msgUtil);

                if (typeToSign.equals("TOKEN")) {
                    provider = util.getActiveProvider().getName();
                }

                ContentSigner sha256Signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider(provider).build(privateKey);
         //Service containSha256=  util.getActiveProvider().getService("MessageDigest","SHA-256");
                //  Service containSha1=util.getActiveProvider().getService("MessageDigest", "SHA-1");
                //String algorithm= containSha256.getAlgorithm();
                //Service containSha1=util.getActiveProvider().getService("MessageDigest", "SHA-1");
                //String algorithm=    containSha256.getAlgorithm();

                generator.addSignerInfoGenerator(
                        new JcaSignerInfoGeneratorBuilder(
                                new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
                        .build(sha256Signer, (X509Certificate) chain[0]));

                Store certs = new JcaCertStore(list);
                generator.addCertificates(certs);
                //generator.addSigner(privateKey, (X509Certificate) chain[0], CMSSignedDataGenerator.DIGEST_SHA256);
                CMSSignedData signedData = generator.generate(content, false, provider);

//original
              /*
                 generator.addSigner(privateKey, (X509Certificate) chain[0], CMSSignedDataGenerator.DIGEST_SHA1);
                 CertStore chainStore = CertStore.getInstance("Collection",new CollectionCertStoreParameters(list),"BC");
                 generator.addCertificatesAndCRLs(chainStore);
                 Provider prov = Security.getProvider("SunPKCS11-epass2003");
                 CMSSignedData signedData = generator.generate(content, false, "BC" );
                 */
                byte[] pk = signedData.getEncoded();

                byte[] outc = new byte[csize];

                PdfDictionary dic2 = new PdfDictionary();

                System.arraycopy(pk, 0, outc, 0, pk.length);

                dic2.put(PdfName.CONTENTS, new PdfString(outc).setHexWriting(true));
                appearance.close(dic2);

                return new File(pdfSignedPath);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new SignException(e);
            }

        }

    }

    /**
     * Dado un archivo PDF que contiene un numero X de firmas, retorna el array
     * de los certificados utilizados para realizar dichas firmas
     *
     * @param pdfFileNameSigned
     * @return
     * @throws IOException
     */
    public List<X509Certificate> getSignCertificate(String pdfFileNameSigned) throws IOException {

        PdfReader reader = new PdfReader(pdfFileNameSigned);
        ArrayList toReturn = new ArrayList();
        AcroFields af = reader.getAcroFields();
        ArrayList<String> names = af.getSignatureNames();
        for (String name : names) {
            PdfPKCS7 pk = af.verifySignature(name);
            toReturn.add(pk.getSigningCertificate());
        }
        return toReturn;
    }

    public class CMSProcessableRange implements CMSProcessable {

        private PdfSignatureAppearance sap;
        private byte[] buf = new byte[8192];

        public CMSProcessableRange(PdfSignatureAppearance sap) {
            this.sap = sap;
        }

        public void write(OutputStream outputStream) throws IOException, CMSException {
            InputStream s = sap.getRangeStream();
            ByteArrayOutputStream ss = new ByteArrayOutputStream();
            int read = 0;
            while ((read = s.read(buf, 0, buf.length)) > 0) {
                outputStream.write(buf, 0, read);
            }
        }

        public Object getContent() {
            return sap;
        }
    }

    public static void main(String[] args) throws Exception {
        //TSAClientBouncyCastle tsaClient = new TSAClientBouncyCastle("http://tsa.safelayer.com:8093/", null, null);
        SignPanel forTest = new SignPanel();
        String userAgentExp = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36";
        UserAgent tmp = UserAgent.parseUserAgentString(userAgentExp + "");
        StoreManagment store = new StoreManagment();
        PKCS11CertStoreImpl tokenStore = new PKCS11CertStoreImpl(PKCS11CertStoreImpl.PKCS11CertStoreType.ETOKEN, tmp, null);
        store.addCertStore(tokenStore);
        List<SofisCertificate> certForTest = store.getSignCertificateFromPKCS11("1234", null, PKCS11CertStoreImpl.PKCS11CertStoreType.ETOKEN, "");
        PDFSignatureImpl sign = new PDFSignatureImpl("ARCHIVO");
        File pdfToSign = new File("/home/mike/Desktop/FirmaApplet/pdf_sin_firmar.pdf");
        String pdfDir = System.getProperty("java.io.tmpdir") + File.separator + pdfToSign.getName().replaceFirst(".pdf", "") + "_signed.pdf";

        File pdfSigned = sign.signPDF(certForTest.get(0), "", pdfToSign, "/home/mike/Desktop/FirmaApplet/pdf_firmado.pdf", null, null);

    }

}
