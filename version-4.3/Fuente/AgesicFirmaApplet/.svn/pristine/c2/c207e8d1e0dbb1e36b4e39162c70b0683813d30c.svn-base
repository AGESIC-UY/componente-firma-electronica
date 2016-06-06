package uy.gub.agesic.firma.cliente.applet;

import eu.bitwalker.useragentutils.Manufacturer;
import eu.bitwalker.useragentutils.UserAgent;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import static org.apache.commons.httpclient.params.HttpMethodParams.USER_AGENT;
import org.apache.commons.io.FileUtils;
import org.icepdf.ri.common.MyAnnotationCallback;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.util.PropertiesManager;
import org.ini4j.Wini;
import uy.gub.agesic.firma.cliente.config.AppletParams;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.config.MessagesUtil;
import uy.gub.agesic.firma.cliente.utils.OSValidator;
import uy.gub.agesic.firma.cliente.utils.ParametersUtils;
import uy.gub.agesic.firma.cliente.ws.trust.client.AgesicFirmaWSClient;

/**
 * Applet que despliega un archivo PDF o XML para su firma. El Applet recibe los
 * siguientes paramentros:
 *
 * 1 - ID_TRANSACCION: el identificador de la transacción
 *
 * 2 - TIPO_DOCUMENTO: el tipo de documento que se debe desplegar para mostrar.
 *
 * @author sofis solutions
 */
