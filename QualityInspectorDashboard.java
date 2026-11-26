package lucentglassmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

/** Quality inspection work queue for the logged-in inspector. */
public class QualityInspectorDashboard extends JFrame {
private static final Color DARK_GREEN = new Color(15, 50, 38);
    private static final Color GREEN = new Color(25, 75, 55);
    private static final Color CREAM = new Color(248, 246, 237);
    private static final Color GOLD = new Color(190, 151, 75);
    private static final Color WHITE = Color.WHITE;

    private final int inspectorId;
    private JTable table;
    private DefaultTableModel model;
    private JTabbedPane tabs;

    public QualityInspectorDashboard(int inspectorId) {
        this.inspectorId = inspectorId;
        setTitle("Lucent Glass Industries - Quality Inspector");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 760);
        setMinimumSize(new Dimension(1000, 620));
        setLayout(new BorderLayout());
        getContentPane().setBackground(CREAM);

        createHeader();
        createSidebar();
        createContent();
        setLocationRelativeTo(null);
        loadOrders();
    }

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    private void createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(new EmptyBorder(18, 28, 18, 28));

        JLabel title = new JLabel("Quality Inspector Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(GREEN);

        JLabel role = new JLabel("QUALITY INSPECTOR");
        role.setFont(new Font("Segoe UI", Font.BOLD, 13));
        role.setForeground(GOLD);

        header.add(title, BorderLayout.WEST);
        header.add(role, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBackground(DARK_GREEN);
        sidebar.setBorder(new EmptyBorder(18, 14, 18, 14));

        JLabel brand = new JLabel("<html><center>LUCENT GLASS<br>INDUSTRIES</center></html>", SwingConstants.CENTER);
        brand.setForeground(new Color(230, 205, 150));
        brand.setFont(new Font("Georgia", Font.BOLD, 20));
        brand.setBorder(new EmptyBorder(10, 4, 24, 4));
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setBackground(DARK_GREEN);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.add(sideMenuButton("Dashboard", 0));
        menu.add(sideMenuButton("Assigned Orders", 0));
        menu.add(sideMenuButton("Attendance", 1));
        menu.add(sideMenuButton("Notifications", 2));
        sidebar.add(menu, BorderLayout.CENTER);

        JButton logout = sideMenuButton("Logout", -1);
        logout.addActionListener(e -> logout());
        sidebar.add(logout, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);
    }

    private JButton sideMenuButton(String label, int page) {
        JButton button = new JButton(label);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setPreferredSize(new Dimension(210, 44));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setForeground(WHITE);
        button.setBackground(DARK_GREEN);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(11, 14, 11, 8));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            if (page >= 0 && tabs != null) tabs.setSelectedIndex(page);
        });
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(GREEN); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(DARK_GREEN); }
        });
        return button;
    }

    private void createContent() {
        model = new DefaultTableModel(new String[]{
                "Order ID", "Product", "Requested Length", "Actual Length",
                "Requested Width", "Actual Width", "Progress", "Employee Note",
                "Order Status", "Quality Status", "Inspection Note"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionBackground(new Color(220, 235, 220));
        table.setSelectionForeground(DARK_GREEN);
        int[] columnWidths = {85, 150, 135, 125, 135, 125, 90, 220, 170, 140, 240};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        table.getTableHeader().setBackground(GREEN);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new LineBorder(new Color(225, 220, 205)));

        tabs = new JTabbedPane();
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) { return 0; }
            @Override protected int calculateTabAreaWidth(int tabPlacement, int runCount, int maxTabWidth) { return 0; }
        });
        tabs.addTab("Inspection Queue", tableScroll);
        tabs.addTab("Attendance", new StaffAttendancePanel(inspectorId));
        tabs.addTab("Notifications", createNotificationsPanel());
        add(tabs, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        buttons.setBackground(CREAM);

        JButton inspect = createButton("Inspect Selected Order");
        JButton refresh = createButton("Refresh");
        JButton logout = createButton("Logout");

        inspect.addActionListener(e -> inspectSelectedOrder());
        refresh.addActionListener(e -> loadOrders());
        logout.addActionListener(e -> logout());

        buttons.add(inspect);
        buttons.add(refresh);
        buttons.add(logout);
        add(buttons, BorderLayout.SOUTH);
    }

    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CREAM);
        DefaultTableModel notificationModel = new DefaultTableModel(new String[]{
                "Notification ID", "Title", "Message", "Type", "Read", "Created At"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable notificationTable = new JTable(notificationModel);
        notificationTable.setRowHeight(30);
        notificationTable.getTableHeader().setBackground(GREEN);
        notificationTable.getTableHeader().setForeground(WHITE);
        notificationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(new JScrollPane(notificationTable), BorderLayout.CENTER);

        JButton refresh = createButton("Refresh Notifications");
        JButton markRead = createButton("Mark Selected as Read");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actions.setBackground(CREAM);
        actions.add(refresh);
        actions.add(markRead);
        panel.add(actions, BorderLayout.SOUTH);

        Runnable reload = () -> {
            notificationModel.setRowCount(0);
            String sql = "SELECT NOTIFICATION_ID,TITLE,MESSAGE,NOTIFICATION_TYPE,IS_READ,CREATED_AT "
                    + "FROM NOTIFICATIONS WHERE USER_ID=? ORDER BY CREATED_AT DESC";
            try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, inspectorId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        notificationModel.addRow(new Object[]{rs.getInt("NOTIFICATION_ID"),
                                rs.getString("TITLE"), rs.getString("MESSAGE"),
                                rs.getString("NOTIFICATION_TYPE"), rs.getString("IS_READ"),
                                rs.getTimestamp("CREATED_AT")});
                    }
                }
            } catch (SQLException ex) {
                showError("Unable to load notifications.\n" + ex.getMessage());
            }
        };
        refresh.addActionListener(e -> reload.run());
        markRead.addActionListener(e -> {
            int selected = notificationTable.getSelectedRow();
            if (selected < 0) {
                showMessage("Select a notification first.");
                return;
            }
            int row = notificationTable.convertRowIndexToModel(selected);
            int notificationId = Integer.parseInt(notificationModel.getValueAt(row, 0).toString());
            String sql = "UPDATE NOTIFICATIONS SET IS_READ='Y' WHERE NOTIFICATION_ID=? AND USER_ID=?";
            try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, notificationId);
                ps.setInt(2, inspectorId);
                ps.executeUpdate();
                reload.run();
            } catch (SQLException ex) {
                showError("Unable to mark notification as read.\n" + ex.getMessage());
            }
        });
        reload.run();
        return panel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(GREEN);
        button.setForeground(WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 17, 10, 17));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /** Show every order assigned to this inspector so order details remain visible. */
    private void loadOrders() {
        model.setRowCount(0);
        String assignReady = "UPDATE ORDERS SET INSPECTOR_ID=NVL(INSPECTOR_ID,?), "
                + "ORDER_STATUS='READY_FOR_INSPECTION', QUALITY_STATUS=NVL(QUALITY_STATUS,'PENDING') "
                + "WHERE NVL(PROGRESS_PERCENT,0)>=100 "
                + "AND ORDER_STATUS IN ('CONFIRMED','IN_PRODUCTION','IN_PROGRESS','INSPECTION_FAILED') "
                + "AND (INSPECTOR_ID IS NULL OR INSPECTOR_ID=?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(assignReady)) {
            ps.setInt(1, inspectorId);
            ps.setInt(2, inspectorId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            showError("Unable to assign completed orders to this inspector.\n" + ex.getMessage());
        }
        String sql = "SELECT O.ORDER_ID, NVL(P.PRODUCT_NAME, I.ITEM_DESCRIPTION) PRODUCT_NAME, "
                + "I.LENGTH_INCHES, O.ACTUAL_LENGTH_INCHES, I.WIDTH_INCHES, "
                + "O.ACTUAL_WIDTH_INCHES, NVL(O.PROGRESS_PERCENT, 0) PROGRESS_PERCENT, "
                + "O.EMPLOYEE_REMARKS, O.ORDER_STATUS, O.QUALITY_STATUS, O.INSPECTION_REMARKS "
                + "FROM ORDERS O "
                + "LEFT JOIN ORDER_ITEMS I ON I.ORDER_ID = O.ORDER_ID "
                + "LEFT JOIN PRODUCTS P ON P.PRODUCT_ID = I.PRODUCT_ID "
                + "WHERE O.INSPECTOR_ID = ? "
                + "ORDER BY O.ORDER_DATE, O.ORDER_ID";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, inspectorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("ORDER_ID"), rs.getString("PRODUCT_NAME"),
                            rs.getBigDecimal("LENGTH_INCHES"), rs.getBigDecimal("ACTUAL_LENGTH_INCHES"),
                            rs.getBigDecimal("WIDTH_INCHES"), rs.getBigDecimal("ACTUAL_WIDTH_INCHES"),
                            rs.getInt("PROGRESS_PERCENT") + "%", rs.getString("EMPLOYEE_REMARKS"),
                            rs.getString("ORDER_STATUS"), rs.getString("QUALITY_STATUS"),
                            rs.getString("INSPECTION_REMARKS")
                    });
                }
            }
        } catch (SQLException ex) {
            showError("Unable to load assigned inspection orders.\n" + ex.getMessage());
        }
    }

    private void inspectSelectedOrder() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            showMessage("Select an order to inspect first.");
            return;
        }
        int row = table.convertRowIndexToModel(selected);
        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String status = String.valueOf(model.getValueAt(row, 8));
        if (!"READY_FOR_INSPECTION".equalsIgnoreCase(status)
                && !"INSPECTION_FAILED".equalsIgnoreCase(status)) {
            showMessage("This order is currently " + status + ". Inspection is available after manufacturing is complete and the order is ready for inspection.");
            return;
        }

        JTextField lengthField = new JTextField();
        JTextField widthField = new JTextField();
        JTextArea noteArea = new JTextArea(4, 28);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        form.setBackground(CREAM);
        form.add(new JLabel("Requested length (inches):"));
        form.add(new JLabel(String.valueOf(model.getValueAt(row, 2))));
        form.add(new JLabel("Measured length (inches):"));
        form.add(lengthField);
        form.add(new JLabel("Requested width (inches):"));
        form.add(new JLabel(String.valueOf(model.getValueAt(row, 4))));
        form.add(new JLabel("Measured width (inches):"));
        form.add(widthField);
        form.add(new JLabel("Inspection remarks / rejection reason:"));
        form.add(new JScrollPane(noteArea));

        int result = JOptionPane.showConfirmDialog(this, form,
                "Inspect Order #" + orderId, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        BigDecimal actualLength;
        BigDecimal actualWidth;
        try {
            actualLength = new BigDecimal(lengthField.getText().trim());
            actualWidth = new BigDecimal(widthField.getText().trim());
            if (actualLength.signum() <= 0 || actualWidth.signum() <= 0) {
                throw new NumberFormatException("Measurements must be positive.");
            }
        } catch (NumberFormatException ex) {
            showMessage("Measured length and width must be valid positive numbers.");
            return;
        }

        BigDecimal requestedLength = new BigDecimal(model.getValueAt(row, 2).toString());
        BigDecimal requestedWidth = new BigDecimal(model.getValueAt(row, 4).toString());
        boolean dimensionsMatch = actualLength.compareTo(requestedLength) == 0
                && actualWidth.compareTo(requestedWidth) == 0;

        Object[] options = {"ACCEPT", "REJECT", "CANCEL"};
        int decision = JOptionPane.showOptionDialog(this,
                dimensionsMatch
                        ? "Dimensions match the customer's order. Accept this product?"
                        : "Dimensions do not match the customer's order. Reject this product?",
                "Quality Decision - Order #" + orderId,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (decision == 2 || decision == JOptionPane.CLOSED_OPTION) return;

        boolean accepted = decision == 0;
        String note = noteArea.getText().trim();
        if (!accepted && note.isEmpty()) {
            showMessage("Please provide the defect details or reason for rejection.");
            return;
        }
        if (accepted && !dimensionsMatch) {
            showMessage("The measured dimensions do not match the customer's specifications. Record this inspection as rejected.");
            return;
        }

        saveInspection(orderId, actualLength, actualWidth, accepted, note);
    }

    /**
     * ACCEPT => INSPECTION_PASSED + APPROVED and notify ADMIN for dispatch approval.
     * REJECT => INSPECTION_FAILED + REJECTED and notify ADMIN and assigned operator.
     */
    private void saveInspection(int orderId, BigDecimal actualLength,
                                BigDecimal actualWidth, boolean accepted, String remarks) {
        String update = "UPDATE ORDERS SET ACTUAL_LENGTH_INCHES=?, ACTUAL_WIDTH_INCHES=?, "
                + "QUALITY_STATUS=?, INSPECTOR_ID=?, "
                + "INSPECTION_REMARKS=?, ORDER_STATUS=? "
                + "WHERE ORDER_ID=? AND INSPECTOR_ID=? "
                + "AND ORDER_STATUS IN ('READY_FOR_INSPECTION','INSPECTION_FAILED')";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(update)) {
                    ps.setBigDecimal(1, actualLength);
                    ps.setBigDecimal(2, actualWidth);
                    ps.setString(3, accepted ? "APPROVED" : "REJECTED");
                    ps.setInt(4, inspectorId);
                    ps.setString(5, remarks);
                    ps.setString(6, accepted ? "INSPECTION_PASSED" : "INSPECTION_FAILED");
                    ps.setInt(7, orderId);
                    ps.setInt(8, inspectorId);
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("The order is no longer assigned to this inspector or has already been updated.");
                    }
                }

                if (accepted) {
                    notifyRole(con, "ADMIN", orderId,
                            "Approval for Dispatch",
                            "Quality inspection accepted Order #" + orderId
                                    + ". Admin dispatch approval required.", "QUALITY");
                } else {
                    int employeeId = getAssignedEmployee(con, orderId);
                    if (employeeId > 0) {
                        notifyUser(con, employeeId, orderId,
                                "Quality Rejected - Rework Required",
                                "Order #" + orderId + " rejected. Reason: " + remarks,
                                "QUALITY");
                    }
                    notifyRole(con, "ADMIN", orderId,
                            "Quality Rejected",
                            "Order #" + orderId + " rejected by inspector. Reason: " + remarks,
                            "QUALITY");
                }

                con.commit();
                showMessage(accepted
                        ? "Inspection accepted. A dispatch approval request has been sent to the administrator."
                        : "Inspection rejected. The administrator and assigned production employee have been notified.");
                loadOrders();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            showError("Unable to save the inspection result. Please verify the database columns and constraints.\n"
                    + ex.getMessage());
        }
    }

    private int getAssignedEmployee(Connection con, int orderId) throws SQLException {
        String sql = "SELECT ASSIGNED_EMPLOYEE_ID FROM ORDERS WHERE ORDER_ID=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("ASSIGNED_EMPLOYEE_ID");
            }
        }
        return 0;
    }

    private void notifyRole(Connection con, String role, int orderId,
                            String title, String message, String type) throws SQLException {
        String sql = "SELECT USER_ID FROM USERS WHERE UPPER(ROLE)=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifyUser(con, rs.getInt("USER_ID"), orderId, title, message, type);
                }
            }
        }
    }

    private void notifyUser(Connection con, int userId, int orderId,
                            String title, String message, String type) throws SQLException {
        String sql = "INSERT INTO NOTIFICATIONS "
                + "(USER_ID, TITLE, MESSAGE, NOTIFICATION_TYPE, IS_READ, CREATED_AT) "
                + "VALUES (?, ?, ?, ?, 'N', SYSTIMESTAMP)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, "Order #" + orderId + ": " + message);
            ps.setString(4, type);
            ps.executeUpdate();
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Lucent Glass Industries", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Quality Inspector Error", JOptionPane.ERROR_MESSAGE);
    }
}
