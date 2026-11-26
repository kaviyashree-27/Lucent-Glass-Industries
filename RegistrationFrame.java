package lucentglassmanagement;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegistrationFrame extends JFrame {

    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField dobField;

    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private JComboBox<String> genderCombo;
    private JTextArea addressArea;

    private JButton createButton;
    private JButton clearButton;
    private JButton backButton;
    private JButton calendarButton;

    private LocalDate selectedDOB;

    private final Color DARK_GREEN = new Color(20, 39, 33);
    private final Color GOLD = new Color(190, 151, 70);
    private final Color CREAM = new Color(248, 247, 240);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(45, 54, 48);

    private final Font LABEL_FONT =
            new Font("Segoe UI", Font.BOLD, 14);

    private final Font FIELD_FONT =
            new Font("Segoe UI", Font.PLAIN, 15);

    public RegistrationFrame() {

        setTitle("Lucent Glass Industries - Customer Registration");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setMinimumSize(new Dimension(1000, 750));

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        getContentPane().setBackground(CREAM);

        createHeader();
        createRegistrationForm();
        createFooter();

        setLocationRelativeTo(null);
    }

    // ================= HEADER =================

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(DARK_GREEN);
        header.setPreferredSize(new Dimension(100, 115));

        header.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel brand = new JLabel(
                "LUCENT GLASS INDUSTRIES",
                SwingConstants.CENTER
        );

        brand.setFont(new Font("Georgia", Font.BOLD, 30));
        brand.setForeground(WHITE);

        JLabel subtitle = new JLabel(
                "CUSTOMER REGISTRATION",
                SwingConstants.CENTER
        );

        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subtitle.setForeground(GOLD);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));

        titlePanel.setOpaque(false);

        titlePanel.add(brand);
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
    }

    // ================= REGISTRATION FORM =================

    private void createRegistrationForm() {

        JPanel outerPanel = new JPanel(new GridBagLayout());

        outerPanel.setBackground(CREAM);

        outerPanel.setBorder(new EmptyBorder(25, 45, 20, 45));

        JPanel card = new JPanel(new BorderLayout(20, 20));

        card.setBackground(WHITE);

        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 222, 210), 1, true),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // FORM TITLE

        JLabel title = new JLabel(
                "Create Your Account",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Georgia", Font.BOLD, 27));
        title.setForeground(DARK_GREEN);

        JLabel description = new JLabel(
                "Enter your details to register with Lucent Glass Industries",
                SwingConstants.CENTER
        );

        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setForeground(Color.GRAY);

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 8));

        heading.setOpaque(false);

        heading.add(title);
        heading.add(description);

        card.add(heading, BorderLayout.NORTH);

        // FORM FIELDS

        JPanel fieldsPanel = new JPanel(new GridBagLayout());

        fieldsPanel.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(9, 12, 9, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        fullNameField = new JTextField();
        usernameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();

        dobField = new JTextField();
        dobField.setEditable(false);
        dobField.setBackground(new Color(250, 250, 247));

        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        genderCombo = new JComboBox<>(
                new String[]{"Select Gender", "Male", "Female", "Other"}
        );

        genderCombo.setFont(FIELD_FONT);
        genderCombo.setPreferredSize(new Dimension(200, 43));

        addressArea = new JTextArea(4, 20);

        addressArea.setFont(FIELD_FONT);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        JScrollPane addressScroll = new JScrollPane(addressArea);

        addressScroll.setPreferredSize(new Dimension(300, 95));

        // DATE PICKER BUTTON

        calendarButton = new JButton("▦");

        calendarButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        calendarButton.setBackground(DARK_GREEN);
        calendarButton.setForeground(GOLD);
        calendarButton.setFocusPainted(false);
        calendarButton.setPreferredSize(new Dimension(48, 43));

        calendarButton.addActionListener(e -> openCalendar());

        JPanel dobPanel = new JPanel(new BorderLayout(8, 0));

        dobPanel.setBackground(WHITE);

        dobPanel.add(dobField, BorderLayout.CENTER);
        dobPanel.add(calendarButton, BorderLayout.EAST);

        // ADD FIELDS WITH LABELS

        addField(fieldsPanel, gbc, 0, 0,
                "Full Name", fullNameField);

        addField(fieldsPanel, gbc, 1, 0,
                "Username", usernameField);

        addField(fieldsPanel, gbc, 0, 1,
                "Phone Number", phoneField);

        addField(fieldsPanel, gbc, 1, 1,
                "Email Address", emailField);

        addField(fieldsPanel, gbc, 0, 2,
                "Gender", genderCombo);

        addField(fieldsPanel, gbc, 1, 2,
                "Date of Birth", dobPanel);

        addField(fieldsPanel, gbc, 0, 3,
                "Password", passwordField);

        addField(fieldsPanel, gbc, 1, 3,
                "Confirm Password", confirmPasswordField);

        addField(fieldsPanel, gbc, 0, 4,
                "Address", addressScroll);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1;

        fieldsPanel.add(Box.createVerticalGlue(), gbc);

        card.add(fieldsPanel, BorderLayout.CENTER);

        // ACCOUNT TYPE

        JLabel accountType = new JLabel(
                "ACCOUNT TYPE : CUSTOMER",
                SwingConstants.CENTER
        );

        accountType.setFont(new Font("Segoe UI", Font.BOLD, 14));
        accountType.setForeground(DARK_GREEN);

        JPanel bottomPanel = new JPanel(new BorderLayout(15, 18));

        bottomPanel.setOpaque(false);

        bottomPanel.add(accountType, BorderLayout.NORTH);

        // BUTTONS

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));

        buttonPanel.setOpaque(false);

        backButton = createButton("BACK TO WELCOME PAGE", GOLD, DARK_GREEN);

        clearButton = createButton("CLEAR", new Color(235, 235, 228), TEXT);

        createButton = createButton("CREATE ACCOUNT", DARK_GREEN, WHITE);

        backButton.addActionListener(e -> goBack());

        clearButton.addActionListener(e -> clearFields());

        createButton.addActionListener(e -> registerCustomer());

        buttonPanel.add(backButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(createButton);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        card.add(bottomPanel, BorderLayout.SOUTH);

        GridBagConstraints outerGbc = new GridBagConstraints();

        outerGbc.fill = GridBagConstraints.BOTH;
        outerGbc.weightx = 1;
        outerGbc.weighty = 1;
        outerGbc.insets = new Insets(5, 5, 5, 5);

        outerPanel.add(card, outerGbc);

        JScrollPane scrollPane = new JScrollPane(outerPanel);

        scrollPane.setBorder(null);

        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ================= FIELD HELPER =================

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int column,
            int row,
            String labelText,
            JComponent field) {

        JPanel fieldPanel = new JPanel(new BorderLayout(0, 8));

        fieldPanel.setBackground(WHITE);

        JLabel label = new JLabel(labelText);

        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);

        field.setFont(FIELD_FONT);

        if (!(field instanceof JScrollPane)) {

            field.setPreferredSize(new Dimension(250, 43));

            field.setBorder(new CompoundBorder(
                    new LineBorder(new Color(210, 210, 200), 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));
        }

        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);

        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;

        panel.add(fieldPanel, gbc);
    }

    // ================= BUTTON DESIGN =================

    private JButton createButton(
            String text,
            Color background,
            Color foreground) {

        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.BOLD, 13));

        button.setBackground(background);
        button.setForeground(foreground);

        button.setFocusPainted(false);

        button.setBorder(new EmptyBorder(15, 12, 15, 12));

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // ================= FOOTER =================

    private void createFooter() {

        JLabel footer = new JLabel(
                "© 2026 LUCENT GLASS INDUSTRIES",
                SwingConstants.CENTER
        );

        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        footer.setForeground(Color.GRAY);

        footer.setBorder(new EmptyBorder(10, 5, 10, 5));

        footer.setBackground(CREAM);

        footer.setOpaque(true);

        add(footer, BorderLayout.SOUTH);
    }
 // ================= DATE OF BIRTH CALENDAR =================

    private void openCalendar() {

        JDialog dialog = new JDialog(
                this,
                "Select Date of Birth",
                true
        );

        dialog.setSize(430, 460);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);

        dialog.setLayout(new BorderLayout());

        Calendar calendar = Calendar.getInstance();

        if (selectedDOB != null) {
            calendar.set(
                    selectedDOB.getYear(),
                    selectedDOB.getMonthValue() - 1,
                    selectedDOB.getDayOfMonth()
            );
        } else {
            calendar.set(2000, Calendar.JANUARY, 1);
        }

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(DARK_GREEN);
        header.setBorder(new EmptyBorder(15, 15, 15, 15));

        JButton previous = new JButton("❮");
        JButton next = new JButton("❯");

        styleCalendarArrow(previous);
        styleCalendarArrow(next);

        JLabel monthYear = new JLabel("", SwingConstants.CENTER);

        monthYear.setFont(new Font("Georgia", Font.BOLD, 22));
        monthYear.setForeground(WHITE);

        header.add(previous, BorderLayout.WEST);
        header.add(monthYear, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);

        dialog.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(5, 12));

        center.setBackground(CREAM);
        center.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel weekdays = new JPanel(new GridLayout(1, 7, 4, 4));

        weekdays.setBackground(CREAM);

        String[] days = {
                "SUN", "MON", "TUE", "WED",
                "THU", "FRI", "SAT"
        };

        for (String day : days) {

            JLabel label = new JLabel(day, SwingConstants.CENTER);

            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setForeground(DARK_GREEN);

            weekdays.add(label);
        }

        center.add(weekdays, BorderLayout.NORTH);

        JPanel dateGrid = new JPanel(new GridLayout(6, 7, 5, 5));

        dateGrid.setBackground(CREAM);

        center.add(dateGrid, BorderLayout.CENTER);

        dialog.add(center, BorderLayout.CENTER);

        Runnable[] refresh = new Runnable[1];

        refresh[0] = () -> {

            dateGrid.removeAll();

            int year = calendar.get(Calendar.YEAR);

            int month = calendar.get(Calendar.MONTH);

            monthYear.setText(
                    calendar.getDisplayName(
                            Calendar.MONTH,
                            Calendar.LONG,
                            Locale.ENGLISH
                    ) + " " + year
            );

            Calendar firstDay = new GregorianCalendar(year, month, 1);

            int startDay = firstDay.get(Calendar.DAY_OF_WEEK) - 1;

            int totalDays = firstDay.getActualMaximum(
                    Calendar.DAY_OF_MONTH
            );

            LocalDate today = LocalDate.now();

            for (int i = 0; i < 42; i++) {

                int dayNumber = i - startDay + 1;

                if (dayNumber < 1 || dayNumber > totalDays) {

                    dateGrid.add(new JLabel(""));

                } else {

                    final int selectedDay = dayNumber;

                    JButton dateButton = new JButton(
                            String.valueOf(dayNumber)
                    );

                    dateButton.setFont(
                            new Font("Segoe UI", Font.PLAIN, 14)
                    );

                    dateButton.setFocusPainted(false);

                    dateButton.setBackground(WHITE);

                    dateButton.setForeground(TEXT);

                    dateButton.setBorder(
                            new LineBorder(
                                    new Color(225, 222, 210),
                                    1,
                                    true
                            )
                    );

                    LocalDate currentDate = LocalDate.of(
                            year,
                            month + 1,
                            dayNumber
                    );

                    if (currentDate.isAfter(today)) {

                        dateButton.setEnabled(false);

                        dateButton.setForeground(
                                new Color(190, 190, 190)
                        );

                    }

                    if (selectedDOB != null &&
                            selectedDOB.equals(currentDate)) {

                        dateButton.setBackground(GOLD);
                        dateButton.setForeground(WHITE);

                    }

                    dateButton.addActionListener(e -> {

                        selectedDOB = LocalDate.of(
                                year,
                                month + 1,
                                selectedDay
                        );

                        dobField.setText(
                                selectedDOB.format(
                                        DateTimeFormatter.ofPattern(
                                                "dd-MM-yyyy"
                                        )
                                )
                        );

                        dialog.dispose();
                    });

                    dateGrid.add(dateButton);
                }
            }

            dateGrid.revalidate();
            dateGrid.repaint();
        };

        previous.addActionListener(e -> {

            calendar.add(Calendar.MONTH, -1);

            refresh[0].run();
        });

        next.addActionListener(e -> {

            Calendar maximum = Calendar.getInstance();

            if (calendar.get(Calendar.YEAR) < maximum.get(Calendar.YEAR)
                    || (calendar.get(Calendar.YEAR) == maximum.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH) < maximum.get(Calendar.MONTH))) {

                calendar.add(Calendar.MONTH, 1);

                refresh[0].run();
            }
        });

        refresh[0].run();

        dialog.setVisible(true);
    }

    // ================= CALENDAR ARROW DESIGN =================

    private void styleCalendarArrow(JButton button) {

        button.setFont(new Font("Segoe UI", Font.BOLD, 17));

        button.setForeground(GOLD);

        button.setBackground(DARK_GREEN);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
 // ================= REGISTER CUSTOMER =================

    private void registerCustomer() {

        String name = fullNameField.getText().trim();

        String username = usernameField.getText().trim();

        String phone = phoneField.getText().trim();

        String email = emailField.getText().trim();

        String gender = (String) genderCombo.getSelectedItem();

        String address = addressArea.getText().trim();

        String password = new String(passwordField.getPassword());

        String confirmPassword =
                new String(confirmPasswordField.getPassword());

        // VALIDATION

        if (name.isEmpty() || username.isEmpty()
                || phone.isEmpty() || email.isEmpty()
                || address.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all the fields.",
                    "Missing Details",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (genderCombo.getSelectedIndex() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select your gender."
            );

            return;
        }

        if (selectedDOB == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select your date of birth."
            );

            return;
        }

        if (selectedDOB.isAfter(LocalDate.now())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Date of birth cannot be in the future."
            );

            return;
        }

        if (!phone.matches("[0-9]{10,15}")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid phone number."
            );

            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid email address."
            );

            return;
        }

        if (password.length() < 8) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password must contain at least 8 characters."
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match."
            );

            return;
        }

        // PASSWORD HASHING

        String passwordHash;

        try {

            passwordHash = hashPassword(password);

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password encryption failed."
            );

            return;
        }

        // DATABASE INSERT

        String sql =
                "INSERT INTO USERS " +
                "(USER_ID, FULL_NAME, USERNAME, PASSWORD_HASH, " +
                "ROLE, PHONE, EMAIL, GENDER, ADDRESS, DOB) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Database connection failed."
                );

                return;
            }

            // Generate next user ID

            int nextUserId = 1;

            String idQuery =
                    "SELECT NVL(MAX(USER_ID), 0) + 1 FROM USERS";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(idQuery)) {

                if (rs.next()) {
                    nextUserId = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, nextUserId);

                ps.setString(2, name);

                ps.setString(3, username);

                ps.setString(4, passwordHash);

                ps.setString(5, "CUSTOMER");

                ps.setString(6, phone);

                ps.setString(7, email);

                ps.setString(8, gender);

                ps.setString(9, address);

                ps.setDate(
                        10,
                        Date.valueOf(selectedDOB)
                );

                int result = ps.executeUpdate();

                if (result > 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Your account has been created successfully!",
                            "Registration Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    dispose();

                    new LoginFrame().setVisible(true);
                }
            }

        } catch (SQLException ex) {

            if (ex.getErrorCode() == 1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Username, email or phone number already exists."
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Registration failed: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );

                ex.printStackTrace();
            }
        }
    }

    // ================= SHA-256 PASSWORD HASH =================

    private String hashPassword(String password) throws Exception {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

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

    // ================= CLEAR FIELDS =================

    private void clearFields() {

        fullNameField.setText("");

        usernameField.setText("");

        phoneField.setText("");

        emailField.setText("");

        dobField.setText("");

        passwordField.setText("");

        confirmPasswordField.setText("");

        addressArea.setText("");

        genderCombo.setSelectedIndex(0);

        selectedDOB = null;
    }

    // ================= BACK TO WELCOME PAGE =================

    private void goBack() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to go back to the Welcome Page?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            dispose();

            new WelcomePage().setVisible(true);
        }
    }
}