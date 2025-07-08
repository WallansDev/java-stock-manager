import java.util.UUID;

import javax.swing.SwingUtilities;

import src.controllers.ProductController;
import src.models.Product;
import src.views.MainView;
import src.views.ProductView;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainView().setVisible(true);
        });
    }
}
