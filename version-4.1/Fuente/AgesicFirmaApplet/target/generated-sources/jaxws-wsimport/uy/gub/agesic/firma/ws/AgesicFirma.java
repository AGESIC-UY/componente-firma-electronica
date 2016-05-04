
package uy.gub.agesic.firma.ws;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "AgesicFirma", targetNamespace = "http://ws.firma.agesic.gub.uy/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface AgesicFirma {


    /**
     * 
     * @param idTransaccion
     * @return
     *     returns java.util.List<byte[]>
     */
    @WebMethod
    @WebResult(name = "respuesta", targetNamespace = "http://ws.firma.agesic.gub.uy/")
    @RequestWrapper(localName = "obtenerDocumentos", targetNamespace = "http://ws.firma.agesic.gub.uy/", className = "uy.gub.agesic.firma.ws.ObtenerDocumentos")
    @ResponseWrapper(localName = "obtenerDocumentosResponse", targetNamespace = "http://ws.firma.agesic.gub.uy/", className = "uy.gub.agesic.firma.ws.ObtenerDocumentosResponse")
    public List<byte[]> obtenerDocumentos(
        @WebParam(name = "id_transaccion", targetNamespace = "http://ws.firma.agesic.gub.uy/")
        String idTransaccion);

    /**
     * 
     * @param certificate
     * @param documentos
     * @param idTransaccion
     * @return
     *     returns java.util.List<uy.gub.agesic.firma.ws.ResultadoValidacion>
     */
    @WebMethod
    @WebResult(name = "respuesta", targetNamespace = "http://ws.firma.agesic.gub.uy/")
    @RequestWrapper(localName = "comunicarDocumentos", targetNamespace = "http://ws.firma.agesic.gub.uy/", className = "uy.gub.agesic.firma.ws.ComunicarDocumentos")
    @ResponseWrapper(localName = "comunicarDocumentosResponse", targetNamespace = "http://ws.firma.agesic.gub.uy/", className = "uy.gub.agesic.firma.ws.ComunicarDocumentosResponse")
    public List<ResultadoValidacion> comunicarDocumentos(
        @WebParam(name = "id_transaccion", targetNamespace = "http://ws.firma.agesic.gub.uy/")
        String idTransaccion,
        @WebParam(name = "documentos", targetNamespace = "http://ws.firma.agesic.gub.uy/")
        List<byte[]> documentos,
        @WebParam(name = "certificate", targetNamespace = "http://ws.firma.agesic.gub.uy/")
        byte[] certificate);

}