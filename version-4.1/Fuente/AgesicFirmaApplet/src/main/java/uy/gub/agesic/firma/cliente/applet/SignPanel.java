/*
 * SignPDF.java
 *
 * Created on 13/03/2012, 01:47:24 PM
 */
package uy.gub.agesic.firma.cliente.applet;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import java.awt.Dialog;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import uy.gub.agesic.firma.cliente.config.AppletParams;
import uy.gub.agesic.firma.cliente.config.ConfigurationUtil;
import uy.gub.agesic.firma.cliente.config.Constantes;
import uy.gub.agesic.firma.cliente.config.MessagesUtil;
import uy.gub.agesic.firma.cliente.exception.DriverException;
import uy.gub.agesic.firma.cliente.exception.StoreInvalidPasswordException;
import uy.gub.agesic.firma.cliente.store.SofisCertificate;
import uy.gub.agesic.firma.cliente.store.StoreManagment;
import uy.gub.agesic.firma.cliente.store.browser.IECertStoreImpl;
import uy.gub.agesic.firma.cliente.store.browser.MozillaCertStoreImpl;
import uy.gub.agesic.firma.cliente.store.pkcs11.PKCS11CertStoreImpl;
import uy.gub.agesic.firma.cliente.store.pkcs11.PKCS11Util;
import uy.gub.agesic.firma.ws.ResultadoValidacion;

/**
 *
 * @author Sofis Solutions
 */
