// Do Ctrl + Shift + P to clean up this file if needed
// So that the packages will load properly in your IDE
import View.NewMainView;
import javax.swing.UIManager; // For setting the look and feel; testing

public class Main {
    public static void main(String[] args) {
        try {
            // This finds the "Nimbus" Look and Feel and applies it
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus isn't available, just use the default
            e.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            new NewMainView().setVisible(true);
        });
    }
}
