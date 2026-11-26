package lucentglassmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OperationsDashboard extends JFrame {

    private final int userId;
    private final String role;

    private final Color green = new Color(25, 75, 55);
    private final Color darkGreen = new Color(15, 50, 38);
    private final Color cream = new Color(248, 246, 237);

    private JTable table;
    private DefaultTableModel model;

    public OperationsDashboard(int userId, String role) {
        this.userId = userId;
        this.role = role;

        setTitle("Lucent Glass - " + role);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 700);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        createSidebar();
        createMainArea();
        loadWork();
    }

    private void createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(darkGreen);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(
                BorderFactory.createEmptyBorder(25, 14, 15, 14));

        JLabel brand = new JLabel(
                "<html><center>LUCENT GLASS<br>INDUSTRIES</center></html>",
                SwingConstants.CENTER);
        brand.setForeground(new Color(230, 205, 150));
        brand.setFont(new Font("Georgia", Font.BOLD, 21));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(30));

        addNav(sidebar, "Dashboard", this::loadWork);
        addNav(sidebar, "Attendance", this::showAttendance);

        sidebar.add(Box.createVerticalGlue());
        addNav(sidebar, "Logout", this::logout);

        add(sidebar, BorderLayout.WEST);
    }

    private void addNav(
            JPanel panel,
            String text,
            Runnable action) {

        JButton button = createButton(text);
        button.setBackground(darkGreen);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 44));
        button.addActionListener(e -> action.run());

        panel.add(button);
        panel.add(Box.createVerticalStrut(8));
    }

    private void createMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(cream);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel(role + " Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(green);
        header.add(title, BorderLayout.WEST);

        main.add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{
                    "Order ID",
                    "Product",
                    "Quantity",
                    "Order Status",
                    "Progress %"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(green);
        table.getTableHeader().setForeground(Color.WHITE);

        main.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10));
        footer.setBackground(cream);

        JButton refresh = createButton("Refresh Work Queue");
        refresh.addActionListener(e -> loadWork());

        footer.add(refresh);
        main.add(footer, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(green);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return button;
    }

    private void loadWork() {
        model.setRowCount(0);

        String statusFilter;

        if ("BILLING_OFFICER".equals(role)) {
            statusFilter = "O.ORDER_STATUS = 'READY_FOR_DISPATCH'";
        } else {
            statusFilter =
                    "O.ORDER_STATUS IN "
                  + "('READY_FOR_DISPATCH', 'DISPATCHED')";
        }

        String sql =
                "SELECT O.ORDER_ID, "
              + "NVL(P.PRODUCT_NAME, I.ITEM_DESCRIPTION) AS PRODUCT_NAME, "
              + "I.QUANTITY, "
              + "O.ORDER_STATUS, "
              + "NVL(O.PROGRESS_PERCENT, 0) AS PROGRESS_PERCENT "
              + "FROM ORDERS O "
              + "LEFT JOIN ORDER_ITEMS I "
              + "ON I.ORDER_ID = O.ORDER_ID "
              + "LEFT JOIN PRODUCTS P "
              + "ON P.PRODUCT_ID = I.PRODUCT_ID "
              + "WHERE " + statusFilter + " "
              + "ORDER BY O.ORDER_DATE DESC";

        try (Connection con = DBConnection.getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ORDER_ID"),
                    rs.getString("PRODUCT_NAME"),
                    rs.getInt("QUANTITY"),
                    rs.getString("ORDER_STATUS"),
                    rs.getInt("PROGRESS_PERCENT") + "%"
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load work queue.\n" + e.getMessage());
        }
    }

    private void showAttendance() {
        getContentPane().removeAll();
        add(new StaffAttendancePanel(userId), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}