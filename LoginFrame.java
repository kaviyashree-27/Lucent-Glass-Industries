
package lucentglassmanagement;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.lang.reflect.Constructor;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPassword;

    private final Color darkGreen = new Color(16, 45, 41);
    private final Color gold = new Color(195, 151, 65);
    private final Color background = new Color(247, 248, 246);
    private final Color textColor = new Color(35, 51, 48);

    public LoginFrame() {

        setTitle("LUCENT GLASS INDUSTRIES | Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(background);

        // LEFT SIDE - COMPANY BRANDING
        JPanel leftPanel = new JPanel() {

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
                );

                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(10, 37, 34),
                    getWidth(), getHeight(),
                    new Color(27, 63, 57)
                );

                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        leftPanel.setLayout(new GridBagLayout());

        JPanel brandContent = new JPanel();
        brandContent.setOpaque(false);
        brandContent.setLayout(
            new BoxLayout(brandContent, BoxLayout.Y_AXIS)
        );

        JLabel companyName = new JLabel("LUCENT");
        companyName.setFont(
            new Font("Georgia", Font.BOLD, 54)
        );
        companyName.setForeground(Color.WHITE);
        companyName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel industryName = new JLabel("GLASS INDUSTRIES");
        industryName.setFont(
            new Font("Segoe UI", Font.BOLD, 24)
        );
        industryName.setForeground(gold);
        industryName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel goldLine = new JPanel();
        goldLine.setBackground(gold);
        goldLine.setMaximumSize(new Dimension(230, 3));
        goldLine.setPreferredSize(new Dimension(230, 3));
        goldLine.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel quote1 = new JLabel("CRAFTING CLARITY.");
        quote1.setFont(
            new Font("Segoe UI", Font.BOLD, 17)
        );
        quote1.setForeground(Color.WHITE);
        quote1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel quote2 = new JLabel("BUILDING EXCELLENCE.");
        quote2.setFont(
            new Font("Segoe UI", Font.BOLD, 17)
        );
        quote2.setForeground(Color.WHITE);
        quote2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel(
            "Quality Glass Solutions for Every Space"
        );
        description.setFont(
            new Font("Segoe UI", Font.PLAIN, 14)
        );
        description.setForeground(new Color(205, 215, 210));
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandContent.add(companyName);
        brandContent.add(Box.createVerticalStrut(8));
        brandContent.add(industryName);
        brandContent.add(Box.createVerticalStrut(25));
        brandContent.add(goldLine);
        brandContent.add(Box.createVerticalStrut(30));
        brandContent.add(quote1);
        brandContent.add(Box.createVerticalStrut(8));
        brandContent.add(quote2);
        brandContent.add(Box.createVerticalStrut(25));
        brandContent.add(description);

        leftPanel.add(brandContent);

        // RIGHT SIDE - LOGIN
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(background);

        JPanel loginCard = new JPanel();
        loginCard.setBackground(Color.WHITE);
        loginCard.setLayout(new GridBagLayout());

        loginCard.setBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(new Color(225, 228, 224), 1),
                new EmptyBorder(40, 45, 40, 45)
            )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel welcomeLabel = new JLabel(
            "Welcome Back!", SwingConstants.CENTER
        );
        welcomeLabel.setFont(
            new Font("Georgia", Font.BOLD, 34)
        );
        welcomeLabel.setForeground(darkGreen);

        JLabel subtitle = new JLabel(
            "Sign in to continue to your account",
            SwingConstants.CENTER
        );
        subtitle.setFont(
            new Font("Segoe UI", Font.PLAIN, 14)
        );
        subtitle.setForeground(Color.GRAY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginCard.add(welcomeLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 8, 35, 8);
        loginCard.add(subtitle, gbc);

        // USERNAME
        gbc.insets = new Insets(10, 8, 8, 8);
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.3;

        JLabel usernameLabel = createLabel("Username");
        loginCard.add(usernameLabel, gbc);

        usernameField = new JTextField();
        styleField(usernameField);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        loginCard.add(usernameField, gbc);

        // PASSWORD
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0.3;

        JLabel passwordLabel = createLabel("Password");
        loginCard.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        styleField(passwordField);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        loginCard.add(passwordField, gbc);

        // SHOW PASSWORD
        showPassword = new JCheckBox("Show Password");
        showPassword.setBackground(Color.WHITE);
        showPassword.setFont(
            new Font("Segoe UI", Font.PLAIN, 13)
        );

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('\u2022');
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 8, 8, 8);
        loginCard.add(showPassword, gbc);

        // FORGOT PASSWORD
        JButton forgotButton = new JButton("Forgot Password?");

        forgotButton.setFont(
            new Font("Segoe UI", Font.BOLD, 13)
        );
        forgotButton.setForeground(new Color(157, 119, 53));
        forgotButton.setBackground(Color.WHITE);
        forgotButton.setBorderPainted(false);
        forgotButton.setFocusPainted(false);
        forgotButton.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        forgotButton.addActionListener(e -> forgotPassword());

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 8, 20, 8);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        loginCard.add(forgotButton, gbc);

        // SIGN IN BUTTON
        JButton loginButton = createButton(
            "SIGN IN", darkGreen, Color.WHITE
        );

        loginButton.addActionListener(e -> login());

        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 8, 18, 8);
        loginCard.add(loginButton, gbc);

        // CREATE ACCOUNT
        JLabel accountText = new JLabel(
            "Don't have an account?",
            SwingConstants.CENTER
        );
        accountText.setFont(
            new Font("Segoe UI", Font.PLAIN, 13)
        );
        accountText.setForeground(Color.GRAY);

        gbc.gridy = 7;
        gbc.insets = new Insets(8, 8, 3, 8);
        loginCard.add(accountText, gbc);

        JButton registerButton = createButton(
            "CREATE ACCOUNT", gold, Color.WHITE
        );

        registerButton.addActionListener(e -> openRegistration());

        gbc.gridy = 8;
        gbc.insets = new Insets(5, 8, 5, 8);
        loginCard.add(registerButton, gbc);

        // FOOTER
        JLabel footer = new JLabel(
            "© 2026 LUCENT GLASS INDUSTRIES",
            SwingConstants.CENTER
        );

        footer.setFont(
            new Font("Segoe UI", Font.PLAIN, 11)
        );
        footer.setForeground(Color.GRAY);

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(background);
        rightContainer.setBorder(new EmptyBorder(30, 35, 20, 35));

        rightContainer.add(loginCard, BorderLayout.CENTER);
        rightContainer.add(footer, BorderLayout.SOUTH);

        rightPanel.add(rightContainer);

        // VERTICAL TWO-PANEL LAYOUT
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel);

        getRootPane().setDefaultButton(loginButton);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // LABEL DESIGN
    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
            new Font("Segoe UI", Font.BOLD, 15)
        );

        label.setForeground(textColor);

        return label;
    }

    // TEXT FIELD DESIGN
    private void styleField(JTextField field) {

        field.setFont(
            new Font("Segoe UI", Font.PLAIN, 16)
        );

        field.setPreferredSize(new Dimension(250, 48));

        field.setBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 216, 211), 1),
                new EmptyBorder(8, 12, 8, 12)
            )
        );

        field.setBackground(new Color(252, 253, 252));
    }

    // BUTTON DESIGN
    private JButton createButton(
        String text, Color bg, Color fg
    ) {

        JButton button = new JButton(text);

        button.setFont(
            new Font("Segoe UI", Font.BOLD, 15)
        );

        button.setBackground(bg);
        button.setForeground(fg);
        button.setPreferredSize(new Dimension(250, 50));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    // PASSWORD HASH
    private String hashPassword(String password)
            throws Exception {

        MessageDigest md =
            MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(
            password.getBytes(StandardCharsets.UTF_8)
        );

        StringBuilder hex = new StringBuilder();

        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }

    // LOGIN DATABASE CHECK
    private void login() {

        String username = usernameField.getText().trim();

        String password =
            new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter username and password.",
                "Missing Details",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String userSql = "SELECT USER_ID, ROLE FROM USERS WHERE UPPER(USERNAME)=UPPER(?) AND PASSWORD_HASH=?";
            try (PreparedStatement ps = con.prepareStatement(userSql)) {
                ps.setString(1, username);
                ps.setString(2, hashPassword(password));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        openDashboard(rs.getString("ROLE"), rs.getInt("USER_ID"));
                        return;
                    }
                }
            }

            String supplierSql = "SELECT SUPPLIER_ID FROM SUPPLIERS WHERE UPPER(USERNAME)=UPPER(?) AND PASSWORD_HASH=? AND STATUS='ACTIVE'";
            try (PreparedStatement ps = con.prepareStatement(supplierSql)) {
                ps.setString(1, username);
                ps.setString(2, hashPassword(password));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int supplierId = rs.getInt("SUPPLIER_ID");
                        new SupplierDashboard(supplierId).setVisible(true);
                        dispose();
                        return;
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "Invalid username or password, or supplier account is inactive.", "Login Failed", JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Database Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // ROLE-BASED DASHBOARD
    private void openDashboard(String role, int userId) {

        try {

            String dashboardName;

            switch (role.trim().toUpperCase()) {

                case "ADMIN":
                    dashboardName = "AdminDashboard";
                    break;

                case "EMPLOYEE":
                case "MANUFACTURING":
                case "MANUFACTURING OPERATOR":
                case "PRODUCTION_OPERATOR":
                case "PRODUCTION OPERATOR":
                case "STORE_MANAGER":
                case "STORE MANAGER":
                    dashboardName = "EmployeeDashboard";
                    break;

                case "CUSTOMER":
                    dashboardName = "CustomerDashboard";
                    break;

                case "QUALITY_INSPECTOR":
                case "QUALITY INSPECTOR":
                    dashboardName = "QualityInspectorDashboard";
                    break;

                case "BILLING_OFFICER":
                case "BILLING OFFICER":
                    dashboardName = "BillingDashboard";
                    break;

                case "DISPATCH_OFFICER":
                case "DISPATCH OFFICER":
                case "DISPATCH":
                    dashboardName = "DispatchDashboard";
                    break;

                case "SUPPLIER":
                    dashboardName = "SupplierDashboard";
                    break;

                default:
                    throw new Exception("Unknown user role: " + role);
            }

            Class<?> dashboardClass = Class.forName(
                "lucentglassmanagement." + dashboardName
            );

            JFrame dashboard;

            try {
                Constructor<?> constructor =
                    dashboardClass.getConstructor(int.class);

                dashboard = (JFrame) constructor.newInstance(userId);

            } catch (NoSuchMethodException ex) {

                dashboard = (JFrame)
                    dashboardClass.getConstructor().newInstance();
            }

            dashboard.setVisible(true);
            dispose();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to open dashboard.\n" + ex.getMessage(),
                "Dashboard Error",
                JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // OPEN REGISTRATION
    private void openRegistration() {

        try {

            RegistrationFrame registration =
                new RegistrationFrame();

            registration.setVisible(true);

            dispose();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to open Registration Page.\n"
                + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    
 // FORGOT PASSWORD
    private void forgotPassword() {

        ForgotPasswordFrame forgotFrame =
                new ForgotPasswordFrame();

        forgotFrame.setVisible(true);
    }

    // MAIN METHOD
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}


