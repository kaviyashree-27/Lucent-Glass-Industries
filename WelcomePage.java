package lucentglassmanagement;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class WelcomePage extends JFrame {

    private Image backgroundImage;

    public WelcomePage() {

        setTitle("LUCENT GLASS INDUSTRIES");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Login button
        JButton loginButton = new JButton();

        loginButton.setOpaque(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        // Create Account button
        JButton registerButton = new JButton();

        registerButton.setOpaque(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        // Load image
        URL imageURL = getClass().getResource(
            "/images/lucent_background.png.jpeg"
        );

        if (imageURL == null) {

            JOptionPane.showMessageDialog(
                this,
                "Image not found! Check images folder."
            );

            return;
        }

        backgroundImage =
            new ImageIcon(imageURL).getImage();

        // Background panel
        JPanel background = new JPanel(null) {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                g.drawImage(
                    backgroundImage,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
                );
            }

            @Override
            public void doLayout() {

                int w = getWidth();
                int h = getHeight();

                double sx = w / 1536.0;
                double sy = h / 864.0;

                // Login button position
                loginButton.setBounds(
                    (int)(570 * sx),
                    (int)(725 * sy),
                    (int)(245 * sx),
                    (int)(95 * sy)
                );

                // Create account button position
                registerButton.setBounds(
                    (int)(850 * sx),
                    (int)(725 * sy),
                    (int)(340 * sx),
                    (int)(95 * sy)
                );
            }
        };

        // Login action
        loginButton.addActionListener(e -> {

            new LoginFrame().setVisible(true);

            dispose();
        });

        // Registration action
        registerButton.addActionListener(e -> {

            new RegistrationFrame().setVisible(true);

            dispose();
        });

        // Add buttons
        background.add(loginButton);
        background.add(registerButton);

        setContentPane(background);

        setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new WelcomePage();

        });
    }
}