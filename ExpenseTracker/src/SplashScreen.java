import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JFrame {
    public SplashScreen() {
        setTitle("Loading...");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.PINK);
        setLayout(new BorderLayout());

        JLabel label = new JLabel(" Loading Dashboard... ", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        add(label, BorderLayout.CENTER);

        setVisible(true);

        // 🛠 Create timer only once!
        Timer timer = new Timer(2000, e -> {
            dispose(); // close splash screen
            new LoginPage(); // open dashboard
        });
        timer.setRepeats(false); // 🔐 run only ONCE!
        timer.start();
    }
}
