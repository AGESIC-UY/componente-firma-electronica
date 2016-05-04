/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.exception;

import java.net.NoRouteToHostException;
import javax.xml.ws.WebServiceException;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.config.MessagesUtil;

/**
 *
 * @author sofis solutions
 */
public class WSInvocationException extends Exception {

    static MessagesUtil msgUtil = new MessagesUtil(null);
    
    public WSInvocationException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {

        if (this.getCause() != null) {
            if (this.getCause().getClass().getName().indexOf("ClientTransportException") >= 0) {
                return msgUtil.getValue("MSG_WS_INVOCATION_CONNECTION_ERROR");
            }

            if (this.getCause() instanceof WebServiceException) {
                if (this.getCause().getMessage() != null && this.getCause().getMessage().toLowerCase().indexOf("failed to access") >= 0) {
                    return msgUtil.getValue("MSG_WS_INVOCATION_CONNECTION_ERROR");
                }

                WebServiceException wsE = (WebServiceException) this.getCause();
                if (wsE.getCause() instanceof NoRouteToHostException) {
                    return msgUtil.getValue("MSG_WS_INVOCATION_CONNECTION_ERROR");
                }

            }
            if (this.getCause() instanceof javax.xml.ws.soap.SOAPFaultException) {
                javax.xml.ws.soap.SOAPFaultException exo = (javax.xml.ws.soap.SOAPFaultException) this.getCause();
                return exo.getFault().getFaultString();

            }
            if (this.getCause() instanceof java.net.SocketTimeoutException) {
                //return "Tiempo de espera agotado al procesar la solicitud";
                return "";

            }
        }

        /*
         * if (this.getCause() instanceof ValidationException) { String toReturn
         * = ""; for (ValidationItem i : ((ValidationException)
         * this.getCause()).getFaultInfo().getItems()) { toReturn = toReturn +
         * i.getMessage() + " - "; return toReturn; }
        }
         */
        return super.getMessage();
    }
}
