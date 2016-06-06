
package uy.gub.agesic.firma.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for comunicarDocumentos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="comunicarDocumentos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id_transaccion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentos" type="{http://www.w3.org/2001/XMLSchema}base64Binary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="certificate" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "comunicarDocumentos", propOrder = {
    "idTransaccion",
    "documentos",
    "certificate"
})
public class ComunicarDocumentos {

    @XmlElement(name = "id_transaccion")
    protected String idTransaccion;
    protected List<byte[]> documentos;
    @XmlElementRef(name = "certificate", namespace = "http://ws.firma.agesic.gub.uy/", type = JAXBElement.class)
    protected JAXBElement<byte[]> certificate;

    /**
     * Gets the value of the idTransaccion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdTransaccion() {
        return idTransaccion;
    }

    /**
     * Sets the value of the idTransaccion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdTransaccion(String value) {
        this.idTransaccion = value;
    }

    /**
     * Gets the value of the documentos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the documentos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocumentos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * byte[]
     * 
     */
    public List<byte[]> getDocumentos() {
        if (documentos == null) {
            documentos = new ArrayList<byte[]>();
        }
        return this.documentos;
    }

    /**
     * Gets the value of the certificate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public JAXBElement<byte[]> getCertificate() {
        return certificate;
    }

    /**
     * Sets the value of the certificate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public void setCertificate(JAXBElement<byte[]> value) {
        this.certificate = ((JAXBElement<byte[]> ) value);
    }

}
