package lucentglassmanagement;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Complete Employee Dashboard for Lucent Glass Industries.
 * Uses the existing USERS, ATTENDANCE, EMPLOYEE_TASKS,
 * EMPLOYEE_STATUS_HISTORY, ORDERS and NOTIFICATIONS tables.
 *
 * EMPLOYEE_TASKS expected columns:
 * TASK_ID, EMPLOYEE_ID, ORDER_ID, ITEM_ID, TASK_TITLE,
 * TASK_DESCRIPTION, TASK_STATUS, PROGRESS_PERCENT,
 * ASSIGNED_BY, ASSIGNED_AT, DUE_DATE, UPDATED_AT
 */
public class EmployeeDashboard extends JFrame {

    private static final Color DARK_GREEN = new Color(28, 67, 52);
    private static final Color GREEN = new Color(54, 112, 82);
    private static final Color LIGHT_GREEN = new Color(226, 239, 225);
    private static final Color CREAM = new Color(250, 247, 235);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(43, 55, 46);
    private static final Color MUTED = new Color(112, 122, 113);
    private static final Color BORDER = new Color(225, 229, 218);

    private final int employeeId;
    private JPanel contentPanel;
    private JLabel pageTitle;
    private JLabel welcomeLabel;
    private String employeeName = "Employee";
    private String employeeUsername = "";

    private JTable taskTable;
    private DefaultTableModel taskModel;

    public EmployeeDashboard(int employeeId) {
        this.employeeId = employeeId;
        setTitle("Lucent Glass Industries - Employee Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1050, 680));
        getContentPane().setBackground(CREAM);
        setLayout(new BorderLayout());
        loadBasicEmployeeDetails();
        createSidebar();
        createMainArea();
        showDashboard();
        setLocationRelativeTo(null);
    }

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    private void loadBasicEmployeeDetails() {
        String sql = "SELECT FULL_NAME, USERNAME FROM USERS WHERE USER_ID=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    employeeName = rs.getString("FULL_NAME");
                    employeeUsername = rs.getString("USERNAME");
                }
            }
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // ---------------- SIDEBAR AND MAIN UI ----------------
    private void createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(DARK_GREEN);
        sidebar.setPreferredSize(new Dimension(255, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(28, 18, 22, 18));

        JLabel brand = new JLabel("<html><center>LUCENT GLASS<br>INDUSTRIES</center></html>", SwingConstants.CENTER);
        brand.setForeground(new Color(226, 190, 105));
        brand.setFont(new Font("Georgia", Font.BOLD, 21));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        brand.setMaximumSize(new Dimension(225, 58));

        JLabel portal = new JLabel("EMPLOYEE PORTAL", SwingConstants.CENTER);
        portal.setForeground(new Color(225, 235, 225));
        portal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        portal.setAlignmentX(Component.CENTER_ALIGNMENT);
        portal.setMaximumSize(new Dimension(225, 24));

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(portal);
        sidebar.add(Box.createVerticalStrut(35));

        addMenuButton(sidebar, "Dashboard", this::showDashboard);
        addMenuButton(sidebar, "My Profile", this::showProfile);
        addMenuButton(sidebar, "Attendance", this::showAttendance);
        addMenuButton(sidebar, "My Tasks", this::showTasks);
        addMenuButton(sidebar, "Manufacturing Work", this::showManufacturingWork);
        addMenuButton(sidebar, "Task History", this::showTaskHistory);
        addMenuButton(sidebar, "Notifications", this::showNotifications);
        addMenuButton(sidebar, "Change Password", this::showChangePassword);

        sidebar.add(Box.createVerticalGlue());
        JButton logout = createMenuButton("Logout");
        logout.addActionListener(e -> logout());
        sidebar.add(logout);
        add(sidebar, BorderLayout.WEST);
    }

