import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Dashboard extends JFrame implements ActionListener {

    JLabel lblStudents, lblExams, lblResults;

    public Dashboard() {

        setTitle("Dashboard - Examination Management System");
        setSize(700, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ---------- Menu Bar ----------
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuFile.add(makeItem("Change Password"));
        menuFile.add(makeItem("Logout"));
        menuFile.add(makeItem("Exit"));

        JMenu menuManage = new JMenu("Manage");
        menuManage.add(makeItem("Students"));
        menuManage.add(makeItem("Exams"));
        menuManage.add(makeItem("Results"));

        JMenu menuHelp = new JMenu("Help");
        menuHelp.add(makeItem("About"));

        menuBar.add(menuFile);
        menuBar.add(menuManage);
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);

        // ---------- Tool Bar ----------
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(makeButton("Students"));
        toolBar.add(makeButton("Exams"));
        toolBar.add(makeButton("Results"));
        toolBar.addSeparator();
        toolBar.add(makeButton("Change Password"));
        toolBar.add(makeButton("Logout"));
        add(toolBar, BorderLayout.NORTH);

        // ---------- Main Panel ----------
        JPanel panel = new JPanel(null);
        add(panel, BorderLayout.CENTER);

        JLabel lblHeading = new JLabel("Examination Management System", JLabel.CENTER);
        lblHeading.setFont(new Font("Arial", Font.BOLD, 22));
        lblHeading.setOpaque(true);
        lblHeading.setBackground(new Color(30, 60, 120));
        lblHeading.setForeground(Color.WHITE);
        lblHeading.setBounds(0, 0, 700, 60);
        panel.add(lblHeading);

        JLabel lblWelcome = new JLabel(
            "Welcome, " + LoginPage.loggedUser + "  (Role: " + LoginPage.loggedRole + ")", JLabel.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        lblWelcome.setBounds(0, 70, 700, 30);
        panel.add(lblWelcome);

        // three boxes showing the totals
        lblStudents = makeBox(40, new Color(52, 152, 219));
        lblExams = makeBox(250, new Color(39, 174, 96));
        lblResults = makeBox(460, new Color(230, 126, 34));
        panel.add(lblStudents);
        panel.add(lblExams);
        panel.add(lblResults);

        // big buttons to open the modules
        JButton btnStudents = makeButton("Manage Students");
        btnStudents.setBounds(40, 260, 190, 45);
        panel.add(btnStudents);

        JButton btnExams = makeButton("Manage Exams");
        btnExams.setBounds(250, 260, 190, 45);
        panel.add(btnExams);

        JButton btnResults = makeButton("Manage Results");
        btnResults.setBounds(460, 260, 190, 45);
        panel.add(btnResults);

        JButton btnPass = makeButton("Change Password");
        btnPass.setBounds(145, 325, 190, 40);
        panel.add(btnPass);

        JButton btnLogout = makeButton("Logout");
        btnLogout.setBounds(355, 325, 190, 40);
        panel.add(btnLogout);

        // refresh the totals every time this window comes in front
        addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                loadCounts();
            }
        });

        loadCounts();
        setVisible(true);
    }

    JMenuItem makeItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(this);
        return item;
    }

    JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.addActionListener(this);
        return btn;
    }

    JLabel makeBox(int x, Color color) {
        JLabel box = new JLabel("", JLabel.CENTER);
        box.setFont(new Font("Arial", Font.BOLD, 16));
        box.setOpaque(true);
        box.setBackground(color);
        box.setForeground(Color.WHITE);
        box.setBounds(x, 130, 190, 90);
        return box;
    }

    // counts the rows of a table
    int getCount(String tableName) {
        int count = 0;
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tableName);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            con.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
        return count;
    }

    void loadCounts() {
        lblStudents.setText("Students: " + getCount("students"));
        lblExams.setText("Exams: " + getCount("exams"));
        lblResults.setText("Results: " + getCount("results"));
    }

    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand();

        if (cmd.equals("Students") || cmd.equals("Manage Students")) {
            new StudentPage();
        }
        else if (cmd.equals("Exams") || cmd.equals("Manage Exams")) {
            new ExamPage();
        }
        else if (cmd.equals("Results") || cmd.equals("Manage Results")) {
            new ResultPage();
        }
        else if (cmd.equals("Change Password")) {
            new ChangePasswordPage();
        }
        else if (cmd.equals("Logout")) {
            int choice = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                // close every open window and go back to login
                for (Window w : Window.getWindows()) {
                    w.dispose();
                }
                LoginPage.loggedUser = "";
                LoginPage.loggedRole = "";
                new LoginPage();
            }
        }
        else if (cmd.equals("Exit")) {
            System.exit(0);
        }
        else if (cmd.equals("About")) {
            JOptionPane.showMessageDialog(this,
                "Examination Management System\nMinor Project NSP\nDeveloped by Khyati Arora (PIET25CS079)");
        }
    }
}
