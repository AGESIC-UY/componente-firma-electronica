/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.store.pkcs11;

import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.util.List;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import sun.security.pkcs11.SunPKCS11;
import sun.security.pkcs11.wrapper.CK_SLOT_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.config.Constantes;
import uy.gub.agesic.firma.cliente.config.MessagesUtil;
import uy.gub.agesic.firma.cliente.exception.DriverException;
import uy.gub.agesic.firma.cliente.exception.StoreInvalidPasswordException;
import uy.gub.agesic.firma.cliente.store.SofisCertificate;
import uy.gub.agesic.firma.cliente.store.StoreManagment;
import uy.gub.agesic.firma.cliente.store.pkcs11.acscrypto.ACSCryptoMateConfigFile;
import uy.gub.agesic.firma.cliente.store.pkcs11.alladin.AlladinConfigFile;
import uy.gub.agesic.firma.cliente.store.pkcs11.epass2003.EPass2003ConfigFile;
import uy.gub.agesic.firma.cliente.store.pkcs11.epass3003Auto.EPass3003AutoConfigFile;
import uy.gub.agesic.firma.cliente.store.pkcs11.gemalto.GemaltoAutoConfigFile;
import uy.gub.agesic.firma.cliente.store.pkcs11.safenet.SafeNetConfigFile;

/**
 *
 * @author sofis solutions
 */
public class PKCS11Util {

    MessagesUtil msgUtil;
    ConfigurationUtil cfgUtil;

    public PKCS11Util(ConfigurationUtil cfgUtil, MessagesUtil msgUtil) {
        this.msgUtil = msgUtil;
        this.cfgUtil = cfgUtil;
    }

