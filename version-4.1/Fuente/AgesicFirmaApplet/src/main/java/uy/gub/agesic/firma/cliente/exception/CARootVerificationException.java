/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.exception;

/**
 *
 * @author sofis solutions
 */
public class CARootVerificationException extends Exception{

    public CARootVerificationException(String string, Exception ex) {
        super(string,ex);
    }

    public CARootVerificationException(String string) {
        super(string);
    }
    
}
