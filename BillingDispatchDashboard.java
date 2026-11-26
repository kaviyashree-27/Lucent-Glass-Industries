package lucentglassmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

/** Billing and dispatch work queues. */
public class BillingDispatchDashboard extends JFrame {
    private static final Color GREEN = new Color(25, 75, 55);
    private static final Color DARK_GREEN = new Color(15, 50, 38);
    private static final Color CREAM = new Color(248, 246, 237);
    private static final Color GOLD = new Color(190, 151, 75);

    private final int userId;
    private final String role;
    private JTable table;
    private DefaultTableModel model;
    private JPanel content;
    private JLabel titleLabel;
    private boolean dispatchQueue = false;

    public BillingDispatchDashboard(int userId, String role) {
        this.userId = userId;
        this.role = role == null ? "" : role.toUpperCase();
        setTitle("Lucent Glass Industries - " + this.role.replace('_', ' '));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 760);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        createSidebar();
        createMainArea();
        loadWork();
    }

    private void createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(DARK_GREEN);
        sidebar.setBorder(new EmptyBorder(20, 14, 18, 14));

        JLabel brand = new JLabel("<html><center>LUCENT GLASS<br>INDUSTRIES</center></html>", SwingConstants.CENTER);
        brand.setForeground(new Color(230, 205, 150));
        brand.setFont(new Font("Georgia", Font.BOLD, 20));
        brand.setBorder(new EmptyBorder(10, 4, 24, 4));
        sidebar.add(brand, BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setBackground(DARK_GREEN);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        addNav(menu, "Dashboard", () -> { dispatchQueue = false; loadWork(); });
        if (role.equals("DISPATCH")) {
            addNav(menu, "Prepare Bills", () -> { dispatchQueue = false; loadWork(); });
            addNav(menu, "Approved for Dispatch", () -> { dispatchQueue = true; loadWork(); });
        } else {
            addNav(menu, "Prepare Bills", () -> { dispatchQueue = false; loadWork(); });
        }
        addNav(menu, "Attendance", this::showAttendance);
        sidebar.add(menu, BorderLayout.CENTER);
        addNav(sidebar, "Logout", this::logout, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);
    }

    private void addNav(JPanel parent, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setBackground(DARK_GREEN);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 13, 12, 8));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.addActionListener(e -> action.run());
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(GREEN); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(DARK_GREEN); }
        });
        parent.add(button);
        parent.add(Box.createVerticalStrut(6));
    }

    private void addNav(JPanel parent, String text, Runnable action, String position) {
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(DARK_GREEN);
        JButton button = new JButton(text);
        button.setBackground(DARK_GREEN);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 13, 12, 8));
        button.addActionListener(e -> action.run());
        holder.add(button, BorderLayout.CENTER);
        parent.add(holder, position);
    }

    private void createMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(CREAM);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 28, 20, 28));
        titleLabel = new JLabel(role.equals("BILLING_OFFICER") ? "Bill Preparation" : "Dispatch Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(GREEN);
        JLabel roleLabel = new JLabel(role.replace('_', ' '));
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLabel.setForeground(GOLD);
        header.add(titleLabel, BorderLayout.WEST);
        header.add(roleLabel, BorderLayout.EAST);

        content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(CREAM);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        main.add(header, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private void loadWork() {
        // Dispatch staff first prepare invoices; after admin approval the same queue
        // displays approved orders ready for delivery.
        boolean billing = role.equals("BILLING_OFFICER") || (role.equals("DISPATCH") && !dispatchQueue);
        titleLabel.setText(billing ? "Orders Ready for Billing" : "Orders Approved for Dispatch");
        content.removeAll();

        String[] columns = billing
                ? new String[]{"Order ID", "Product", "Quantity", "Subtotal", "GST", "Total", "Order Status", "Bill Status"}
                : new String[]{"Order ID", "Product", "Quantity", "Total", "Order Status", "Delivery Status", "Delivery Address"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setBackground(GREEN);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        String sql;
        if (billing) {
            sql = "SELECT O.ORDER_ID, NVL(P.PRODUCT_NAME,I.ITEM_DESCRIPTION) PRODUCT, I.QUANTITY, "
                    + "O.SUBTOTAL,O.GST_AMOUNT,O.TOTAL_AMOUNT,O.ORDER_STATUS,O.BILL_STATUS "
                    + "FROM ORDERS O LEFT JOIN ORDER_ITEMS I ON I.ORDER_ID=O.ORDER_ID "
                    + "LEFT JOIN PRODUCTS P ON P.PRODUCT_ID=I.PRODUCT_ID "
                    + "WHERE O.QUALITY_STATUS='APPROVED' AND O.ORDER_STATUS IN ('INSPECTION_PASSED','READY_FOR_DISPATCH') "
                    + "AND O.BILL_STATUS IN ('NOT_PREPARED','REJECTED') ORDER BY O.ORDER_DATE";
        } else {
            sql = "SELECT O.ORDER_ID, NVL(P.PRODUCT_NAME,I.ITEM_DESCRIPTION) PRODUCT, I.QUANTITY, "
                    + "O.TOTAL_AMOUNT,O.ORDER_STATUS,O.DELIVERY_STATUS,O.DELIVERY_ADDRESS "
                    + "FROM ORDERS O LEFT JOIN ORDER_ITEMS I ON I.ORDER_ID=O.ORDER_ID "
                    + "LEFT JOIN PRODUCTS P ON P.PRODUCT_ID=I.PRODUCT_ID "
                    + "WHERE O.BILL_STATUS='APPROVED' AND O.ORDER_STATUS='READY_FOR_DISPATCH' "
                    + "AND O.DELIVERY_STATUS<>'DELIVERED' ORDER BY O.ORDER_DATE";
        }

        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                if (billing) {
                    model.addRow(new Object[]{rs.getInt("ORDER_ID"), rs.getString("PRODUCT"), rs.getInt("QUANTITY"),
                            rs.getBigDecimal("SUBTOTAL"), rs.getBigDecimal("GST_AMOUNT"), rs.getBigDecimal("TOTAL_AMOUNT"),
                            rs.getString("ORDER_STATUS"), rs.getString("BILL_STATUS")});
                } else {
                    model.addRow(new Object[]{rs.getInt("ORDER_ID"), rs.getString("PRODUCT"), rs.getInt("QUANTITY"),
                            rs.getBigDecimal("TOTAL_AMOUNT"), rs.getString("ORDER_STATUS"), rs.getString("DELIVERY_STATUS"),
                            rs.getString("DELIVERY_ADDRESS")});
                }
            }
        } catch (SQLException ex) {
            showError("Unable to load assigned work.\n" + ex.getMessage());
        }

        if (model.getRowCount() == 0) {
            JLabel empty = new JLabel(billing
                    ? "No quality-approved orders are waiting for bill preparation."
                    : "No admin-approved invoices are ready for dispatch. Billing must be submitted and approved first.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(90, 100, 92));
            empty.setBorder(new EmptyBorder(10, 4, 12, 4));
            content.add(empty, BorderLayout.NORTH);
        }

        content.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actions.setBackground(CREAM);
        JButton action = button(billing ? "Calculate Bill and Send to Admin" : "Start Delivery · In Transit");
        JButton delivered = button("Confirm Delivered");
        JButton refresh = button("Refresh");
        action.addActionListener(e -> {
            if (billing) {
                prepareBill();
            } else {
                markInTransit();
            }
        });
        delivered.addActionListener(e -> markDelivered());
        refresh.addActionListener(e -> loadWork());
        if (billing || role.equals("DISPATCH")) actions.add(action);
        if (!billing && role.equals("DISPATCH")) actions.add(delivered);
        actions.add(refresh);
        content.add(actions, BorderLayout.SOUTH);
        content.revalidate();
        content.repaint();
    }

    private void prepareBill() {
        int row = selectedRow();
        if (row < 0) return;
        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String sql = "SELECT O.SUBTOTAL,O.GST_AMOUNT FROM ORDERS O WHERE O.ORDER_ID=? FOR UPDATE";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                BigDecimal oldSubtotal;
                BigDecimal oldGst;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Order not found.");
                        oldSubtotal = rs.getBigDecimal("SUBTOTAL");
                        oldGst = rs.getBigDecimal("GST_AMOUNT");
                    }
                }
                BigDecimal subtotal;
                String sumSql = "SELECT NVL(SUM(LINE_TOTAL),0) FROM ORDER_ITEMS WHERE ORDER_ID=?";
                try (PreparedStatement ps = con.prepareStatement(sumSql)) {
                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) { rs.next(); subtotal = rs.getBigDecimal(1); }
                }
                BigDecimal gstRate = oldSubtotal.signum() == 0 ? BigDecimal.ZERO : oldGst.divide(oldSubtotal, 8, RoundingMode.HALF_UP);
                BigDecimal gst = subtotal.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal total = subtotal.add(gst).setScale(2, RoundingMode.HALF_UP);
                String update = "UPDATE ORDERS SET SUBTOTAL=?,GST_AMOUNT=?,TOTAL_AMOUNT=?,BILL_STATUS='PENDING_APPROVAL' "
                        + "WHERE ORDER_ID=? AND QUALITY_STATUS='APPROVED' "
                        + "AND ORDER_STATUS IN ('INSPECTION_PASSED','READY_FOR_DISPATCH')";
                try (PreparedStatement ps = con.prepareStatement(update)) {
                    ps.setBigDecimal(1, subtotal); ps.setBigDecimal(2, gst); ps.setBigDecimal(3, total); ps.setInt(4, orderId);
                    if (ps.executeUpdate() == 0) throw new SQLException("This order is no longer ready for billing.");
                }
                notifyRole(con, "ADMIN", "Bill Approval Required", "Bill for Order #" + orderId + " is ready. Total: " + total, "BILLING");
                con.commit();
                JOptionPane.showMessageDialog(this, "Bill calculated and sent to the administrator for approval.\nSubtotal: " + subtotal + "\nGST: " + gst + "\nTotal: " + total);
                loadWork();
            } catch (Exception ex) { con.rollback(); throw ex; }
        } catch (Exception ex) { showError("Unable to prepare bill.\n" + ex.getMessage()); }
    }

    private void markInTransit() {
        int row = selectedRow();
        if (row < 0) return;
        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());
        // Keep ORDER_STATUS on its schema-approved value; delivery movement belongs in DELIVERY_STATUS.
        String sql = "UPDATE ORDERS SET DELIVERY_STATUS='IN_TRANSIT' "
                + "WHERE ORDER_ID=? AND BILL_STATUS='APPROVED' AND ORDER_STATUS='READY_FOR_DISPATCH'";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                if (ps.executeUpdate() == 0) throw new SQLException("Order must have an approved invoice and be ready for dispatch.");
            }
            int customerId;
            try (PreparedStatement ps = con.prepareStatement("SELECT CUSTOMER_ID FROM ORDERS WHERE ORDER_ID=?")) {
                ps.setInt(1, orderId); try (ResultSet rs = ps.executeQuery()) { rs.next(); customerId = rs.getInt(1); }
            }
            notifyUser(con, customerId, "Your order is on its way", "Order #" + orderId + " has left the factory and is in transit.", "DELIVERY");
            notifyRole(con, "ADMIN", "Order in transit", "Dispatch started for Order #" + orderId + ".", "DELIVERY");
            con.commit();
            JOptionPane.showMessageDialog(this, "Dispatch started. Customer tracking is now updated to In Transit.");
            loadWork();
        } catch (SQLException ex) { showError("Unable to start delivery.\n" + ex.getMessage()); }
    }

    private void markDelivered() {
        int row = selectedRow();
        if (row < 0) return;
        int orderId = Integer.parseInt(model.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Confirm that Order #" + orderId + " has been delivered?", "Confirm Delivery", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        String sql = "UPDATE ORDERS SET ORDER_STATUS='COMPLETED',DELIVERY_STATUS='DELIVERED' "
                + "WHERE ORDER_ID=? AND BILL_STATUS='APPROVED' AND ORDER_STATUS='READY_FOR_DISPATCH' AND DELIVERY_STATUS='IN_TRANSIT'";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                if (ps.executeUpdate() == 0) throw new SQLException("Start delivery first. Only an in-transit order can be marked delivered.");
            }
            int customerId;
            try (PreparedStatement ps = con.prepareStatement("SELECT CUSTOMER_ID FROM ORDERS WHERE ORDER_ID=?")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); customerId = rs.getInt(1); }
            }
            notifyUser(con, customerId, "Order Delivered", "Your Order #" + orderId + " has been delivered.", "DELIVERY");
            notifyRole(con, "ADMIN", "Order Delivered", "Dispatch officer marked Order #" + orderId + " as delivered.", "DELIVERY");
            con.commit();
            JOptionPane.showMessageDialog(this, "Delivery recorded. Customer and administrator have been notified.");
            loadWork();
        } catch (SQLException ex) { showError("Unable to update delivery status.\n" + ex.getMessage()); }
    }

    private int selectedRow() {
        if (table == null || table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return -1;
        }
        return table.convertRowIndexToModel(table.getSelectedRow());
    }

    private void notifyRole(Connection con, String role, String title, String message, String type) throws SQLException {
        String sql = "SELECT USER_ID FROM USERS WHERE UPPER(ROLE)=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) notifyUser(con, rs.getInt(1), title, message, type);
            }
        }
    }

    private void notifyUser(Connection con, int recipientId, String title, String message, String type) throws SQLException {
        String sql = "INSERT INTO NOTIFICATIONS(USER_ID,TITLE,MESSAGE,NOTIFICATION_TYPE,IS_READ,CREATED_AT) VALUES(?,?,?,?, 'N', SYSTIMESTAMP)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, recipientId); ps.setString(2, title); ps.setString(3, message); ps.setString(4, type); ps.executeUpdate();
        }
    }

    private void showAttendance() {
        content.removeAll();
        titleLabel.setText("Attendance");
        content.add(new StaffAttendancePanel(userId), BorderLayout.CENTER);
        content.revalidate(); content.repaint();
    }

    private JButton button(String text) {
        JButton b = new JButton(text); b.setBackground(GREEN); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 15, 10, 15)); return b;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Operations Dashboard", JOptionPane.ERROR_MESSAGE);
    }

    private void logout() {
        if (JOptionPane.showConfirmDialog(this, "Log out?", "Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dispose(); new LoginFrame().setVisible(true);
        }
    }
}

