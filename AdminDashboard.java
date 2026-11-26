package lucentglassmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

public class AdminDashboard extends JFrame {
    private final Color GREEN = new Color(25, 75, 55);
    private final Color DARK_GREEN = new Color(15, 50, 38);
    private final Color CREAM = new Color(248, 246, 237);
    private final Color GOLD = new Color(190, 151, 75);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(45, 55, 48);

    private JPanel contentPanel;
    private JTable table;
    private DefaultTableModel model;
    private JLabel pageTitle;

    public AdminDashboard() {

        setTitle("LUCENT GLASS INDUSTRIES - Admin Dashboard");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setMinimumSize(new Dimension(1100, 650));

        getContentPane().setBackground(CREAM);

        setLayout(new BorderLayout());

        createSidebar();

        createMainArea();

        showDashboard();

        // Keep the admin order queue in sync when customer or production screens update an order.
        new Timer(10000, e -> {
            if (pageTitle != null && "Order Details".equals(pageTitle.getText())) showOrders();
        }).start();

        setLocationRelativeTo(null);
    }

    // ================= SIDEBAR =================

    private void createSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout());

        sidebar.setPreferredSize(new Dimension(245, 0));

        sidebar.setBackground(DARK_GREEN);

        JLabel brand = new JLabel(
                "<html><center>LUCENT<br>GLASS INDUSTRIES</center></html>",
                SwingConstants.CENTER);

        brand.setFont(new Font("Georgia", Font.BOLD, 23));

        brand.setForeground(new Color(230, 205, 150));

        brand.setBorder(new EmptyBorder(25, 10, 25, 10));

        sidebar.add(brand, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 0, 5));

        menuPanel.setBackground(DARK_GREEN);

        menuPanel.setBorder(new EmptyBorder(10, 12, 15, 12));

        addMenu(menuPanel, "Dashboard", this::showDashboard);

        addMenu(menuPanel, "All Users",
                () -> showUsers("ALL"));

        addMenu(menuPanel, "Employee Details",
                () -> showUsers("EMPLOYEE"));

        addMenu(menuPanel, "Customer Details",
                () -> showUsers("CUSTOMER"));

        addMenu(menuPanel, "Product Details",
                this::showProducts);

        addMenu(menuPanel, "Order Details",
                this::showOrders);

        addMenu(menuPanel, "Quality Approval",
                this::showQualityApprovals);

        addMenu(menuPanel, "Bill Approval",
                this::showBillApprovals);

        addMenu(menuPanel, "Notifications",
                this::showNotifications);

        addMenu(menuPanel, "Raw Material Details",
                this::showRawMaterials);

        addMenu(menuPanel, "Supplier Details",
                this::showSuppliers);

        addMenu(menuPanel, "Add Supplier",
                this::addSupplier);

        addMenu(menuPanel, "Manage Employee",
                () -> showManageUsers("EMPLOYEE"));

        addMenu(menuPanel, "Manage Customer",
                () -> showManageUsers("CUSTOMER"));

        addMenu(menuPanel, "Add New Employee",
                this::addEmployee);

        addMenu(menuPanel, "Logout",
                this::logout);

        sidebar.add(menuPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
    }

    private void addMenu(JPanel panel, String text, Runnable action) {

        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.BOLD, 13));

        button.setForeground(WHITE);

        button.setBackground(DARK_GREEN);

        button.setFocusPainted(false);

        button.setBorder(new EmptyBorder(12, 12, 12, 8));

        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
                button.setBackground(GREEN);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(DARK_GREEN);
            }
        });

        button.addActionListener(e -> action.run());

        panel.add(button);
    }

    // ================= MAIN AREA =================

    private void createMainArea() {

        JPanel main = new JPanel(new BorderLayout());

        main.setBackground(CREAM);

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(WHITE);

        header.setBorder(new EmptyBorder(18, 28, 18, 28));

        pageTitle = new JLabel("Dashboard");

        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 25));

        pageTitle.setForeground(GREEN);

        JLabel adminLabel = new JLabel("ADMINISTRATOR");

        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        adminLabel.setForeground(GOLD);

        header.add(pageTitle, BorderLayout.WEST);

        header.add(adminLabel, BorderLayout.EAST);

        contentPanel = new JPanel(new BorderLayout(15, 15));

        contentPanel.setBackground(CREAM);

        contentPanel.setBorder(
                new EmptyBorder(25, 28, 25, 28));

        main.add(header, BorderLayout.NORTH);

        main.add(contentPanel, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);
    }

    private void setPage(String title) {

        pageTitle.setText(title);

        contentPanel.removeAll();

        contentPanel.revalidate();

        contentPanel.repaint();
    }

    // ================= DASHBOARD =================

    private void showDashboard() {

        setPage("Dashboard Overview");

        JPanel cards = new JPanel(
                new GridLayout(2, 3, 20, 20));

        cards.setBackground(CREAM);

        cards.add(createCountCard(
                "TOTAL USERS",
                "SELECT COUNT(*) FROM USERS"));

        cards.add(createCountCard(
                "EMPLOYEES",
                "SELECT COUNT(*) FROM USERS WHERE UPPER(ROLE) IN "
              + "('EMPLOYEE','MANUFACTURING','PRODUCTION_OPERATOR','QUALITY_INSPECTOR','DISPATCH','DISPATCH_OFFICER')"));

        cards.add(createCountCard(
                "CUSTOMERS",
                "SELECT COUNT(*) FROM USERS WHERE UPPER(ROLE)='CUSTOMER'"));

        cards.add(createCountCard(
                "PRODUCTS",
                "SELECT COUNT(*) FROM PRODUCTS"));

        cards.add(createCountCard(
                "TOTAL ORDERS",
                "SELECT COUNT(*) FROM ORDERS"));

        cards.add(createCountCard(
                "RAW MATERIALS",
                "SELECT COUNT(*) FROM RAW_MATERIALS"));

        contentPanel.add(cards, BorderLayout.NORTH);

        JLabel welcome = new JLabel(
                "<html><div style='text-align:center;'>"
                + "<h1>Welcome to Lucent Glass Industries</h1>"
                + "<p>Manage your factory operations from one place.</p>"
                + "</div></html>",
                SwingConstants.CENTER);

        welcome.setForeground(GREEN);

        welcome.setFont(
                new Font("Segoe UI", Font.PLAIN, 20));

        contentPanel.add(welcome, BorderLayout.CENTER);

        contentPanel.revalidate();

        contentPanel.repaint();
    }

    private JPanel createCountCard(String heading, String sql) {

        JPanel card = new JPanel(new BorderLayout(5, 15));

        card.setBackground(WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 220, 205)),
                        new EmptyBorder(25, 22, 25, 22)));

        JLabel title = new JLabel(heading);

        title.setFont(
                new Font("Segoe UI", Font.BOLD, 14));

        title.setForeground(new Color(100, 105, 95));

        JLabel count = new JLabel(getCount(sql));

        count.setFont(
                new Font("Segoe UI", Font.BOLD, 35));

        count.setForeground(GREEN);

        card.add(title, BorderLayout.NORTH);

        card.add(count, BorderLayout.CENTER);

        return card;
    }

    private String getCount(String sql) {

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getString(1);
            }

        } catch (Exception e) {

            System.out.println(
                    "Count error: " + e.getMessage());
        }

        return "N/A";
    }
 // ================= USER DETAILS =================

    private void showUsers(String role) {

        setPage(role.equals("ALL") ? "All Users" :
                role.equals("EMPLOYEE") ? "Employee Details" :
                "Customer Details");

        createTable(new String[]{
                "User ID",
                "Full Name",
                "Username",
                "Role",
                "Phone",
                "Email",
                "Address"
        });

        String sql = "SELECT USER_ID, FULL_NAME, USERNAME, ROLE, PHONE, EMAIL, ADDRESS "
                + "FROM USERS";
        if (role.equals("EMPLOYEE")) {
            sql += " WHERE UPPER(TRIM(ROLE)) NOT IN ('ADMIN','CUSTOMER')";
        } else if (!role.equals("ALL")) {
            sql += " WHERE UPPER(TRIM(ROLE))=?";
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (!role.equals("ALL") && !role.equals("EMPLOYEE")) {
                ps.setString(1, role);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    model.addRow(new Object[]{
                            rs.getInt("USER_ID"),
                            rs.getString("FULL_NAME"),
                            rs.getString("USERNAME"),
                            rs.getString("ROLE"),
                            rs.getString("PHONE"),
                            rs.getString("EMAIL"),
                            rs.getString("ADDRESS")
                    });
                }
            }

        } catch (Exception e) {

            showError("Unable to load user details.\n"
                    + e.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ================= TABLE DESIGN =================

    private void createTable(String[] columns) {

        model = new DefaultTableModel(columns, 0) {

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(32);

        table.setFont(
                new Font("Segoe UI", Font.PLAIN, 13));

        table.setForeground(TEXT);

        table.setSelectionBackground(
                new Color(220, 235, 220));

        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();

        header.setBackground(GREEN);

        header.setForeground(WHITE);

        header.setFont(
                new Font("Segoe UI", Font.BOLD, 13));

        header.setPreferredSize(
                new Dimension(0, 38));

        table.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scroll = new JScrollPane(table);

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 215, 200)));

        contentPanel.add(scroll, BorderLayout.CENTER);
    }

    private JPanel createButtonPanel() {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 10));

        panel.setBackground(CREAM);

        return panel;
    }

    private JButton createActionButton(String text) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 13));

        button.setBackground(GREEN);

        button.setForeground(WHITE);

        button.setFocusPainted(false);

        button.setBorder(
                new EmptyBorder(10, 18, 10, 18));

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // ================= MANAGE EMPLOYEE / CUSTOMER =================

    private void showManageUsers(String role) {

        setPage(role.equals("EMPLOYEE")
                ? "Manage Employee"
                : "Manage Customer");

        createTable(new String[]{
                "User ID",
                "Full Name",
                "Username",
                "Role",
                "Phone",
                "Email",
                "Address"
        });

        loadManageTable(role);

        JPanel buttons = createButtonPanel();

        JButton update = createActionButton(
                role.equals("EMPLOYEE")
                        ? "Update Employee"
                        : "Update Customer");

        JButton delete = createActionButton(
                role.equals("EMPLOYEE")
                        ? "Delete Employee"
                        : "Delete Customer");

        update.addActionListener(
                e -> updateSelected(role));

        delete.addActionListener(
                e -> deleteSelected(role));

        buttons.add(update);
        buttons.add(delete);

        contentPanel.add(buttons, BorderLayout.SOUTH);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void loadManageTable(String role) {

        String sql = "SELECT USER_ID, FULL_NAME, USERNAME, ROLE, PHONE, EMAIL, ADDRESS "
                + "FROM USERS WHERE "
                + (role.equals("EMPLOYEE")
                    ? "UPPER(TRIM(ROLE)) NOT IN ('ADMIN','CUSTOMER')"
                    : "UPPER(TRIM(ROLE))='CUSTOMER'");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    model.addRow(new Object[]{
                            rs.getInt("USER_ID"),
                            rs.getString("FULL_NAME"),
                            rs.getString("USERNAME"),
                            rs.getString("ROLE"),
                            rs.getString("PHONE"),
                            rs.getString("EMAIL"),
                            rs.getString("ADDRESS")
                    });
                }
            }

        } catch (Exception e) {

            showError("Unable to load records.\n"
                    + e.getMessage());
        }
    }

    // ================= UPDATE EMPLOYEE / CUSTOMER =================

    private void updateSelected(String role) {

        int row = table.getSelectedRow();

        if (row == -1) {

            showMessage("Please select a record first.");

            return;
        }

        int userId = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        JTextField name = new JTextField(
                valueAt(row, 1));

        JTextField username = new JTextField(
                valueAt(row, 2));

        JTextField phone = new JTextField(
                valueAt(row, 4));

        JTextField email = new JTextField(
                valueAt(row, 5));

        JTextArea address = new JTextArea(
                valueAt(row, 6), 3, 20);

        address.setLineWrap(true);
        address.setWrapStyleWord(true);

        JPanel form = new JPanel(
                new GridLayout(0, 2, 12, 12));

        form.setBorder(
                new EmptyBorder(20, 20, 20, 20));

        form.setBackground(CREAM);

        addFormRow(form, "Full Name", name);

        addFormRow(form, "Username", username);

        addFormRow(form, "Phone", phone);

        addFormRow(form, "Email", email);

        addFormRow(form, "Address",
                new JScrollPane(address));

        int result = showFormDialog(
                role.equals("EMPLOYEE")
                        ? "Update Employee"
                        : "Update Customer",
                form,
                "Save Changes");

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String fullName = name.getText().trim();

        String userName = username.getText().trim();

        if (fullName.isEmpty() || userName.isEmpty()) {

            showMessage("Name and Username are required.");

            return;
        }

        String sql = "UPDATE USERS SET FULL_NAME=?, USERNAME=?, PHONE=?, EMAIL=?, ADDRESS=? "
                + "WHERE USER_ID=? AND "
                + (role.equals("EMPLOYEE")
                    ? "UPPER(TRIM(ROLE)) NOT IN ('ADMIN','CUSTOMER')"
                    : "UPPER(TRIM(ROLE))='CUSTOMER'");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullName);

            ps.setString(2, userName);

            ps.setString(3, phone.getText().trim());

            ps.setString(4, email.getText().trim());

            ps.setString(5, address.getText().trim());

            ps.setInt(6, userId);

            int updated = ps.executeUpdate();

            if (updated > 0) {

                showMessage("Record updated successfully.");

            } else {

                showMessage("No matching record found.");
            }

            showManageUsers(role);

        } catch (Exception e) {

            showError("Update failed.\n"
                    + e.getMessage());
        }
    }

    private String valueAt(int row, int column) {

        Object value = model.getValueAt(row, column);

        return value == null ? "" : value.toString();
    }

    // ================= DELETE EMPLOYEE / CUSTOMER =================

    private void deleteSelected(String role) {

        int row = table.getSelectedRow();

        if (row == -1) {

            showMessage("Please select a record first.");

            return;
        }

        int userId = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        String name = model.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete "
                        + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM USERS WHERE USER_ID=? AND "
                + (role.equals("EMPLOYEE")
                    ? "UPPER(TRIM(ROLE)) NOT IN ('ADMIN','CUSTOMER')"
                    : "UPPER(TRIM(ROLE))='CUSTOMER'");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            int deleted = ps.executeUpdate();

            if (deleted > 0) {

                showMessage("Record deleted successfully.");

            } else {

                showMessage("Record not found.");
            }

            showManageUsers(role);

        } catch (Exception e) {

            showError(
                    "Unable to delete this user. "
                    + "The user may be linked to existing records.\n"
                    + e.getMessage());
        }
    }
 // ================= ADD NEW EMPLOYEE =================

    private void addEmployee() {

        setPage("Add New Employee");

        JTextField name = new JTextField();
        JTextField username = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();
        JTextField address = new JTextField();
        JComboBox<String> employeeRole = new JComboBox<>(new String[]{
                "MANUFACTURING", "QUALITY_INSPECTOR", "BILLING_OFFICER", "DISPATCH"
        });

        JPasswordField password = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();

        JPanel form = new JPanel(
                new GridLayout(0, 2, 12, 14));

        form.setBackground(CREAM);

        form.setBorder(
                new EmptyBorder(25, 30, 25, 30));

        addFormRow(form, "Full Name", name);
        addFormRow(form, "Employee Role", employeeRole);
        addFormRow(form, "Username", username);
        addFormRow(form, "Phone", phone);
        addFormRow(form, "Email", email);
        addFormRow(form, "Address", address);
        addFormRow(form, "Password", password);
        addFormRow(form, "Confirm Password", confirmPassword);

        int result = showFormDialog(
                "Add New Employee",
                form,
                "Save Employee");

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String fullName = name.getText().trim();
        String role = employeeRole.getSelectedItem().toString();
        String userName = username.getText().trim();
        String phoneNo = phone.getText().trim();
        String emailId = email.getText().trim();
        String addressText = address.getText().trim();

        String pass = new String(password.getPassword());
        String confirmPass = new String(confirmPassword.getPassword());

        if (fullName.isEmpty()
                || userName.isEmpty()
                || phoneNo.isEmpty()
                || emailId.isEmpty()
                || pass.isEmpty()
                || confirmPass.isEmpty()) {

            showMessage("Please fill all required fields.");
            return;
        }

        if (!pass.equals(confirmPass)) {

            showMessage("Passwords do not match.");
            return;
        }

        String sql = "INSERT INTO USERS "
                + "(USER_ID, FULL_NAME, USERNAME, PASSWORD_HASH, ROLE, "
                + "PHONE, EMAIL, ADDRESS) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int newId = 1;

                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery(
                             "SELECT NVL(MAX(USER_ID),0)+1 FROM USERS")) {

                    if (rs.next()) {
                        newId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, newId);
                    ps.setString(2, fullName);
                    ps.setString(3, userName);
                    ps.setString(4, hashPassword(pass));
                    ps.setString(5, role);
                    ps.setString(6, phoneNo);
                    ps.setString(7, emailId);
                    ps.setString(8, addressText);

                    ps.executeUpdate();
                }

                con.commit();

                showMessage(
                        "Employee added successfully.\nRole: " + role + "\nEmployee ID: " + newId);

                showUsers("EMPLOYEE");

            } catch (Exception e) {

                con.rollback();
                throw e;

            } finally {

                con.setAutoCommit(true);
            }

        } catch (Exception e) {

            showError("Unable to add employee.\n" + e.getMessage());
        }
    }

    // ================= PRODUCT DETAILS =================
    // Admin can view product information only. Product changes are handled
    // by the Store Manager, so this page intentionally has no edit buttons.

    private void showProducts() {

        setPage("Product Details");

        createTable(new String[]{
                "Product ID",
                "Product Name",
                "Category",
                "Price",
                "Stock Quantity",
                "Active"
        });

        String sql = "SELECT PRODUCT_ID, PRODUCT_NAME, CATEGORY, "
                + "PRICE, STOCK_QTY, ACTIVE_FLAG "
                + "FROM PRODUCTS ORDER BY PRODUCT_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("PRODUCT_ID"),
                        rs.getString("PRODUCT_NAME"),
                        rs.getString("CATEGORY"),
                        rs.getBigDecimal("PRICE"),
                        rs.getInt("STOCK_QTY"),
                        rs.getString("ACTIVE_FLAG")
                });
            }

        } catch (Exception e) {

            showError("Unable to load products.\n" + e.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ================= ORDER DETAILS =================
    // Each ORDER_ITEMS row is displayed separately. This supports both
    // standard products and custom-size items with ITEM_DESCRIPTION.

    private void showOrders() {

        setPage("Order Details");

        createTable(new String[]{
                "Order ID",
                "Customer ID",
                "Order Date",
                "Order Type",
                "Product",
                "Length (Inches)",
                "Width (Inches)",
                "Quantity",
                "Unit Price",
                "Line Total",
                "Order Status",
                "Total Amount",
                "Delivery Status",
                "Payment Method",
                "Assigned Employee"
        });

        String sql =
                "SELECT O.ORDER_ID, O.CUSTOMER_ID, O.ORDER_DATE, "
              + "O.ORDER_TYPE, "
              + "NVL(P.PRODUCT_NAME, I.ITEM_DESCRIPTION) AS PRODUCT, "
              + "I.LENGTH_INCHES, I.WIDTH_INCHES, "
              + "I.QUANTITY, I.UNIT_PRICE, I.LINE_TOTAL, "
              + "O.ORDER_STATUS, O.TOTAL_AMOUNT, "
              + "O.DELIVERY_STATUS, O.PAYMENT_METHOD, U.FULL_NAME AS EMPLOYEE_NAME "
              + "FROM ORDERS O "
              + "LEFT JOIN ORDER_ITEMS I ON O.ORDER_ID = I.ORDER_ID "
              + "LEFT JOIN USERS U ON U.USER_ID=O.ASSIGNED_EMPLOYEE_ID "
              + "LEFT JOIN PRODUCTS P ON I.PRODUCT_ID = P.PRODUCT_ID "
              + "ORDER BY O.ORDER_DATE DESC, O.ORDER_ID DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("ORDER_ID"),
                        rs.getInt("CUSTOMER_ID"),
                        rs.getTimestamp("ORDER_DATE"),
                        rs.getString("ORDER_TYPE"),
                        rs.getString("PRODUCT"),
                        rs.getBigDecimal("LENGTH_INCHES"),
                        rs.getBigDecimal("WIDTH_INCHES"),
                        rs.getInt("QUANTITY"),
                        rs.getBigDecimal("UNIT_PRICE"),
                        rs.getBigDecimal("LINE_TOTAL"),
                        rs.getString("ORDER_STATUS"),
                        rs.getBigDecimal("TOTAL_AMOUNT"),
                        rs.getString("DELIVERY_STATUS"),
                        rs.getString("PAYMENT_METHOD"),
                        rs.getString("EMPLOYEE_NAME") == null ? "Not Assigned" : rs.getString("EMPLOYEE_NAME")
                });
            }

        } catch (Exception e) {

            showError("Unable to load orders.\n" + e.getMessage());
        }

        JPanel actions = createButtonPanel();
        JButton confirmOrder = createActionButton("Confirm Order");
        JButton rejectOrder = createActionButton("Reject Order");
        JButton assign = createActionButton("Assign / Change Manufacturing Employee");
        JButton refresh = createActionButton("Refresh");
        confirmOrder.addActionListener(e -> confirmSelectedOrder());
        rejectOrder.addActionListener(e -> rejectSelectedOrder());
        assign.addActionListener(e -> assignManufacturingEmployee());
        refresh.addActionListener(e -> showOrders());
        actions.add(confirmOrder);
        actions.add(rejectOrder);
        actions.add(assign);
        actions.add(refresh);
        contentPanel.add(actions, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    /** Confirm a customer request and automatically create its first production task. */
    private void confirmSelectedOrder() {
        if (table == null || table.getSelectedRow() < 0) {
            showMessage("Select a pending customer order first.");
            return;
        }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String status = String.valueOf(model.getValueAt(row, 10));
        if (!"PENDING".equalsIgnoreCase(status) && !"PENDING_APPROVAL".equalsIgnoreCase(status)) {
            showMessage("Only pending customer orders can be confirmed.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int employeeId = getLeastLoadedManufacturingEmployee(con);
                if (employeeId == 0) throw new SQLException("Register a manufacturing employee before confirming orders.");

                int adminId = 0;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT USER_ID FROM USERS WHERE UPPER(TRIM(ROLE)) IN ('ADMIN','ADMINISTRATOR') ORDER BY USER_ID");
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) adminId = rs.getInt(1);
                }

                String update = "UPDATE ORDERS SET ORDER_STATUS='CONFIRMED',ADMIN_CONFIRMED='Y',"
                        + "ASSIGNED_EMPLOYEE_ID=?,ASSIGNED_DATE=SYSDATE,PROGRESS_PERCENT=0,"
                        + "DELIVERY_STATUS='NOT_DISPATCHED' WHERE ORDER_ID=? AND ORDER_STATUS IN ('PENDING','PENDING_APPROVAL')";
                try (PreparedStatement ps = con.prepareStatement(update)) {
                    ps.setInt(1, employeeId); ps.setInt(2, orderId);
                    if (ps.executeUpdate() == 0) throw new SQLException("Order status changed. Refresh and try again.");
                }

                int taskId;
                try (PreparedStatement ps = con.prepareStatement("SELECT NVL(MAX(TASK_ID),0)+1 FROM EMPLOYEE_TASKS");
                     ResultSet rs = ps.executeQuery()) {
                    rs.next(); taskId = rs.getInt(1);
                }
                String taskSql = "INSERT INTO EMPLOYEE_TASKS (TASK_ID,EMPLOYEE_ID,ORDER_ID,ITEM_ID,TASK_TITLE,"
                        + "TASK_DESCRIPTION,TASK_STATUS,PROGRESS_PERCENT,ASSIGNED_BY,ASSIGNED_AT,UPDATED_AT) "
                        + "VALUES(?,?,?,(SELECT MIN(ITEM_ID) FROM ORDER_ITEMS WHERE ORDER_ID=?),?,?,'ASSIGNED',0,?,SYSTIMESTAMP,SYSTIMESTAMP)";
                try (PreparedStatement ps = con.prepareStatement(taskSql)) {
                    ps.setInt(1, taskId); ps.setInt(2, employeeId); ps.setInt(3, orderId); ps.setInt(4, orderId);
                    ps.setString(5, "Manufacture Order #" + orderId);
                    ps.setString(6, "Produce the customer order to the approved dimensions and specifications.");
                    if (adminId == 0) ps.setNull(7, java.sql.Types.INTEGER); else ps.setInt(7, adminId);
                    ps.executeUpdate();
                }
                notifyUser(con, employeeId, "New production order assigned", "Order #" + orderId + " is confirmed and assigned to you.", "TASK");
                notifyUser(con, Integer.parseInt(model.getValueAt(row, 1).toString()), "Order confirmed",
                        "Your Order #" + orderId + " is confirmed and now in production.", "ORDER");
                con.commit();
                showMessage("Order confirmed and assigned to the employee with the lightest workload.");
                showOrders();
            } catch (SQLException ex) { con.rollback(); throw ex; }
        } catch (SQLException ex) { showError("Unable to confirm order.\n" + ex.getMessage()); }
    }

    /** Pick the active production user with the fewest unfinished manufacturing tasks. */
    private int getLeastLoadedManufacturingEmployee(Connection con) throws SQLException {
        String sql = "SELECT U.USER_ID FROM USERS U LEFT JOIN EMPLOYEE_TASKS T "
                + "ON T.EMPLOYEE_ID=U.USER_ID AND T.TASK_STATUS IN ('ASSIGNED','IN_PROGRESS','REWORK') "
                + "WHERE UPPER(TRIM(U.ROLE)) IN ('MANUFACTURING','PRODUCTION_OPERATOR','EMPLOYEE') "
                + "GROUP BY U.USER_ID ORDER BY COUNT(T.TASK_ID),U.USER_ID";
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private void rejectSelectedOrder() {
        if (table == null || table.getSelectedRow() < 0) { showMessage("Select a pending order first."); return; }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());
        int customerId = Integer.parseInt(model.getValueAt(row, 1).toString());
        if (JOptionPane.showConfirmDialog(this, "Reject Order #" + orderId + "?", "Reject Order",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        String reason = JOptionPane.showInputDialog(this, "Reason for rejection:", "Order review", JOptionPane.QUESTION_MESSAGE);
        if (reason == null) return;
        String sql = "UPDATE ORDERS SET ORDER_STATUS='REJECTED',ADMIN_CONFIRMED='N',REJECTION_REASON=? "
                + "WHERE ORDER_ID=? AND ORDER_STATUS IN ('PENDING','PENDING_APPROVAL')";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reason.trim()); ps.setInt(2, orderId);
            if (ps.executeUpdate() == 0) { showMessage("This order is no longer pending."); return; }
            notifyUser(con, customerId, "Order update", "Order #" + orderId + " was rejected. " + reason.trim(), "ORDER");
            showMessage("Order rejected. Customer status updated."); showOrders();
        } catch (SQLException ex) { showError("Unable to reject order.\n" + ex.getMessage()); }
    }

    private void notifyUser(Connection con, int userId, String title, String message, String type) throws SQLException {
        String sql = "INSERT INTO NOTIFICATIONS(USER_ID,TITLE,MESSAGE,NOTIFICATION_TYPE,IS_READ,CREATED_AT) VALUES(?,?,?,?, 'N', SYSTIMESTAMP)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setString(2, title); ps.setString(3, message); ps.setString(4, type); ps.executeUpdate();
        }
    }


    private void assignManufacturingEmployee() {
        if (table == null || table.getSelectedRow() < 0) {
            showMessage("Select an order first.");
            return;
        }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String currentName = model.getValueAt(row, 14).toString();
        try (Connection con = DBConnection.getConnection()) {
            java.util.ArrayList<Integer> ids = new java.util.ArrayList<>();
            java.util.ArrayList<String> labels = new java.util.ArrayList<>();
            String query = "SELECT USER_ID,FULL_NAME,USERNAME FROM USERS "
                    + "WHERE UPPER(ROLE) IN ('MANUFACTURING','PRODUCTION_OPERATOR','EMPLOYEE') ORDER BY FULL_NAME";
            try (PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("USER_ID"));
                    labels.add(rs.getString("FULL_NAME") + " (" + rs.getString("USERNAME") + ")");
                }
            }
            if (ids.isEmpty()) { showMessage("No manufacturing employees found. Add an employee with MANUFACTURING role first."); return; }
            JComboBox<String> choices = new JComboBox<>(labels.toArray(new String[0]));
            int choice = JOptionPane.showConfirmDialog(this, choices,
                    "Assign Order #" + orderId + " (currently: " + currentName + ")", JOptionPane.OK_CANCEL_OPTION);
            if (choice != JOptionPane.OK_OPTION) return;
            int selectedId = ids.get(choices.getSelectedIndex());
            String update = "UPDATE ORDERS SET ASSIGNED_EMPLOYEE_ID=?,ASSIGNED_DATE=SYSDATE "
                    + "WHERE ORDER_ID=? AND ORDER_STATUS IN ('CONFIRMED','IN_PRODUCTION','READY_FOR_INSPECTION','INSPECTION_FAILED')";
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setInt(1, selectedId); ps.setInt(2, orderId);
                if (ps.executeUpdate() == 0) throw new SQLException("This order cannot be reassigned in its current status.");
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE EMPLOYEE_TASKS SET EMPLOYEE_ID=?,UPDATED_AT=SYSTIMESTAMP WHERE ORDER_ID=? AND TASK_STATUS IN ('ASSIGNED','IN_PROGRESS','REWORK')")) {
                ps.setInt(1, selectedId); ps.setInt(2, orderId); ps.executeUpdate();
            }
            notifyUser(con, selectedId, "Production order reassigned", "Order #" + orderId + " is now assigned to you.", "TASK");
            showMessage("Order #" + orderId + " assigned successfully.");
            showOrders();
        } catch (SQLException ex) { showError("Unable to assign employee. Check ASSIGNED_EMPLOYEE_ID / ASSIGNED_DATE columns.\n" + ex.getMessage()); }
    }

    // ================= BILL APPROVAL =================

    private void showBillApprovals() {
        setPage("Bill Approval - Awaiting Admin Decision");
        createTable(new String[]{"Order ID", "Customer ID", "Subtotal", "GST", "Total", "Bill Status", "Order Date"});
        String sql = "SELECT ORDER_ID,CUSTOMER_ID,SUBTOTAL,GST_AMOUNT,TOTAL_AMOUNT,BILL_STATUS,ORDER_DATE "
                + "FROM ORDERS WHERE BILL_STATUS='PENDING_APPROVAL' AND QUALITY_STATUS='APPROVED' "
                + "ORDER BY ORDER_DATE";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt("ORDER_ID"), rs.getInt("CUSTOMER_ID"),
                    rs.getBigDecimal("SUBTOTAL"), rs.getBigDecimal("GST_AMOUNT"), rs.getBigDecimal("TOTAL_AMOUNT"),
                    rs.getString("BILL_STATUS"), rs.getTimestamp("ORDER_DATE")});
        } catch (SQLException ex) {
            showError("Unable to load bills for approval.\n" + ex.getMessage());
        }
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        actions.setBackground(CREAM);
        JButton viewInvoice = createActionButton("View Invoice");
        JButton approve = createActionButton("Approve Bill for Dispatch");
        JButton refresh = createActionButton("Refresh");
        viewInvoice.addActionListener(e -> viewSelectedInvoice());
        approve.addActionListener(e -> approveSelectedBill());
        refresh.addActionListener(e -> showBillApprovals());
        actions.add(viewInvoice); actions.add(approve); actions.add(refresh);
        contentPanel.add(actions, BorderLayout.SOUTH);
        contentPanel.revalidate(); contentPanel.repaint();
    }

    private void viewSelectedInvoice() {
        if (table == null || table.getSelectedRow() < 0) {
            showMessage("Select a bill first.");
            return;
        }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String sql = "SELECT NVL(P.PRODUCT_NAME,I.ITEM_DESCRIPTION) PRODUCT, I.QUANTITY, "
                + "I.UNIT_PRICE,I.LINE_TOTAL FROM ORDER_ITEMS I "
                + "LEFT JOIN PRODUCTS P ON P.PRODUCT_ID=I.PRODUCT_ID "
                + "WHERE I.ORDER_ID=? ORDER BY I.ITEM_ID";
        StringBuilder invoice = new StringBuilder();
        invoice.append("LUCENT GLASS INDUSTRIES\n")
                .append("TAX INVOICE                                      ORDER #").append(orderId).append("\n")
                .append("==========================================================================\n")
                .append(String.format("%-30s %8s %14s %14s%n", "PRODUCT / SERVICE", "QTY", "UNIT PRICE", "AMOUNT"))
                .append("--------------------------------------------------------------------------\n");
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoice.append(String.format("%-30s %8d %14s %14s%n",
                            rs.getString("PRODUCT"), rs.getInt("QUANTITY"),
                            rs.getBigDecimal("UNIT_PRICE"), rs.getBigDecimal("LINE_TOTAL")));
                }
            }
        } catch (SQLException ex) {
            showError("Unable to load invoice line items.\n" + ex.getMessage());
            return;
        }
        invoice.append("\n--------------------------------------------------------------------------")
                .append("\n                                             Subtotal: ").append(model.getValueAt(row, 2))
                .append("\n                                             GST:      ").append(model.getValueAt(row, 3))
                .append("\n                                             TOTAL:    ").append(model.getValueAt(row, 4))
                .append("\n==========================================================================")
                .append("\n                         Thank you for choosing Lucent Glass Industries.");
        JTextArea area = new JTextArea(invoice.toString(), 18, 75);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Invoice Details - Order #" + orderId, JOptionPane.INFORMATION_MESSAGE);
    }

    private void approveSelectedBill() {
        if (table == null || table.getSelectedRow() < 0) {
            showMessage("Select a bill awaiting approval.");
            return;
        }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Approve the bill for Order #" + orderId + " and release it to Dispatch?",
                "Confirm Bill Approval", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String update = "UPDATE ORDERS SET BILL_STATUS='APPROVED', ORDER_STATUS='READY_FOR_DISPATCH' "
                + "WHERE ORDER_ID=? AND BILL_STATUS='PENDING_APPROVAL' AND QUALITY_STATUS='APPROVED' "
                + "AND ORDER_STATUS IN ('INSPECTION_PASSED','READY_FOR_DISPATCH')";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setInt(1, orderId);
                if (ps.executeUpdate() == 0) throw new SQLException("Bill is no longer waiting for admin approval.");
            }
            notifyDispatchUsers(con, orderId);
            con.commit();
            showMessage("Bill approved. The Dispatch Officer has been notified.");
            showBillApprovals();
        } catch (SQLException ex) {
            showError("Unable to approve bill.\n" + ex.getMessage());
        }
    }

    // ================= QUALITY APPROVAL =================

    private void showQualityApprovals() {

        setPage("Quality Inspection - Awaiting Admin Review");

        createTable(new String[]{"Order ID", "Customer ID", "Quality Status",
                "Actual Length", "Actual Width", "Inspector Remarks", "Order Date"});

        String sql = "SELECT ORDER_ID, CUSTOMER_ID, QUALITY_STATUS, "
                + "ACTUAL_LENGTH_INCHES, ACTUAL_WIDTH_INCHES, INSPECTION_REMARKS, ORDER_DATE "
                + "FROM ORDERS WHERE QUALITY_STATUS='APPROVED' "
                + "AND ORDER_STATUS='INSPECTION_PASSED' "
                + "ORDER BY ORDER_DATE DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("ORDER_ID"), rs.getInt("CUSTOMER_ID"),
                        rs.getString("QUALITY_STATUS"), rs.getObject("ACTUAL_LENGTH_INCHES"),
                        rs.getObject("ACTUAL_WIDTH_INCHES"), rs.getString("INSPECTION_REMARKS"),
                        rs.getTimestamp("ORDER_DATE")});
            }
        } catch (SQLException ex) {
            showError("Unable to load quality approvals.\n" + ex.getMessage());
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        actions.setBackground(CREAM);
        JButton approve = new JButton("Approve Quality and Release to Billing");
        approve.setFont(new Font("Segoe UI", Font.BOLD, 13));
        approve.setForeground(WHITE);
        approve.setBackground(GREEN);
        approve.setFocusPainted(false);
        approve.addActionListener(e -> approveSelectedQualityOrder());
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> showQualityApprovals());
        actions.add(approve);
        actions.add(refresh);
        contentPanel.add(actions, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void approveSelectedQualityOrder() {
        if (table == null || table.getSelectedRow() < 0) {
            showMessage("Select a quality-approved order first.");
            return;
        }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Approve the quality result for Order #" + orderId + " and release it for billing?",
                "Confirm Quality Approval", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String update = "UPDATE ORDERS SET BILL_STATUS='NOT_PREPARED' "
                + "WHERE ORDER_ID=? AND QUALITY_STATUS='APPROVED' "
                + "AND ORDER_STATUS='INSPECTION_PASSED'";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(update)) {
                ps.setInt(1, orderId);
                if (ps.executeUpdate() == 0) {
                    con.rollback();
                    showMessage("This order is no longer waiting for approval. Refresh the list.");
                    return;
                }
            }
            notifyRole(con, "BILLING_OFFICER", "Order Ready for Billing",
                    "Quality approval completed for Order #" + orderId + ". Prepare the bill and submit it for admin approval.", "BILLING");
            con.commit();
            showMessage("Quality approved. The Billing Officer has been notified.");
            showQualityApprovals();
        } catch (SQLException ex) {
            showError("Dispatch approval failed.\n" + ex.getMessage());
        }
    }

    // ================= ADMIN NOTIFICATIONS =================

    private void showNotifications() {
        setPage("Notifications");
        createTable(new String[]{"Notification ID", "Title", "Message", "Created At", "Read"});
        String sql = "SELECT NOTIFICATION_ID, TITLE, MESSAGE, CREATED_AT, IS_READ "
                + "FROM NOTIFICATIONS WHERE USER_ID IN "
                + "(SELECT USER_ID FROM USERS WHERE UPPER(ROLE)='ADMIN') "
                + "ORDER BY CREATED_AT DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt("NOTIFICATION_ID"),
                    rs.getString("TITLE"), rs.getString("MESSAGE"),
                    rs.getTimestamp("CREATED_AT"), rs.getString("IS_READ")});
        } catch (SQLException ex) {
            showError("Unable to load notifications.\n" + ex.getMessage());
        }
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(CREAM);
        JButton markRead = new JButton("Mark as Read");
        markRead.addActionListener(e -> markSelectedAdminNotificationRead());
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> showNotifications());
        actions.add(markRead);
        actions.add(refresh);
        contentPanel.add(actions, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void markSelectedAdminNotificationRead() {
        if (table == null || table.getSelectedRow() < 0) {
            showMessage("Select a notification first.");
            return;
        }
        int row = table.convertRowIndexToModel(table.getSelectedRow());
        int notificationId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String sql = "UPDATE NOTIFICATIONS SET IS_READ='Y' WHERE NOTIFICATION_ID=? "
                + "AND USER_ID IN (SELECT USER_ID FROM USERS WHERE UPPER(ROLE)='ADMIN')";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
            showNotifications();
        } catch (SQLException ex) {
            showError("Unable to update notification.\n" + ex.getMessage());
        }
    }

    private void notifyDispatchUsers(Connection con, int orderId) throws SQLException {
        String sql = "SELECT USER_ID FROM USERS WHERE UPPER(ROLE) IN ('DISPATCH','DISPATCH_OFFICER')";
        String title = "New order approved for dispatch";
        String message = "Order #" + orderId + " is approved by Admin. Please pack the product and prepare the invoice.";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                insertAdminNotification(con, rs.getInt("USER_ID"), title, message, "DISPATCH");
            }
        }
    }

    private void notifyRole(Connection con, String role, String title,
                             String message, String type) throws SQLException {
        String sql = "SELECT USER_ID FROM USERS WHERE UPPER(ROLE)=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    insertAdminNotification(con, rs.getInt("USER_ID"), title, message, type);
                }
            }
        }
    }

    private void insertAdminNotification(Connection con, int userId, String title,
                                         String message, String type) throws SQLException {
        String sql = "INSERT INTO NOTIFICATIONS(USER_ID,TITLE,MESSAGE,NOTIFICATION_TYPE,IS_READ,CREATED_AT) "
                + "VALUES(?,?,?,?, 'N', SYSTIMESTAMP)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, message);
            ps.setString(4, type);
            ps.executeUpdate();
        }
    }

    // ================= RAW MATERIAL DETAILS =================

    private void showRawMaterials() {

        setPage("Raw Material Details");

        createTable(new String[]{
                "Material ID",
                "Material Name",
                "Material Type",
                "Sheet Length",
                "Sheet Width",
                "Quantity",
                "Reorder Level",
                "Unit Cost"
        });

        String sql = "SELECT MATERIAL_ID, MATERIAL_NAME, MATERIAL_TYPE, "
                + "SHEET_LENGTH_INCHES, SHEET_WIDTH_INCHES, "
                + "QUANTITY_ON_HAND, REORDER_LEVEL, UNIT_COST "
                + "FROM RAW_MATERIALS ORDER BY MATERIAL_ID";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("MATERIAL_ID"),
                        rs.getString("MATERIAL_NAME"),
                        rs.getString("MATERIAL_TYPE"),
                        rs.getBigDecimal("SHEET_LENGTH_INCHES"),
                        rs.getBigDecimal("SHEET_WIDTH_INCHES"),
                        rs.getInt("QUANTITY_ON_HAND"),
                        rs.getInt("REORDER_LEVEL"),
                        rs.getBigDecimal("UNIT_COST")
                });
            }

        } catch (Exception e) {

            showError("Unable to load raw materials.\n"
                    + e.getMessage());
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ================= SUPPLIER DETAILS =================

    private void addSupplier() {
        setPage("Add Supplier");

        JTextField supplierName = new JTextField();
        JTextField contactPerson = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();
        JTextField address = new JTextField();
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JComboBox<String> status = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBackground(CREAM);
        form.setBorder(new EmptyBorder(18, 20, 18, 20));
        addFormRow(form, "Supplier Name", supplierName);
        addFormRow(form, "Contact Person", contactPerson);
        addFormRow(form, "Phone", phone);
        addFormRow(form, "Email", email);
        addFormRow(form, "Address", address);
        addFormRow(form, "Supplier Username", username);
        addFormRow(form, "Initial Password", password);
        addFormRow(form, "Status", status);

        if (showFormDialog("Add Supplier", form, "Save Supplier") != JOptionPane.OK_OPTION) {
            return;
        }

        String name = supplierName.getText().trim();
        String login = username.getText().trim();
        String pass = new String(password.getPassword());
        if (name.isEmpty() || login.isEmpty() || pass.isEmpty()) {
            showError("Supplier Name, Supplier Username, and Initial Password are required.");
            return;
        }

        String sql = "INSERT INTO SUPPLIERS "
                + "(SUPPLIER_ID, SUPPLIER_NAME, CONTACT_PERSON, PHONE, EMAIL, ADDRESS, STATUS, USERNAME, PASSWORD_HASH) "
                + "VALUES ((SELECT NVL(MAX(SUPPLIER_ID),0)+1 FROM SUPPLIERS),?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, contactPerson.getText().trim());
            ps.setString(3, phone.getText().trim());
            ps.setString(4, email.getText().trim());
            ps.setString(5, address.getText().trim());
            ps.setString(6, status.getSelectedItem().toString());
            ps.setString(7, login);
            ps.setString(8, hashPassword(pass));
            ps.executeUpdate();
            showMessage("Supplier added. Share the username and initial password with them.");
            showSuppliers();
        } catch (Exception ex) {
            showError("Unable to add supplier. Check that USERNAME and PASSWORD_HASH columns exist.\n"
                    + ex.getMessage());
        }
    }

    private void showSuppliers() {
        setPage("Supplier Details");
        createTable(new String[]{
                "Supplier ID", "Supplier Name", "Contact Person", "Phone",
                "Email", "Address", "Status", "Username"
        });

        String sql = "SELECT SUPPLIER_ID, SUPPLIER_NAME, CONTACT_PERSON, PHONE, "
                   + "EMAIL, ADDRESS, STATUS, USERNAME "
                   + "FROM SUPPLIERS ORDER BY SUPPLIER_ID";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("SUPPLIER_ID"),
                        rs.getString("SUPPLIER_NAME"),
                        rs.getString("CONTACT_PERSON"),
                        rs.getString("PHONE"),
                        rs.getString("EMAIL"),
                        rs.getString("ADDRESS"),
                        rs.getString("STATUS"),
                        rs.getString("USERNAME")
                });
            }
        } catch (SQLException ex) {
            showError("Unable to load supplier details.\n" + ex.getMessage());
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        actions.setBackground(CREAM);
        JButton add = createActionButton("Add Supplier");
        JButton refresh = createActionButton("Refresh");
        add.addActionListener(e -> addSupplier());
        refresh.addActionListener(e -> showSuppliers());
        actions.add(add);
        actions.add(refresh);
        contentPanel.add(actions, BorderLayout.SOUTH);

        contentPanel.revalidate();
        contentPanel.repaint();
    }// ================= FORM DESIGN =================

    private void addFormRow(JPanel panel, String label, Component field) {

        JLabel text = new JLabel(label);

        text.setFont(
                new Font("Segoe UI", Font.BOLD, 13));

        text.setForeground(GREEN);

        if (field instanceof JTextField) {

            ((JTextField) field).setFont(
                    new Font("Segoe UI", Font.PLAIN, 14));

            ((JTextField) field).setPreferredSize(
                    new Dimension(230, 35));
        }

        if (field instanceof JPasswordField) {

            ((JPasswordField) field).setFont(
                    new Font("Segoe UI", Font.PLAIN, 14));

            ((JPasswordField) field).setPreferredSize(
                    new Dimension(230, 35));
        }

        panel.add(text);
        panel.add(field);
    }

    // ================= PROFESSIONAL DIALOG =================

    private int showFormDialog(
            String title, JPanel form, String saveText) {

        JDialog dialog = new JDialog(this, title, true);

        dialog.setUndecorated(true);

        dialog.setLayout(new BorderLayout());

        dialog.getRootPane().setBorder(
                BorderFactory.createLineBorder(GOLD, 2));

        // Header

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(DARK_GREEN);

        header.setBorder(
                new EmptyBorder(18, 22, 18, 22));

        JLabel heading = new JLabel(title);

        heading.setFont(
                new Font("Segoe UI", Font.BOLD, 20));

        heading.setForeground(
                new Color(235, 210, 160));

        header.add(heading, BorderLayout.WEST);

        // Footer

        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 15));

        footer.setBackground(CREAM);

        JButton cancel = createActionButton("Cancel");

        cancel.setBackground(
                new Color(110, 110, 100));

        JButton save = createActionButton(saveText);

        final int[] result = {
                JOptionPane.CANCEL_OPTION
        };

        cancel.addActionListener(e -> {

            result[0] = JOptionPane.CANCEL_OPTION;

            dialog.dispose();
        });

        save.addActionListener(e -> {

            result[0] = JOptionPane.OK_OPTION;

            dialog.dispose();
        });

        footer.add(cancel);
        footer.add(save);

        dialog.add(header, BorderLayout.NORTH);

        dialog.add(form, BorderLayout.CENTER);

        dialog.add(footer, BorderLayout.SOUTH);

        // Escape key

        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.pack();

        dialog.setMinimumSize(
                new Dimension(570, 0));

        dialog.setLocationRelativeTo(this);

        dialog.setVisible(true);

        return result[0];
    }

    // ================= PASSWORD HASH =================

    private String hashPassword(String password)
            throws Exception {

        MessageDigest md =
                MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(
                password.getBytes(StandardCharsets.UTF_8));

        BigInteger number = new BigInteger(1, hash);

        StringBuilder hex =
                new StringBuilder(number.toString(16));

        while (hex.length() < 64) {

            hex.insert(0, "0");
        }

        return hex.toString();
    }

    // ================= SUCCESS MESSAGE =================

    private void showMessage(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Lucent Glass Industries",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ================= ERROR MESSAGE =================

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // ================= LOGOUT =================

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {

            dispose();

            new LoginFrame().setVisible(true);
        }
    }

    // ================= MAIN METHOD =================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new AdminDashboard().setVisible(true);
        });
    }

}
