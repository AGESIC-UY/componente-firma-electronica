/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agesic.firma.validaciones.util;


import es.mityc.firmaJava.libreria.xades.ResultadoValidacion;
import es.mityc.javasign.trust.TrustAbstract;
import es.mityc.javasign.xml.xades.policy.IValidacionPolicy;
import es.mityc.javasign.xml.xades.policy.PolicyResult;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.w3c.dom.Element;

/**
 *
 * @author usuario
 */
public class CertPolicy implements IValidacionPolicy {
  
  	public String getIdentidadPolicy() {
  		return "CertPolicy";
  	}
  
  	
  	public PolicyResult validaPolicy(Element element, ResultadoValidacion resultadovalidacion) {
  		PolicyResult pr = new PolicyResult();
  		X509Certificate cert = (X509Certificate) resultadovalidacion.getDatosFirma().getCadenaFirma().getCertificates().get(0);
  		
  		try {
  			cert.checkValidity(new Date());
  			pr.setResult(PolicyResult.StatusValidation.valid);
  			
  		} catch (CertificateExpiredException e) {
  			pr.setResult(PolicyResult.StatusValidation.invalid);
  			pr.setDescriptionResult(e.getMessage());
  			
  		} catch (CertificateNotYetValidException e) {
  			pr.setResult(PolicyResult.StatusValidation.invalid);
  			pr.setDescriptionResult(e.getMessage());
  			
  		}
  		
  		return pr;
  	}

    public void setTruster(TrustAbstract ta) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
  	
  	
    
}
