package uy.gub.agesic.firma.cliente.store.pkcs11.epass2003;

import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import sun.security.pkcs11.wrapper.CK_SLOT_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;

/**
 *
 * @author Sofis Solutions
 */
public class EPass2003ConfigFile {

    private ConfigurationUtil cfgUtil;
    final static String PATHS_SEPARATOR = ";";

    public EPass2003ConfigFile(ConfigurationUtil cfgUtil) {
        this.cfgUtil = cfgUtil;
    }

    public void createConfigFile(String path, UserAgent userAgent) throws IOException {

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
            System.out.println("CREANDO epass2003 CONFIG FILE EN " + path);
            f.createNewFile();
        } else {
            //si ya existe el archivo retornamos el archivo existente no lo volvemos a crear.
            if (f.length() > 0) {
                return;
            }

        }

        File flib;
        String name = "epass2003";

        if (isWindows || userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.WINDOWS)) {

            String paths = cfgUtil.getValue("EPASS2003_WIN");

            for (String p : paths.split(PATHS_SEPARATOR)) {
                p = p.trim();
                if (p.length() > 0) {
                    flib = new File(p);
                    if (flib.exists() && flib.canRead()) {
                        writeFile(f, name, p);
                        break;
                    }
                }
            }

        } else {
            String paths = "";
            if (userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.LINUX)) {
                paths = cfgUtil.getValue("EPASS2003_LIN");
            } else {
                //MAC
                paths = cfgUtil.getValue("EPASS2003_MAC");
            }

            for (String p : paths.split(PATHS_SEPARATOR)) {
                p = p.trim();
                if (p.length() > 0) {
                    flib = new File(p);
                    if (flib.exists() && flib.canRead()) {
                        writeFile(f, name, p);
                        break;
                    } else {
                        //pruebo con numbre lib en minuscula
                        p = p.toLowerCase();
                        flib = new File(p);
                        if (flib.exists() && flib.canRead()) {
                            writeFile(f, name, p);
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

    private void writeFile(File f, String name, String lib) throws IOException {

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
        out.close();
    }
}
