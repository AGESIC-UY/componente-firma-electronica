/*File
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.store.pkcs11;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.OperatingSystem;
import java.security.ProviderException;
import java.security.Security;
import sun.security.pkcs11.SunPKCS11;
import sun.security.pkcs11.wrapper.CK_SLOT_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;

/**
 *
 * @author Sofis Solutions
 */
public class PKCS11ConfigFile {

    private ConfigurationUtil cfgUtil;
    final static String PATHS_SEPARATOR = ";";

    public PKCS11ConfigFile(ConfigurationUtil cfgUtil) {
        this.cfgUtil = cfgUtil;
    }

    public SunPKCS11 createConfigFile(String path, String token, UserAgent userAgent) throws IOException {

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
            System.out.println("CREANDO " + token +" CONFIG FILE EN " + path + "-" + userAgent.getOperatingSystem().getGroup() + "-" + isWindows);
            f.createNewFile();
        } else {
            //si ya existe el archivo retornamos el archivo existente no lo volvemos a crear.
            if (f.length() > 0) {
                return checkPath(f,token);
            }

        }
        File flib;
        if (isWindows || userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.WINDOWS)) {

            String paths = cfgUtil.getValue(token.toUpperCase() + "_WIN");
            for (String p : paths.split(PATHS_SEPARATOR)) {
                p = p.trim();
                if (p.length() > 0) {
                    flib = new File(p);
                    if (flib.exists() && flib.canRead()) {
                        writeFile(f, token, p, userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.WINDOWS), "2", userAgent);
                        SunPKCS11 tokDriver = checkPath(f,token);
                        if (tokDriver != null) {
                            return tokDriver;
                        }
                    }
                }
            }
        } else {
            String paths = "";
            if (userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.LINUX)) {
                paths = cfgUtil.getValue(token.toUpperCase() + "_LIN");
            } else {
                //MAC
                paths =cfgUtil.getValue(token.toUpperCase() + "_MAC");
            }
            //System.out.println("userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.LINUX):" + userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.LINUX));
            //System.out.println(token.toUpperCase() + "_LIN " + paths);
            for (String p : paths.split(PATHS_SEPARATOR)) {
                //System.out.println("p:" + p);
                p = p.trim();
                if (p.length() > 0) {
                    flib = new File(p);
                    //System.out.println("flib:" + flib);
                    if (flib.exists() && flib.canRead()) {
                        writeFile(f, token, p, false, "", userAgent);
                        SunPKCS11 tokDriver = checkPath(f,token);
                        if (tokDriver != null) {
                            return tokDriver;
                        }
                    } else {
                        //pruebo con numbre lib en minuscula
                        p = p.toLowerCase();
                        flib = new File(p);
                        if (flib.exists() && flib.canRead()) {
                            writeFile(f, token, p, false, "", userAgent);
                            SunPKCS11 tokDriver = checkPath(f,token);
                            if (tokDriver != null) {
                                return tokDriver;
                            }
                        }
                    }
                }
            }
        }
        return null;
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

    private void writeFile(File f, String name, String lib, boolean slot, String slotId, UserAgent userAgent) throws IOException {

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

        if (isWindows || userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.WINDOWS)) {
            lib = lib.replace("\\", "\\\\");
            lib = "\"" + lib + "\"";
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write("name = " + name);
        out.newLine();
        out.write("library= " + lib);
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
        if (slot && name.equals("aladdin")){
            out.write("slot= " + slotId);
            out.newLine();

        }

        out.close();
  }

    public SunPKCS11 checkPath(File aladdinConf,String token) {
        SunPKCS11 sunPKCS11 = null;
        Security.removeProvider("SunPKCS11-"+token);
        if (aladdinConf.length() != 0L) {
            if (Security.getProvider("SunPKCS11-"+token) == null) {
                String path = null;
                try {
                    path = aladdinConf.getCanonicalPath();
                } catch (Exception w) {
                    path = aladdinConf.getPath();
                }
                try {
                    sunPKCS11 = new sun.security.pkcs11.SunPKCS11(path);
                    Security.addProvider(sunPKCS11);
                    System.out.println("OK PATH: " +path + " " + " SunPKCS11-"+token);
                } catch (Exception pe) {
                    System.out.println("ERROR PATH: " +path + " " + " SunPKCS11-"+token);
                    sunPKCS11 = null;
                    
                }

            } else {
                sunPKCS11 = (SunPKCS11) Security.getProvider("SunPKCS11-"+token);
            }
        }
        return sunPKCS11;
    }
}
