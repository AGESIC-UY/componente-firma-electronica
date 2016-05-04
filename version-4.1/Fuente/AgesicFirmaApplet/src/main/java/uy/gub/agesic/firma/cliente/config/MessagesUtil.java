/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uy.gub.agesic.firma.cliente.config;

import com.itextpdf.text.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.ImageIcon;
import sun.misc.BASE64Decoder;

/**
 * Acceso al archivo de mensajes del APPLET
 *
 * @author sofis solutions (www.sofis-solutions.com)
 */
public class MessagesUtil {

    static ResourceBundle bundle = null;
    static Locale locale = null;
    private HashMap<String, String> options = null;

    public MessagesUtil(HashMap<String, String> options) {
        this.options = options;
        bundle = ResourceBundle.getBundle("messages");
    }

    public void setLocale(String localeStr) {
        if (localeStr != null && !localeStr.equalsIgnoreCase("")) {
            locale = new Locale(localeStr);
        } else {
            locale = Locale.ROOT;
        }
        bundle = ResourceBundle.getBundle("messages", locale);
    }

     public boolean containsKey(String key) {
        try {
            if (options == null || !this.options.containsKey(key)) {
                return bundle.containsKey(key);
            } else {
                return options.containsKey(key);
            }

        } catch (Exception e) {
            return false;
        }
    }
    public String getValue(String key) {
        try {
            if (options == null || !this.options.containsKey(key)) {
                return bundle.getString(key);
            } else {
                return options.get(key);
            }

        } catch (Exception e) {
            return "??" + key + "??";
        }
    }

    public boolean getBooleanValue(String key) {
        String value = null;
        if (this.options == null || !this.options.containsKey(key)) {
            value = bundle.getString(key);
        } else {
            value = options.get(key);
        }

        if (value != null && !value.equalsIgnoreCase("")) {
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception w) {
                return false;
            }
        }
        return false;
    }

    public Integer getIntValue(String key) {

        try {
            String value = null;
            if (this.options == null || !this.options.containsKey(key)) {
                value = bundle.getString(key);
            } else {
                value = options.get(key);
            }

            return Integer.valueOf(value);
        } catch (Exception e) {
            return null;
        }

    }

    public Image getIItextmageValue(String key) {
        String value = null;
        if (this.options == null || !this.options.containsKey(key)) {
            value = bundle.getString(key);
            if (value != null && !value.equalsIgnoreCase("")) {
                try {
                    URL url = null;
                    if (!value.startsWith("/")) {
                        url = ConfigurationUtil.class.getResource("/" + value);
                    } else {
                        url = ConfigurationUtil.class.getResource(value);
                    }

                    return Image.getInstance(url);
                } catch (Throwable w) {
                    w.printStackTrace();
                    return null;
                }

            }
        } else {
            value = options.get(key);
            //es la imagen en base 64
            if (value != null && !value.equalsIgnoreCase("")) {
                try {
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] imageByte = decoder.decodeBuffer(value);
                    return Image.getInstance(imageByte);
                } catch (Exception q) {
                }

            }

        }

        return null;
    }

    public java.awt.Image getAWTImageValue(String key) {
        String value = null;
        if (this.options == null || !this.options.containsKey(key)) {
            value = bundle.getString(key);
            if (value != null && value.trim().length() > 0) {
                try {
                    URL url = null;
                    if (!value.startsWith("/")) {
                        url = ConfigurationUtil.class.getResource("/" + value);
                    } else {
                        url = ConfigurationUtil.class.getResource(value);
                    }

                    ImageIcon stampImg = new ImageIcon(url);
                    return stampImg.getImage();
                } catch (Throwable w) {
                    return null;
                }
            } else {
                return null;
            }

        } else {
            value = options.get(key);
            //es la imagen en base 64
            if (value != null && !value.equalsIgnoreCase("")) {
                try {
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] imageByte = decoder.decodeBuffer(value);
                    ImageIcon stampImg = new ImageIcon(imageByte);
                    return stampImg.getImage();
                } catch (Exception q) {
                    q.printStackTrace();
                }

            }

        }


        return null;
    }

    public InputStream getInputStream(String key) {
        String value = null;
        if (this.options == null || !this.options.containsKey(key)) {
            value = bundle.getString(key);
        } else {
            value = options.get(key);
        }


        if (value != null && !value.equalsIgnoreCase("")) {
            try {
                URL url = null;
                if (!value.startsWith("/")) {
                    url = ConfigurationUtil.class.getResource("/" + value);
                } else {
                    url = ConfigurationUtil.class.getResource(value);
                }
                return url.openStream();
            } catch (Throwable w) {
                w.printStackTrace();
                return null;
            }

        }
        return null;
    }

    public Set<String> keySet() {
        if (options == null) {
            return bundle.keySet();
        } else {
            Set<String> setToReturn = bundle.keySet();
            setToReturn.addAll(options.keySet());
            return setToReturn;
        }
    }
}
