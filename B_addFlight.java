package airline.managment.system;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class B_addFlight extends JInternalFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/an_airlines";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private JTextField txtFlightId, txtFlightName, txtSource, txtDepart, txtDate, txtDepTime, txtArrTime, txtCharge;
    private JButton btnAdd, btnClear, btnClose;

    public B_addFlight() {
        super("Add Flight", true, true, true, true);
        setSize(650, 550); // Increased height for new fields
        setFrameIcon(new ImageIcon("src/assets/airplane.png"));
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitle = new JLabel("Add New Flight");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(new EmptyBorder(5, 5, 5, 5));
        header.add(lblTitle, BorderLayout.WEST);

        JPanel colorStrip = new JPanel();
        colorStrip.setPreferredSize(new Dimension(6, 50));
        colorStrip.setBackground(new Color(0, 102, 204));
        header.add(colorStrip, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        // FORM
        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(Color.WHITE);

        int xLabel = 30, xField = 180, y = 20, height = 35, gap = 45;

        formPanel.add(createLabel("Flight ID:", xLabel, y));
        txtFlightId = createTextField(xField, y, 300, height);
        formPanel.add(txtFlightId);

        y += gap;
        formPanel.add(createLabel("Flight Name:", xLabel, y));
        txtFlightName = createTextField(xField, y, 300, height);
        formPanel.add(txtFlightName);

        y += gap;
        formPanel.add(createLabel("Source:", xLabel, y));
        txtSource = createTextField(xField, y, 300, height);
        formPanel.add(txtSource);

        y += gap;
        formPanel.add(createLabel("Destination:", xLabel, y));
        txtDepart = createTextField(xField, y, 300, height);
        formPanel.add(txtDepart);

        y += gap;
        formPanel.add(createLabel("Date (YYYY-MM-DD):", xLabel, y));
        txtDate = createTextField(xField, y, 300, height);
        formPanel.add(txtDate);

        y += gap;
        formPanel.add(createLabel("Dep Time (HH:MM):", xLabel, y));
        txtDepTime = createTextField(xField, y, 300, height);
        formPanel.add(txtDepTime);

        y += gap;
        formPanel.add(createLabel("Arr Time (HH:MM):", xLabel, y));
        txtArrTime = createTextField(xField, y, 300, height);
        formPanel.add(txtArrTime);

        y += gap;
        formPanel.add(createLabel("Charge:", xLabel, y));
        txtCharge = createTextField(xField, y, 300, height);
        formPanel.add(txtCharge);

        // BUTTONS
        y += gap + 10;
        btnAdd = createButton("Add Flight", new Color(0, 102, 204), Color.WHITE);
        btnAdd.setBounds(80, y, 140, 40);
        formPanel.add(btnAdd);

        btnClear = createButton("Clear", new Color(230, 230, 230), new Color(0, 0, 0));
        btnClear.setBounds(240, y, 120, 40);
        formPanel.add(btnClear);

        btnClose = createButton("Close", new Color(204, 0, 0), Color.WHITE);
        btnClose.setBounds(370, y, 120, 40);
        formPanel.add(btnClose);

        btnAdd.addActionListener(e -> addFlightToDB());
        btnClear.addActionListener(e -> clearForm());
        btnClose.addActionListener(e -> { try { setClosed(true); } catch (Exception ignored) {} });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setBounds(x, y, 150, 30);
        return label;
    }

    private JTextField createTextField(int x, int y, int w, int h) {
        JTextField field = new JTextField();
        field.setBounds(x, y, w, h);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        return field;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(new EmptyBorder(5, 15, 5, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new LineBorder(bg.darker(), 1, true));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });

        return btn;
    }

    private void clearForm() {
        txtFlightId.setText("");
        txtFlightName.setText("");
        txtSource.setText("");
        txtDepart.setText("");
        txtDate.setText("");
        txtDepTime.setText("");
        txtArrTime.setText("");
        txtCharge.setText("");
    }

    private void addFlightToDB() {
        String id = txtFlightId.getText().trim();
        String name = txtFlightName.getText().trim();
        String source = txtSource.getText().trim();
        String depart = txtDepart.getText().trim();
        String date = txtDate.getText().trim();
        String depTime = txtDepTime.getText().trim();
        String arrTime = txtArrTime.getText().trim();
        String charge = txtCharge.getText().trim();

        if (id.isEmpty() || name.isEmpty() || source.isEmpty() || depart.isEmpty() || date.isEmpty() || depTime.isEmpty() || arrTime.isEmpty() || charge.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "INSERT INTO flight (id, flightname, source, depart, date, deptime, arrtime, flightcharge) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, id);
            pst.setString(2, name);
            pst.setString(3, source);
            pst.setString(4, depart);
            pst.setString(5, date);
            pst.setString(6, depTime);
            pst.setString(7, arrTime);
            pst.setString(8, charge);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Flight added successfully.");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add flight.");
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Flight ID already exists.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && getDesktopPane() != null) {
            Dimension desktopSize = getDesktopPane().getSize();
            Dimension jInternalFrameSize = this.getSize();
            setLocation((desktopSize.width - jInternalFrameSize.width) / 2,
                        (desktopSize.height - jInternalFrameSize.height) / 2);
        }
    }
}