    //utilizado para cuando el usuario indica la ruta
    public void createConfigFile(String path, String modelo, UserAgent userAgent) throws IOException {
        String property = "java.io.tmpdir";
        String tempDir = System.getProperty(property);
        String pathSeparator = System.getProperty("file.separator");
        if (!tempDir.endsWith(pathSeparator)) {
            tempDir = tempDir + pathSeparator;
        }

        String configFilePath;
        String name;

        //bk archivo si existe y crearlo con los datos proporcionados
        if (modelo.contains(Constantes.MODELO_ALADDIN)) {
            configFilePath = tempDir + "aladdin.cfg";
            File f = new File(configFilePath);
            if (f.exists()) {
                f.renameTo(new File(tempDir + "aladdin.cfg.bk"));
            }
            f.createNewFile();
            name = "aladdin";

            writeFile(f, name, path, userAgent.getOperatingSystem().equals(OperatingSystem.WINDOWS_7), "2", userAgent);

        }

        if (modelo.contains(Constantes.MODELO_SAFENET)) {
            configFilePath = tempDir + "safenet.cfg";
            File f = new File(configFilePath);
            if (f.exists()) {
                f.renameTo(new File(tempDir + "safenet.cfg.bk"));
            }
            f.createNewFile();
            name = "safenet";

            writeFile(f, name, path, true, "6", userAgent);

        }

        if (modelo.contains(Constantes.MODELO_EPASS2003)) {
            configFilePath = tempDir + "epass2003.cfg";
            File f = new File(configFilePath);
            if (f.exists()) {
                f.renameTo(new File(tempDir + "epass2003.cfg.bk"));
            }
            f.createNewFile();
            name = "epass2003";
            writeFile(f, name, path, false, "", userAgent);

        }

        if (modelo.contains(Constantes.MODELO_EPASS3003AUTO)) {
            configFilePath = tempDir + "epass3003auto.cfg";
            File f = new File(configFilePath);
            if (f.exists()) {
                f.renameTo(new File(tempDir + "epass3003auto.cfg.bk"));
            }
            f.createNewFile();
            name = "epass3003auto";
            writeFile(f, name, path, false, "", userAgent);
        }
        //New driver gemalto
        if (modelo.contains(Constantes.MODELO_GEMALTO) || modelo.contains("Cedula")) {
            configFilePath = tempDir + "gemalto.cfg";
            File f = new File(configFilePath);
            if (f.exists()) {
                f.renameTo(new File(tempDir + "gemalto.cfg.bk"));
            }
            f.createNewFile();
            name = "gemalto";

            writeFile(f, name, path, false, "", userAgent);

        }

        //New driver ACSCryptoMate
        if (modelo.contains(Constantes.MODELO_ACSCryptoMate) || modelo.contains("Abitab")) {
            configFilePath = tempDir + "aCSCryptoMate.cfg";
            File f = new File(configFilePath);
            if (f.exists()) {
                f.renameTo(new File(tempDir + "aCSCryptoMate.cfg.bk"));
            }
            f.createNewFile();
            name = "aCSCryptoMate";

            writeFile(f, name, path, false, "", userAgent);

        }

        if (modelo.equals(Constantes.MODELO_OTRO)) {

            int i = 1;
            File f;
            do {
                configFilePath = tempDir + "eToken" + i + ".cfg";
                f = new File(configFilePath);
                name = "eToken" + i;
                i++;

            } while (f.exists());

            f.createNewFile();

            writeFile(f, name, path, false, "", userAgent);
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
        out.write("library = " + lib);
        out.newLine();

        Long slotIdl = null;
        try {
            slotIdl = slotInfo(lib);
        } catch (Exception w) {
            w.printStackTrace();
        }

//        if (slot){
//            out.write("slot= "+slotId);
//            out.newLine();
//        }
        if (slotIdl != null) {
            out.write("slot= " + slotIdl);
            out.newLine();
        }
        out.close();

    }

    public String loadETokenLib(String model, UserAgent userAgent) {

        boolean isWindows = true;
        boolean isLinux = false;
        //si no puede determiar el SO por user Agent se prueba de otra forma
        if (userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.UNKNOWN)) {

            String os = System.getProperty("os.name");
            if (os != null && os.toLowerCase().indexOf("win") >= 0) {
                isWindows = true;
            } else {
                isWindows = false;
            }

            if (os != null && os.toLowerCase().indexOf("nix") >= 0 || os.toLowerCase().indexOf("nux") >= 0 || os.toLowerCase().indexOf("aix") > 0) {
                isLinux = true;
            } else {
                isLinux = false;
            }

        }

        if (isWindows || userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.WINDOWS)) {

            if (model.contains(Constantes.MODELO_ALADDIN)) {
                return cfgUtil.getValue("LIB_ALADDIN_WIN");
            }

            if (model.contains(Constantes.MODELO_SAFENET)) {
                return cfgUtil.getValue("LIB_SAFENET_WIN");
            }

            if (model.contains(Constantes.MODELO_EPASS2003)) {
                return cfgUtil.getValue("LIB_EPASS2003_WIN");
            }

            if (model.contains(Constantes.MODELO_EPASS3003AUTO)) {
                return cfgUtil.getValue("LIB_EPASS3003_WIN");
            }

            if (model.contains(Constantes.MODELO_GEMALTO)) {
                return cfgUtil.getValue("LIB_GEMALTO_WIN");
            }

            if (model.contains(Constantes.MODELO_ACSCryptoMate)) {
                return cfgUtil.getValue("LIB_ACSCryptoMate_WIN");
            }
        }

        if (isLinux || userAgent.getOperatingSystem().getGroup().equals(OperatingSystem.LINUX)) {

            if (model.contains(Constantes.MODELO_ALADDIN)) {
                return cfgUtil.getValue("LIB_ALADDIN_LIN");
            }

            if (model.contains(Constantes.MODELO_SAFENET)) {
                return cfgUtil.getValue("LIB_SAFENET_LIN");
            }

            if (model.contains(Constantes.MODELO_EPASS2003)) {
                return cfgUtil.getValue("LIB_EPASS2003_LIN");
            }

            if (model.contains(Constantes.MODELO_EPASS3003AUTO)) {
                return cfgUtil.getValue("LIB_EPASS3003_LIN");
            }

            if (model.contains(Constantes.MODELO_GEMALTO)) {
                return cfgUtil.getValue("LIB_GEMALTO_LIN");
            }

            if (model.contains(Constantes.MODELO_ACSCryptoMate)) {
                return cfgUtil.getValue("LIB_ACSCryptoMate_LIN");
            }
        }

        //MAC
        if (model.contains(Constantes.MODELO_ALADDIN)) {
            return cfgUtil.getValue("LIB_ALADDIN_MAC");
        }

        if (model.contains(Constantes.MODELO_SAFENET)) {
            return cfgUtil.getValue("LIB_SAFENET_MAC");
        }

        if (model.contains(Constantes.MODELO_EPASS2003)) {
            return cfgUtil.getValue("LIB_EPASS2003_MAC");
        }

