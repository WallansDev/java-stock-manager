import javax.swing.SwingUtilities;
import src.views.MainView;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainView().setVisible(true);
        });
    }
}