    private void addMenuButton(JPanel sidebar, String text, Runnable action) {
        JButton button = createMenuButton(text);
        button.addActionListener(e -> action.run());
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(7));
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setPreferredSize(new Dimension(205, 42));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setForeground(Color.WHITE);
        button.setBackground(DARK_GREEN);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(10, 15, 10, 10));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(GREEN); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(DARK_GREEN); }
        });
        return button;
    }

    private void createMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(CREAM);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CREAM);
        header.setBorder(new EmptyBorder(22, 30, 18, 30));

        pageTitle = new JLabel("Employee Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 25));
        pageTitle.setForeground(DARK_GREEN);
        welcomeLabel = new JLabel("LUCENT GLASS INDUSTRIES", SwingConstants.RIGHT);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        welcomeLabel.setForeground(GREEN);
        header.add(pageTitle, BorderLayout.WEST);
        header.add(welcomeLabel, BorderLayout.EAST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CREAM);
        contentPanel.setBorder(new EmptyBorder(5, 25, 25, 25));
        main.add(header, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private void setPage(String title, JComponent component) {
        pageTitle.setText(title);
        contentPanel.removeAll();
        contentPanel.add(component, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel pagePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CREAM);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JLabel sectionHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(DARK_GREEN);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(DARK_GREEN);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setGridColor(BORDER);
        table.setSelectionBackground(LIGHT_GREEN);
        table.setSelectionForeground(DARK_GREEN);
        table.getTableHeader().setBackground(DARK_GREEN);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        return table;
    }

    private JScrollPane wrapTable(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER));
        return scroll;
    }

    // ---------------- DASHBOARD ----------------
    private void showDashboard() {
        JPanel page = pagePanel();
        JLabel greeting = new JLabel("Welcome, " + employeeName + "!");
        greeting.setFont(new Font("Segoe UI", Font.BOLD, 24));
        greeting.setForeground(TEXT);
        JLabel subtitle = new JLabel("Manage your profile, attendance and assigned manufacturing work.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        page.add(greeting);
        page.add(Box.createVerticalStrut(8));
        page.add(subtitle);
        page.add(Box.createVerticalStrut(28));

        JPanel cards = new JPanel(new GridLayout(2, 2, 18, 18));
        cards.setBackground(CREAM);
        cards.add(createCard("MY PROFILE", "View your personal and company details.", "Open Profile", this::showProfile));
        cards.add(createCard("ATTENDANCE", "Mark your daily check-in and check-out.", "Open Attendance", this::showAttendance));
        cards.add(createCard("MY TASKS", "View work assigned by the administrator.", "View Tasks", this::showTasks));
        cards.add(createCard("MANUFACTURING WORK", "Update production progress and submit completed work for quality inspection.", "Open Work", this::showManufacturingWork));
        page.add(cards);
        setPage("Employee Dashboard", page);
    }

    private JPanel createCard(String title, String description, String buttonText, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(10, 12));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(22, 22, 22, 22)));
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 17));
        heading.setForeground(DARK_GREEN);
        JLabel desc = new JLabel("<html>" + description + "</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(MUTED);
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(heading);
        top.add(Box.createVerticalStrut(10));
        top.add(desc);
        JButton open = createActionButton(buttonText);
        open.addActionListener(e -> action.run());
        card.add(top, BorderLayout.CENTER);
        card.add(open, BorderLayout.SOUTH);
        return card;
    }

    // ---------------- PROFILE ----------------
    private void showProfile() {
        JPanel page = pagePanel();
        page.add(sectionHeading("My Profile"));
        page.add(Box.createVerticalStrut(15));
        JPanel details = new JPanel(new GridLayout(0, 2, 15, 15));
        details.setBackground(CARD);
        details.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(25, 25, 25, 25)));
        String sql = "SELECT USER_ID,FULL_NAME,USERNAME,ROLE,PHONE,EMAIL,GENDER,ADDRESS,DOB FROM USERS WHERE USER_ID=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    addDetail(details, "Employee ID", String.valueOf(rs.getInt("USER_ID")));
                    addDetail(details, "Full Name", safe(rs.getString("FULL_NAME")));
                    addDetail(details, "Username", safe(rs.getString("USERNAME")));
                    addDetail(details, "Role", safe(rs.getString("ROLE")));
                    addDetail(details, "Phone", safe(rs.getString("PHONE")));
                    addDetail(details, "Email", safe(rs.getString("EMAIL")));
                    addDetail(details, "Gender", safe(rs.getString("GENDER")));
                    addDetail(details, "Address", safe(rs.getString("ADDRESS")));
                    Date dob = rs.getDate("DOB");
                    addDetail(details, "Date of Birth", dob == null ? "Not provided" : new SimpleDateFormat("dd-MM-yyyy").format(dob));
                } else {
                    addDetail(details, "Profile", "Employee profile not found");
                }
            }
        } catch (SQLException ex) { showDatabaseError(ex); }
        page.add(details);
        setPage("My Profile", page);
    }

    private void addDetail(JPanel panel, String label, String value) {
        JLabel name = new JLabel(label);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(GREEN);
        JLabel data = new JLabel(value);
        data.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        data.setForeground(TEXT);
        panel.add(name);
        panel.add(data);
    }

    // ---------------- ATTENDANCE ----------------
    private void showAttendance() {
        JPanel page = pagePanel();
        page.add(sectionHeading("Attendance"));
        page.add(Box.createVerticalStrut(8));
        JLabel info = new JLabel("Record your daily entry and exit time and review previous records.");
        info.setForeground(MUTED);
        page.add(info);
        page.add(Box.createVerticalStrut(18));

        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(22, 25, 22, 25)));
        JLabel todayHeading = new JLabel("TODAY'S ATTENDANCE");
        todayHeading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        todayHeading.setForeground(DARK_GREEN);
        JLabel dateLabel = new JLabel("Date: " + new SimpleDateFormat("dd MMMM yyyy").format(new Date()));
        JLabel statusLabel = new JLabel();
        statusLabel.setForeground(TEXT);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        JButton checkInButton = createActionButton("CHECK IN");
        JButton checkOutButton = createActionButton("CHECK OUT");
        checkInButton.addActionListener(e -> checkIn());
        checkOutButton.addActionListener(e -> checkOut());
        buttonRow.add(checkInButton);
        buttonRow.add(checkOutButton);
        card.add(todayHeading);
        card.add(Box.createVerticalStrut(12));
        card.add(dateLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(buttonRow);
        page.add(card);
        page.add(Box.createVerticalStrut(22));
        page.add(sectionHeading("Previous Attendance History"));
        page.add(Box.createVerticalStrut(10));

        DefaultTableModel historyModel = readOnlyModel(new String[]{"Date", "Check In", "Check Out", "Status", "Remarks"});
        JTable historyTable = createTable(historyModel);
        String sql = "SELECT ATTENDANCE_DATE,CHECK_IN,CHECK_OUT,ATTENDANCE_STATUS,REMARKS FROM ATTENDANCE WHERE EMPLOYEE_ID=? ORDER BY ATTENDANCE_DATE DESC";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) historyModel.addRow(new Object[]{formatDate(rs.getDate("ATTENDANCE_DATE")), formatTime(rs.getTimestamp("CHECK_IN")), formatTime(rs.getTimestamp("CHECK_OUT")), safe(rs.getString("ATTENDANCE_STATUS")), safe(rs.getString("REMARKS"))});
            }
        } catch (SQLException ex) { showDatabaseError(ex); }
        JScrollPane historyScroll = wrapTable(historyTable);
        historyScroll.setPreferredSize(new Dimension(850, 250));
        page.add(historyScroll);
        loadTodayAttendance(statusLabel, checkInButton, checkOutButton);
        JScrollPane pageScroll = new JScrollPane(page);
        pageScroll.setBorder(null);
        pageScroll.getViewport().setBackground(CREAM);
        pageScroll.getVerticalScrollBar().setUnitIncrement(16);
        setPage("Attendance", pageScroll);
    }

    private void loadTodayAttendance(JLabel status, JButton checkInButton, JButton checkOutButton) {
        String sql = "SELECT CHECK_IN,CHECK_OUT,ATTENDANCE_STATUS,REMARKS FROM ATTENDANCE WHERE EMPLOYEE_ID=? AND TRUNC(ATTENDANCE_DATE)=TRUNC(SYSDATE)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp in = rs.getTimestamp("CHECK_IN");
                    Timestamp out = rs.getTimestamp("CHECK_OUT");
                    status.setText("Status: " + safe(rs.getString("ATTENDANCE_STATUS")) + " | Check In: " + formatTime(in) + " | Check Out: " + formatTime(out) + " | Remark: " + safe(rs.getString("REMARKS")));
                    checkInButton.setEnabled(in == null);
                    checkOutButton.setEnabled(in != null && out == null);
                } else {
                    status.setText("Status: Not checked in yet.");
                    checkInButton.setEnabled(true);
                    checkOutButton.setEnabled(false);
                }
            }
        } catch (SQLException ex) { status.setText("Unable to load today's attendance."); showDatabaseError(ex); }
    }

    private void checkIn() {
        String remark = JOptionPane.showInputDialog(this, "Attendance remark (optional):", "On time");
        if (remark == null) return;
        String find = "SELECT ATTENDANCE_ID FROM ATTENDANCE WHERE EMPLOYEE_ID=? AND TRUNC(ATTENDANCE_DATE)=TRUNC(SYSDATE)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(find)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "You have already checked in today.");
                    showAttendance();
                    return;
                }
            }
            int id = nextId(con, "ATTENDANCE", "ATTENDANCE_ID");
            String insert = "INSERT INTO ATTENDANCE(ATTENDANCE_ID,EMPLOYEE_ID,ATTENDANCE_DATE,CHECK_IN,ATTENDANCE_STATUS,REMARKS) VALUES(?,?,TRUNC(SYSDATE),SYSTIMESTAMP,'PRESENT',?)";
            try (PreparedStatement add = con.prepareStatement(insert)) {
                add.setInt(1, id);
                add.setInt(2, employeeId);
                add.setString(3, remark.trim());
                add.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Check-in recorded successfully.");
            showAttendance();
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private void checkOut() {
        String remark = JOptionPane.showInputDialog(this, "Check-out remark (optional):", "Shift completed");
        if (remark == null) return;
        String sql = "UPDATE ATTENDANCE SET CHECK_OUT=SYSTIMESTAMP, REMARKS=CASE WHEN TRIM(NVL(REMARKS,'')) IS NULL THEN ? WHEN TRIM(?) IS NULL THEN REMARKS ELSE REMARKS || ' | Check-out: ' || ? END WHERE EMPLOYEE_ID=? AND TRUNC(ATTENDANCE_DATE)=TRUNC(SYSDATE) AND CHECK_IN IS NOT NULL AND CHECK_OUT IS NULL";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, remark.trim());
            ps.setString(2, remark.trim());
            ps.setString(3, remark.trim());
            ps.setInt(4, employeeId);
            if (ps.executeUpdate() > 0) JOptionPane.showMessageDialog(this, "Check-out recorded successfully.");
            else JOptionPane.showMessageDialog(this, "Please check in first or check-out is already recorded.");
            showAttendance();
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    // ---------------- MY TASKS ----------------
    private void showTasks() {
        JPanel page = new JPanel(new BorderLayout(10, 12));
        page.setBackground(CREAM);
        JLabel heading = sectionHeading("My Assigned Tasks");
        page.add(heading, BorderLayout.NORTH);
        taskModel = readOnlyModel(new String[]{"Task ID", "Order ID", "Task Title", "Description", "Status", "Progress", "Due Date"});
        taskTable = createTable(taskModel);
        page.add(wrapTable(taskTable), BorderLayout.CENTER);
        JButton refresh = createActionButton("REFRESH TASKS");
        refresh.addActionListener(e -> loadTasks());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(CREAM);
        bottom.add(refresh);
        page.add(bottom, BorderLayout.SOUTH);
        loadTasks();
        setPage("My Tasks", page);
    }

    private void loadTasks() {
        if (taskModel == null) return;
        taskModel.setRowCount(0);
        String sql = "SELECT TASK_ID,ORDER_ID,TASK_TITLE,TASK_DESCRIPTION,TASK_STATUS,PROGRESS_PERCENT,DUE_DATE FROM EMPLOYEE_TASKS WHERE EMPLOYEE_ID=? ORDER BY ASSIGNED_AT DESC";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) taskModel.addRow(new Object[]{rs.getInt("TASK_ID"), rs.getInt("ORDER_ID"), rs.getString("TASK_TITLE"), rs.getString("TASK_DESCRIPTION"), rs.getString("TASK_STATUS"), rs.getInt("PROGRESS_PERCENT") + "%", formatDate(rs.getDate("DUE_DATE"))});
            }
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    // ---------------- MANUFACTURING WORK ----------------
    private void showManufacturingWork() {
        JPanel page = new JPanel(new BorderLayout(10, 15));
        page.setBackground(CREAM);
        JLabel heading = sectionHeading("Assigned Manufacturing Work");
        JLabel info = new JLabel("Update production progress and submit completed orders for quality inspection.");
        info.setForeground(MUTED);
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(heading);
        top.add(Box.createVerticalStrut(8));
        top.add(info);
        page.add(top, BorderLayout.NORTH);

        DefaultTableModel model = readOnlyModel(new String[]{"Task ID", "Order ID", "Task Title", "Description", "Status", "Progress"});
        JTable workTable = createTable(model);
        page.add(wrapTable(workTable), BorderLayout.CENTER);

        JButton start = createActionButton("START WORK");
        JButton progress = createActionButton("UPDATE PROGRESS");
        JButton complete = createActionButton("MARK COMPLETED");
        JButton refresh = createActionButton("REFRESH");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        buttons.setBackground(CREAM);
        buttons.add(start); buttons.add(progress); buttons.add(complete); buttons.add(refresh);
        page.add(buttons, BorderLayout.SOUTH);

        Runnable reload = () -> loadManufacturingTable(model);
        start.addActionListener(e -> updateManufacturingStatus(workTable, model, "IN_PROGRESS", 10));
        progress.addActionListener(e -> updateProgress(workTable, model));
        complete.addActionListener(e -> completeManufacturingTask(workTable, model));
        refresh.addActionListener(e -> reload.run());
        reload.run();
        setPage("Manufacturing Work", page);
    }

    private void loadManufacturingTable(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT TASK_ID,ORDER_ID,TASK_TITLE,TASK_DESCRIPTION,TASK_STATUS,PROGRESS_PERCENT FROM EMPLOYEE_TASKS WHERE EMPLOYEE_ID=? ORDER BY ASSIGNED_AT DESC";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) model.addRow(new Object[]{rs.getInt("TASK_ID"), rs.getInt("ORDER_ID"), rs.getString("TASK_TITLE"), rs.getString("TASK_DESCRIPTION"), rs.getString("TASK_STATUS"), rs.getInt("PROGRESS_PERCENT") + "%"});
            }
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private int selectedTaskId(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task first.");
            return -1;
        }
        return Integer.parseInt(model.getValueAt(row, 0).toString());
    }

    private void updateManufacturingStatus(JTable table, DefaultTableModel model, String status, int percent) {
        int taskId = selectedTaskId(table, model);
        if (taskId < 0) return;
        String sql = "UPDATE EMPLOYEE_TASKS SET TASK_STATUS=?,PROGRESS_PERCENT=?,UPDATED_AT=SYSTIMESTAMP WHERE TASK_ID=? AND EMPLOYEE_ID=? AND TASK_STATUS IN ('ASSIGNED','REWORK','IN_PROGRESS')";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, percent); ps.setInt(3, taskId); ps.setInt(4, employeeId);
            if (ps.executeUpdate() > 0) {
                addTaskHistory(con, taskId, status, percent, "Manufacturing work started");
                updateOrderProgress(con, Integer.parseInt(model.getValueAt(table.getSelectedRow(), 1).toString()), percent,
                        "Manufacturing work started");
                JOptionPane.showMessageDialog(this, "Manufacturing work started.");
            } else JOptionPane.showMessageDialog(this, "This task cannot be started now.");
            loadManufacturingTable(model);
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private void updateProgress(JTable table, DefaultTableModel model) {
        int taskId = selectedTaskId(table, model);
        if (taskId < 0) return;
        String current = String.valueOf(model.getValueAt(table.getSelectedRow(), 4));
        if (!"IN_PROGRESS".equalsIgnoreCase(current)) {
            JOptionPane.showMessageDialog(this, "Start the task before updating progress.");
            return;
        }
        String value = JOptionPane.showInputDialog(this, "Enter progress percentage (1 to 100):", "50");
        if (value == null) return;
        int percent;
        try { percent = Integer.parseInt(value.trim()); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Enter a valid number."); return; }
        if (percent < 1 || percent > 100) { JOptionPane.showMessageDialog(this, "Progress must be between 1 and 100."); return; }
        if (percent == 100) {
            completeManufacturingTask(table, model);
            return;
        }
        String remarks = JOptionPane.showInputDialog(this, "Work progress remarks:", "Progress updated");
        if (remarks == null) remarks = "Progress updated";
        String sql = "UPDATE EMPLOYEE_TASKS SET PROGRESS_PERCENT=?,UPDATED_AT=SYSTIMESTAMP WHERE TASK_ID=? AND EMPLOYEE_ID=? AND TASK_STATUS='IN_PROGRESS'";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, percent); ps.setInt(2, taskId); ps.setInt(3, employeeId);
            if (ps.executeUpdate() > 0) {
                addTaskHistory(con, taskId, "IN_PROGRESS", percent, remarks);
                updateOrderProgress(con, Integer.parseInt(model.getValueAt(table.getSelectedRow(), 1).toString()), percent, remarks);
                JOptionPane.showMessageDialog(this, "Progress updated.");
            }
            loadManufacturingTable(model);
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private void updateOrderProgress(Connection con, int orderId, int percent, String remarks) throws SQLException {
        String sql = "UPDATE ORDERS SET ORDER_STATUS='IN_PRODUCTION',PROGRESS_PERCENT=?,"
                + "EMPLOYEE_REMARKS=?,PROGRESS_UPDATED_AT=SYSTIMESTAMP WHERE ORDER_ID=? "
                + "AND ORDER_STATUS IN ('CONFIRMED','IN_PRODUCTION','INSPECTION_FAILED')";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, percent); ps.setString(2, remarks); ps.setInt(3, orderId); ps.executeUpdate();
        }
    }

    private void completeManufacturingTask(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        int taskId = selectedTaskId(table, model);
        if (taskId < 0) return;
        int orderId = Integer.parseInt(model.getValueAt(row, 1).toString());
        String current = String.valueOf(model.getValueAt(row, 4));
        if (!"IN_PROGRESS".equalsIgnoreCase(current)) {
            JOptionPane.showMessageDialog(this, "Start the task before marking it completed.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Have you completed this manufacturing task? Set progress to 100%?",
                "Confirm Completion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                String update = "UPDATE EMPLOYEE_TASKS SET TASK_STATUS='COMPLETED',PROGRESS_PERCENT=100,UPDATED_AT=SYSTIMESTAMP WHERE TASK_ID=? AND EMPLOYEE_ID=? AND TASK_STATUS='IN_PROGRESS'";
                try (PreparedStatement ps = con.prepareStatement(update)) {
                    ps.setInt(1, taskId);
                    ps.setInt(2, employeeId);
                    if (ps.executeUpdate() == 0) throw new SQLException("Task could not be updated.");
                }
                addTaskHistory(con, taskId, "COMPLETED", 100, "Manufacturing completed");

                String remainingSql = "SELECT COUNT(*) FROM EMPLOYEE_TASKS WHERE ORDER_ID=? AND TASK_STATUS<>'COMPLETED'";
                int remaining;
                try (PreparedStatement ps = con.prepareStatement(remainingSql)) {
                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        remaining = rs.getInt(1);
                    }
                }

                if (remaining == 0) {
                    int inspectorId = getOrAssignQualityInspector(con, orderId);
                    String orderUpdate = "UPDATE ORDERS SET ORDER_STATUS='READY_FOR_INSPECTION', QUALITY_STATUS='PENDING', "
                            + "PROGRESS_PERCENT=100,PROGRESS_UPDATED_AT=SYSTIMESTAMP,INSPECTOR_ID=? WHERE ORDER_ID=?";
                    try (PreparedStatement ps = con.prepareStatement(orderUpdate)) {
                        ps.setInt(1, inspectorId);
                        ps.setInt(2, orderId);
                        if (ps.executeUpdate() == 0) throw new SQLException("Order not found for quality inspection.");
                    }
                    insertNotification(con, inspectorId,
                            "Product Ready for Inspection",
                            "Manufacturing is 100% completed for Order #" + orderId + ". Please inspect and accept or reject the product.",
                            "QUALITY");
                }

                con.commit();
                JOptionPane.showMessageDialog(this, remaining == 0
                        ? "Manufacturing completed. Quality Inspector has been notified."
                        : "Task completed. Other manufacturing tasks for this order are still pending.");
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
        loadManufacturingTable(model);
    }

    private int getOrAssignQualityInspector(Connection con, int orderId) throws SQLException {
        int inspectorId = 0;
        String existingSql = "SELECT INSPECTOR_ID FROM ORDERS WHERE ORDER_ID=? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(existingSql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) inspectorId = rs.getInt("INSPECTOR_ID");
            }
        }
        if (inspectorId > 0) return inspectorId;

        String inspectorSql = "SELECT USER_ID FROM USERS WHERE UPPER(ROLE)='QUALITY_INSPECTOR' ORDER BY USER_ID";
        try (PreparedStatement ps = con.prepareStatement(inspectorSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) inspectorId = rs.getInt("USER_ID");
        }
        if (inspectorId <= 0) {
            throw new SQLException("No user is registered with QUALITY_INSPECTOR role.");
        }
        return inspectorId;
    }

    private void insertNotification(Connection con, int userId, String title,
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

    private void addTaskHistory(Connection con, int taskId, String status,
                                int progress, String remarks) throws SQLException {
        String sql = "INSERT INTO EMPLOYEE_STATUS_HISTORY(HISTORY_ID,TASK_ID,UPDATED_BY,STATUS,PROGRESS_PERCENT,REMARKS,UPDATED_AT) "
                + "VALUES((SELECT NVL(MAX(HISTORY_ID),0)+1 FROM EMPLOYEE_STATUS_HISTORY),?,?,?,?,?,SYSTIMESTAMP)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, employeeId);
            ps.setString(3, status);
            ps.setInt(4, progress);
            ps.setString(5, remarks);
            ps.executeUpdate();
        }
    }

    // ---------------- TASK HISTORY ----------------
    private void showTaskHistory() {
        JPanel page = new JPanel(new BorderLayout(10, 12));
        page.setBackground(CREAM);
        page.add(sectionHeading("Task History"), BorderLayout.NORTH);
        DefaultTableModel model = readOnlyModel(new String[]{"History ID", "Task ID", "Status", "Progress", "Remarks", "Updated At"});
        JTable table = createTable(model);
        String sql = "SELECT h.HISTORY_ID,h.TASK_ID,h.STATUS,h.PROGRESS_PERCENT,h.REMARKS,h.UPDATED_AT FROM EMPLOYEE_STATUS_HISTORY h JOIN EMPLOYEE_TASKS t ON h.TASK_ID=t.TASK_ID WHERE t.EMPLOYEE_ID=? ORDER BY h.UPDATED_AT DESC";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) model.addRow(new Object[]{rs.getInt("HISTORY_ID"), rs.getInt("TASK_ID"), rs.getString("STATUS"), rs.getInt("PROGRESS_PERCENT") + "%", rs.getString("REMARKS"), formatTime(rs.getTimestamp("UPDATED_AT"))});
            }
        } catch (SQLException ex) { showDatabaseError(ex); }
        page.add(wrapTable(table), BorderLayout.CENTER);
        JButton refresh = createActionButton("REFRESH");
        refresh.addActionListener(e -> showTaskHistory());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT)); bottom.setBackground(CREAM); bottom.add(refresh);
        page.add(bottom, BorderLayout.SOUTH);
        setPage("Task History", page);
    }

    // ---------------- NOTIFICATIONS ----------------
    private void showNotifications() {
        JPanel page = new JPanel(new BorderLayout(10, 12));
        page.setBackground(CREAM);
        page.add(sectionHeading("My Notifications"), BorderLayout.NORTH);
        DefaultTableModel model = readOnlyModel(new String[]{"Notification ID", "Title", "Message", "Type", "Read", "Date"});
        JTable table = createTable(model);
        String sql = "SELECT NOTIFICATION_ID,TITLE,MESSAGE,NOTIFICATION_TYPE,IS_READ,CREATED_AT FROM NOTIFICATIONS WHERE USER_ID=? ORDER BY CREATED_AT DESC";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) model.addRow(new Object[]{rs.getInt("NOTIFICATION_ID"), rs.getString("TITLE"), rs.getString("MESSAGE"), rs.getString("NOTIFICATION_TYPE"), rs.getString("IS_READ"), formatTime(rs.getTimestamp("CREATED_AT"))});
            }
        } catch (SQLException ex) { showDatabaseError(ex); }
        page.add(wrapTable(table), BorderLayout.CENTER);
        JButton markRead = createActionButton("MARK SELECTED AS READ");
        markRead.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a notification first."); return; }
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            String update = "UPDATE NOTIFICATIONS SET IS_READ='Y' WHERE NOTIFICATION_ID=? AND USER_ID=?";
            try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(update)) {
                ps.setInt(1, id); ps.setInt(2, employeeId); ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Notification marked as read.");
                showNotifications();
            } catch (SQLException ex) { showDatabaseError(ex); }
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT)); bottom.setBackground(CREAM); bottom.add(markRead);
        page.add(bottom, BorderLayout.SOUTH);
        setPage("Notifications", page);
    }

    // ---------------- CHANGE PASSWORD ----------------
    private void showChangePassword() {
        JPanel page = pagePanel();
        page.add(sectionHeading("Change Password"));
        page.add(Box.createVerticalStrut(18));
        JPanel form = new JPanel(new GridLayout(0, 2, 12, 15));
        form.setBackground(CARD);
        form.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(25, 25, 25, 25)));
        JPasswordField oldPassword = new JPasswordField(20);
        JPasswordField newPassword = new JPasswordField(20);
        JPasswordField confirmPassword = new JPasswordField(20);
        form.add(new JLabel("Current Password")); form.add(oldPassword);
        form.add(new JLabel("New Password")); form.add(newPassword);
        form.add(new JLabel("Confirm New Password")); form.add(confirmPassword);
        JButton save = createActionButton("UPDATE PASSWORD");
        save.addActionListener(e -> {
            String oldText = new String(oldPassword.getPassword());
            String newText = new String(newPassword.getPassword());
            String confirmText = new String(confirmPassword.getPassword());
            if (oldText.isEmpty() || newText.isEmpty() || confirmText.isEmpty()) { JOptionPane.showMessageDialog(this, "Fill all password fields."); return; }
            if (!newText.equals(confirmText)) { JOptionPane.showMessageDialog(this, "New password and confirmation do not match."); return; }
            if (newText.length() < 6) { JOptionPane.showMessageDialog(this, "Password must contain at least 6 characters."); return; }
            changePassword(oldText, newText);
        });
        page.add(form);
        page.add(Box.createVerticalStrut(15));
        save.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(save);
        setPage("Change Password", page);
    }

    private void changePassword(String oldPassword, String newPassword) {
        String select = "SELECT PASSWORD_HASH FROM USERS WHERE USER_ID=?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(select)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { JOptionPane.showMessageDialog(this, "Employee account not found."); return; }
                String stored = rs.getString("PASSWORD_HASH");
                if (!hashPassword(oldPassword).equalsIgnoreCase(stored)) { JOptionPane.showMessageDialog(this, "Current password is incorrect."); return; }
            }
            String update = "UPDATE USERS SET PASSWORD_HASH=? WHERE USER_ID=?";
            try (PreparedStatement ups = con.prepareStatement(update)) {
                ups.setString(1, hashPassword(newPassword)); ups.setInt(2, employeeId); ups.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Password changed successfully.");
            showDashboard();
        } catch (SQLException ex) { showDatabaseError(ex); }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) result.append(String.format("%02x", b));
            return result.toString();
        } catch (Exception ex) { throw new IllegalStateException(ex); }
    }

    // ---------------- COMMON HELPERS ----------------
    private int nextId(Connection con, String table, String column) throws SQLException {
        String sql = "SELECT NVL(MAX(" + column + "),0)+1 FROM " + table;
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) { rs.next(); return rs.getInt(1); }
    }

    private String safe(String value) {
        if (value == null || value.trim().isEmpty()) return "Not provided";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String formatDate(java.sql.Date date) {
        return date == null ? "-" : new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    private String formatTime(Timestamp timestamp) {
        return timestamp == null ? "-" : new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(timestamp);
    }

    private void showDatabaseError(SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database operation failed.\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}

