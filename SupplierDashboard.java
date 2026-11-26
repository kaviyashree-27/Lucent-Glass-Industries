package lucentglassmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

/** Supplier portal styled to match the Lucent Glass admin dashboard. */
public class SupplierDashboard extends JFrame {

    private static final Color GREEN = new Color(25, 75, 55);
    private static final Color DARK_GREEN = new Color(15, 50, 38);
    private static final Color CREAM = new Color(248, 246, 237);
    private static final Color GOLD = new Color(190, 151, 75);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(45, 55, 48);

    private final int supplierId;
    private JTable table;
    private DefaultTableModel model;
    private JPanel contentPanel;
    private JLabel pageTitle;
    private JLabel supplierNameLabel;

    public SupplierDashboard(int supplierId) {
        this.supplierId = supplierId;
        setTitle("LUCENT GLASS INDUSTRIES - Supplier Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 650));
        setLayout(new BorderLayout());
        getContentPane().setBackground(CREAM);

        createSidebar();
        createMainArea();
        showOverview();
        setLocationRelativeTo(null);
    }

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

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(DARK_GREEN);
        menu.setBorder(new EmptyBorder(10, 12, 15, 12));
        addMenuButton(menu, "Dashboard", this::showOverview);
        addMenuButton(menu, "My Material Submissions", this::loadSubmissionsPage);
        addMenuButton(menu, "Submit Raw Material", this::showSubmitMaterialPage);
        JLabel assistance = new JLabel("<html>Need assistance?contact<br><font color='#E6CD96'>lucentglassindustries@gmail.com</font></html>");
        assistance.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        assistance.setForeground(WHITE);
        assistance.setBorder(new EmptyBorder(14, 10, 14, 5));
        assistance.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(Box.createVerticalGlue());
        menu.add(assistance);
        addMenuButton(menu, "Logout", this::logout);
        sidebar.add(menu, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);
    }

