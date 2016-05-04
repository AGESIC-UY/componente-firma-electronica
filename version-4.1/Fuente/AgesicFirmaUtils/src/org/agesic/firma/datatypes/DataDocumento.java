/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agesic.firma.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author usuario
 */
public class DataDocumento implements Serializable{
    private List<byte[]> doc = new ArrayList<byte[]>();
    private Boolean valid;
    private List<byte[]> cert = new ArrayList<byte[]>();
    
    public List<byte[]> getDoc() {
        return doc;
    }

    public void setDoc(List<byte[]> doc) {
        this.doc = doc;
    }

    public List<byte[]> getCert() {
        return cert;
    }

    public void setCert(List<byte[]> cert) {
        this.cert = cert;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
    
}