/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.exception;

import java.net.SocketTimeoutException;

/**
 *
 *  La exception que es lanzada cuando se cumple con el TIME OUT definido para la invocacion de los servicios web
 * @author sofis solutions
 */
public class WSInvocationTimeOutException extends Exception{

    public WSInvocationTimeOutException(SocketTimeoutException e) {
        super(e);
    }
    
}
