import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentPage extends JFrame implements ActionListener {

    JTextField txtId, txtRoll, txtName, txtPhone, txtSearch;
    JComboBox<String> cmbCourse, cmbSemester;
    JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnClear, btnClose;
    JTable table;
    DefaultTableModel model;

    public StudentPage() {

        setTitle("Student Records");
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

        JLabel lblTitle = new JLabel("Student Records", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(30, 60, 120));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 0, 800, 45);
        panel.add(lblTitle);

        JLabel l1 = new JLabel("Student ID:");
        l1.setBounds(40, 65, 100, 25);
        panel.add(l1);
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBounds(140, 65, 200, 25);
        panel.add(txtId);

        JLabel l2 = new JLabel("Roll No:");
        l2.setBounds(40, 105, 100, 25);
        panel.add(l2);
        txtRoll = new JTextField();
        txtRoll.setBounds(140, 105, 200, 25);
        panel.add(txtRoll);

        JLabel l3 = new JLabel("Name:");
        l3.setBounds(40, 145, 100, 25);
        panel.add(l3);
        txtName = new JTextField();
        txtName.setBounds(140, 145, 200, 25);
        panel.add(txtName);

        JLabel l4 = new JLabel("Course:");
        l4.setBounds(420, 65, 100, 25);
        panel.add(l4);
        cmbCourse = new JComboBox<String>(new String[]{"B.Tech CSE", "B.Tech IT", "B.Tech ECE", "B.Tech ME"});
        cmbCourse.setBounds(520, 65, 200, 25);
        panel.add(cmbCourse);

        JLabel l5 = new JLabel("Semester:");
        l5.setBounds(420, 105, 100, 25);
        panel.add(l5);
        cmbSemester = new JComboBox<String>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
        cmbSemester.setBounds(520, 105, 200, 25);
        panel.add(cmbSemester);

        JLabel l6 = new JLabel("Phone:");
        l6.setBounds(420, 145, 100, 25);
        panel.add(l6);
        txtPhone = new JTextField();
        txtPhone.setBounds(520, 145, 200, 25);
        panel.add(txtPhone);

        JLabel l7 = new JLabel("Search (name / roll no):");
        l7.setBounds(40, 195, 170, 25);
        panel.add(l7);
        txtSearch = new JTextField();
        txtSearch.setBounds(210, 195, 200, 25);
        panel.add(txtSearch);

        // ---------- Table ----------
        String[] columns = {"ID", "Roll No", "Name", "Course", "Semester", "Phone"};
        model = new DefaultTableModel(columns, 0) {
            // user cannot type inside the table cells
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);   // click on a column heading to sort
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(40, 240, 700, 270);
        panel.add(sp);

        // when a row is clicked, show it in the form
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

    // fills the table, keyword "" means all records
    void loadTable(String keyword) {
        // remove the old selection first (otherwise Java prints a sorter warning)
        table.clearSelection();
        table.getSelectionModel().setAnchorSelectionIndex(-1);
        table.getSelectionModel().setLeadSelectionIndex(-1);
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM students WHERE name LIKE ? OR roll_no LIKE ? ORDER BY student_id"
            );
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("roll_no"),
                    rs.getString("name"),
                    rs.getString("course"),
                    rs.getInt("semester"),
                    rs.getString("phone")
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
        txtRoll.setText(table.getValueAt(row, 1).toString());
        txtName.setText(table.getValueAt(row, 2).toString());
        cmbCourse.setSelectedItem(table.getValueAt(row, 3).toString());
        cmbSemester.setSelectedItem(table.getValueAt(row, 4).toString());
        txtPhone.setText(table.getValueAt(row, 5).toString());
    }

    void clearFields() {
        txtId.setText("");
        txtRoll.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtSearch.setText("");
        cmbCourse.setSelectedIndex(0);
        cmbSemester.setSelectedIndex(0);
        table.clearSelection();
    }

    // form validation, returns true if everything is fine
    boolean validateInput() {
        String roll = txtRoll.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();

        if (roll.equals("") || name.equals("") || phone.equals("")) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return false;
        }
        if (!roll.matches("[A-Za-z0-9]+")) {
            JOptionPane.showMessageDialog(this, "Roll No can contain only letters and digits");
            return false;
        }
        if (!name.matches("[A-Za-z ]+")) {
            JOptionPane.showMessageDialog(this, "Name can contain only letters");
            return false;
        }
        if (!phone.matches("[0-9]{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be 10 digits");
            return false;
        }
        return true;
    }

    void addStudent() {
        if (!validateInput()) {
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO students (roll_no, name, course, semester, phone) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setString(1, txtRoll.getText().trim());
            ps.setString(2, txtName.getText().trim());
            ps.setString(3, cmbCourse.getSelectedItem().toString());
            ps.setInt(4, Integer.parseInt(cmbSemester.getSelectedItem().toString()));
            ps.setString(5, txtPhone.getText().trim());
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Student added successfully");
            clearFields();
            loadTable("");
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "This Roll No already exists");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    void updateStudent() {
        if (txtId.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table first");
            return;
        }
        if (!validateInput()) {
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE students SET roll_no = ?, name = ?, course = ?, semester = ?, phone = ? WHERE student_id = ?"
            );
            ps.setString(1, txtRoll.getText().trim());
            ps.setString(2, txtName.getText().trim());
            ps.setString(3, cmbCourse.getSelectedItem().toString());
            ps.setInt(4, Integer.parseInt(cmbSemester.getSelectedItem().toString()));
            ps.setString(5, txtPhone.getText().trim());
            ps.setInt(6, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Student updated successfully");
            clearFields();
            loadTable("");
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "This Roll No already exists");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    void deleteStudent() {
        if (txtId.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table first");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this student?\nHis/her results will also be deleted.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE student_id = ?");
            ps.setInt(1, Integer.parseInt(txtId.getText()));
            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Student deleted successfully");
            clearFields();
            loadTable("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnAdd) {
            addStudent();
        }
        else if (e.getSource() == btnUpdate) {
            updateStudent();
        }
        else if (e.getSource() == btnDelete) {
            deleteStudent();
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
