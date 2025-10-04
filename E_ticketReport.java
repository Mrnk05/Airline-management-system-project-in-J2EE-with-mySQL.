package airline.managment.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class E_ticketReport extends javax.swing.JInternalFrame {

    Connection con;
    PreparedStatement pst;

    // UI Components (Re-declared for clarity in this version)
    private javax.swing.JButton jButton1; // Close/Refresh button
    private javax.swing.JButton jButton2; // Search button
    private javax.swing.JLabel jLabel1; // Image icon
    private javax.swing.JLabel jLabel2; // Search Label
    private javax.swing.JTextField jTextField1; // Search Text Field
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2; // New Panel for Search/Filter
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;

    public E_ticketReport() {
        // Set the title for the internal frame
        super("E-Ticket Report", true, true, true, true);
        
        initComponents();
        
        // Initial data load when the window opens
        LoadData();
        
        // Custom styling methods
        customizeTable();
        customizeTableHeader();
        customizeButton();
    }

    // --- Component Initialization ---
    @SuppressWarnings("unchecked")
    private void initComponents() {
        
        // Main Panel with Gradient Background
        jPanel1 = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(16, 77, 106); // Dark Blue
                Color color2 = new Color(110, 156, 190); // Light Blue
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new JButton("Close"); // Changed text from OK to Close
        jLabel1 = new JLabel();
        
        // New Components for Search Functionality
        jPanel2 = new JPanel();
        jLabel2 = new JLabel("Customer ID:");
        jTextField1 = new JTextField();
        jButton2 = new JButton("Search");

        // --- Frame and Panel Setup ---
        setPreferredSize(new Dimension(1176, 689));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // Remove default internal frame border

        jPanel1.setBorder(BorderFactory.createTitledBorder(null, "Ticket Report",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Century Gothic", Font.BOLD, 36), new Color(255, 255, 255)));

        // --- Table Styling and Model ---
        jTable1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        jTable1.setForeground(new Color(240, 240, 240));
        jTable1.setBackground(new Color(30, 45, 60));
        jTable1.setRowHeight(32); // Slightly larger row height
        jTable1.setSelectionBackground(new Color(60, 120, 170));
        jTable1.setSelectionForeground(Color.white);
        jTable1.setGridColor(new Color(70, 100, 140));
        jTable1.setShowHorizontalLines(true);
        jTable1.setShowVerticalLines(false);
        jTable1.setAutoCreateRowSorter(true);
        jTable1.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 17));
        jTable1.getTableHeader().setBackground(new Color(16, 77, 106));
        jTable1.getTableHeader().setForeground(Color.white);
        jTable1.getTableHeader().setReorderingAllowed(false);
        
        jTable1.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Ticket No", "Flight No", "Customer ID", "Class", "Price", "Seats", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false,false,false,false,false,false,false
            };
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        // --- Close Button Styling ---
        jButton1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jButton1.setForeground(Color.white);
        jButton1.setBackground(new Color(190, 50, 50)); // Red for Close
        jButton1.setFocusPainted(false);
        jButton1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jButton1.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        
        // --- Search Panel Setup (jPanel2) ---
        jPanel2.setBackground(new Color(20, 90, 130, 150)); // Semi-transparent color
        jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        
        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jLabel2.setForeground(Color.white);
        
        jTextField1.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        jTextField1.setPreferredSize(new Dimension(200, 40));
        
        jButton2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jButton2.setForeground(Color.white);
        jButton2.setBackground(new Color(40, 150, 90)); // Green for Search
        jButton2.setFocusPainted(false);
        jButton2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jButton2.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt); // Call the new Search method
            }
        });
        
        // Assemble Search Panel
        jPanel2.add(jLabel2);
        jPanel2.add(jTextField1);
        jPanel2.add(jButton2);
        
        // --- Image Icon ---
        ImageIcon icon = new ImageIcon(getClass().getResource("/graphics/TRicon.png"));
        Image img = icon.getImage().getScaledInstance(320, 320, Image.SCALE_SMOOTH);
        jLabel1.setIcon(new ImageIcon(img));
        
        // --- Group Layout for jPanel1 (Main Panel) ---
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 420, GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        
        // --- Main Content Pane Layout ---
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    // --- Customization Methods ---
    
    private void customizeTable() {
        // Custom renderer for alternating row colors and centered content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    // Cleaner alternating colors
                    setBackground(row % 2 == 0 ? new Color(30, 60, 90) : new Color(16, 77, 106));
                }
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        });
    }

    private void customizeTableHeader() {
         // Custom renderer to center the table header text
        jTable1.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                return this;
            }
        });
    }


    private void customizeButton() {
        // Mouse hover effects for the Close button
        jButton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                jButton1.setBackground(new Color(220, 80, 80));
                jButton1.setForeground(Color.white);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                jButton1.setBackground(new Color(190, 50, 50));
                jButton1.setForeground(Color.white);
            }
        });
        
        // Mouse hover effects for the Search button
         jButton2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                jButton2.setBackground(new Color(60, 180, 110));
                jButton2.setForeground(Color.white);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                jButton2.setBackground(new Color(40, 150, 90));
                jButton2.setForeground(Color.white);
            }
        });
    }

    // --- Action Handlers ---
    
    // Close button action
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        this.hide();
    }
    
    // New Search button action
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        String custid = jTextField1.getText().trim();
        if (custid.isEmpty()) {
            LoadData(); // If search box is empty, load all data
        } else {
            SearchData(custid); // Search for specific customer ID
        }
    }

    // --- Database Methods ---
    
    // Method to establish connection (for reuse)
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/an_airlines", "root", "");
    }

    // Method to load ALL data
    public void LoadData() {
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT * from ticket");
            
            // Revert search field
            jTextField1.setText(""); 
            
            executeAndDisplayQuery(pst);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(E_ticketReport.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error: MySQL JDBC Driver not found. Please check your libraries.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            Logger.getLogger(E_ticketReport.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Database connection failed. Ensure MySQL server is running and database 'an_airlines' exists.\nError: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeConnection(con, pst, null);
        }
    }
    
    // Method to load SEARCHED data
    public void SearchData(String custid) {
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT * from ticket WHERE custid = ?");
            pst.setString(1, custid);

            executeAndDisplayQuery(pst);
            
            if (jTable1.getRowCount() == 0) {
                 JOptionPane.showMessageDialog(this, "No tickets found for Customer ID: " + custid, "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(E_ticketReport.class.getName()).log(Level.SEVERE, null, ex);
             JOptionPane.showMessageDialog(this, "Error: MySQL JDBC Driver not found. Please check your libraries.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            Logger.getLogger(E_ticketReport.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "SQL Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeConnection(con, pst, null);
        }
    }
    
    // Reusable method to execute query and populate table
    private void executeAndDisplayQuery(PreparedStatement preparedStatement) throws SQLException {
        ResultSet rs = preparedStatement.executeQuery();

        ResultSetMetaData rsm = rs.getMetaData();
        int c = rsm.getColumnCount();

        DefaultTableModel Df = (DefaultTableModel) jTable1.getModel();
        Df.setRowCount(0); // Clear existing rows

        while (rs.next()) {
            Vector<String> v2 = new Vector<>();
            v2.add(rs.getString("id"));
            v2.add(rs.getString("flightid"));
            v2.add(rs.getString("custid"));
            v2.add(rs.getString("class"));
            v2.add(rs.getString("price"));
            v2.add(rs.getString("seats"));
            v2.add(rs.getString("date"));
            Df.addRow(v2);
        }
    }
    
    // Utility to close resources safely
    private void closeConnection(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            Logger.getLogger(E_ticketReport.class.getName()).log(Level.WARNING, "Error closing resources", e);
        }
    }
}