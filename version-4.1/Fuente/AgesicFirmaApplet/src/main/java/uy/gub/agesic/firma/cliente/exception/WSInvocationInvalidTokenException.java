/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.exception;

/**
 *
 * @author sofis solutions
 */
public class WSInvocationInvalidTokenException extends Exception{

    public WSInvocationInvalidTokenException(String message) {
        super(message);
    }
    
    public WSInvocationInvalidTokenException(Throwable cause) {
        super(cause);
    }

    public WSInvocationInvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public WSInvocationInvalidTokenException() {
    }
    
    
    
}
