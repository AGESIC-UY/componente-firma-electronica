package uy.gub.agesic.firma.cliente.utils;

import java.util.HashMap;
import uy.gub.agesic.firma.cliente.applet.SignAppletStub;

/**
 *
 * @author Sofis Solutions (www.sofis-solutions.com)
 */
public class ParametersUtils {

    private static String[] props = new String[]{
        "ID_TRANSACCION",
        "TIPO_DOCUMENTO",
        "AGESIC_FIRMA_WS",
        "TSA_ENABLED",
        "STORE",
        "TSA_ENABLED",
        "TSA_URL",
        "TSA_USER",
        "TSA_PASSWORD",
        "OCSP_ENABLED",
        "OCSP_DEFAULT_URL",
        "CA_ROOT_VALIDATION_ENABLE",
        "KEYSTORE_TRUSTORE_PASS",
        "KEYSTORE_TRUSTORE_NAME",
        "USER_VALIDATION_ENABLE",
        "CRL_ENABLED",
        "CRL_DEFAULT_URL",
        "MTOM",
        "LOGGING_HANDLER",
        "PDFSIGNATURE_APPEARANCE_ENABLED",
        "WS_REQUEST_TIMEOUT",
        "WS_CONNECT_TIMEOUT",
        "SHOW_CONTINUE",
        "SHOW_CLOSE",
        "AUTO_CONTINUE",
        "DIGEST_ALG",
        "SIGN_ALG",
        "TIPO_DOCUMENTO",
        "USER_DATA",
        "DOCUMENTO_VISIBLE",
        "LOCALE",
        "TIPO_FIRMA",
        "USUARIO_DOCUMENTO",
        "USUARIO_DOCUMENTO_FIRMADO",
        "LIB_ALADDIN_WIN",
        "LIB_ALADDIN_LIN",
        "LIB_ALADDIN_MAC",
        "LIB_SAFENET_WIN",
        "LIB_SAFENET_LIN",
        "LIB_SAFENET_MAC",
        "LIB_EPASS2003_WIN",
        "LIB_EPASS2003_LIN",
        "LIB_EPASS2003_MAC",
        "LIB_EPASS3003_WIN",
        "LIB_EPASS3003_LIN",
        "LIB_EPASS3003_MAC",
        "LIB_GEMALTO_WIN",
        "LIB_GEMALTO_LIN",
        "ALADDIN_WIN",
        "ALADDIN_LIN",
        "ALADDIN_MAC",
        "SAFENET_WIN",
        "SAFENET_LIN",
        "SAFENET_MAC",
        "EPASS2003_WIN",
        "EPASS2003_LIN",
        "EPASS2003_MAC",
        "EPASS3003_WIN",
        "EPASS3003_LIN",
        "EPASS3003_MAC",
        "GEMALTO_WIN",
        "GEMALTO_LIN",
        "IMAGE_ORG",
        "IMAGE_AGESIC",
        "IMAGE_AGESIC_ORG_W",
        "IMAGE_AGESIC_ORG_H",
        "URL_OK_POST"
    };

    public static void setJNLPParametes(String args[], HashMap<String, String> params) {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (params == null) {
                    params = new HashMap<String, String>();
                }
                for (String prop : props) {
                    System.out.println("arg:" + arg);
                    if (arg.startsWith("-" + prop + "=")) {
                        String strValue = arg.replaceAll("-" + prop + "=", "");
                        params.put(prop, strValue);
                        System.out.println("Param:" + prop + " value:" + strValue);
                        break;
                    } else {
                        params.put(arg, arg);
                    }
                }
            }
        }
    }

    public static void setJNLPParametes(SignAppletStub appletStub, HashMap<String, String> params) {
        for (String prop : props) {
            String valueParam = null;
            try {
                valueParam = appletStub.getParameter(prop);
            } catch (Exception e) {
                break;
            }
            if (valueParam != null && !valueParam.isEmpty()) {
                params.put(prop, valueParam);
                System.out.println("Param:" + prop + " value:" + valueParam);
            }
        }
    }
}
