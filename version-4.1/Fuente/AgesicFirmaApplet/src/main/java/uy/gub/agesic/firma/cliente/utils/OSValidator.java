package uy.gub.agesic.firma.cliente.utils;

/**
 *
 * @author Sofis Solutions (www.sofis-solutions.com)
 */
public class OSValidator {

    private static String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static String OS_ARCH = System.getProperty("os.arch").toLowerCase();
    private static String OS_VERSION = System.getProperty("os.version").toLowerCase();

    public static void main(String[] args) {

        System.out.println(getOSDetail());

        if (isWindows()) {
            System.out.println("This is Windows");
        } else if (isMac()) {
            System.out.println("This is Mac");
        } else if (isUnix()) {
            System.out.println("This is Unix or Linux");
        } else if (isSolaris()) {
            System.out.println("This is Solaris");
        } else {
            System.out.println("Your OS is not support!!");
        }
    }

    public static boolean isWindows() {
        return (OS_NAME.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS_NAME.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS_NAME.indexOf("nix") >= 0 || OS_NAME.indexOf("nux") >= 0 || OS_NAME.indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (OS_NAME.indexOf("sunos") >= 0);
    }

    public static String getOSName() {
        return OS_NAME;
    }
    
    public static String getOSCodeName() {
        if (isWindows()) {
            return "win";
        } else if (isMac()) {
            return "osx";
        } else if (isUnix()) {
            return "uni";
        } else if (isSolaris()) {
            return "sol";
        } else {
            return "err";
        }
    }

    public static String getOSArch() {
        return OS_ARCH;
    }

    public static String getOSVersion() {
        return OS_VERSION;
    }

    public static String getOSDetail() {
        return "name:"+OS_NAME + ", version:" + OS_VERSION + ", arch:" + OS_ARCH;
    }
}
