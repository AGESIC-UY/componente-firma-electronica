/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agesic.firma.datatypes;

/**
 *
 * @author sofis
 */
public class ResultadoValidacion {
    
    String code;
    String message;
    Integer docIndex;

    public ResultadoValidacion() {
    }

    public ResultadoValidacion(String code, String message, Integer docIndex) {
        this.code = code;
        this.message = message;
        this.docIndex = docIndex;
    }

    
    
    public Integer getDocIndex() {
        return docIndex;
    }

    public void setDocIndex(Integer docIndex) {
        this.docIndex = docIndex;
    }
    
    

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
}
