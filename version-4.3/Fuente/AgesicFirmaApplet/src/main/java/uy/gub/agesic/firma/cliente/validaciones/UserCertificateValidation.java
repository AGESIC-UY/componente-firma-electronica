/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.validaciones;

import java.security.cert.X509Certificate;
import java.util.StringTokenizer;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.config.MessagesUtil;
import uy.gub.agesic.firma.cliente.exception.UserVerificationException;

/**
 *
 * @author sofis solutions.
 */
public class UserCertificateValidation {

    static MessagesUtil msgUtil = new MessagesUtil(null);
    
    /**
     * Valida el certificado
     *
     * @param certificate
     * @param user_data
     */
    public static void validateCertificate(X509Certificate X509cert, String user_data) throws UserVerificationException {

        if (user_data == null) {
            throw new UserVerificationException(msgUtil.getValue("MSG_UserCertificateValidation_USER_DATA_PARAM_NOT_VALID"));
        }
        //X509cert.checkValidity();
        String sdn = X509cert.getSubjectDN().getName();
        String idCertificate = getCertificateIdentificacion(sdn);
        if (idCertificate == null){
            
            throw new UserVerificationException(msgUtil.getValue("MSG_UserCertificateValidation_SERIALNUMBER_NOT_VALID"));
        }
        String nroDoc = idCertificate.substring(3);
        if (!nroDoc.equals(user_data)) {
            //si el certificado no es valido
            throw new UserVerificationException(nroDoc + "!="+user_data);
        }


    }

    /**
     * Obtiene la identificación a partir del SubjectDN del certificado
     *
     * @param sdn String con el SubjectDN
     * @return String con la identificación
     */
    private static String getCertificateIdentificacion(String sdn) {
        System.out.println("SDN " + sdn);
        StringTokenizer stk = new StringTokenizer(sdn, ",");
        String token;
        String respuesta = null;
        while (stk.hasMoreTokens()) {
            token = stk.nextToken();
            if (token.contains("SERIALNUMBER")) {
                String[] partes = token.split("=");
                respuesta = partes[1];
                break;
            }
        }
        return respuesta;
    }
}
