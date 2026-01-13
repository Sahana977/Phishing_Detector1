import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.KeyEvent;

public class KeyloggerDetector {

    // List of suspicious keylogger methods
    private static final List<String> SUSPICIOUS_METHODS = Arrays.asList(
            "org.jnativehook.keyboard.NativeKeyListener",
            "java.awt.Robot",
            "java.awt.event.KeyListener"
    );

    // Detects keyloggers using reflection
    private static boolean detectKeylogger() {
        try {
            Package[] packages = Package.getPackages();
            for (Package p : packages) {
                String packageName = p.getName();
                for (String method : SUSPICIOUS_METHODS) {
                    if (packageName.contains(method)) {
                        System.out.println("⚠ WARNING: Possible Keylogger Detected: " + packageName);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Detects keylogging through AWT Event Monitoring
    private static boolean detectAWTKeylogger() {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_SHIFT); // Simulate key press
            robot.keyRelease(KeyEvent.VK_SHIFT);

            Toolkit toolkit = Toolkit.getDefaultToolkit();
            if (toolkit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK)) {
                return true; // Suspicious behavior detected
            }
        } catch (Exception e) {
            return true; // If error occurs, assume keylogger might be interfering
        }
        return false;
    }

    public static void main(String[] args) {
        boolean keyloggerFound = detectKeylogger() || detectAWTKeylogger();

        if (keyloggerFound) {
            JOptionPane.showMessageDialog(null, "⚠ WARNING: Possible Keylogger Detected!\nClose suspicious applications.",
                    "Security Alert", JOptionPane.WARNING_MESSAGE);
        } else {
            System.out.println("✅ No keylogger detected.");
        }
    }
}
