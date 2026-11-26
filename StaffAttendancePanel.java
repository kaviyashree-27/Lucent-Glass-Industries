package lucentglassmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Shared attendance screen for all staff roles. */
public class StaffAttendancePanel extends JPanel {
    private final int employeeId;
    private final Color cream = new Color(248, 246, 237);
    private final Color green = new Color(25, 75, 55);
    private final DefaultTableModel model;

    public StaffAttendancePanel(int employeeId) {
        this.employeeId = employeeId;

        setLayout(new BorderLayout(12, 12));
        setBackground(cream);

        JLabel heading = new JLabel("Attendance", SwingConstants.LEFT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(green);
        add(heading, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{
                    "Date", "Check In", "Check Out", "Status", "Remarks"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(green);
        table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 8));
        actions.setBackground(cream);

        JButton checkIn = createButton("Check In");
        JButton checkOut = createButton("Check Out");
        JButton refresh = createButton("Refresh");

        checkIn.addActionListener(e -> recordCheckIn());
        checkOut.addActionListener(e -> recordCheckOut());
        refresh.addActionListener(e -> load());

        actions.add(checkIn);
        actions.add(checkOut);
        actions.add(refresh);
        add(actions, BorderLayout.SOUTH);

        load();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(green);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void load() {
        model.setRowCount(0);

        String sql =
                "SELECT ATTENDANCE_DATE, CHECK_IN, CHECK_OUT, "
              + "ATTENDANCE_STATUS, REMARKS "
              + "FROM ATTENDANCE "
              + "WHERE EMPLOYEE_ID = ? "
              + "ORDER BY ATTENDANCE_DATE DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        formatDate(rs.getDate("ATTENDANCE_DATE")),
                        formatTime(rs.getTimestamp("CHECK_IN")),
                        formatTime(rs.getTimestamp("CHECK_OUT")),
                        rs.getString("ATTENDANCE_STATUS"),
                        rs.getString("REMARKS")
                    });
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load attendance:\n" + e.getMessage());
        }
    }

    private void recordCheckIn() {
        String sql =
                "INSERT INTO ATTENDANCE "
              + "(ATTENDANCE_ID, EMPLOYEE_ID, ATTENDANCE_DATE, "
              + "CHECK_IN, ATTENDANCE_STATUS, REMARKS) "
              + "VALUES ("
              + "(SELECT NVL(MAX(ATTENDANCE_ID), 0) + 1 FROM ATTENDANCE), "
              + "?, TRUNC(SYSDATE), SYSTIMESTAMP, "
              + "'PRESENT', 'Staff check-in')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Check-in recorded.");

            load();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Check-in failed. You may already have checked in today."
                    + "\n" + e.getMessage());
        }
    }

    private void recordCheckOut() {
        String sql =
                "UPDATE ATTENDANCE "
              + "SET CHECK_OUT = SYSTIMESTAMP, "
              + "REMARKS = 'Staff check-out' "
              + "WHERE EMPLOYEE_ID = ? "
              + "AND TRUNC(ATTENDANCE_DATE) = TRUNC(SYSDATE) "
              + "AND CHECK_IN IS NOT NULL "
              + "AND CHECK_OUT IS NULL";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            int updated = ps.executeUpdate();

            if (updated == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Check in first. A check-out may already be recorded.");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Check-out recorded.");
            }

            load();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Check-out failed:\n" + e.getMessage());
        }
    }

    private String formatDate(Date date) {
        return date == null
                ? "-"
                : new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    private String formatTime(Timestamp timestamp) {
        return timestamp == null
                ? "-"
                : new SimpleDateFormat("hh:mm a").format(timestamp);
    }
}