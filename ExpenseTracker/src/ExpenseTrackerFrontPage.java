import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class ExpenseTrackerFrontPage extends JFrame {
    // Using 1280x720 for 16:9 aspect ratio
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    
    private Image bgImage;

    public ExpenseTrackerFrontPage() {
        setTitle("BrokenNoMore");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Load the background image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/background.jpg"));
        bgImage = bgIcon.getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
        
        // Create a custom JPanel for the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // 💫 Title
        JLabel title = new JLabel("BrokenNoMore", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 60)); // Larger font for bigger screen
        title.setForeground(Color.WHITE);
        title.setBounds(WIDTH/2 - 250, 60, 500, 70);
        backgroundPanel.add(title);

        // 💌 Welcome text
        JLabel welcome = new JLabel("Welcome to your personal expense tracker!", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.ITALIC, 24)); // Modern font
        welcome.setForeground(Color.WHITE);
        welcome.setBounds(WIDTH/2 - 280, 150, 560, 30);
        backgroundPanel.add(welcome);

        // 📘 About section
        JTextArea about = new JTextArea(
            "BrokenNoMore helps you track your spending and save smarter.\n");
        about.setLineWrap(true);
        about.setWrapStyleWord(true);
        about.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        about.setOpaque(false);
        about.setEditable(false);
        about.setForeground(Color.WHITE);
        about.setBounds(WIDTH/2 - 250, 200, 500, 80);
        backgroundPanel.add(about);

        // 💅 Lessgoo button
        JButton enterBtn = new JButton("Lessgoo");
        enterBtn.setBounds(WIDTH/2 - 80, 300, 160, 50); // Centered button
        enterBtn.setBackground(new Color(255, 105, 180)); // Hot pink
        enterBtn.setForeground(Color.WHITE);
        enterBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        enterBtn.setFocusPainted(false);
        enterBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        backgroundPanel.add(enterBtn);

        enterBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close this frame
                new SplashScreen(); // Splash screen
            }
        });

        // 🧾 Footer with name and links
        JLabel footer = new JLabel("<html><div style='text-align:left;'>"
                + "Farhana Maisha Chowdhury<br>"
                + "<a href='https://linkedin.com/in/farhanamaishachowdhury '>LinkedIn</a> | "
                + "<a href='https://github.com/farhanamaisha'>GitHub</a></div></html>");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footer.setForeground(Color.WHITE);
        footer.setBounds(20, HEIGHT - 70, 300, 50);
        backgroundPanel.add(footer);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTrackerFrontPage().setVisible(true));
    }
}