public class SignPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    //el manejador de los Stores de los certificados
    private StoreManagment storeMg = new StoreManagment();
    //el applet que contiene el dialogo
    SignApplet viewer = null;
    //el dialogo el cual está abriendo este panel
    private Dialog dialog = null;

    //el dialogo donde se abre el panel de Working
    private Dialog workingDialog = null;
    //el entorno de ejecución del Applet
    private UserAgent userAgent = null;
    //el file chooser para seleccionar donde almacenar los archivos firmados si corresponde.
    private JFileChooser chooser = new JFileChooser();
    private MessagesUtil msgUtil;
    private ConfigurationUtil cfgUtil;
    private String modelo;
    private String pathDriver;

    /**
     * Creates new form SignPDF
     */
    public SignPanel(SignApplet viewer) {
        this.viewer = viewer;
        msgUtil = this.viewer.msgUtil;
        cfgUtil = this.viewer.cfgUtil;
        initComponents();
        initUserAgent();
        initCustomComponents();
        initStoreManagment();
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    /**
     * Creates new form SignPDF
     */
    public SignPanel() {

        /*    initComponents();
         initCustomComponents();
         initStoreManagment();*/
    }

    private void initUserAgent() {
        userAgent = viewer.getUserAgent();
    }

    private void initCustomComponents() {
        jComboBoxCertificados.removeAllItems();
        messageCertificados.setVisible(true);
        messageCertificados.setText("");

        //al string se le indica el prexijo HTML para que realice el wrap text de forma automatica
        // jLabelTextoBotonFirmar.setText("<html>" + msgUtil.getValue("MSG_NOTE_BOTON_FIRMAR"));
        jLabelTitleStep1.setText("<html>" + msgUtil.getValue("STEP1"));
        jLabelTitleStep2.setText("<html>" + msgUtil.getValue("STEP2"));

        jLabelPaso1.setText("<html>" + msgUtil.getValue("MSG_STEP1"));
        jLabelPaso2.setText("<html>" + msgUtil.getValue("MSG_STEP2"));

        jRadioButtonToken.setText(msgUtil.getValue("TOKEN_LABEL"));
        jRadioButtonArchivo.setText(msgUtil.getValue("FILE_LABEL"));
        jRadioButtonNavegador.setText(msgUtil.getValue("BROWSER_LABEL"));
        jButtonSelectFile.setText(msgUtil.getValue("FILE_SELECT"));
        jButtonCargarCertificados.setText(msgUtil.getValue("CERTIFICATES_LOAD"));
        jLabelCertificate.setText("<html>" + msgUtil.getValue("CERTIFICATE_SELECTION_LABEL"));
        jButtonFirmar.setText(msgUtil.getValue("CONFIRM_SIGN"));
        jButtonCerrar.setText(msgUtil.getValue("CANCEL_SIGN"));

        jRadioButtonToken.setActionCommand("TOKEN");
        jRadioButtonArchivo.setActionCommand("FILE");
        jRadioButtonNavegador.setActionCommand("BROWSER");

        HashMap storesHash = new HashMap();
        String stores = cfgUtil.getValue("STORE");
        StringTokenizer st = new StringTokenizer(stores, ";");
        while (st.hasMoreTokens()) {
            String store = st.nextToken();
            try {
                StoreManagment.store_types type = StoreManagment.store_types.valueOf(store);
                storesHash.put(type, type);
            } catch (Exception w) {
            }

        }
        System.out.print("TIPO FIRMA: " + cfgUtil.getValue(AppletParams.PARAM_TIPO_FIRMA));
        jRadioButtonToken.setVisible(false);
        jRadioButtonArchivo.setVisible(false);
        jRadioButtonNavegador.setVisible(false);

        if (storesHash.containsKey(StoreManagment.store_types.PKCS11)) {
            jRadioButtonToken.setVisible(true);
            buttonGroup3.add(jRadioButtonToken);
        }
        if (storesHash.containsKey(StoreManagment.store_types.PKCS12)) {
            jRadioButtonArchivo.setVisible(true);
            buttonGroup3.add(jRadioButtonArchivo);
        }

        if (storesHash.containsKey(StoreManagment.store_types.MOZILLA) || storesHash.containsKey(StoreManagment.store_types.IE)) {
            if (userAgent != null) {
                //la opcion de certificados en el navegador solo disponible para entornos no LINUX
                if (userAgent.getOperatingSystem().equals(OperatingSystem.LINUX)) {
                    jRadioButtonNavegador.setVisible(false);
                } else {
                    //entorno WINDOWS, el navegador que esta ejecutando el APPLET debe ser FIREFOX o IE
                    if ((userAgent.getBrowser().getGroup().equals(Browser.FIREFOX) && storesHash.containsKey(StoreManagment.store_types.MOZILLA))
                            || (userAgent.getBrowser().getGroup().equals(Browser.IE) && storesHash.containsKey(StoreManagment.store_types.IE))) {
                        jRadioButtonNavegador.setVisible(true);
                        buttonGroup3.add(jRadioButtonNavegador);
                    }
                }
            }
        }

        chooser = new JFileChooser();
        //FileNameExtensionFilter filter = new FileNameExtensionFilter( "JPG & GIF Images", "jpg", "gif");
        //chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(new File(viewer.getUserHomePath()));

        jTextFieldFile.setEnabled(false);
        jButtonSelectFile.setEnabled(false);
        jTextFieldContraseniaPaso1.setEnabled(false);
        jButtonCargarCertificados.setEnabled(false);

        //For custom property TIPO_FIRMA
        if (cfgUtil.getValue(AppletParams.PARAM_TIPO_FIRMA).equalsIgnoreCase("token")) {
            jLabelFile.setVisible(false);
            jLabelPaso1.setText("<html>Ingrese la contraseña para el token conectado.<br><br>En caso de haber mas de un token conectado se desplegara<br>una ventana para seleccionar el deseado</html>");
            jButtonSelectFile.setVisible(false);
            jRadioButtonArchivo.setVisible(false);
            jRadioButtonNavegador.setVisible(false);
            jRadioButtonToken.setVisible(false);
            jTextFieldFile.setVisible(false);
            jTextFieldContraseniaPaso1.setText("");
            jTextFieldContraseniaPaso1.setEnabled(true);
            jButtonCargarCertificados.setEnabled(true);
            jRadioButtonToken.setSelected(true);
        }

        if (cfgUtil.getValue(AppletParams.PARAM_TIPO_FIRMA).equalsIgnoreCase("archivo")) {
            jRadioButtonArchivo.setVisible(false);
            jRadioButtonToken.setVisible(false);
            jRadioButtonNavegador.setVisible(false);
            jTextFieldContraseniaPaso1.setText("");
            jTextFieldFile.setText("");
            jTextFieldContraseniaPaso1.setEnabled(true);
            jTextFieldFile.setEnabled(true);
            jButtonSelectFile.setEnabled(true);
            jButtonCargarCertificados.setEnabled(true);
            jRadioButtonArchivo.setSelected(true);

        }

        if (cfgUtil.getValue(AppletParams.PARAM_TIPO_FIRMA).equalsIgnoreCase("navegador")) {
            jRadioButtonNavegador.setVisible(false);
            jRadioButtonToken.setVisible(false);
            jRadioButtonArchivo.setVisible(false);
            jTextFieldContraseniaPaso1.setText("");
            jTextFieldFile.setText("");
            jTextFieldContraseniaPaso1.setEnabled(true);
            jTextFieldFile.setEnabled(true);
            jButtonSelectFile.setEnabled(true);
            jButtonCargarCertificados.setEnabled(true);
            jRadioButtonNavegador.setSelected(true);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jLabelPaso1 = new javax.swing.JLabel();
        jLabelPaso1Contrasenia = new javax.swing.JLabel();
        jLabelPaso2 = new javax.swing.JLabel();
        jButtonFirmar = new javax.swing.JButton();
        jButtonCargarCertificados = new javax.swing.JButton();
        jComboBoxCertificados = new javax.swing.JComboBox();
        jLabelCertificate = new javax.swing.JLabel();
        messageCertificados = new javax.swing.JLabel();
        jButtonCerrar = new javax.swing.JButton();
        jTextFieldContraseniaPaso1 = new javax.swing.JPasswordField();
        jLabelTitleStep1 = new javax.swing.JLabel();
        jLabelTitleStep2 = new javax.swing.JLabel();
        jRadioButtonToken = new javax.swing.JRadioButton();
        jRadioButtonArchivo = new javax.swing.JRadioButton();
        jRadioButtonNavegador = new javax.swing.JRadioButton();
        jButtonSelectFile = new javax.swing.JButton();
        jLabelFile = new javax.swing.JLabel();
        jTextFieldFile = new javax.swing.JTextField();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setMinimumSize(new java.awt.Dimension(592, 313));

        jLabelPaso1.setText("MSG_PASO1");

        jLabelPaso1Contrasenia.setText("Contraseña:");

        jLabelPaso2.setText("MSG_PASO2");

        jButtonFirmar.setText("Firmar");
        jButtonFirmar.setEnabled(false);
        jButtonFirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFirmarActionPerformed(evt);
            }
        });

        jButtonCargarCertificados.setText("Cargar Certificados");
        jButtonCargarCertificados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCargarCertificadosActionPerformed(evt);
            }
        });

        jComboBoxCertificados.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabelCertificate.setText("Certificado:");

        messageCertificados.setForeground(new java.awt.Color(255, 0, 51));
        messageCertificados.setText("Mensajes de Error");

        jButtonCerrar.setText("Cerrar");
        jButtonCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCerrarActionPerformed(evt);
            }
        });

        jLabelTitleStep1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelTitleStep1.setText("Paso 1 de 2:");

        jLabelTitleStep2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelTitleStep2.setText("Paso 2 de 2:");

        jRadioButtonToken.setText("jRadioButton1");
        jRadioButtonToken.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonTokenActionPerformed(evt);
            }
        });

        jRadioButtonArchivo.setText("jRadioButton2");
        jRadioButtonArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonArchivoActionPerformed(evt);
            }
        });

        jRadioButtonNavegador.setText("jRadioButton3");
        jRadioButtonNavegador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonNavegadorActionPerformed(evt);
            }
        });

        jButtonSelectFile.setText("jButton1");
        jButtonSelectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectFileActionPerformed(evt);
            }
        });

        jLabelFile.setText("Archivo:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jLabelTitleStep1))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabelTitleStep2)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabelCertificate)
                            .add(layout.createSequentialGroup()
                                .add(jButtonFirmar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jButtonCerrar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jLabelPaso2))
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jButtonCargarCertificados)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jRadioButtonToken)
                                    .add(jLabelFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabelPaso1Contrasenia))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(jRadioButtonArchivo)
                                        .add(18, 18, 18)
                                        .add(jRadioButtonNavegador))
                                    .add(layout.createSequentialGroup()
                                        .add(jTextFieldFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(jButtonSelectFile))
                                    .add(jTextFieldContraseniaPaso1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(jLabelPaso1)
                            .add(jComboBoxCertificados, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 418, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(0, 78, Short.MAX_VALUE))))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(messageCertificados, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelPaso1)
                    .add(jLabelTitleStep1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButtonToken)
                    .add(jRadioButtonArchivo)
                    .add(jRadioButtonNavegador))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextFieldFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabelFile)
                    .add(jButtonSelectFile))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabelPaso1Contrasenia)
                    .add(jTextFieldContraseniaPaso1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButtonCargarCertificados)
                .add(15, 15, 15)
                .add(messageCertificados)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabelTitleStep2)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabelPaso2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabelCertificate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jComboBoxCertificados, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonFirmar)
                    .add(jButtonCerrar))
                .add(18, 18, 18))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void initStoreManagment() {
        Browser brower = null;
        if (userAgent == null) {
            //no se puede detectar la información del usuario.
        } else {
            brower = userAgent.getBrowser();
        }

        if (brower != null) {
            System.out.println("BROWSER " + brower.getGroup());
        }
        String stores = cfgUtil.getValue("STORE");
        StringTokenizer st = new StringTokenizer(stores, ";");
        while (st.hasMoreTokens()) {
            String store = st.nextToken();
            try {
                StoreManagment.store_types type = StoreManagment.store_types.valueOf(store);
                switch (type) {
                    case PKCS11:
                        PKCS11CertStoreImpl tokenStore = new PKCS11CertStoreImpl(PKCS11CertStoreImpl.PKCS11CertStoreType.ETOKEN, userAgent, null);
                        storeMg.addCertStore(tokenStore);
                        break;
                    case PKCS12:
                        PKCS11CertStoreImpl fileStore = new PKCS11CertStoreImpl(PKCS11CertStoreImpl.PKCS11CertStoreType.FILE, userAgent, null);
                        storeMg.addCertStore(fileStore);
                        break;
                    case IE:
                        if (brower != null && brower.getGroup().equals(Browser.IE)) {
                            IECertStoreImpl iexplorerStore = new IECertStoreImpl();
                            storeMg.addCertStore(iexplorerStore);
                        }
                        break;
                    case MOZILLA:
                        if (brower != null && brower.getGroup().equals(Browser.FIREFOX)) {
                            String profile = viewer.getFirefoxContextInformation();
                            MozillaCertStoreImpl mozillaStore = new MozillaCertStoreImpl(profile);
                            storeMg.addCertStore(mozillaStore);

                        }
                        break;
                }
            } catch (Exception w) {
                w.printStackTrace();
                //store no soportado
                ErrorDialog.showStackTraceErrorDialog(this, w);
            }
        }

    }

    public JButton getjButtonFirmar() {
        return jButtonFirmar;
    }

    public void setjButtonFirmar(JButton jButtonFirmar) {
        this.jButtonFirmar = jButtonFirmar;
    }

    public JComboBox getjComboBoxCertificados() {
        return jComboBoxCertificados;
    }

    public void setjComboBoxCertificados(JComboBox jComboBoxCertificados) {
        this.jComboBoxCertificados = jComboBoxCertificados;
    }

    public JPasswordField getjTextFieldContraseniaPaso1() {
        return jTextFieldContraseniaPaso1;
    }

    public void setjTextFieldContraseniaPaso1(JPasswordField jTextFieldContraseniaPaso1) {
        this.jTextFieldContraseniaPaso1 = jTextFieldContraseniaPaso1;
    }

    private void jButtonFirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFirmarActionPerformed
        Image imageWorking = msgUtil.getAWTImageValue("WORKING_IMAGE");

        String passwordCertificate = null;//jTextFieldCertificatePassword.getText();
        String passwordCertStore = new String();
        if (jTextFieldContraseniaPaso1.getPassword() != null) {
            passwordCertStore = new String(jTextFieldContraseniaPaso1.getPassword());
        }

        String pass = null;
        if (passwordCertificate != null && !passwordCertificate.trim().equalsIgnoreCase("")) {
            pass = passwordCertificate;
        } else {
            pass = passwordCertStore;
        }
        //Determinar el certificador seleccionado
        SofisCertificate cert = (SofisCertificate) jComboBoxCertificados.getSelectedItem();

        //invocamos al Worker que realiza la firma y envia el PDF
        SignBackgroundWorker worker = new SignBackgroundWorker(cert, pass, this.viewer.getDocumentos(), this.viewer.getTipoDocumentos(), this.viewer.getUserData(), this.viewer.getIdTransaccion(), this.viewer.isUsuarioDocumentoFirmado(), this.viewer.getOpciones(), this, buttonGroup3.getSelection().getActionCommand());
        worker.execute();

        //creamos el panel que despliega el mensaje que se esta trabajando
        SignWorkingPanel panelWorking = new SignWorkingPanel(imageWorking);

        //cerramos el dialogo de este panel y abrimos el dialog del panel working
        this.dialog.setVisible(true);

        //creamos el dialogo modal donde se incluye el panel con el combo para seleccionar el certificado
        //con el cual se va  firmar
        workingDialog = new Dialog(dialog, msgUtil.getValue("MSG_WORKING_DIALOG_TITLE"));
        panelWorking.setDialog(workingDialog);
        workingDialog.add(panelWorking);
        workingDialog.setModal(true);
        workingDialog.setResizable(false);
        workingDialog.pack();
        workingDialog.setLocationRelativeTo(null);
        workingDialog.setVisible(true);
}//GEN-LAST:event_jButtonFirmarActionPerformed

    /**
     * Cierra el dialogo de este panel y el dialogo que este panel abre el de
     * trabajo
     */
    public void close() {
        this.workingDialog.setVisible(false);
        this.dialog.setVisible(false);

    }

    /**
     * Despliega un mensaje de error a partir de una exception
     *
     * @param message
     */
    public void displayErrorMessage(Throwable exception) {
        //cierra todos los panls y despliega el error
        this.workingDialog.setVisible(false);
        this.dialog.setVisible(true);
        ErrorDialog.showMessageDialog(this, exception);
    }
    //los codigos de los mensajes de error
    private static String INVALID_CERTIFICATE_ERROR_CODE = "INVALID_CERTIFICATE";
    private static String INVALID_DIGEST = "INVALID_DIGEST";
    private static String INVALID_SIGN_ERROR_CODE = "INVALID_SIGN";
    private static String INVALID_GENERAL = "INVALID_GENERAL";

    public void displayWarningMessage(File[] documentosSigned, List<ResultadoValidacion> resultadoValidacion) {
        //cierra todos los panls y despliega el error
        this.workingDialog.setVisible(false);
        this.dialog.setVisible(true);

        String message = "<html>";
        for (ResultadoValidacion rv : resultadoValidacion) {
            if (msgUtil.containsKey(rv.getCode())) {
                if (rv.getMessage() != null && !rv.getMessage().equalsIgnoreCase("")) {
                    message = message + "<br>" + msgUtil.getValue(rv.getCode()) + " - " + rv.getMessage();
                } else {
                    message = message + "<br>" + msgUtil.getValue(rv.getCode());
                }

            } else {
                message = message + "<br>" + rv.getMessage();
            }
        }
        ErrorDialog.showWarningDialog(this, message);
        //continua  aunaque falle
        this.viewer.signSuccessful(documentosSigned);

    }

    /**
     * Despliega un mensaje de error a partir de una exception
     *
     * @param message
     */
    public void displayErrorMessage(List<ResultadoValidacion> resultadoValidacion) {
        //cierra todos los panls y despliega el error
        this.workingDialog.setVisible(false);
        this.dialog.setVisible(true);

        String message = "<html>";
        for (ResultadoValidacion rv : resultadoValidacion) {
            if (msgUtil.containsKey(rv.getCode())) {
                if (rv.getMessage() != null && !rv.getMessage().equalsIgnoreCase("")) {
                    message = message + "<br>" + msgUtil.getValue(rv.getCode()) + " - " + rv.getMessage();
                } else {
                    message = message + "<br>" + msgUtil.getValue(rv.getCode());
                }

            } else {
                message = message + "<br>" + rv.getMessage();
            }
        }
        ErrorDialog.showMessageDialog(this, message);
    }

    /**
     * Despliega un mensaje de error
     *
     * @param message
     */
    public void displayPopupErrorMessage(String message) {
        ErrorDialog.showMessageDialog(this, message);
    }

    public void displayErrorMessage(String message) {
        messageCertificados.setText(message);
        messageCertificados.setVisible(true);
        dialog.repaint();
        dialog.pack();
    }

    public void displayPopupEtokenModel() {

        PKCS11Util util = new PKCS11Util(cfgUtil, msgUtil);
        String ligAladdin = util.loadETokenLib(Constantes.MODELO_ALADDIN, userAgent);
        ligAladdin = ((ligAladdin != null) && (ligAladdin.length() != 0)) ? " (" + ligAladdin + ")" : "";
        String ligEPass2003 = util.loadETokenLib(Constantes.MODELO_EPASS2003, userAgent);
        ligEPass2003 = ((ligEPass2003 != null) && (ligEPass2003.length() != 0)) ? " (" + ligEPass2003 + ")" : "";
        String ligEPass3003auto = util.loadETokenLib(Constantes.MODELO_EPASS3003AUTO, userAgent);
        ligEPass3003auto = ((ligEPass3003auto != null) && (ligEPass3003auto.length() != 0)) ? " (" + ligEPass3003auto + ")" : "";
        String ligGemalto = util.loadETokenLib(Constantes.MODELO_GEMALTO, userAgent);
        ligGemalto = ((ligGemalto != null) && (ligGemalto.length() != 0)) ? " (" + ligGemalto + ")" : "";

        String empty = "---------";
        Object[] models = {empty, Constantes.MODELO_ALADDIN + ligAladdin, Constantes.MODELO_EPASS2003 + ligEPass2003, Constantes.MODELO_EPASS3003AUTO + ligEPass3003auto, "Cedula" + ligGemalto, Constantes.MODELO_OTRO};

        UIManager.put("OptionPane.okButtonText", "Aceptar");

        //modelo = (String)JOptionPane.showInputDialog(
        modelo = (String) JOptionPane.showInputDialog(
                this,
                "\nNo se ha encontrado un modelo de eToken instalado.\nSeleccione el modelo de su eToken. Luego de seleccionarlo,\nse desplegará una ventana para indicar dónde se encuentra instalado el mismo.\n\n",
                "Seleccionar modelo",
                JOptionPane.WARNING_MESSAGE,
                null,
                models,
                empty);

        if ((modelo != null) && (modelo.length() > 0) && (!modelo.equals(empty))) {
            displayDriverFileChooser();
        } else {
            displayPopupErrorMessage(msgUtil.getValue("MSG_NO_MODEL_DRIVER_CHOOSED"));
        }

    }

    public void displayDriverFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        if (fc.showDialog(this, msgUtil.getValue("MSG_OPEN")) == JFileChooser.APPROVE_OPTION) {
            pathDriver = fc.getSelectedFile().getAbsolutePath();
            System.out.println("pathDriver: " + pathDriver);
            PKCS11Util util = new PKCS11Util(cfgUtil, msgUtil);
            try {
                util.createConfigFile(pathDriver, modelo, userAgent);
            } catch (IOException ex) {
                displayPopupErrorMessage(msgUtil.getValue("MSG_NO_MODEL_DRIVER_CHOOSED"));
                Logger.getLogger(SignPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String displayChooseProvider() {

        List<Object> models = new ArrayList<Object>();
        for (Provider prov : Security.getProviders()) {
            if (!prov.getServices().isEmpty()) {
                if (prov.getName().contains("SunPKCS11-")) {
                    if (prov.getName().contains("gemalto")) {
                        models.add("cedula");
                    } else {
                        models.add(prov.getName().substring(10));

                    }
                }
            }

        }
        String empty = "---------";
        Object[] modelsActive = models.toArray();
        UIManager.put("OptionPane.okButtonText", "Aceptar");
        //jButtonCerrar.setText("Aceptar");

        String activeModel = (String) JOptionPane.showInputDialog(
                this,
                "\nHay mas de un modelo de eToken conectado.\nSeleccione el que desea tener activo ",
                "Seleccionar modelo",
                JOptionPane.WARNING_MESSAGE,
                null,
                modelsActive, empty);

        if (!activeModel.equals(empty)) {
            if (activeModel.contains("cedula")) {
                return "SunPKCS11-gemalto";
            } else {
                return "SunPKCS11-" + activeModel;

            }
        } else {
            displayPopupErrorMessage(msgUtil.getValue("MSG_NO_ETOKEN_CHOOSED"));
        }
        return "";
    }
    private void jButtonCargarCertificadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCargarCertificadosActionPerformed
        jComboBoxCertificados.removeAllItems();
        displayErrorMessage("");
        List<SofisCertificate> certs = null;
        try {
            String passwordCertStore = new String();
            if (jTextFieldContraseniaPaso1.getPassword() != null) {
                passwordCertStore = new String(jTextFieldContraseniaPaso1.getPassword());
            }

            ButtonModel b = buttonGroup3.getSelection();
            String selected = b.getActionCommand();
            if (selected.equalsIgnoreCase("TOKEN")) {
                PKCS11Util util = new PKCS11Util(cfgUtil, msgUtil);
                String activeProvider = "";

                certs = storeMg.getSignCertificateFromPKCS11(passwordCertStore, null, PKCS11CertStoreImpl.PKCS11CertStoreType.ETOKEN, activeProvider);
                if (util.getNumProviders() > 1) {
                    activeProvider = displayChooseProvider();
                    certs = storeMg.getSignCertificateFromPKCS11(passwordCertStore, null, PKCS11CertStoreImpl.PKCS11CertStoreType.ETOKEN, activeProvider);

                }
            }
            if (selected.equalsIgnoreCase("BROWSER")) {
                certs = storeMg.getSignCertificateFromBrowser();
            }

            if (selected.equalsIgnoreCase("FILE")) {
                String file = jTextFieldFile.getText();
                if (file != null && file.length() > 0) {
                    File f = new File(file);
                    if (f.exists()) {
                        certs = storeMg.getSignCertificateFromPKCS11(passwordCertStore, jTextFieldFile.getText(), PKCS11CertStoreImpl.PKCS11CertStoreType.FILE, "");
                    }
                }
            }

            if (!storeMg.isPKCS11_TOKEN_DETECTED() && passwordCertStore.trim().length() > 0) {
                displayErrorMessage(msgUtil.getValue("MSG_INGRESO_PASSWORD_INVALIDO"));
            }
        } catch (StoreInvalidPasswordException e) {
            displayPopupErrorMessage(msgUtil.getValue("MSG_CONTRASENIA_INVALIDA"));
            jButtonFirmar.setEnabled(false);
            return;
        } catch (DriverException e) {
            e.printStackTrace();
            //@TODO
            //despliega file chooser para que indique donde esta el modelo
            //displayPopupErrorMessage(msgUtil.getValue("MSG_NO_DRIVERS_INSTALADOS"));
            displayPopupEtokenModel();
            jButtonFirmar.setEnabled(false);
            return;
        }

        if (certs != null && certs.size() > 0) {
            for (SofisCertificate cert : certs) {
                if (cert.isOk()) {
                    jComboBoxCertificados.addItem(cert);
                }
            }
            jComboBoxCertificados.setEnabled(true);
            jButtonFirmar.setEnabled(true);
            //jTextFieldCertificatePassword.setEnabled(true);
        } else {
            displayPopupErrorMessage(msgUtil.getValue("MSG_EMPTY_CERTIFICADOS"));
            jButtonFirmar.setEnabled(false);
        }
    }//GEN-LAST:event_jButtonCargarCertificadosActionPerformed

    private void jButtonCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCerrarActionPerformed
        System.out.println("jButtonCerrarActionPerformed...");
        this.dialog.setVisible(false);
        this.close();
        this.viewer.cerrarApplet();
    }//GEN-LAST:event_jButtonCerrarActionPerformed

    private void jButtonSelectFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectFileActionPerformed
        // TODO add your handling code here:
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            jTextFieldFile.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_jButtonSelectFileActionPerformed

    private void jRadioButtonTokenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonTokenActionPerformed

        jTextFieldContraseniaPaso1.setText("");
        jTextFieldFile.setText("");

        jTextFieldFile.setEnabled(false);
        jButtonSelectFile.setEnabled(false);

        jTextFieldContraseniaPaso1.setEnabled(true);
        jButtonCargarCertificados.setEnabled(true);

    }//GEN-LAST:event_jRadioButtonTokenActionPerformed

    private void jRadioButtonArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonArchivoActionPerformed

        jTextFieldContraseniaPaso1.setText("");
        jTextFieldFile.setText("");

        jTextFieldContraseniaPaso1.setEnabled(true);
        jTextFieldFile.setEnabled(true);
        jButtonSelectFile.setEnabled(true);
        jButtonCargarCertificados.setEnabled(true);
    }//GEN-LAST:event_jRadioButtonArchivoActionPerformed

    private void jRadioButtonNavegadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonNavegadorActionPerformed

        jTextFieldContraseniaPaso1.setText("");
        jTextFieldFile.setText("");

        jTextFieldContraseniaPaso1.setEnabled(false);
        jTextFieldFile.setEnabled(false);
        jButtonSelectFile.setEnabled(false);

        jButtonCargarCertificados.setEnabled(true);
    }//GEN-LAST:event_jRadioButtonNavegadorActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JButton jButtonCargarCertificados;
    private javax.swing.JButton jButtonCerrar;
    private javax.swing.JButton jButtonFirmar;
    private javax.swing.JButton jButtonSelectFile;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBoxCertificados;
    private javax.swing.JLabel jLabelCertificate;
    private javax.swing.JLabel jLabelFile;
    private javax.swing.JLabel jLabelPaso1;
    private javax.swing.JLabel jLabelPaso1Contrasenia;
    private javax.swing.JLabel jLabelPaso2;
    private javax.swing.JLabel jLabelTitleStep1;
    private javax.swing.JLabel jLabelTitleStep2;
    private javax.swing.JRadioButton jRadioButtonArchivo;
    private javax.swing.JRadioButton jRadioButtonNavegador;
    private javax.swing.JRadioButton jRadioButtonToken;
    private javax.swing.JPasswordField jTextFieldContraseniaPaso1;
    private javax.swing.JTextField jTextFieldFile;
    private javax.swing.JLabel messageCertificados;
    // End of variables declaration//GEN-END:variables

}
