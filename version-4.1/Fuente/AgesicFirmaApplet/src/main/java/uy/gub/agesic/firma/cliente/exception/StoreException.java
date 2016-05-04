/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uy.gub.agesic.firma.cliente.exception;

import es.mityc.javasign.pkstore.CertStoreException;

/**
 *
 * @author Sofis Solutions
 */
public class StoreException extends Exception{

    public StoreException(CertStoreException ex) {
        super(ex);
    }

    public StoreException(Exception e) {
        super(e);
    }
    
    public StoreException(String message) {
        super(message);
    }

}
