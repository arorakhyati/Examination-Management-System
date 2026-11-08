import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ChangePasswordPage extends JFrame implements ActionListener {

    JPasswordField txtOld, txtNew, txtConfirm;
    JButton btnChange, btnClose;

    public ChangePasswordPage() {

        setTitle("Change Password");
        setSize(420, 320);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lblTitle = new JLabel("Change Password", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(30, 60, 120));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 0, 420, 50);
        add(lblTitle);

        JLabel l1 = new JLabel("Old Password:");
        l1.setBounds(40, 80, 130, 25);
        add(l1);
        txtOld = new JPasswordField();
        txtOld.setBounds(180, 80, 180, 25);
        add(txtOld);

        JLabel l2 = new JLabel("New Password:");
        l2.setBounds(40, 120, 130, 25);
        add(l2);
        txtNew = new JPasswordField();
        txtNew.setBounds(180, 120, 180, 25);
        add(txtNew);

        JLabel l3 = new JLabel("Confirm Password:");
        l3.setBounds(40, 160, 130, 25);
        add(l3);
        txtConfirm = new JPasswordField();
        txtConfirm.setBounds(180, 160, 180, 25);
        add(txtConfirm);

        btnChange = new JButton("Change");
        btnChange.setBounds(80, 215, 110, 30);
        add(btnChange);

        btnClose = new JButton("Close");
        btnClose.setBounds(220, 215, 110, 30);
        add(btnClose);

        btnChange.addActionListener(this);
        btnClose.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnClose) {
            dispose();
        }
        if (e.getSource() == btnChange) {
            changePassword();
        }
    }

    void changePassword() {

        String oldPass = new String(txtOld.getPassword());
        String newPass = new String(txtNew.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        // validation
        if (oldPass.equals("") || newPass.equals("") || confirm.equals("")) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields");
            return;
        }
        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters long");
            return;
        }
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New password and confirm password do not match");
            return;
        }
        if (newPass.equals(oldPass)) {
            JOptionPane.showMessageDialog(this, "New password must be different from the old password");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            // first check the old password
            PreparedStatement ps1 = con.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?"
            );
            ps1.setString(1, LoginPage.loggedUser);
            ps1.setString(2, oldPass);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Old password is wrong");
                con.close();
                return;
            }

            // now save the new password
            PreparedStatement ps2 = con.prepareStatement(
                "UPDATE users SET password = ? WHERE username = ?"
            );
            ps2.setString(1, newPass);
            ps2.setString(2, LoginPage.loggedUser);
            ps2.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this, "Password changed successfully");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}