    private void addMenuButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(DARK_GREEN);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 12, 10, 8));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(GREEN); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(DARK_GREEN); }
        });
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    private void createMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(CREAM);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(new EmptyBorder(18, 28, 18, 28));
        pageTitle = new JLabel("Supplier Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 25));
        pageTitle.setForeground(GREEN);
        supplierNameLabel = new JLabel("SUPPLIER", SwingConstants.RIGHT);
        supplierNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        supplierNameLabel.setForeground(GOLD);
        header.add(pageTitle, BorderLayout.WEST);
        header.add(supplierNameLabel, BorderLayout.EAST);

        contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(CREAM);
        contentPanel.setBorder(new EmptyBorder(25, 28, 25, 28));
        main.add(header, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
        loadSupplierName();
    }

    private void loadSupplierName() {
        String sql = "SELECT SUPPLIER_NAME FROM SUPPLIERS WHERE SUPPLIER_ID=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) supplierNameLabel.setText(rs.getString(1).toUpperCase());
            }
        } catch (Exception e) {
            supplierNameLabel.setText("SUPPLIER PORTAL");
        }
    }

    private void setPage(String title) {
        pageTitle.setText(title);
        contentPanel.removeAll();
    }

    private JPanel createCountCard(String title, String sql) {
        JPanel card = new JPanel(new BorderLayout(5, 15));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 220, 205)),
                new EmptyBorder(22, 22, 22, 22)));
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        heading.setForeground(new Color(100, 105, 95));
        JLabel count = new JLabel(getCount(sql));
        count.setFont(new Font("Segoe UI", Font.BOLD, 34));
        count.setForeground(GREEN);
        card.add(heading, BorderLayout.NORTH);
        card.add(count, BorderLayout.CENTER);
        return card;
    }

    private String getCount(String sql) {
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) { return "0"; }
        return "0";
    }

    private void showOverview() {
        setPage("Supplier Dashboard");
        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 18));
        cards.setBackground(CREAM);
        cards.add(createCountCard("TOTAL SUBMISSIONS", "SELECT COUNT(*) FROM MATERIAL_SUBMISSIONS WHERE SUPPLIER_ID=" + supplierId));
        cards.add(createCountCard("PENDING REVIEW", "SELECT COUNT(*) FROM MATERIAL_SUBMISSIONS WHERE SUPPLIER_ID=" + supplierId + " AND STATUS='PENDING'"));
        cards.add(createCountCard("APPROVED", "SELECT COUNT(*) FROM MATERIAL_SUBMISSIONS WHERE SUPPLIER_ID=" + supplierId + " AND STATUS='APPROVED'"));
        contentPanel.add(cards, BorderLayout.NORTH);

        JLabel welcome = new JLabel("<html><div style='text-align:center;'><h1>Welcome to your Supplier Portal</h1>Submit material details and follow their review status here.</div></html>", SwingConstants.CENTER);
        welcome.setForeground(GREEN);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        contentPanel.add(welcome, BorderLayout.CENTER);

        contentPanel.revalidate(); contentPanel.repaint();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        panel.setBackground(CREAM);
        return panel;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(GREEN);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void createTable() {
        model = new DefaultTableModel(new String[]{
                "Submission ID", "Material Name", "Type", "Length (in)", "Width (in)",
                "Quantity", "Reorder Level", "Unit Cost", "Status", "Submitted At"
        }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(220, 235, 220));
        table.setSelectionForeground(Color.BLACK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JTableHeader th = table.getTableHeader();
        th.setBackground(GREEN);
        th.setForeground(WHITE);
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setPreferredSize(new Dimension(0, 38));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 215, 200)));
        contentPanel.add(scroll, BorderLayout.CENTER);
    }

    private void loadSubmissionsPage() {
        setPage("My Material Submissions");
        createTable();
        String sql = "SELECT SUBMISSION_ID, MATERIAL_NAME, MATERIAL_TYPE, SHEET_LENGTH_INCHES, SHEET_WIDTH_INCHES, QUANTITY, REORDER_LEVEL, UNIT_COST, STATUS, SUBMITTED_AT FROM MATERIAL_SUBMISSIONS WHERE SUPPLIER_ID=? ORDER BY SUBMITTED_AT DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) model.addRow(new Object[]{rs.getInt("SUBMISSION_ID"), rs.getString("MATERIAL_NAME"), rs.getString("MATERIAL_TYPE"), rs.getBigDecimal("SHEET_LENGTH_INCHES"), rs.getBigDecimal("SHEET_WIDTH_INCHES"), rs.getInt("QUANTITY"), rs.getInt("REORDER_LEVEL"), rs.getBigDecimal("UNIT_COST"), rs.getString("STATUS"), rs.getTimestamp("SUBMITTED_AT")});
            }
        } catch (Exception e) { showError("Unable to load submissions.\n" + e.getMessage()); }
        JPanel buttons = createButtonPanel();
        JButton refresh = createActionButton("Refresh"), dashboard = createActionButton("Dashboard");
        refresh.addActionListener(e -> loadSubmissionsPage()); dashboard.addActionListener(e -> showOverview());
        buttons.add(refresh); buttons.add(dashboard);
        contentPanel.add(buttons, BorderLayout.SOUTH);
        contentPanel.revalidate(); contentPanel.repaint();
    }

    private void showSubmitMaterialPage() {
        setPage("Submit Raw Material");
        JTextField name = new JTextField(), type = new JTextField(), length = new JTextField(), width = new JTextField();
        JTextField quantity = new JTextField(), reorder = new JTextField("0"), cost = new JTextField(), notes = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
        form.setBorder(new EmptyBorder(20, 20, 20, 20)); form.setBackground(CREAM);
        addFormRow(form, "Material Name", name); addFormRow(form, "Material Type", type);
        addFormRow(form, "Sheet Length (inches)", length); addFormRow(form, "Sheet Width (inches)", width);
        addFormRow(form, "Quantity", quantity); addFormRow(form, "Reorder Level", reorder);
        addFormRow(form, "Unit Cost", cost); addFormRow(form, "Notes", notes);
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(CREAM);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 2), new EmptyBorder(0, 0, 12, 0)));
        JPanel formHeader = new JPanel(new BorderLayout());
        formHeader.setBackground(DARK_GREEN);
        formHeader.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel heading = new JLabel("Submit Material for Admin Review");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(new Color(235, 210, 160));
        formHeader.add(heading, BorderLayout.WEST);
        formCard.add(formHeader, BorderLayout.NORTH);
        formCard.add(form, BorderLayout.CENTER);
        JPanel center = new JPanel(new GridBagLayout()); center.setBackground(CREAM);
        center.add(formCard);
        contentPanel.add(center, BorderLayout.CENTER);
        JPanel buttons = createButtonPanel();
        JButton cancel = createActionButton("Cancel"); cancel.setBackground(new Color(110, 110, 100));
        JButton submit = createActionButton("Submit");
        cancel.addActionListener(e -> showOverview());
        submit.addActionListener(e -> saveMaterialSubmission(name, type, length, width, quantity, reorder, cost, notes));
        buttons.add(cancel); buttons.add(submit);
        contentPanel.add(buttons, BorderLayout.SOUTH);
        contentPanel.revalidate(); contentPanel.repaint();
    }

    private void saveMaterialSubmission(JTextField name, JTextField type, JTextField length,
            JTextField width, JTextField quantity, JTextField reorder, JTextField cost, JTextField notes) {
        try {
            String materialName = name.getText().trim();
            if (materialName.isEmpty()) throw new IllegalArgumentException("Material name is required.");
            BigDecimal sheetLength = new BigDecimal(length.getText().trim());
            BigDecimal sheetWidth = new BigDecimal(width.getText().trim());
            BigDecimal unitCost = new BigDecimal(cost.getText().trim());
            int qty = Integer.parseInt(quantity.getText().trim());
            int reorderQty = Integer.parseInt(reorder.getText().trim());
            if (sheetLength.signum() <= 0 || sheetWidth.signum() <= 0 || qty < 0 || reorderQty < 0 || unitCost.signum() < 0)
                throw new IllegalArgumentException("Dimensions must be positive. Quantity, reorder level, and cost cannot be negative.");

            String sql = "INSERT INTO MATERIAL_SUBMISSIONS (SUBMISSION_ID, SUPPLIER_ID, MATERIAL_NAME, MATERIAL_TYPE, SHEET_LENGTH_INCHES, SHEET_WIDTH_INCHES, QUANTITY, REORDER_LEVEL, UNIT_COST, NOTES, STATUS, SUBMITTED_AT) VALUES ((SELECT NVL(MAX(SUBMISSION_ID),0)+1 FROM MATERIAL_SUBMISSIONS),?,?,?,?,?,?,?,?,?,'PENDING',SYSTIMESTAMP)";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, supplierId); ps.setString(2, materialName); ps.setString(3, type.getText().trim());
                ps.setBigDecimal(4, sheetLength); ps.setBigDecimal(5, sheetWidth); ps.setInt(6, qty);
                ps.setInt(7, reorderQty); ps.setBigDecimal(8, unitCost); ps.setString(9, notes.getText().trim());
                ps.executeUpdate();
            }
            showMessage("Material submitted to admin for review.");
            loadSubmissionsPage();
        } catch (NumberFormatException e) { showError("Enter valid numbers for dimensions, quantity, reorder level, and cost."); }
        catch (Exception e) { showError("Unable to submit material.\n" + e.getMessage()); }
    }

    private int showFormDialog(String title, JPanel form) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(GOLD, 2));
        JPanel heading = new JPanel(new BorderLayout()); heading.setBackground(DARK_GREEN); heading.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel label = new JLabel(title); label.setFont(new Font("Segoe UI", Font.BOLD, 18)); label.setForeground(new Color(235, 210, 160)); heading.add(label, BorderLayout.WEST);
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12)); footer.setBackground(CREAM);
        JButton cancel = createActionButton("Cancel"), save = createActionButton("Submit");
        cancel.setBackground(new Color(110, 110, 100));
        final int[] result = {JOptionPane.CANCEL_OPTION};
        cancel.addActionListener(e -> dialog.dispose()); save.addActionListener(e -> { result[0] = JOptionPane.OK_OPTION; dialog.dispose(); });
        footer.add(cancel); footer.add(save); dialog.add(heading, BorderLayout.NORTH); dialog.add(form, BorderLayout.CENTER); dialog.add(footer, BorderLayout.SOUTH);
        dialog.pack(); dialog.setMinimumSize(new Dimension(560, 0)); dialog.setLocationRelativeTo(this); dialog.setVisible(true);
        return result[0];
    }

    private void addFormRow(JPanel panel, String label, JTextField field) {
        JLabel text = new JLabel(label); text.setFont(new Font("Segoe UI", Font.BOLD, 13)); text.setForeground(GREEN);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14)); field.setPreferredSize(new Dimension(230, 35));
        panel.add(text); panel.add(field);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) { dispose(); new LoginFrame().setVisible(true); }
    }

    private void showMessage(String message) { JOptionPane.showMessageDialog(this, message, "Lucent Glass Industries", JOptionPane.INFORMATION_MESSAGE); }
    private void showError(String message) { JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE); }
}
