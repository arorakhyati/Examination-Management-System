import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame implements ActionListener {

    // these remember who is logged in, other pages use them
    public static String loggedUser = "";
    public static String loggedRole = "";

    JTextField txtUsername;
    JPasswordField txtPassword;
    JButton btnLogin, btnExit;

    public LoginPage() {

        setTitle("Login - Examination Management System");
        setSize(420, 320);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel lblHeading = new JLabel("Examination Management System", JLabel.CENTER);
        lblHeading.setFont(new Font("Arial", Font.BOLD, 18));
        lblHeading.setOpaque(true);
        lblHeading.setBackground(new Color(30, 60, 120));
        lblHeading.setForeground(Color.WHITE);
        lblHeading.setBounds(0, 0, 420, 60);
        add(lblHeading);

        JLabel lblLogin = new JLabel("Please login to continue", JLabel.CENTER);
        lblLogin.setBounds(0, 70, 420, 25);
        add(lblLogin);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(60, 110, 90, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(160, 110, 190, 25);
        add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(60, 150, 90, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(160, 150, 190, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(90, 205, 100, 30);
        add(btnLogin);

        btnExit = new JButton("Exit");
        btnExit.setBounds(220, 205, 100, 30);
        add(btnExit);

        btnLogin.addActionListener(this);
        btnExit.addActionListener(this);

        // pressing Enter will also click the Login button
        getRootPane().setDefaultButton(btnLogin);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnExit) {
            System.exit(0);
        }

        if (e.getSource() == btnLogin) {
            login();
        }
    }

    // checks username and password from the database
    void login() {

        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        // form validation
        if (user.equals("") || pass.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT role FROM users WHERE username = ? AND password = ?"
            );
            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                loggedUser = user;
                loggedRole = rs.getString("role");
                con.close();
                dispose();
                new Dashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
                txtPassword.setText("");
                con.close();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
