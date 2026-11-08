import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ResultPage extends JFrame implements ActionListener {

    JTextField txtId, txtStudentId, txtExamId, txtMarks, txtSearch;
    JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnClear, btnClose;
    JTable table;
    DefaultTableModel model;

    public ResultPage() {

        setTitle("Result Records");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ---------- Tool Bar ----------
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnSearch = new JButton("Search");
        btnClear = new JButton("Clear");
        btnClose = new JButton("Close");
        toolBar.add(btnAdd);
        toolBar.add(btnUpdate);
        toolBar.add(btnDelete);
        toolBar.addSeparator();
        toolBar.add(btnSearch);
        toolBar.add(btnClear);
        toolBar.addSeparator();
        toolBar.add(btnClose);
        add(toolBar, BorderLayout.NORTH);

        // ---------- Form ----------
        JPanel panel = new JPanel(null);
        add(panel, BorderLayout.CENTER);

        JLabel lblTitle = new JLabel("Result Records", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(30, 60, 120));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 0, 800, 45);
        panel.add(lblTitle);

        JLabel l1 = new JLabel("Result ID:");
        l1.setBounds(40, 65, 100, 25);
        panel.add(l1);
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBounds(140, 65, 200, 25);
        panel.add(txtId);

        JLabel l2 = new JLabel("Student ID:");
        l2.setBounds(40, 105, 100, 25);
        panel.add(l2);
        txtStudentId = new JTextField();
        txtStudentId.setBounds(140, 105, 200, 25);
        panel.add(txtStudentId);

        JLabel l3 = new JLabel("Exam ID:");
        l3.setBounds(420, 65, 100, 25);
        panel.add(l3);
        txtExamId = new JTextField();
        txtExamId.setBounds(520, 65, 200, 25);
        panel.add(txtExamId);

        JLabel l4 = new JLabel("Marks:");
        l4.setBounds(420, 105, 100, 25);
        panel.add(l4);
        txtMarks = new JTextField();
        txtMarks.setBounds(520, 105, 200, 25);
        panel.add(txtMarks);

        JLabel l5 = new JLabel("(Grade is calculated automatically)");
        l5.setBounds(40, 145, 300, 25);
        panel.add(l5);

        JLabel l6 = new JLabel("Search (student / subject):");
        l6.setBounds(40, 195, 170, 25);
        panel.add(l6);
        txtSearch = new JTextField();
        txtSearch.setBounds(210, 195, 200, 25);
        panel.add(txtSearch);

        // ---------- Table ----------
        String[] columns = {"ID", "Student ID", "Student Name", "Exam ID", "Subject", "Marks", "Max", "Grade"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        // make the Subject column wider and the ID columns narrower
        table.getColumnModel().getColumn(4).setPreferredWidth(170);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(6).setPreferredWidth(50);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(40, 240, 700, 270);
        panel.add(sp);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showSelectedRow();
            }
        });

        btnAdd.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnSearch.addActionListener(this);
        btnClear.addActionListener(this);
        btnClose.addActionListener(this);

        // only Admin can add, update or delete
        if (!LoginPage.loggedRole.equals("Admin")) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }

        loadTable("");
        setVisible(true);
    }

    // gets data from results, students and exams tables together
    void loadTable(String keyword) {
        // remove the old selection first (otherwise Java prints a sorter warning)
        table.clearSelection();
        table.getSelectionModel().setAnchorSelectionIndex(-1);
        table.getSelectionModel().setLeadSelectionIndex(-1);
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT r.result_id, s.student_id, s.name, e.exam_id, e.subject, r.marks, e.max_marks, r.grade " +
                "FROM results r, students s, exams e " +
                "WHERE r.student_id = s.student_id AND r.exam_id = e.exam_id " +
                "AND (s.name LIKE ? OR e.subject LIKE ?) ORDER BY r.result_id"
            );
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4),
                    rs.getString(5), rs.getInt(6), rs.getInt(7), rs.getString(8)
                });
            }
            con.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    void showSelectedRow() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        txtId.setText(table.getValueAt(row, 0).toString());
        txtStudentId.setText(table.getValueAt(row, 1).toString());
        txtExamId.setText(table.getValueAt(row, 3).toString());
        txtMarks.setText(table.getValueAt(row, 5).toString());
    }

    void clearFields() {
        txtId.setText("");
        txtStudentId.setText("");
        txtExamId.setText("");
        txtMarks.setText("");
        txtSearch.setText("");
        table.clearSelection();
    }

    // gives the grade according to percentage
    String findGrade(int marks, int maxMarks) {
        int percent = marks * 100 / maxMarks;

        if (percent >= 90) return "A+";
        else if (percent >= 75) return "A";
        else if (percent >= 60) return "B";
        else if (percent >= 50) return "C";
        else if (percent >= 40) return "D";
        else return "F";
    }

    // checks that the student id and exam id really exist, returns max marks of the exam (or -1)
    int getMaxMarks(Connection con, int studentId, int examId) throws Exception {

        PreparedStatement ps1 = con.prepareStatement("SELECT * FROM students WHERE student_id = ?");
        ps1.setInt(1, studentId);
        if (!ps1.executeQuery().next()) {
            JOptionPane.showMessageDialog(this, "No student found with this Student ID");
            return -1;
        }

        PreparedStatement ps2 = con.prepareStatement("SELECT max_marks FROM exams WHERE exam_id = ?");
        ps2.setInt(1, examId);
        ResultSet rs = ps2.executeQuery();
        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "No exam found with this Exam ID");
            return -1;
        }
        return rs.getInt("max_marks");
    }

    // form validation, returns true if everything is fine
    boolean validateInput() {
        String sid = txtStudentId.getText().trim();
        String eid = txtExamId.getText().trim();
        String marks = txtMarks.getText().trim();

        if (sid.equals("") || eid.equals("") || marks.equals("")) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return false;
        }
        if (!sid.matches("[0-9]+") || !eid.matches("[0-9]+") || !marks.matches("[0-9]{1,3}")) {
            JOptionPane.showMessageDialog(this, "Student ID, Exam ID and Marks must be numbers");
            return false;
        }
        return true;
    }

    void addResult() {
        if (!validateInput()) {
            return;
        }
        try {
            int studentId = Integer.parseInt(txtStudentId.getText().trim());
            int examId = Integer.parseInt(txtExamId.getText().trim());
            int marks = Integer.parseInt(txtMarks.getText().trim());

            Connection con = DBConnection.getConnection();
            int max = getMaxMarks(con, studentId, examId);
            if (max == -1) {
                con.close();
                return;
            }
            if (marks > max) {
                JOptionPane.showMessageDialog(this, "Marks cannot be more than " + max);
                con.close();
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO results (student_id, exam_id, marks, grade) VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ps.setInt(3, marks);
            ps.setString(4, findGrade(marks, max));
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Result added successfully");
            clearFields();
            loadTable("");
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Result of this student for this exam already exists");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    void updateResult() {
        if (txtId.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please select a result from the table first");
            return;
        }
        if (!validateInput()) {
            return;
        }
        try {
            int studentId = Integer.parseInt(txtStudentId.getText().trim());
            int examId = Integer.parseInt(txtExamId.getText().trim());
            int marks = Integer.parseInt(txtMarks.getText().trim());

            Connection con = DBConnection.getConnection();
            int max = getMaxMarks(con, studentId, examId);
            if (max == -1) {
                con.close();
                return;
            }
            if (marks > max) {
                JOptionPane.showMessageDialog(this, "Marks cannot be more than " + max);
                con.close();
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                "UPDATE results SET student_id = ?, exam_id = ?, marks = ?, grade = ? WHERE result_id = ?"
            );
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ps.setInt(3, marks);
            ps.setString(4, findGrade(marks, max));
            ps.setInt(5, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Result updated successfully");
            clearFields();
            loadTable("");
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Result of this student for this exam already exists");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    void deleteResult() {
        if (txtId.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please select a result from the table first");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this result?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM results WHERE result_id = ?");
            ps.setInt(1, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Result deleted successfully");
            clearFields();
            loadTable("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnAdd) {
            addResult();
        }
        else if (e.getSource() == btnUpdate) {
            updateResult();
        }
        else if (e.getSource() == btnDelete) {
            deleteResult();
        }
        else if (e.getSource() == btnSearch) {
            loadTable(txtSearch.getText().trim());
        }
        else if (e.getSource() == btnClear) {
            clearFields();
            loadTable("");
        }
        else if (e.getSource() == btnClose) {
            dispose();
        }
    }
}
