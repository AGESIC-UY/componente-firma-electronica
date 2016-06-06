/*File
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.store.pkcs11.gemalto;

import uy.gub.agesic.firma.cliente.store.pkcs11.epass2003.*;
import uy.gub.agesic.firma.cliente.store.pkcs11.alladin.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.OperatingSystem;
import sun.security.pkcs11.wrapper.CK_SLOT_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;

/**
 *
 * @author Sofis Solutions
 */
public class GemaltoAutoConfigFile {

    private ConfigurationUtil cfgUtil;
    final static String PATHS_SEPARATOR = ";";

    public GemaltoAutoConfigFile(ConfigurationUtil cfgUtil) {
        this.cfgUtil = cfgUtil;
    }

    public void createConfigFile(String path, UserAgent userAgent) throws IOException {
        System.out.print("User agent: " + userAgent.getOperatingSystem());
        boolean isWindows = false;
        //si no puede determiar el SO por user Agent se prueba de otra forma
        if (userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.UNKNOWN)) {

            String os = System.getProperty("os.name");
            if (os != null && os.toLowerCase().indexOf("win") >= 0) {
                isWindows = true;
            } else {
                isWindows = false;
            }

        }
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("CREANDO gemalto CONFIG FILE EN " + path);
            f.createNewFile();
        } else {
            //si ya existe el archivo retornamos el archivo existente no lo volvemos a crear.
            return;
        }

        File flib;
        String name = "gemalto";

        if (isWindows || userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.WINDOWS)) {

            String paths = cfgUtil.getValue("GEMALTO_WIN");

            for (String p : paths.split(PATHS_SEPARATOR)) {
                p = p.trim();
                if (p.length() > 0) {
                    flib = new File(p);
                    if (flib.exists() && flib.canRead()) {
                        writeFile(f, name, p,userAgent);
                        break;
                    }
                }
            }

          } else {
            String paths = "";
            if (userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.LINUX)) {
                paths = cfgUtil.getValue("GEMALTO_LIN");
            } else {
                //MAC
                paths = cfgUtil.getValue("GEMALTO_MAC");
            }

            for (String p : paths.split(PATHS_SEPARATOR)) {
                p = p.trim();
                if (p.length() > 0) {
                    flib = new File(p);
                    if (flib.exists() && flib.canRead()) {
                        writeFile(f, name, p,userAgent);
                        break;
                    } else {
                        //pruebo con numbre lib en minuscula
                        p = p.toLowerCase();
                        flib = new File(p);
                        if (flib.exists() && flib.canRead()) {
                            writeFile(f, name, p,userAgent);
                            break;
                        }
                    }
                }
            }
        }
    }

    protected static Long slotInfo(String dllPksc11) throws Exception {
        System.out.println("slotInfo:" + dllPksc11);
        PKCS11 p11 = PKCS11.getInstance(dllPksc11, "C_GetFunctionList", null, false);
        System.out.println("slotInfo: PKCS11 CLASS TYPE:" + p11);
        long[] slots = p11.C_GetSlotList(true);
        System.out.println("*****SLOTS SIZE:" + slots.length);
        for (long sl : slots) {
            System.out.println("*****SLOT ID:" + sl);
            CK_SLOT_INFO info = p11.C_GetSlotInfo(sl);
            System.out.println("*****SLOT INFO:" + info.toString());
            return sl;

        }
        return null;
    }

    private void writeFile(File f, String name, String lib,UserAgent userAgent) throws IOException {
boolean isWindows = false;
         //si no puede determiar el SO por user Agent se prueba de otra forma
        if (userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.UNKNOWN)) {
            
            String os = System.getProperty("os.name");
            if (os!=null && os.toLowerCase().indexOf("win") >=0){
                isWindows = true;
            }else{
                isWindows = false;
            }
        }        
        
        if (isWindows || userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.WINDOWS)) {
        lib= lib.replace("\\","\\\\");
        lib= "\""+lib+"\"";
        }
        
        BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write("name = " + name);
        out.newLine();
        out.write("library= "+ lib);
        out.newLine();

        Long slotIdl = null;
        try {
            slotIdl = slotInfo(lib);
        } catch (Exception w) {
        }


        if (slotIdl != null) {
            out.write("slot= " + slotIdl);
            out.newLine();

        }
        out.close();


    }
}
