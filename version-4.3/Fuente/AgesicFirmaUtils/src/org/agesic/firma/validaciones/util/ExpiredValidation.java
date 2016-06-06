/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agesic.firma.validaciones.util;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.List;
import org.agesic.firma.datatypes.ResultadoValidacion;

/**
 *
 * @author sofis solutions
 */
public class ExpiredValidation {

    public static ResultadoValidacion validate(X509Certificate c, Calendar cal) {
        //se valida si el certificado expir{o
        try {
            if (cal == null){
                c.checkValidity();
            }else{
                c.checkValidity(cal.getTime());
            }
            
            return null;
        } catch (CertificateExpiredException w) {
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(FirmaValidate.EXPIRED_CERTIFICATE_ERROR_CODE, w.getMessage(), null);
            return resultadoValidacion;
        } catch (CertificateNotYetValidException w) {
            ResultadoValidacion resultadoValidacion = new ResultadoValidacion(FirmaValidate.EXPIRED_CERTIFICATE_ERROR_CODE, w.getMessage(), null);
            return resultadoValidacion;
        }
    }
}
