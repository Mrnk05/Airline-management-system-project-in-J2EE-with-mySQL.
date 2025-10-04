    package airline.managment.system;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

    public class A_Login extends JFrame {

    private JPanel contentPane, leftPanel, rightPanel;
    private CardLayout cardLayout;
    private JTextField txtEmailLogin, txtEmailRegister, txtNameRegister;
    private JPasswordField txtPasswordLogin, txtPasswordRegister;
    private JButton btnLogin, btnRegister, btnSwitchToRegister, btnSwitchToLogin;

    public A_Login() {
        setTitle("A.N Airlines - Login & Registration");
        setSize(900, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // LEFT PANEL - Airline Branding
        leftPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 153, 255),
                        0, getHeight(), new Color(224, 255, 255)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(350, 550));
        leftPanel.setLayout(null);

        JLabel lblLogo = new JLabel(new ImageIcon("src/assets/airplane.png"));
        lblLogo.setBounds(90, 80, 170, 170);
        leftPanel.add(lblLogo);

        JLabel lblTitle = new JLabel("A.N Airlines");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(70, 260, 200, 40);
        leftPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Fly with Comfort & Luxury");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblSubtitle.setBounds(50, 300, 250, 20);
        leftPanel.add(lblSubtitle);

        contentPane.add(leftPanel, BorderLayout.WEST);

        // RIGHT PANEL - Forms
        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);
        rightPanel.setBackground(Color.WHITE);

        // Login Panel
        JPanel loginPanel = createLoginPanel();

        // Registration Panel
        JPanel registerPanel = createRegisterPanel();

        rightPanel.add(loginPanel, "login");
        rightPanel.add(registerPanel, "register");

        contentPane.add(rightPanel, BorderLayout.CENTER);

        // Listeners for switching
        btnSwitchToRegister.addActionListener(e -> cardLayout.show(rightPanel, "register"));
        btnSwitchToLogin.addActionListener(e -> cardLayout.show(rightPanel, "login"));

        btnLogin.addActionListener(e -> loginUser());
        btnRegister.addActionListener(e -> registerUser());
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(null);

        JLabel lblLoginTitle = new JLabel("Login");
        lblLoginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLoginTitle.setBounds(50, 40, 200, 30);
        loginPanel.add(lblLoginTitle);

        JLabel lblEmailLogin = new JLabel("Gmail:");
        lblEmailLogin.setBounds(50, 100, 100, 25);
        loginPanel.add(lblEmailLogin);

        txtEmailLogin = createTextField();
        txtEmailLogin.setBounds(50, 130, 250, 35);
        loginPanel.add(txtEmailLogin);

        JLabel lblPassLogin = new JLabel("Password:");
        lblPassLogin.setBounds(50, 180, 100, 25);
        loginPanel.add(lblPassLogin);

        txtPasswordLogin = createPasswordField();
        txtPasswordLogin.setBounds(50, 210, 250, 35);
        loginPanel.add(txtPasswordLogin);

        btnLogin = createButton("Login");
        btnLogin.setBounds(50, 270, 250, 40);
        loginPanel.add(btnLogin);

        btnSwitchToRegister = new JButton("Don't have an account? Register");
        btnSwitchToRegister.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSwitchToRegister.setForeground(Color.BLUE);
        btnSwitchToRegister.setContentAreaFilled(false);
        btnSwitchToRegister.setBorderPainted(false);
        btnSwitchToRegister.setBounds(50, 320, 250, 30);
        loginPanel.add(btnSwitchToRegister);

        return loginPanel;
    }

    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel();
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setLayout(null);

        JLabel lblRegTitle = new JLabel("Register");
        lblRegTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblRegTitle.setBounds(50, 40, 200, 30);
        registerPanel.add(lblRegTitle);

        JLabel lblNameReg = new JLabel("Full Name:");
        lblNameReg.setBounds(50, 100, 100, 25);
        registerPanel.add(lblNameReg);

        txtNameRegister = createTextField();
        txtNameRegister.setBounds(50, 130, 250, 35);
        registerPanel.add(txtNameRegister);

        JLabel lblEmailReg = new JLabel("Gmail:");
        lblEmailReg.setBounds(50, 180, 100, 25);
        registerPanel.add(lblEmailReg);

        txtEmailRegister = createTextField();
        txtEmailRegister.setBounds(50, 210, 250, 35);
        registerPanel.add(txtEmailRegister);

        JLabel lblPassReg = new JLabel("Password:");
        lblPassReg.setBounds(50, 260, 100, 25);
        registerPanel.add(lblPassReg);

        txtPasswordRegister = createPasswordField();
        txtPasswordRegister.setBounds(50, 290, 250, 35);
        registerPanel.add(txtPasswordRegister);

        btnRegister = createButton("Register");
        btnRegister.setBounds(50, 350, 250, 40);
        registerPanel.add(btnRegister);

        btnSwitchToLogin = new JButton("Already have an account? Login");
        btnSwitchToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSwitchToLogin.setForeground(Color.BLUE);
        btnSwitchToLogin.setContentAreaFilled(false);
        btnSwitchToLogin.setBorderPainted(false);
        btnSwitchToLogin.setBounds(50, 400, 250, 30);
        registerPanel.add(btnSwitchToLogin);

        return registerPanel;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    private JPasswordField createPasswordField() {
        JPasswordField txt = new JPasswordField();
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(0, 153, 255));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0, 123, 255));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(0, 153, 255));
            }
        });
        return btn;
    }

    // LOGIN
    private void loginUser() {
        String email = txtEmailLogin.getText();
        String password = String.valueOf(txtPasswordLogin.getPassword());

        if (!email.endsWith("@gmail.com")) {
            JOptionPane.showMessageDialog(this, "Only Gmail addresses are allowed.");
            return;
        }

        try {
            Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/an_airlines", "root", ""
);
            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM users WHERE email=? AND password=?"
            );
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                this.dispose();
                new Main().setVisible(true); // Open dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.");
            }
            con.close();
        } catch (HeadlessException | SQLException ex) {
        }
    }

    // REGISTER
    private void registerUser() {
        String name = txtNameRegister.getText();
        String email = txtEmailRegister.getText();
        String password = String.valueOf(txtPasswordRegister.getPassword());

        if (!email.endsWith("@gmail.com")) {
            JOptionPane.showMessageDialog(this, "Only Gmail addresses are allowed.");
            return;
        }

        try {
            Connection con = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/an_airlines", "root", ""
);
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO users(name, email, password) VALUES (?, ?, ?)"
            );
            pst.setString(1, name);
            pst.setString(2, email);
            pst.setString(3, password);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                cardLayout.show(rightPanel, "login");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register.");
            }
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new A_Login().setVisible(true));
    }
}
