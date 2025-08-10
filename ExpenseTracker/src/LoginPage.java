import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Map<String, String> userData = new HashMap<>(); // username -> password
    private final File userFile = new File("users.txt");

    public LoginPage() {
        setTitle("BrokenNoMore - Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        loadUserData();

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(50, 50, 100, 25);
        add(lblUser);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 180, 25);
        add(usernameField);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(50, 100, 100, 25);
        add(lblPass);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 180, 25);
        add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(50, 160, 130, 30);
        add(loginBtn);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(200, 160, 130, 30);
        add(registerBtn);

        JButton guestBtn = new JButton("Continue as Guest");
        guestBtn.setBounds(100, 210, 180, 30);
        add(guestBtn);

        loginBtn.addActionListener(e -> attemptLogin());
        registerBtn.addActionListener(e -> attemptRegister());
        guestBtn.addActionListener(e -> {
            dispose();
            new DashboardPage("Guest", null);
        });

        setVisible(true);
    }

    private void loadUserData() {
        if (!userFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    userData.put(parts[0], parts[1]);
                }
            }
        } catch (IOException ignored) {}
    }

    private void saveUserData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(userFile))) {
            for (var entry : userData.entrySet()) {
                pw.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException ignored) {}
    }

    private void attemptLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        if (userData.containsKey(user) && userData.get(user).equals(pass)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            new DashboardPage(user, pass);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    private void attemptRegister() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }
        if (userData.containsKey(user)) {
            JOptionPane.showMessageDialog(this, "Username already exists.");
            return;
        }
        userData.put(user, pass);
        saveUserData();
        JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}
