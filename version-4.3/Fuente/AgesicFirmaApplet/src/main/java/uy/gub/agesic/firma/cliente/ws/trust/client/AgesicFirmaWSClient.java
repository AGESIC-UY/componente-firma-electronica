package uy.gub.agesic.firma.cliente.ws.trust.client;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.exception.WSInvocationException;
import uy.gub.agesic.firma.cliente.exception.WSInvocationTimeOutException;
import uy.gub.agesic.firma.ws.ResultadoValidacion;

/**
 *
 * @author sofis solutions.
 */
public class AgesicFirmaWSClient {

    static ConfigurationUtil cfgUtil = null;
    private static String AGESIC_FIRMA_WS;
    private static Boolean MTOM;
    private static Boolean LOGGING_HANDLER;

    public AgesicFirmaWSClient(HashMap<String, String> options) {
        cfgUtil = new ConfigurationUtil(options);
        AGESIC_FIRMA_WS = cfgUtil.getValue("AGESIC_FIRMA_WS");
        MTOM = cfgUtil.getBooleanValue("MTOM");
        LOGGING_HANDLER = cfgUtil.getBooleanValue("LOGGING_HANDLER");
    }

    public File[] obtenerDocumentos(String idTransaccion, String tipoDocumentos) throws WSInvocationException, WSInvocationTimeOutException {
        return obtenerDocumentosInvocacion(idTransaccion, tipoDocumentos);
    }

    private File[] obtenerDocumentosInvocacion(String idTransaccion, String tipoDocumentos) throws WSInvocationException, WSInvocationTimeOutException {
        try {
            System.out.println("AGESIC_FIRMA_WS" + AGESIC_FIRMA_WS);
            URL urlWSLD = new URL(AGESIC_FIRMA_WS);
            uy.gub.agesic.firma.ws.AgesicFirmaWS service = new uy.gub.agesic.firma.ws.AgesicFirmaWS(urlWSLD,
                    new QName("http://ws.firma.agesic.gub.uy/", "AgesicFirmaWS"));
            uy.gub.agesic.firma.ws.AgesicFirma port = null;
            if (MTOM) {
                MTOMFeature feature = new MTOMFeature(true, 0);
                port = service.getAgesicFirmaWSPort(feature);
            } else {
                port = service.getAgesicFirmaWSPort();
            }
            BindingProvider bp = (BindingProvider) port;
            Integer requestTimeOut = cfgUtil.getIntValue("WS_REQUEST_TIMEOUT");
            Integer connectionTimeOut = cfgUtil.getIntValue("WS_CONNECT_TIMEOUT");
            if (requestTimeOut != null) {
                bp.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", requestTimeOut);
            }
            if (connectionTimeOut != null) {
                bp.getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", connectionTimeOut);
            }
            if (LOGGING_HANDLER) {
                Binding binding = bp.getBinding();
                List handlerList = binding.getHandlerChain();
                if (handlerList == null) {
                    handlerList = new ArrayList();
                }
                LoggingHandler loggingHandler = new LoggingHandler();
                handlerList.add(loggingHandler);
                binding.setHandlerChain(handlerList);
            }
            List<byte[]> documentosDataHandler = port.obtenerDocumentos(idTransaccion);
            //Descarga del archivo.
            File docFile = null;
            File[] docFiles = null;
            if (documentosDataHandler != null) {
                int x = 0;
                docFiles = new File[documentosDataHandler.size()];
                for (byte[] data : documentosDataHandler) {
//                    byte[] data = IOUtils.toByteArray(dataHandler.getInputStream());
                    String path = System.getProperty("java.io.tmpdir") + File.separator + idTransaccion + "_" + x + "." + tipoDocumentos;
                    System.out.println("PATH " + path);
                    //Guardamos el archivo en disco.
                    docFile = new File(path);
                    FileUtils.writeByteArrayToFile(docFile, data);
                    docFiles[x] = docFile;
                    x = x + 1;
                }
            }
            return docFiles;
        } catch (javax.xml.ws.soap.SOAPFaultException e) {
            if (e.getFault().getFaultString().indexOf("java.lang.SecurityException") >= 0) {
            }
            e.printStackTrace();
            throw new WSInvocationException(e);
        } catch (javax.xml.ws.WebServiceException e) {
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                throw new WSInvocationTimeOutException((java.net.SocketTimeoutException) e.getCause());
            }

            e.printStackTrace();
            throw new WSInvocationException(e);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new WSInvocationException(e);
        }
    }

    public List<ResultadoValidacion> comunicarDocumentos(String idTransaccion, File[] documentos, X509Certificate cert) throws WSInvocationException, WSInvocationTimeOutException {
        return this.comunicarDocumentosInvocacion(idTransaccion, documentos, cert);
    }

    private List<ResultadoValidacion> comunicarDocumentosInvocacion(String idTransaccion, File[] documentos, X509Certificate cert) throws WSInvocationException, WSInvocationTimeOutException {
        try {

            URL urlWSLD = new URL(AGESIC_FIRMA_WS);
            uy.gub.agesic.firma.ws.AgesicFirmaWS service = new uy.gub.agesic.firma.ws.AgesicFirmaWS(urlWSLD,
                    new QName("http://ws.firma.agesic.gub.uy/", "AgesicFirmaWS"));
            uy.gub.agesic.firma.ws.AgesicFirma port = null;
            if (MTOM) {
                MTOMFeature feature = new MTOMFeature(true, 0);
                port = service.getAgesicFirmaWSPort(feature);
            } else {
                port = service.getAgesicFirmaWSPort();
            }
            BindingProvider bp = (BindingProvider) port;
            Integer requestTimeOut = cfgUtil.getIntValue("WS_REQUEST_TIMEOUT");
            Integer connectionTimeOut = cfgUtil.getIntValue("WS_CONNECT_TIMEOUT");
            if (requestTimeOut != null) {
                bp.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", requestTimeOut);
            }
            if (connectionTimeOut != null) {
                bp.getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", connectionTimeOut);
            }

            if (LOGGING_HANDLER) {
                Binding binding = bp.getBinding();
                List handlerList = binding.getHandlerChain();
                if (handlerList == null) {
                    handlerList = new ArrayList();
                }
                LoggingHandler loggingHandler = new LoggingHandler();
                handlerList.add(loggingHandler);
                binding.setHandlerChain(handlerList);
            }
            List<byte[]> documentosHandler = new ArrayList();;
            if (documentos != null) {
                for (File file : documentos) {

                    documentosHandler.add(IOUtils.toByteArray(new FileInputStream(file)));
                }
            }

            byte[] certificate = cert.getEncoded();

            List result = port.comunicarDocumentos(idTransaccion, documentosHandler, certificate);

            System.out.println("comunicarDocumentosInvocacion " + result);

            return result;
        } catch (Exception ex) {
            throw new WSInvocationException(ex);
        }
    }
}
