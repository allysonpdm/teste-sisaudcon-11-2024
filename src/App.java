import database.MigrationManager;
import Views.LoginView;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        
        try {
            MigrationManager migrationManager = new MigrationManager();
            migrationManager.runMigrations();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao executar migrations: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}
