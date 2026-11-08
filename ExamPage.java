import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ExamPage extends JFrame implements ActionListener {

    JTextField txtId, txtSubject, txtDate, txtMax, txtSearch;
    JComboBox<String> cmbSemester;
    JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnClear, btnClose;
    JTable table;
    DefaultTableModel model;

    public ExamPage() {

        setTitle("Exam Records");
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

        JLabel lblTitle = new JLabel("Exam Records", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(30, 60, 120));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 0, 800, 45);
        panel.add(lblTitle);

        JLabel l1 = new JLabel("Exam ID:");
        l1.setBounds(40, 65, 100, 25);
        panel.add(l1);
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBounds(140, 65, 200, 25);
        panel.add(txtId);

        JLabel l2 = new JLabel("Subject:");
        l2.setBounds(40, 105, 100, 25);
        panel.add(l2);
        txtSubject = new JTextField();
        txtSubject.setBounds(140, 105, 200, 25);
        panel.add(txtSubject);

        JLabel l3 = new JLabel("Semester:");
        l3.setBounds(40, 145, 100, 25);
        panel.add(l3);
        cmbSemester = new JComboBox<String>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
        cmbSemester.setBounds(140, 145, 200, 25);
        panel.add(cmbSemester);

        JLabel l4 = new JLabel("Exam Date:");
        l4.setBounds(420, 65, 100, 25);
        panel.add(l4);
        txtDate = new JTextField();
        txtDate.setBounds(520, 65, 200, 25);
        panel.add(txtDate);

        JLabel l5 = new JLabel("(yyyy-mm-dd)");
        l5.setBounds(520, 90, 200, 20);
        panel.add(l5);

        JLabel l6 = new JLabel("Max Marks:");
        l6.setBounds(420, 125, 100, 25);
        panel.add(l6);
        txtMax = new JTextField();
        txtMax.setBounds(520, 125, 200, 25);
        panel.add(txtMax);

        JLabel l7 = new JLabel("Search (subject):");
        l7.setBounds(40, 195, 170, 25);
        panel.add(l7);
        txtSearch = new JTextField();
        txtSearch.setBounds(210, 195, 200, 25);
        panel.add(txtSearch);

        // ---------- Table ----------
        String[] columns = {"ID", "Subject", "Semester", "Exam Date", "Max Marks"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);
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

    void loadTable(String keyword) {
        // remove the old selection first (otherwise Java prints a sorter warning)
        table.clearSelection();
        table.getSelectionModel().setAnchorSelectionIndex(-1);
        table.getSelectionModel().setLeadSelectionIndex(-1);
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM exams WHERE subject LIKE ? ORDER BY exam_id"
            );
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("exam_id"),
                    rs.getString("subject"),
                    rs.getInt("semester"),
                    rs.getString("exam_date"),
                    rs.getInt("max_marks")
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
        txtSubject.setText(table.getValueAt(row, 1).toString());
        cmbSemester.setSelectedItem(table.getValueAt(row, 2).toString());
        txtDate.setText(table.getValueAt(row, 3).toString());
        txtMax.setText(table.getValueAt(row, 4).toString());
    }

    void clearFields() {
        txtId.setText("");
        txtSubject.setText("");
        txtDate.setText("");
        txtMax.setText("");
        txtSearch.setText("");
        cmbSemester.setSelectedIndex(0);
        table.clearSelection();
    }

    boolean validateInput() {
        String subject = txtSubject.getText().trim();
        String date = txtDate.getText().trim();
        String max = txtMax.getText().trim();

        if (subject.equals("") || date.equals("") || max.equals("")) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return false;
        }
        // check the date format
        try {
            Date.valueOf(date);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Date must be in yyyy-mm-dd format, for example 2026-12-05");
            return false;
        }
        // max marks must be a number between 1 and 100
        if (!max.matches("[0-9]{1,3}") || Integer.parseInt(max) < 1 || Integer.parseInt(max) > 100) {
            JOptionPane.showMessageDialog(this, "Max Marks must be a number between 1 and 100");
            return false;
        }
        return true;
    }

    void addExam() {
        if (!validateInput()) {
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO exams (subject, semester, exam_date, max_marks) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, txtSubject.getText().trim());
            ps.setInt(2, Integer.parseInt(cmbSemester.getSelectedItem().toString()));
            ps.setDate(3, Date.valueOf(txtDate.getText().trim()));
            ps.setInt(4, Integer.parseInt(txtMax.getText().trim()));
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Exam added successfully");
            clearFields();
            loadTable("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    void updateExam() {
        if (txtId.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please select an exam from the table first");
            return;
        }
        if (!validateInput()) {
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE exams SET subject = ?, semester = ?, exam_date = ?, max_marks = ? WHERE exam_id = ?"
            );
            ps.setString(1, txtSubject.getText().trim());
            ps.setInt(2, Integer.parseInt(cmbSemester.getSelectedItem().toString()));
            ps.setDate(3, Date.valueOf(txtDate.getText().trim()));
            ps.setInt(4, Integer.parseInt(txtMax.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Exam updated successfully");
            clearFields();
            loadTable("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    void deleteExam() {
        if (txtId.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please select an exam from the table first");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this exam?\nAll results of this exam will also be deleted.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM exams WHERE exam_id = ?");
            ps.setInt(1, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Exam deleted successfully");
            clearFields();
            loadTable("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnAdd) {
            addExam();
        }
        else if (e.getSource() == btnUpdate) {
            updateExam();
        }
        else if (e.getSource() == btnDelete) {
            deleteExam();
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
