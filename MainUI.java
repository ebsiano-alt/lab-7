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

    private void showLogin() {
        JFrame f = new JFrame("SkillForge - Login");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(420, 300);
        f.setLocationRelativeTo(null);
        JPanel p = new JPanel();
        p.setLayout(null);

     
        JLabel lab = new JLabel("Email:");
        lab.setBounds(10, 60, 80, 25);
        p.add(lab);
        JTextField emailF = new JTextField();
        emailF.setBounds(100, 60, 250, 25);
        p.add(emailF);

        JLabel pass = new JLabel("Password:");
        pass.setBounds(10, 95, 80, 25);
        p.add(pass);
        JPasswordField passF = new JPasswordField();
        passF.setBounds(100, 95, 250, 25);
        p.add(passF);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(100, 135, 110, 30);
        p.add(loginBtn);

        JButton signupBtn = new JButton("Signup");
        signupBtn.setBounds(240, 135, 110, 30);
        p.add(signupBtn);

        JLabel status = new JLabel("");
        status.setBounds(10, 180, 380, 25);
        p.add(status);

        loginBtn.addActionListener(e -> {
            String email = emailF.getText().trim();
            String passw = new String(passF.getPassword());
            User u = auth.login(email, passw);
            if (u == null) {
                status.setText("Invalid credentials.");
            } else {
                f.dispose();
                if ("student".equals(u.getRole())) {
                    new StudentDashboard((Student) u, db).show();
                } else {
                    new InstructorDashboard((Instructor) u, db).show();
                }
            }
        });

        signupBtn.addActionListener(e -> {
            f.dispose();
            showSignup();
        });

        f.setContentPane(p);
        f.setVisible(true);
    }

    private void showSignup() {
        JFrame f = new JFrame(" Signup");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(480, 360);
        f.setLocationRelativeTo(null);
        JPanel p = new JPanel();
        p.setLayout(null);

        JLabel header = new JLabel("<html><b>Register</b></html>");
        header.setBounds(10, 5, 400, 25);
        p.add(header);

        JLabel roleL = new JLabel("Role:");
        roleL.setBounds(10, 40, 80, 25);
        p.add(roleL);
        String[] roles = {"student", "instructor"};
        JComboBox<String> roleC = new JComboBox<>(roles);
        roleC.setBounds(100, 40, 160, 25);
        p.add(roleC);

        JLabel userL = new JLabel("Username:");
        userL.setBounds(10, 80, 80, 25);
        p.add(userL);
        JTextField userF = new JTextField();
        userF.setBounds(100, 80, 260, 25);
        p.add(userF);

        JLabel emailL = new JLabel("Email:");
        emailL.setBounds(10, 120, 80, 25);
        p.add(emailL);
        JTextField emailF = new JTextField();
        emailF.setBounds(100, 120, 260, 25);
        p.add(emailF);

        JLabel passL = new JLabel("Password:");
        passL.setBounds(10, 160, 80, 25);
        p.add(passL);
        JPasswordField passF = new JPasswordField();
        passF.setBounds(100, 160, 260, 25);
        p.add(passF);

        JButton createBtn = new JButton("Create Account");
        createBtn.setBounds(100, 205, 150, 30);
        p.add(createBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(270, 205, 90, 30);
        p.add(backBtn);

     
        

        JLabel status = new JLabel("");
        status.setBounds(10, 295, 440, 25);
        p.add(status);

        createBtn.addActionListener(ev -> {
            String role = (String) roleC.getSelectedItem();
            String username = userF.getText().trim();
            String email = emailF.getText().trim();
            String pass = new String(passF.getPassword());
            boolean ok;
            if ("student".equals(role)) {
                ok = auth.signupStudent(username, email, pass);
            } else {
                ok = auth.signupInstructor(username, email, pass);
            }
            if (ok) {
                JOptionPane.showMessageDialog(f, "Account created. Please login.");
                f.dispose();
                showLogin();
            } else {
                status.setText("Failed to create account: maybe email already used or invalid input.");
            }
        });

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