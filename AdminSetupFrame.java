package lucentglassmanagement;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class AdminSetupFrame extends JFrame {

    private JTextField nameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;

    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private JCheckBox showPassword;

    private final Color darkGreen = new Color(16, 45, 41);
    private final Color gold = new Color(195, 151, 65);
    private final Color cream = new Color(247, 248, 243);

    public AdminSetupFrame() {

        setTitle("Lucent Glass Industries - Admin Setup");
        setSize(750, 720);
        setMinimumSize(new Dimension(650, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(cream);

        // HEADER
        JPanel header = new JPanel();
        header.setBackground(darkGreen);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(22, 10, 22, 10));

        JLabel company = new JLabel("LUCENT");
        company.setFont(new Font("Georgia", Font.BOLD, 35));
        company.setForeground(Color.WHITE);
        company.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel industry = new JLabel("GLASS INDUSTRIES");
        industry.setFont(new Font("Segoe UI", Font.BOLD, 18));
        industry.setForeground(gold);
        industry.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("ADMINISTRATOR SETUP");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 12));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(company);
        header.add(Box.createVerticalStrut(4));
        header.add(industry);
        header.add(Box.createVerticalStrut(12));
        header.add(heading);

        main.add(header, BorderLayout.NORTH);

        // FORM PANEL
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 45, 20, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Admin Account");
        title.setFont(new Font("Georgia", Font.BOLD, 27));
        title.setForeground(darkGreen);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel(
            "Create the primary administrator account"
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 15, 10);
        form.add(subtitle, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 10, 8, 10);

        // FULL NAME
        addLabel(form, gbc, "Full Name", 2);
        nameField = new JTextField();
        addField(form, gbc, nameField, 2);

        // USERNAME
        addLabel(form, gbc, "Username", 3);
        usernameField = new JTextField();
        addField(form, gbc, usernameField, 3);

        // EMAIL
        addLabel(form, gbc, "Email Address", 4);
        emailField = new JTextField();
        addField(form, gbc, emailField, 4);

        // PHONE
        addLabel(form, gbc, "Phone Number", 5);
        phoneField = new JTextField();
        addField(form, gbc, phoneField, 5);

        // PASSWORD
        addLabel(form, gbc, "Password", 6);
        passwordField = new JPasswordField();
        addField(form, gbc, passwordField, 6);

        // CONFIRM PASSWORD
        addLabel(form, gbc, "Confirm Password", 7);
        confirmPasswordField = new JPasswordField();
        addField(form, gbc, confirmPasswordField, 7);

        // SHOW PASSWORD
        showPassword = new JCheckBox("Show Password");
        showPassword.setBackground(Color.WHITE);
        showPassword.setFont(
            new Font("Segoe UI", Font.PLAIN, 12)
        );

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
                confirmPasswordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('\u2022');
                confirmPasswordField.setEchoChar('\u2022');
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 8;
        form.add(showPassword, gbc);

        // CREATE BUTTON
        JButton createButton = new JButton("CREATE ADMIN ACCOUNT");
        styleButton(createButton, darkGreen, Color.WHITE);

        createButton.addActionListener(e -> createAdmin());

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 10, 8, 10);
        form.add(createButton, gbc);

        // FOOTER
        JLabel footer = new JLabel(
            "Authorized Administrator Setup"
        );
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(Color.GRAY);

        gbc.gridy = 10;
        gbc.insets = new Insets(5, 10, 5, 10);
        form.add(footer, gbc);

        main.add(form, BorderLayout.CENTER);

        add(main);
        setVisible(true);
    }

    // LABEL
    private void addLabel(
        JPanel panel, GridBagConstraints gbc,
        String text, int row
    ) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(40, 55, 49));

        gbc.gridx = 0;
        gbc.gridy = row;

        panel.add(label, gbc);
    }

    // TEXT FIELD
    private void addField(
        JPanel panel, GridBagConstraints gbc,
        JTextField field, int row
    ) {

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 38));

        field.setBorder(
            new CompoundBorder(
                new LineBorder(new Color(210, 215, 208)),
                new EmptyBorder(5, 10, 5, 10)
            )
        );

        gbc.gridx = 1;
        gbc.gridy = row;

        panel.add(field, gbc);
    }

    // BUTTON STYLE
    private void styleButton(
        JButton button, Color bg, Color fg
    ) {

        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(
            new Font("Segoe UI", Font.BOLD, 14)
        );
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(300, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // SHA-256 PASSWORD HASH
    private String hashPassword(String password)
            throws Exception {

        MessageDigest md =
            MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(
            password.getBytes(StandardCharsets.UTF_8)
        );

        StringBuilder result = new StringBuilder();

        for (byte b : hash) {
            result.append(
                String.format("%02x", b & 0xff)
            );
        }

        return result.toString();
    }

    // CREATE ADMIN
    private void createAdmin() {

        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        String password =
            new String(passwordField.getPassword());

        String confirmPassword =
            new String(confirmPasswordField.getPassword());

        // VALIDATION
        if (name.isEmpty() || username.isEmpty()
                || email.isEmpty() || phone.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                this, "Please fill all the fields."
            );
            return;
        }

        if (!email.matches(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {

            JOptionPane.showMessageDialog(
                this, "Enter a valid email address."
            );
            return;
        }

        if (!phone.matches("[0-9]{10,15}")) {

            JOptionPane.showMessageDialog(
                this, "Enter a valid phone number."
            );
            return;
        }

        if (password.length() < 8) {

            JOptionPane.showMessageDialog(
                this, "Password must contain at least 8 characters."
            );
            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                this, "Passwords do not match."
            );
            return;
        }

        String sql =
            "INSERT INTO USERS " +
            "(USER_ID, FULL_NAME, USERNAME, PASSWORD_HASH, " +
            "ROLE, PHONE, EMAIL) " +
            "VALUES (?, ?, ?, ?, 'ADMIN', ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Database connection failed.");
            }

            con.setAutoCommit(false);

            try {

                // Prevent simultaneous first-time setup
                try (Statement lock = con.createStatement()) {
                    lock.execute("LOCK TABLE USERS IN EXCLUSIVE MODE");
                }

                // Check whether admin already exists
                String checkSql =
                    "SELECT COUNT(*) FROM USERS " +
                    "WHERE UPPER(TRIM(ROLE)) = 'ADMIN'";

                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(checkSql)) {

                    rs.next();

                    if (rs.getInt(1) > 0) {

                        con.rollback();

                        JOptionPane.showMessageDialog(
                            this,
                            "Admin account already exists!\n" +
                            "A second initial admin cannot be created.",
                            "Setup Already Completed",
                            JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }
                }

                // Generate User ID
                int nextId = 1;

                String idSql =
                    "SELECT NVL(MAX(USER_ID), 0) + 1 FROM USERS";

                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(idSql)) {

                    if (rs.next()) {
                        nextId = rs.getInt(1);
                    }
                }

                String passwordHash = hashPassword(password);

                try (PreparedStatement ps =
                    con.prepareStatement(sql)) {

                    ps.setInt(1, nextId);
                    ps.setString(2, name);
                    ps.setString(3, username);
                    ps.setString(4, passwordHash);
                    ps.setString(5, phone);
                    ps.setString(6, email);

                    ps.executeUpdate();
                }

                con.commit();

                JOptionPane.showMessageDialog(
                    this,
                    "Admin account created successfully!\n" +
                    "You can now log in as Administrator.",
                    "Setup Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );

                dispose();
                new LoginFrame().setVisible(true);

            } catch (Exception ex) {

                con.rollback();
                throw ex;

            } finally {

                con.setAutoCommit(true);
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Admin Setup Failed:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // MAIN
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new AdminSetupFrame();
        });
    }
}