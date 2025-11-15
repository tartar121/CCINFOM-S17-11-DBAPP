// Do Ctrl + Shift + P to clean up this file if needed
// So that the packages will load properly in your IDE
import View.MainView;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainView().setVisible(true);
        });
    }
}
