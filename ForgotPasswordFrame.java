package lucentglassmanagement;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class ForgotPasswordFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    private final Color GREEN = new Color(36, 91, 67);
    private final Color DARK_GREEN = new Color(25, 66, 48);
    private final Color CREAM = new Color(247, 243, 229);
    private final Color WHITE = new Color(255, 253, 247);

    public ForgotPasswordFrame() {

        setTitle("Lucent Glass - Reset Password");
        setSize(550, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CREAM);

        // HEADER
        JPanel header = new JPanel();
        header.setBackground(GREEN);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel company = new JLabel("LUCENT GLASS");
        company.setFont(new Font("Georgia", Font.BOLD, 27));
        company.setForeground(Color.WHITE);
        company.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("INDUSTRIES");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(225, 235, 220));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(company);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);

        mainPanel.add(header, BorderLayout.NORTH);

        // FORM CARD
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(221, 216, 199), 1, true),
            new EmptyBorder(25, 35, 25, 35)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 0, 7, 0);

        JLabel title = new JLabel("Reset Your Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(DARK_GREEN);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        card.add(title, gbc);

        JLabel description = new JLabel(
            "Enter your registered email and create a new password."
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        description.setForeground(Color.GRAY);
        description.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 20, 0);
        card.add(description, gbc);

        // EMAIL
        addLabel(card, gbc, "Registered Email Address", 2);

        emailField = new JTextField();
        styleField(emailField);
        addField(card, gbc, emailField, 3);

        // NEW PASSWORD
        addLabel(card, gbc, "New Password", 4);

        newPasswordField = new JPasswordField();
        styleField(newPasswordField);
        addField(card, gbc, newPasswordField, 5);

        // CONFIRM PASSWORD
        addLabel(card, gbc, "Confirm Password", 6);

        confirmPasswordField = new JPasswordField();
        styleField(confirmPasswordField);
        addField(card, gbc, confirmPasswordField, 7);

        // RESET BUTTON
        JButton resetButton = new JButton("RESET PASSWORD");
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetButton.setBackground(GREEN);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );
        resetButton.setPreferredSize(new Dimension(200, 43));

        gbc.gridy = 8;
        gbc.insets = new Insets(20, 0, 10, 0);
        card.add(resetButton, gbc);

        resetButton.addActionListener(e -> resetPassword());

        // BACK TO LOGIN
        JButton backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setForeground(GREEN);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        gbc.gridy = 9;
        gbc.insets = new Insets(5, 0, 0, 0);
        card.add(backButton, gbc);

        backButton.addActionListener(e -> dispose());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(CREAM);
        centerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        centerPanel.add(card);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void addLabel(
            JPanel panel,
            GridBagConstraints gbc,
            String text,
            int row) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(DARK_GREEN);

        gbc.gridy = row;
        gbc.insets = new Insets(8, 0, 2, 0);

        panel.add(label, gbc);
    }

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            JTextField field,
            int row) {

        gbc.gridy = row;
        gbc.insets = new Insets(2, 0, 8, 0);

        panel.add(field, gbc);
    }

    private void styleField(JTextField field) {

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(350, 40));
        field.setBackground(Color.WHITE);
        field.setForeground(DARK_GREEN);
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(195, 204, 188), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }

    // RESET PASSWORD
    private void resetPassword() {

        String email = emailField.getText().trim();

        char[] newPassword =
            newPasswordField.getPassword();

        char[] confirmPassword =
            confirmPasswordField.getPassword();

        try {

            if (email.isEmpty()
                    || newPassword.length == 0
                    || confirmPassword.length == 0) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields.",
                    "Missing Details",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid email address."
                );
                return;
            }

            if (newPassword.length < 8) {

                JOptionPane.showMessageDialog(
                    this,
                    "Password must contain at least 8 characters."
                );
                return;
            }

            if (!java.util.Arrays.equals(
                    newPassword, confirmPassword)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match!",
                    "Password Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String hashedPassword =
                hashPassword(new String(newPassword));

            String sql =
                "UPDATE USERS SET PASSWORD_HASH = ? " +
                "WHERE LOWER(EMAIL) = LOWER(?)";

            try (Connection con =
                    DBConnection.getConnection();

                 PreparedStatement pst =
                    con.prepareStatement(sql)) {

                pst.setString(1, hashedPassword);
                pst.setString(2, email);

                int rows = pst.executeUpdate();

                if (rows > 0) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Password reset successfully!\n" +
                        "You can now login with your new password.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    dispose();

                } else {

                    JOptionPane.showMessageDialog(
                        this,
                        "This email address is not registered.",
                        "Email Not Found",
                        JOptionPane.WARNING_MESSAGE
                    );
                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                this,
                "Unable to reset password.\n" +
                ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );

        } finally {

            java.util.Arrays.fill(newPassword, '\0');
            java.util.Arrays.fill(confirmPassword, '\0');
        }
    }

    // SHA-256 PASSWORD HASHING
    private String hashPassword(String password)
            throws Exception {

        MessageDigest md =
            MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(
            password.getBytes(StandardCharsets.UTF_8)
        );

        StringBuilder hex = new StringBuilder();

        for (byte b : hash) {

            hex.append(
                String.format("%02x", b & 0xff)
            );
        }

        return hex.toString();
    }
}