public class SignApplet extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("SignApplet");

    //el contralodor del visor de PDF
    SwingController controller;
    // el visor del XML
    private TextArea textArea;
    //el conjunto de documentos a despliega el visor
    private File[] documentos = null;
    //el conjunto de documentos firmados
    private File[] documentosSigned = null;
    //los datos del id de la transaccion y del tipo de documento a desplegar
    private String tipoDocumentos = null;
    private String idTransaccion = null;
    private String userData = null;
    private String tipoFirma = null;
    private boolean documentoVisible = true;
    private boolean usuarioDocumento = false;
    private boolean usuarioDocumentoFirmado = false;
    //opciones
    private HashMap<String, String> opciones = null;

    //los componentes que se crean de forma programatica
    private JButton buttonSign;
    private JButton buttonClose;
    //navegacion para multiples documentos
    private JButton buttonNext;
    private JButton buttonPrev;
    private Label navigationLabel = new Label();
    private Integer pageNumber = 0;
    private JPanel panelActions = new JPanel();
    private JPanel panelButtons = new JPanel();
    private JPanel panelNavigation = new JPanel();
    private Label messages = new Label();
    //el dialogo que abre el panel que solicita seleccionar el certificado
    Dialog signPDFPanelDialog = null;
    SignPanel signPdfPanel = null;
    private String userHomePath = null;
    private UserAgent userAgent = null;
    ConfigurationUtil cfgUtil = new ConfigurationUtil(null);
    MessagesUtil msgUtil = new MessagesUtil(null);

    /**
     * Los tipos de mensajes a desplegar al usuario
     */
    private enum message_type {
        INFO, ERROR
    };

    /**
     * Constructor por defecto
     */
    public SignApplet() {
        init();
        start();
    }

    private void initUserAgent() {
        userAgent = getBrowserIfo();
        userHomePath = System.getProperty("user.home");
        System.out.println("USER_HOME:" + System.getProperty("user.home"));
        System.out.println("DIGEST_AL:" + cfgUtil.getValue("DIGEST_ALG"));
        System.out.println("AGESIC_FIRMA_WS:" + cfgUtil.getValue("AGESIC_FIRMA_WS"));
    }

    /**
     * Obtiene la información del entorno de ejecucion del applet
     *
     * @return
     */
    public UserAgent getBrowserIfo() {
        try {
            UserAgent uAgent = new UserAgent("User-agent header sent: Mozilla/5.0 (" + OSValidator.getOSName() + " " + OSValidator.getOSVersion() + "; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0");
            System.out.println("getBrowserInfo:" + uAgent.toString());
            return uAgent;
        } catch (Exception ex) {
            Logger.getLogger(SignApplet.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Obtiene la información sobre el Profile del Firefox del usuario
     *
     * @return
     */
    public String getFirefoxContextInformation() {
        try {
            String toReturn = null;
            if (toReturn == null || toReturn.equalsIgnoreCase("")) {
                String APPDATA = "appdata";
                String PROFILE_PATH_LINUX = userHomePath + "/.mozilla/firefox/";
                String PROFILE_PATH_WINDOWS = System.getenv(APPDATA) + "\\Mozilla\\Firefox\\";
                if (getBrowserIfo().getOperatingSystem().getManufacturer().equals(Manufacturer.MICROSOFT)) {
                    File profileIni = new File(PROFILE_PATH_WINDOWS + "profiles.ini");
                    Wini ini = new Wini(profileIni);
                    String profilename = ini.get("Profile0", "Path", String.class);
                    toReturn = PROFILE_PATH_WINDOWS + "\\" + profilename;
                    File f = new File(toReturn);
                    //BUG WIN XP usamos la cononica y no el path
                    try {
                        toReturn = f.getCanonicalPath();
                    } catch (Exception w) {
                        toReturn = f.getPath();
                    }
                } else {
                    String prfL = PROFILE_PATH_LINUX + "profiles.ini";
                    File profileIni = new File(prfL);
                    Wini ini = new Wini(profileIni);
                    String profilename = ini.get("Profile0", "Path", String.class);
                    toReturn = PROFILE_PATH_LINUX + profilename;
                }
            }
            return toReturn;
        } catch (Exception ex) {
            Logger.getLogger(SignApplet.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    private void initOpciones() {
        opciones = new HashMap();
        //todas las opciones de configuracion si estan como PARAM del applet entonces
        //se tomarán de ahi y no del .properties
        if (cfgUtil == null) {
            cfgUtil = new ConfigurationUtil(null);
        }

        for (String key : cfgUtil.keySet()) {
            try {
                String keyValue = getParams(key);
                if (keyValue != null) {
                    opciones.put(key, keyValue);
                }
            } catch (Exception w) {
                logger.log(Level.SEVERE, key, w);
            }
        }
        //inicializa la configuracin con los parametros del applet
        cfgUtil = new ConfigurationUtil(opciones);

        //todas las opciones de configuracion si estan como PARAM del applet entonces
        //se tomarán de ahi y no del .properties
        if (msgUtil == null) {
            msgUtil = new MessagesUtil(null);
        }
        String locale = cfgUtil.getValue(AppletParams.PARAM_LOCALE);
        msgUtil.setLocale(locale);
        for (String key : msgUtil.keySet()) {
            try {
                String keyValue = getParams(key);
                if (keyValue != null) {
                    opciones.put(key, keyValue);
                }
            } catch (Exception w) {
                logger.log(Level.SEVERE, null, w);
            }
        }

        //inicializa los mensajes con los parametros del applet
        msgUtil = new MessagesUtil(opciones);
    }

    /**
     * Inicializa los componentes que se agregan de forma programatica al panel
     * del applet.
     */
    private void initCustomComponents() {

        buttonSign = new JButton();
        buttonSign.setActionCommand("ButtonFirmar");
        buttonSign.setLabel("Firmar");
        buttonSign.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonFirmarActionPerformed(evt);
            }
        });
        Dimension size = new Dimension(63, 23);
        buttonSign.setMaximumSize(size);
        buttonSign.setMinimumSize(size);

        panelButtons.add(buttonSign);

        buttonClose = new JButton();
        String s = cfgUtil.getValue("SHOW_CLOSE");
        Boolean closeB = true;
        try {
            closeB = Boolean.parseBoolean(s);
        } catch (Exception w) {
        }
        if (closeB) {
            buttonClose.setActionCommand("ButtonCerrar");
            buttonClose.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    System.exit(0);
                }
            });

            buttonClose.setMaximumSize(size);
            buttonClose.setMinimumSize(size);
            panelButtons.add(buttonClose);
        }

        buttonNext = new JButton();
        buttonNext.setActionCommand("ButtonFirmar");
        buttonNext.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSiguienteActionPerformed(evt);
            }
        });
        size = new Dimension(63, 23);

        buttonNext.setMaximumSize(size);
        buttonNext.setMinimumSize(size);

        buttonPrev = new JButton();
        buttonPrev.setActionCommand("ButtonFirmar");
        buttonPrev.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAnteriorActionPerformed(evt);
            }
        });
        size = new Dimension(63, 23);
        buttonPrev.setMaximumSize(size);
        buttonPrev.setMinimumSize(size);
        buttonPrev.setEnabled(false);

        panelNavigation.add(navigationLabel);
        panelNavigation.add(buttonPrev);
        panelNavigation.add(buttonNext);

        messages = new Label();
        messages.setVisible(false);
    }

    /**
     * Metodo invocado desde JavaScript para cerrar el applet
     */
    public void cerrarApplet() {
        this.destroy();
        AccessController.doPrivileged(new PrivilegedAction() {

            public Void run() {
                System.exit(0);
                return null;
            }
        });
    }

    /**
     * Navega a la url que se le pasa como parametro
     *
     * @param url
     */
    public void navegarApplet(String url) {
        URL anchor = null;
        try {
            anchor = new URL(url);
        } catch (MalformedURLException e) {
            anchor = null;
        }
        if (anchor != null) {
            //getAppletContext().showDocument(anchor, "_top");
        }

    }

    private void botonSiguienteActionPerformed(java.awt.event.ActionEvent evt) {
        File[] docs = null;
        if (documentosSigned != null) {
            docs = documentosSigned;
        } else {
            docs = documentos;
        }

        if (docs != null && (docs.length - 1) > pageNumber) {
            pageNumber = pageNumber + 1;
            displayDocument(pageNumber);
            //se llegó al final se deshabilita el siguiente
            if (pageNumber == docs.length - 1) {
                buttonNext.setEnabled(false);
            }
            buttonPrev.setEnabled(true);
            this.validate();
        }
    }

    private void botonAnteriorActionPerformed(java.awt.event.ActionEvent evt) {

        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            displayDocument(pageNumber);
            if (pageNumber == 0) {
                buttonPrev.setEnabled(false);
            }
            buttonNext.setEnabled(true);
            this.validate();
        }

    }

    /**
     * Boton de firma, el mismo inicializa el PANEL que solicita la selección
     * del certificado a utilizar
     *
     * @param evt
     */
    private void botonFirmarActionPerformed(java.awt.event.ActionEvent evt) {
        //creamos el dialogo modal donde se incluye el panel con el combo para seleccionar el certificado
        //con el cual se va  firmar
        Frame appletFrame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);
        signPDFPanelDialog = new JDialog(appletFrame, msgUtil.getValue("MSG_SELECT_CERTIFICATE_TITLE"));
        signPdfPanel = new SignPanel(this);
        System.out.println("dimension:" + signPdfPanel.getSize());;

        signPdfPanel.setDialog(signPDFPanelDialog);

        signPDFPanelDialog.add(signPdfPanel);

        signPDFPanelDialog.setModal(true);
        signPDFPanelDialog.setResizable(false);
        ((JDialog) signPDFPanelDialog).setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        signPDFPanelDialog.pack();
        //para que se visualice en el medio de la pantalla

        signPDFPanelDialog.setLocationRelativeTo(null);
        //signPDFPanelDialog.setLocation(this.buttonSign.getLocation()); 
        signPDFPanelDialog.setVisible(true);
    }

    /**
     * @deprecated ver ErrorDialog.java
     * @param type
     * @param message
     */
    private void setMessage(message_type type, String message) {
        this.messages.setVisible(true);
        this.messages.setText(message);

    }

    /**
     * Inicializa el applet, invoca al servicio web para descargar el PDF
     * correspondiente
     */
    private void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SignApplet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(SignApplet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SignApplet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(SignApplet.class.getName()).log(Level.SEVERE, null, ex);
        }

        final SignApplet applet = this;
        applet.setMinimumSize(new Dimension(600, 500));
        applet.setMaximumSize(new Dimension(900, 700));
        initOpciones();
        initCustomComponents();
        initUserAgent();
        String locale = cfgUtil.getValue(AppletParams.PARAM_LOCALE);
        msgUtil.setLocale(locale);
        String closeText = msgUtil.getValue("CLOSE_LABEL");
        buttonClose.setText(closeText);
        String signText = msgUtil.getValue("SIGN_LABEL");
        buttonSign.setText(signText);
        String nextText = msgUtil.getValue("NEXT_LABEL");
        buttonNext.setText(nextText);
        String prevText = msgUtil.getValue("PREV_LABEL");
        buttonPrev.setText(prevText);

        String navText = msgUtil.getValue("MULTIPLE_DOCS_LABEL");
        navigationLabel.setText(navText);

        final String chooseFileToSignText = msgUtil.getValue("CHOOSE_FILE_TO_SIGN_LABEL");
        this.documentosSigned = null;

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    idTransaccion = getParams(AppletParams.PARAM_ID_TRANSACCION);
                    tipoDocumentos = cfgUtil.getValue(AppletParams.PARAM_TIPO_DOCUMENTOS);
                    userData = cfgUtil.getValue(AppletParams.PARAM_USER_DATA);
                    tipoFirma = cfgUtil.getValue(AppletParams.PARAM_TIPO_FIRMA);
                    try {
                        documentoVisible = Boolean.parseBoolean(cfgUtil.getValue(AppletParams.PARAM_DOCUMENTO_VISIBLE));
                    } catch (Exception w) {
                        documentoVisible = true;
                    }

                    try {
                        usuarioDocumento = Boolean.parseBoolean(cfgUtil.getValue(AppletParams.PARAM_USUARIO_DOCUMENTO));
                    } catch (Exception w) {
                        usuarioDocumento = false;
                    }
                    try {
                        usuarioDocumentoFirmado = Boolean.parseBoolean(cfgUtil.getValue(AppletParams.PARAM_USUARIO_DOCUMENTO_FIRMADO));
                    } catch (Exception w) {
                        usuarioDocumentoFirmado = false;
                    }

                    if (tipoDocumentos == null) {
                        //no se tiene la orden de compra ni la oferta ID
                        ErrorDialog.showMessageDialog(getContentPane(), "No se recibió el tipo de documentos como parametro");
                        return;
                    }
                    if (idTransaccion == null) {
                        ErrorDialog.showMessageDialog(getContentPane(), "No se recibió el id de la transacción como parametro");
                        return;
                    }
                    if (!usuarioDocumento) {
                        AgesicFirmaWSClient swsClient = new AgesicFirmaWSClient(opciones);
                        try {
                            documentos = swsClient.obtenerDocumentos(idTransaccion, tipoDocumentos);
                            if (documentos == null || documentos.length == 0) {
                                ErrorDialog.showMessageDialog(getContentPane(), "No se obtuvo el documento a partir del ID de Transacción");
                                return;
                            }
                        } catch (Throwable e) {
                            //no se pudo obtener los archivos desde el servicio web
                            ErrorDialog.showMessageDialog(getContentPane(), e);
                            return;
                        }
                    } else {
                        try {
                            //se debe obtener los documentos desde un FileChooser
                            JFileChooser chooser = new JFileChooser();
                            chooser.setCurrentDirectory(new File(userHomePath));
                            chooser.setDialogTitle(chooseFileToSignText);
                            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            int returnVal = chooser.showOpenDialog(applet);

                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                documentos = new File[]{chooser.getSelectedFile()};
                            }
                        } catch (Throwable t) {
                        }
                    }

                    UIManager.put("OptionPane.okButtonText", "Cerrar");
                    // create a controller and a swing factory
                    controller = new SwingController();
                    PropertiesManager properties = new PropertiesManager(System.getProperties(),
                            ResourceBundle.getBundle(PropertiesManager.DEFAULT_MESSAGE_BUNDLE));
                    SwingViewBuilder factory = new SwingViewBuilder(controller, properties);
                    // add interactive mouse link annotation support via callback
                    controller.getDocumentViewController().setAnnotationCallback(
                            new org.icepdf.ri.common.MyAnnotationCallback(
                                    controller.getDocumentViewController()));

                    // build viewer component and add it to the applet content pane.
                    MyAnnotationCallback myAnnotationCallback = new MyAnnotationCallback(
                            controller.getDocumentViewController());
                    controller.getDocumentViewController().setAnnotationCallback(myAnnotationCallback);

                    BorderLayout layout = new BorderLayout();
                    getContentPane().setLayout(layout);
                    // Agrega el visor de PDF al contentPane del Applet

                    if (documentoVisible && tipoDocumentos.equalsIgnoreCase("pdf")) {
                        getContentPane().add(factory.buildViewerPanel(), BorderLayout.CENTER);
                        getContentPane().add(factory.buildCompleteMenuBar(), BorderLayout.NORTH);
                    }
                    if (documentoVisible && tipoDocumentos.equalsIgnoreCase("xml")) {
                        textArea = new TextArea();
                        textArea.setEditable(false);
                        getContentPane().add(textArea, BorderLayout.CENTER);
                    }
                    if (documentoVisible && tipoDocumentos.equalsIgnoreCase("hash")) {
                        textArea = new TextArea();
                        textArea.setEditable(false);
                        getContentPane().add(textArea, BorderLayout.CENTER);
                    }

                    if (documentos != null) {
                        if (documentoVisible && documentos.length > 1) {
                            buttonNext.setVisible(true);
                            buttonPrev.setVisible(true);
                            navigationLabel.setVisible(true);
                        } else {
                            buttonNext.setVisible(false);
                            buttonPrev.setVisible(false);
                            navigationLabel.setVisible(false);
                        }

                        BorderLayout layoutActions = new BorderLayout();
                        panelActions.setLayout(layoutActions);
                        panelActions.add(panelNavigation, BorderLayout.NORTH);
                        Image imageAgesic = msgUtil.getAWTImageValue("IMAGE_AGESIC");
                        if (imageAgesic != null) {
                            Integer w = msgUtil.getIntValue("IMAGE_AGESIC_ORG_W");
                            Integer h = msgUtil.getIntValue("IMAGE_AGESIC_ORG_W");
                            PaintImage pAge = new PaintImage(imageAgesic, w, h);
                            Dimension panelD = new Dimension(w, h);
                            pAge.setPreferredSize(panelD);

                            panelActions.add(pAge, BorderLayout.WEST);
                        }
                        Image imageOrg = msgUtil.getAWTImageValue("IMAGE_ORG");
                        if (imageOrg != null) {

                            Integer w = msgUtil.getIntValue("IMAGE_AGESIC_ORG_W");
                            Integer h = msgUtil.getIntValue("IMAGE_AGESIC_ORG_W");
                            PaintImage pOrg = new PaintImage(imageOrg, w, h);
                            Dimension panelD = new Dimension(w, h);
                            pOrg.setPreferredSize(panelD);
                            panelActions.add(pOrg, BorderLayout.EAST);
                        } else {

                        }

                        panelActions.add(panelButtons, BorderLayout.SOUTH);
                        getContentPane().add(panelActions, BorderLayout.SOUTH);
                        getContentPane().add(messages, BorderLayout.BEFORE_FIRST_LINE);
                    } else {
                        buttonNext.setVisible(false);
                        buttonPrev.setVisible(false);
                        navigationLabel.setVisible(false);
                        BorderLayout layoutActions = new BorderLayout();
                        panelActions.setLayout(layoutActions);
                        panelActions.add(panelNavigation, BorderLayout.NORTH);
                        panelActions.add(panelButtons, BorderLayout.SOUTH);
                        getContentPane().add(panelActions, BorderLayout.SOUTH);
                        getContentPane().add(messages, BorderLayout.BEFORE_FIRST_LINE);
                    }

                }
            });
        } catch (Exception ex) {
            ErrorDialog.showStackTraceErrorDialog(this.getContentPane(), ex);
        }

    }

    public void openDocument(String documentPath) {
        controller.closeDocument();
        controller.openDocument(documentPath);

    }

    private void displayDocument(Integer pageNumber) {

        File[] docs = null;
        if (documentosSigned != null) {
            docs = documentosSigned;
        } else {
            docs = documentos;
        }
        String documentPath = null;
        if (docs != null && docs.length > 0) {
            //Se despliega el primer archivo
            File file = docs[pageNumber.intValue()];
            //BUG WIN XP usamos la cononica y no el path
            try {
                documentPath = file.getCanonicalPath();
            } catch (Exception w) {
                documentPath = file.getPath();
            }
            if (documentPath != null) {
                if (!documentoVisible) {
                    botonFirmarActionPerformed(null);
                    return;
                }
                if (documentoVisible && tipoDocumentos.equalsIgnoreCase("pdf")) {
                    try {
                        controller.openDocument(file.toURI().toURL());
                    } catch (Exception w) {
                        logger.log(Level.SEVERE, null, w);
                    }
                }

                if (documentoVisible && tipoDocumentos.equalsIgnoreCase("xml")) {
                    try {
                        String xmlString = FileUtils.readFileToString(file);
                        textArea.setText(xmlString);
                    } catch (Exception w) {
                        logger.log(Level.SEVERE, null, w);
                    }
                }

                if (documentoVisible && tipoDocumentos.equalsIgnoreCase("hash")) {
                    try {
                        String hashString = FileUtils.readFileToString(file);
                        textArea.setText(hashString);
                    } catch (Exception w) {
                        logger.log(Level.SEVERE, null, w);
                    }
                }
            }
        } else {
            buttonSign.setVisible(false);
        }
        this.validate();
    }

    /**
     * Abre el archivo en el visor de documentos obtener
     */
    private void start() {
        // resolve the url
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    displayDocument(pageNumber);
                }
            });
        } catch (Exception e) {
            ErrorDialog.showStackTraceErrorDialog(this.getContentPane(), e);
        }
    }

    public void stop() {
        // closing document.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    if (controller != null) {
                        controller.closeDocument();
                    }
                }
            });
        } catch (Exception e) {
            ErrorDialog.showStackTraceErrorDialog(this.getContentPane(), e);
        }
    }

    /**
     * Elimina el applet
     */
    public void destroy() {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    // dispose the viewer component
                    if (controller != null) {
                        controller.dispose();
                        controller = null;
                    }
                    getContentPane().removeAll();
                }
            });
        } catch (Exception e) {
            ErrorDialog.showStackTraceErrorDialog(this.getContentPane(), e);
        }
    }

    /**
     * Metodo invocado cuando se finaliza la firma del documento y el envio al
     * servidor si corresponde
     *
     * @param pdfSigned
     */
    public void signSuccessful(File[] documentosSigned) {
        this.documentosSigned = documentosSigned;
        pageNumber = 0;

        if (this.documentoVisible && this.tipoDocumentos.equalsIgnoreCase("pdf")) {
            displayDocument(pageNumber);
        }
        if (this.documentoVisible && this.tipoDocumentos.equalsIgnoreCase("xml")) {
            displayDocument(pageNumber);
        }

        if (this.documentoVisible && this.tipoDocumentos.equalsIgnoreCase("hash")) {
            displayDocument(pageNumber);
        }

        boolean ok = true;
        if (usuarioDocumentoFirmado) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(userHomePath));
            String chooseFileSignedText = msgUtil.getValue("CHOOSE_FILE_SIGNED_LABEL");
            chooser.setDialogTitle(chooseFileSignedText);
            int size = documentosSigned.length;
            for (int i = 0; i < size; i++) {
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setSelectedFile(new File("doc_" + i + "." + tipoDocumentos));
                int returnVal = chooser.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = documentosSigned[i];
                    File toSave = chooser.getSelectedFile();
                    try {
                        FileUtils.writeByteArrayToFile(toSave, FileUtils.readFileToByteArray(f));
                    } catch (Exception w) {
                        ErrorDialog.showMessageDialog(getContentPane(), w.getMessage());
                    }
                } else {
                    ok = false;
                    break;
                }
            }

        }
        //cierra el panel de firma y el del worker
        this.signPdfPanel.close();
        if (ok) {
            try {
                sendPost();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
                ErrorDialog.showMessageDialog(getContentPane(), "No se pudo enviar el ID de transacción.");
            }

            //elimina los botones de firmar y despliega el mensaje apropiado
            this.panelButtons.removeAll();
            this.panelButtons.add(new JLabel(msgUtil.getValue("SIGN_OK")));
            this.navigationLabel.setText(msgUtil.getValue("MULTIPLE_DOCS_SIGNED_LABEL"));

            String s = cfgUtil.getValue("SHOW_CONTINUE");
            String continueLabel = msgUtil.getValue("CONTINUE_LABEL");
            Boolean continueB = true;
            try {
                continueB = Boolean.parseBoolean(s);
            } catch (Exception w) {
            }

            if (continueB) {
                JButton buttonContinue = new JButton();
                buttonContinue.setText(continueLabel);
                buttonContinue.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                    }
                });
                this.panelButtons.add(buttonContinue);
            }

            s = cfgUtil.getValue("SHOW_CLOSE");
            Boolean closeB = true;
            try {
                closeB = Boolean.parseBoolean(s);
            } catch (Exception w) {
            }
            if (closeB) {
                buttonClose = new JButton();
                Dimension size = new Dimension(63, 23);
                buttonClose.setActionCommand("ButtonCerrar");
                buttonClose.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        System.exit(0);
                    }
                });

                buttonClose.setMaximumSize(size);
                buttonClose.setMinimumSize(size);
                String closeText = msgUtil.getValue("CLOSE_LABEL");
                buttonClose.setText(closeText);
                this.panelButtons.add(buttonClose);
            }

            boolean autoContinue = cfgUtil.getBooleanValue("AUTO_CONTINUE");
            if (autoContinue) {
            }
        }
        this.validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public File[] getDocumentos() {
        return documentos;
    }

    public void setDocumentos(File[] documentos) {
        this.documentos = documentos;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public Dialog getSignPDFPanelDialog() {
        return signPDFPanelDialog;
    }

    public void setSignPDFPanelDialog(Dialog signPDFPanelDialog) {
        this.signPDFPanelDialog = signPDFPanelDialog;
    }

    public String getTipoDocumentos() {
        return tipoDocumentos;
    }

    public void setTipoDocumentos(String tipoDocumentos) {
        this.tipoDocumentos = tipoDocumentos;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public boolean isDocumentoVisible() {
        return documentoVisible;
    }

    public void setDocumentoVisible(boolean documentoVisible) {
        this.documentoVisible = documentoVisible;
    }

    public boolean isUsuarioDocumento() {
        return usuarioDocumento;
    }

    public boolean isUsuarioDocumentoFirmado() {
        return usuarioDocumentoFirmado;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(UserAgent userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserHomePath() {
        return userHomePath;
    }

    public void setUserHomePath(String userHomePath) {
        this.userHomePath = userHomePath;
    }

    public HashMap<String, String> getOpciones() {
        return opciones;
    }

    public void setOpciones(HashMap<String, String> opciones) {
        this.opciones = opciones;
    }

    class PaintImage extends JPanel {

        public Image image;
        private int w;
        private int h;

        public PaintImage(Image image, int w, int h) {
            super();
            try {
                this.image = image;
                this.w = w;
                this.h = h;
            } catch (Exception e) {
                //Not handled.
            }
        }

        public void paintComponent(Graphics g) {
            g.drawImage(image, 0, 0, w, h, null);
            repaint();
        }
    }

    private void sendPost() throws Exception {
        System.out.println("sendPost...");
        String urlPost = getParams(AppletParams.PARAM_URL_OK_POST);
        System.out.println(AppletParams.PARAM_URL_OK_POST + ":" + urlPost);
        System.out.println(AppletParams.PARAM_ID_TRANSACCION + ":" + idTransaccion);

        if (urlPost != null && !urlPost.isEmpty()
                && idTransaccion != null && !idTransaccion.isEmpty()) {
            URL url = new URL(urlPost);
            URLConnection con;
            if (url.getProtocol().equalsIgnoreCase("https")) {
                con = (HttpsURLConnection) url.openConnection();
                ((HttpsURLConnection)con).setRequestMethod("POST");
            } else if (url.getProtocol().equalsIgnoreCase("http")) {
                con = (HttpURLConnection) url.openConnection();
                ((HttpURLConnection)con).setRequestMethod("POST");
            } else {
                throw new Exception("La Url de conexión no es http o https");
            }
            
            //add reuqest header
//            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "idtransaccion=" + idTransaccion;

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode;
            if (url.getProtocol().equalsIgnoreCase("https")) {
                responseCode = ((HttpsURLConnection)con).getResponseCode();
            } else {
                responseCode = ((HttpURLConnection)con).getResponseCode();
            }
            System.out.println("\nSending 'POST' request to URL : " + urlPost);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
        }
    }

    public String getParams(String key) {
        return params.get(key);
    }

    private static HashMap<String, String> params = new HashMap();

    public static void main(String args[]) {
        logger.log(Level.INFO, "Main SignApplet....");
        //Carga los argumentos en params.
        ParametersUtils.setJNLPParametes(args, params);

        new SignApplet().setVisible(true);
    }
}
