
package uy.gub.agesic.firma.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uy.gub.agesic.firma.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ObtenerDocumentos_QNAME = new QName("http://ws.firma.agesic.gub.uy/", "obtenerDocumentos");
    private final static QName _ComunicarDocumentosResponse_QNAME = new QName("http://ws.firma.agesic.gub.uy/", "comunicarDocumentosResponse");
    private final static QName _ComunicarDocumentos_QNAME = new QName("http://ws.firma.agesic.gub.uy/", "comunicarDocumentos");
    private final static QName _ObtenerDocumentosResponse_QNAME = new QName("http://ws.firma.agesic.gub.uy/", "obtenerDocumentosResponse");
    private final static QName _ComunicarDocumentosCertificate_QNAME = new QName("http://ws.firma.agesic.gub.uy/", "certificate");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uy.gub.agesic.firma.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ComunicarDocumentos }
     * 
     */
    public ComunicarDocumentos createComunicarDocumentos() {
        return new ComunicarDocumentos();
    }

    /**
     * Create an instance of {@link ObtenerDocumentos }
     * 
     */
    public ObtenerDocumentos createObtenerDocumentos() {
        return new ObtenerDocumentos();
    }

    /**
     * Create an instance of {@link ObtenerDocumentosResponse }
     * 
     */
    public ObtenerDocumentosResponse createObtenerDocumentosResponse() {
        return new ObtenerDocumentosResponse();
    }

    /**
     * Create an instance of {@link ResultadoValidacion }
     * 
     */
    public ResultadoValidacion createResultadoValidacion() {
        return new ResultadoValidacion();
    }

    /**
     * Create an instance of {@link ComunicarDocumentosResponse }
     * 
     */
    public ComunicarDocumentosResponse createComunicarDocumentosResponse() {
        return new ComunicarDocumentosResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerDocumentos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.firma.agesic.gub.uy/", name = "obtenerDocumentos")
    public JAXBElement<ObtenerDocumentos> createObtenerDocumentos(ObtenerDocumentos value) {
        return new JAXBElement<ObtenerDocumentos>(_ObtenerDocumentos_QNAME, ObtenerDocumentos.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComunicarDocumentosResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.firma.agesic.gub.uy/", name = "comunicarDocumentosResponse")
    public JAXBElement<ComunicarDocumentosResponse> createComunicarDocumentosResponse(ComunicarDocumentosResponse value) {
        return new JAXBElement<ComunicarDocumentosResponse>(_ComunicarDocumentosResponse_QNAME, ComunicarDocumentosResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComunicarDocumentos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.firma.agesic.gub.uy/", name = "comunicarDocumentos")
    public JAXBElement<ComunicarDocumentos> createComunicarDocumentos(ComunicarDocumentos value) {
        return new JAXBElement<ComunicarDocumentos>(_ComunicarDocumentos_QNAME, ComunicarDocumentos.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObtenerDocumentosResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.firma.agesic.gub.uy/", name = "obtenerDocumentosResponse")
    public JAXBElement<ObtenerDocumentosResponse> createObtenerDocumentosResponse(ObtenerDocumentosResponse value) {
        return new JAXBElement<ObtenerDocumentosResponse>(_ObtenerDocumentosResponse_QNAME, ObtenerDocumentosResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.firma.agesic.gub.uy/", name = "certificate", scope = ComunicarDocumentos.class)
    public JAXBElement<byte[]> createComunicarDocumentosCertificate(byte[] value) {
        return new JAXBElement<byte[]>(_ComunicarDocumentosCertificate_QNAME, byte[].class, ComunicarDocumentos.class, ((byte[]) value));
    }

}
