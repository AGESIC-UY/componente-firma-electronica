package org.agesic.firma.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.3.0.v20110604-r9504", date="2015-12-22T17:27:13")
@StaticMetamodel(Documentos.class)
public class Documentos_ { 

    public static volatile SingularAttribute<Documentos, Integer> id;
    public static volatile SingularAttribute<Documentos, Date> fechaModif;
    public static volatile SingularAttribute<Documentos, byte[]> certificate;
    public static volatile SingularAttribute<Documentos, byte[]> archivo;
    public static volatile SingularAttribute<Documentos, String> tipoFirma;
    public static volatile SingularAttribute<Documentos, Boolean> firmado;
    public static volatile SingularAttribute<Documentos, Boolean> firmaValida;
    public static volatile SingularAttribute<Documentos, String> idTransaction;

}