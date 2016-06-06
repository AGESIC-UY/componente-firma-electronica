/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uy.gub.agesic.firma.cliente.exception;



/**
 *
 * @author Sofis Solutions
 */
public class DriverException extends Exception{

    public DriverException(Exception e) {
        super(e);
    }
    
    public DriverException(String message) {
        super(message);
    }

}