        if (model.contains(Constantes.MODELO_EPASS3003AUTO)) {
            return cfgUtil.getValue("LIB_EPASS3003_MAC");
        }

        if (model.contains(Constantes.MODELO_GEMALTO)) {
            return cfgUtil.getValue("LIB_GEMALTO_MAC");
        }
        if (model.contains(Constantes.MODELO_ACSCryptoMate)) {
            return cfgUtil.getValue("LIB_ACSCryptoMate_MAC");
        }

        return "";

    }

    /**
     * Carga en memoria el KeysStore que se corresponde con el TOKEN
     * etokenConfNombre
     *
     * @param ksPass
     * @param etokenConfNombre
     * @return
     * @throws java.lang.Exception
     */
    public KeyStore loadTokenKeyStore(String ksPass, UserAgent userAgent, String activeProvider) throws Exception {
        int numProviders = 0;
        String property = "java.io.tmpdir";
        String tempDir = System.getProperty(property);
        String pathSeparator = System.getProperty("file.separator");
        if (!tempDir.endsWith(pathSeparator)) {
            tempDir = tempDir + pathSeparator;
        }

        /**
         * *****************************************************
         */
        String configFilePath;
        boolean hayMod = false;
        //otro
        int i = 1;
        File eTokenConf;
        String name;
        SunPKCS11 sunPKCS11eToken = null;
        System.out.println("ACtive provider " + activeProvider);

        do {
            name = "eToken" + i;

            configFilePath = tempDir + "eToken" + i + ".cfg";
            eTokenConf = new File(configFilePath);

            if (eTokenConf.exists()) {
                Security.removeProvider("SunPKCS11-" + name);
                if (Security.getProvider("SunPKCS11-" + name) == null) {
                    //BUG win XP se usa la canonica
                    String path = null;
                    try {
                        path = eTokenConf.getCanonicalPath();
                    } catch (Exception w) {
                        path = eTokenConf.getPath();
                    }

                    try {
                        sunPKCS11eToken = new sun.security.pkcs11.SunPKCS11(path);
                        Security.addProvider(sunPKCS11eToken);
                    } catch (ProviderException pe) {
                        sunPKCS11eToken = null;
                        pe.printStackTrace();
                    }

                } else {
                    sunPKCS11eToken = (SunPKCS11) Security.getProvider("SunPKCS11-" + name);
                }

                if (sunPKCS11eToken != null) {

                    hayMod = true;
                    Provider prov = Security.getProvider("SunPKCS11-" + name);
                    System.out.println("prov " + name + ": " + prov);
                    numProviders++;
                    if (!prov.getServices().isEmpty()) {
                        System.out.println("conectado");
                        //conectado
                        KeyStore keyStore = null;
                        try {

                            if ((activeProvider).equals(prov.getName()) || activeProvider.equals("")) {
                                keyStore = KeyStore.getInstance("PKCS11", prov);
                                keyStore.load(null, ksPass.toCharArray());
                            }
                        } catch (Exception ioEx) {
                            if (ioEx.getCause() instanceof UnrecoverableKeyException || ioEx.getCause() instanceof FailedLoginException) {
                                throw new StoreInvalidPasswordException(msgUtil.getValue("MSG_CONTRASENIA_INVALIDA"));
                            }
                            ioEx.printStackTrace();
                        }
                        return keyStore;
                    }
                }
            }

            i++;

        } while (eTokenConf.exists());


        System.out.println("REMOVE PROVIDER");
        Security.removeProvider("SunPKCS11-aladdin");
        Security.removeProvider("SunPKCS11-safenet");
        Security.removeProvider("SunPKCS11-epass2003");
        Security.removeProvider("SunPKCS11-epass3003");
        Security.removeProvider("SunPKCS11-gemalto");
        Security.removeProvider("SunPKCS11-aCSCryptoMate");
        System.out.println("REMOVE PROVIDER");
        /**
         * *****************************************************
         */
        //crea el archivos de configuracion si es necesario.
        //aladdin
        PKCS11ConfigFile f = new PKCS11ConfigFile(cfgUtil);
        configFilePath = tempDir + "aladdin.cfg";
        System.out.println("configFilePath:" + configFilePath);
        SunPKCS11 sunPKCSAlladin = f.createConfigFile(configFilePath, "aladdin", userAgent);

        //safenet
        f = new PKCS11ConfigFile(cfgUtil);
        configFilePath = tempDir + "safenet.cfg";
        System.out.println("configFilePath:" + configFilePath);
        SunPKCS11 sunPKCSSafenet = f.createConfigFile(configFilePath, "safenet", userAgent);

        //epass2003
        f = new PKCS11ConfigFile(cfgUtil);
        configFilePath = tempDir + "epass2003.cfg";
        System.out.println("configFilePath:" + configFilePath);
        SunPKCS11 sunPKCSEpass2003 = f.createConfigFile(configFilePath, "epass2003", userAgent);

        //epass3003auto
        f = new PKCS11ConfigFile(cfgUtil);
        configFilePath = tempDir + "epass3003.cfg";
        System.out.println("configFilePath:" + configFilePath);
        SunPKCS11 sunPKCSEpass3003auto = f.createConfigFile(configFilePath, "epass3003", userAgent);

        f = new PKCS11ConfigFile(cfgUtil);
        configFilePath = tempDir + "gemalto.cfg";
        System.out.println("configFilePath:" + configFilePath);
        SunPKCS11 sunPKCSGemalto = f.createConfigFile(configFilePath, "gemalto", userAgent);

        //ACSCryptoMate
        f = new PKCS11ConfigFile(cfgUtil);
        configFilePath = tempDir + "aCSCryptoMate.cfg";
        System.out.println("configFilePath:" + configFilePath);
        SunPKCS11 sunPKCSACSCryptoMate = f.createConfigFile(configFilePath, "aCSCryptoMate", userAgent);


        if ((!hayMod) && (sunPKCSAlladin == null) && (sunPKCSSafenet == null) && (sunPKCSEpass2003 == null) && (sunPKCSEpass3003auto == null) && (sunPKCSGemalto == null) && (sunPKCSACSCryptoMate == null)) {
            throw new DriverException("No se han encontrado modelos instalados de eToken");
        }

        KeyStore keyStore = null;
        Provider prov = null;
        Provider provConect = null;
        if (sunPKCSAlladin != null) {
            prov = Security.getProvider("SunPKCS11-aladdin");
            System.out.println("prov aladdin: " + prov + "-" + prov.getServices().isEmpty());
            if ((prov != null) && ((activeProvider).equals(prov.getName()) || activeProvider.equals(""))) {
                if (!prov.getServices().isEmpty()) {
                    provConect = prov;
                }
            }
        }

        if (sunPKCSSafenet != null) {
            prov = Security.getProvider("SunPKCS11-safenet");
            System.out.println("prov safenet: " + prov + "-" + prov.getServices().isEmpty());
            if ((prov != null) && ((activeProvider).equals(prov.getName()) || activeProvider.equals(""))) {
                if (!prov.getServices().isEmpty()) {
                    provConect = prov;
                }
            }
        }

        if (sunPKCSEpass2003 != null) {
            prov = Security.getProvider("SunPKCS11-epass2003");
            if ((prov != null) && ((activeProvider).equals(prov.getName()) || activeProvider.equals(""))) {
                System.out.println("prov epass2003: " + prov + "-" + prov.getServices().isEmpty());
                if (!prov.getServices().isEmpty()) {
                    provConect = prov;
                }
            }
        }

        if (sunPKCSEpass3003auto != null) {
            prov = Security.getProvider("SunPKCS11-epass3003");
            if ((prov != null) && ((activeProvider).equals(prov.getName()) || activeProvider.equals(""))) {
                System.out.println("prov epass3003: " + prov + "-" + prov.getServices().isEmpty());
                if (!prov.getServices().isEmpty()) {
                    provConect = prov;
                }
            }
        }

        if (sunPKCSGemalto != null) {
            prov = Security.getProvider("SunPKCS11-gemalto");
            if ((prov != null) && ((activeProvider).equals(prov.getName()) || activeProvider.equals(""))) {
                System.out.println("prov gemalto: " + prov + "-" + prov.getServices().isEmpty());
                if (!prov.getServices().isEmpty()) {
                    provConect = prov;
                }
            }
        }

        if (sunPKCSACSCryptoMate != null) {
            prov = Security.getProvider("SunPKCS11-aCSCryptoMate");
            if ((prov != null) && ((activeProvider).equals(prov.getName()) || activeProvider.equals(""))) {
                System.out.println("prov aCSCryptoMate: " + prov + "-" + prov.getServices().isEmpty());
                if (!prov.getServices().isEmpty()) {
                    provConect = prov;
                }
            }
        }

        if (provConect == null) {
            //throw new StoreException("No se detecta el eToken");
            throw new DriverException("No se han encontrado modelos conectados de eToken");
        }

        try {
            keyStore = KeyStore.getInstance("PKCS11", provConect);
            keyStore.load(null, ksPass.toCharArray());
        } catch (Exception ioEx) {
            if (ioEx.getCause() instanceof UnrecoverableKeyException || ioEx.getCause() instanceof FailedLoginException) {
                throw new StoreInvalidPasswordException(msgUtil.getValue("MSG_CONTRASENIA_INVALIDA"));
            }
            ioEx.printStackTrace();
        }
        return keyStore;
    }

    /**
     * Carga en memoria el Keystore con el path del certificado
     *
     * @param ksPass
     * @param pathKeystore
     * @return
     * @throws java.lang.Exception
     */
    public KeyStore loadJKSKeyStore(String ksPass, String pathKeystore) throws Exception {
        File f = new File(pathKeystore);
        InputStream inStream = new FileInputStream(f);
        KeyStore keyStorePKCS12 = KeyStore.getInstance("PKCS12");
        keyStorePKCS12.load(inStream, ksPass.toCharArray());
        return keyStorePKCS12;
    }

    public Provider getActiveProvider() {
        String property = "java.io.tmpdir";
        String tempDir = System.getProperty(property);
        String pathSeparator = System.getProperty("file.separator");
        if (!tempDir.endsWith(pathSeparator)) {
            tempDir = tempDir + pathSeparator;
        }
        String configFilePath;
        int i = 1;
        File eTokenConf;
        String name;
        Provider prov;
        Provider provConect = null;
        do {
            name = "eToken" + i;
            configFilePath = tempDir + "eToken" + i + ".cfg";
            eTokenConf = new File(configFilePath);

            if (eTokenConf.exists()) {

                if (Security.getProvider("SunPKCS11-" + name) != null) {
                    prov = Security.getProvider("SunPKCS11-" + name);
                    if (!prov.getServices().isEmpty()) {
                        provConect = prov;
                    }
                }
                i++;

            }
        } while (eTokenConf.exists());

        if (Security.getProvider("SunPKCS11-aladdin") != null) {
            prov = Security.getProvider("SunPKCS11-aladdin");
            if (!prov.getServices().isEmpty()) {
                provConect = prov;
            }
        }

        if (Security.getProvider("SunPKCS11-safenet") != null) {
            prov = Security.getProvider("SunPKCS11-safenet");
            if (!prov.getServices().isEmpty()) {
                provConect = prov;
            }
        }

        if (Security.getProvider("SunPKCS11-epass2003") != null) {

            prov = Security.getProvider("SunPKCS11-epass2003");
            if (!prov.getServices().isEmpty()) {
                provConect = prov;
            }
        }

        if (Security.getProvider("SunPKCS11-epass3003auto") != null) {
            prov = Security.getProvider("SunPKCS11-epass3003auto");
            if (!prov.getServices().isEmpty()) {
                provConect = prov;
            }
        }
        if (Security.getProvider("SunPKCS11-gemalto") != null) {
            prov = Security.getProvider("SunPKCS11-gemalto");
            if (!prov.getServices().isEmpty()) {
                provConect = prov;
            }

        }

        if (Security.getProvider("SunPKCS11-gemalto") != null) {
            prov = Security.getProvider("SunPKCS11-gemalto");
            if (!prov.getServices().isEmpty()) {
                provConect = prov;
            }

        }

        if (Security.getProvider("SunPKCS11-aCSCryptoMate") != null) {
            prov = Security.getProvider("SunPKCS11-aCSCryptoMate");
            if (!prov.getServices().isEmpty()) {
                provConect = prov;
            }

        }
        return provConect;
    }

    public int getNumProviders() {
        Provider prov = null;
        int activeProviders = 0;
        String property = "java.io.tmpdir";
        String tempDir = System.getProperty(property);
        String pathSeparator = System.getProperty("file.separator");
        if (!tempDir.endsWith(pathSeparator)) {
            tempDir = tempDir + pathSeparator;
        }
        String configFilePath;
        int i = 1;
        File eTokenConf;
        String name;

        do {
            name = "eToken" + i;
            configFilePath = tempDir + "eToken" + i + ".cfg";
            eTokenConf = new File(configFilePath);

            if (eTokenConf.exists()) {

                if (Security.getProvider("SunPKCS11-" + name) != null) {
                    prov = Security.getProvider("SunPKCS11-" + name);
                    if (!prov.getServices().isEmpty()) {
                        activeProviders++;
                    }
                }
                i++;

            }
        } while (eTokenConf.exists());

        if (Security.getProvider("SunPKCS11-aladdin") != null) {
            prov = Security.getProvider("SunPKCS11-aladdin");
            if (!prov.getServices().isEmpty()) {
                activeProviders++;
            }
        }

        if (Security.getProvider("SunPKCS11-safenet") != null) {
            prov = Security.getProvider("SunPKCS11-safenet");
            if (!prov.getServices().isEmpty()) {
                activeProviders++;
            }
        }

        if (Security.getProvider("SunPKCS11-epass2003") != null) {

            prov = Security.getProvider("SunPKCS11-epass2003");
            if (!prov.getServices().isEmpty()) {
                activeProviders++;
            }
        }

        if (Security.getProvider("SunPKCS11-epass3003auto") != null) {
            prov = Security.getProvider("SunPKCS11-epass3003auto");
            if (!prov.getServices().isEmpty()) {
                activeProviders++;
            }
        }
        if (Security.getProvider("SunPKCS11-aCSCryptoMate") != null) {
            prov = Security.getProvider("SunPKCS11-aCSCryptoMate");
            if (!prov.getServices().isEmpty()) {
                activeProviders++;
            }

        }
        return activeProviders;
    }

    /**
     * Busca un archivo con el nombre especificado en todos los directorios raiz
     * del sistema (en Windows, estos son C:\, D:\, etc, en linux solo /). Una
     * vez obtenido el archivo, el metodo getPath() permite obtener la ruta
     * completa al mismo.
     *
     * @param nombre Nombre exacto (case sensitive) del archivo buscado
     */
    public File buscarArchivoConfiguracion(String nombre) {
        File[] roots = File.listRoots();
        File etokenConfig = null;
        if (roots != null) {
            FilenameFilter filter = new SofisFilenameFilter(nombre);
            int i = 0;
            while (i < roots.length && etokenConfig == null) {
                File[] files = roots[i].listFiles(filter);
                if (files != null && files.length > 0) {
                    etokenConfig = files[0];
                }
                i++;
            }
        }
        return etokenConfig;
    }

    class MyCallbackHandler implements CallbackHandler {

        public void handle(Callback[] callbacks) throws IOException,
                UnsupportedCallbackException {
            //do nothig, the driver login implementation is invoked
        }
    }

    /**
     * Clase que permite realizar la busqueda de archivos en los directorios
     * raiz del sistema (en Windows, estos son C:\, D:\, etc, en linux solo /).
     */
    private class SofisFilenameFilter implements FilenameFilter {

        String nombre = null;

        public SofisFilenameFilter(String nombre) {
            if (nombre != null) {
                this.nombre = nombre;
            }
        }

        public boolean accept(File dir, String name) {
            return nombre != null && nombre.equals(name);
        }
    }

    public static void main(String[] args) {
        try {

            //String aCSCryptoMateConf = "C:\\Users\\Santiago\\AppData\\Local\\Temp\\aCSCryptoMate.cfg";
            String aCSCryptoMateConf = "C:\\Users\\Santiago\\AppData\\Local\\Temp\\gemalto.cfg";
            File f = new File("C:\\Program Files\\Advanced Card Systems Ltd\\ACOS5-CryptoMate Admin Client Kit\\Middleware\\x86\\PKCS\\acospkcs11.dll");
            System.out.println("f.e" + f.exists());
            SunPKCS11 sunPKCS11ACSCryptoMate = null;
            if (aCSCryptoMateConf.length() != 0L) {

                if (Security.getProvider("SunPKCS11-aCSCryptoMate") == null) {
                    //BUG win XP se usa la canonica
                    String path = null;
                    try {
                        path = aCSCryptoMateConf;
                    } catch (Exception w) {
                        path = aCSCryptoMateConf;
                    }
                    try {
                        sunPKCS11ACSCryptoMate = new sun.security.pkcs11.SunPKCS11(path);

                        Security.addProvider(sunPKCS11ACSCryptoMate);
                    } catch (ProviderException pe) {
                        sunPKCS11ACSCryptoMate = null;
                        pe.printStackTrace();
                    }

                } else {
                    sunPKCS11ACSCryptoMate = (SunPKCS11) Security.getProvider("SunPKCS11-aCSCryptoMate");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
}
