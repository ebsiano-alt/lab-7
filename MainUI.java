package lab_7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainUI {
    private final JsonDatabaseManager db;
    private final AuthService auth;

    public MainUI() {
        db = new JsonDatabaseManager();
        auth = new AuthService(db);
        SwingUtilities.invokeLater(this::showLogin);
    }

   

        backBtn.addActionListener(ev -> {
            f.dispose();
            showLogin();
        });

        f.setContentPane(p);
        f.setVisible(true);
    }

    public static void main(String[] args) {
      
        SwingUtilities.invokeLater(() -> {
           
            new MainUI();
        });
    }
}