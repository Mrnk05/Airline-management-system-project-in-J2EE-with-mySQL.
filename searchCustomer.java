package airline.managment.system;

 

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.Date;
import java.util.logging.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.toedter.calendar.JDateChooser;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class searchCustomer extends javax.swing.JInternalFrame {

    // -------------------- JDBC & State --------------------
    private Connection con;
    private PreparedStatement pst;

    private String path = null;        // chosen image path
    private byte[] userimage = null;   // image bytes to save

    private byte[] currentPhotoFromDb = null; // preserve DB photo on update

    private final DateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    // -------------------- Swing Widgets --------------------
    private javax.swing.JButton jButton1; // Browse
    private javax.swing.JButton jButton2; // Update
    private javax.swing.JButton jButton3; // Cancel
    private javax.swing.JButton jButton4; // Search by ID

    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;

    private javax.swing.JPanel jPanel1;  // form left
    private javax.swing.JPanel jPanel2;  // gender + dob + contact + actions
    private javax.swing.JPanel jPanel3;  // container

    private javax.swing.JScrollPane jScrollPane1;

    private javax.swing.JRadioButton r1; // Male
    private javax.swing.JRadioButton r2; // Female
    private ButtonGroup genderGroup;

    private javax.swing.JTextArea txtaddress;
    private javax.swing.JTextField txtcontact;
    private javax.swing.JTextField txtcustid;
    private javax.swing.JTextField txtfirstname;
    private javax.swing.JTextField txtlastname;
    private javax.swing.JTextField txtnic;
    private javax.swing.JTextField txtpassport;
    private javax.swing.JLabel txtphoto;

    private JDateChooser txtdob; // DOB chooser

    // -------------------- Styling constants --------------------
    private static final Color COL_BG_DARK = new Color(14, 16, 24);
    private static final Color COL_CARD = new Color(24, 28, 42);
    private static final Color COL_MUTED = new Color(170, 178, 189);
    private static final Color COL_ACCENT = new Color(0, 224, 170); // teal neon
    private static final Color COL_ACCENT_2 = new Color(158, 99, 232); // purple neon
    private static final Color COL_ERR = new Color(255, 85, 85);
    private static final Font  FONT_H1 = new Font("Tw Cen MT", Font.BOLD, 44);
    private static final Font  FONT_H2 = new Font("Tw Cen MT", Font.BOLD, 24);
    private static final Font  FONT_BODY = new Font("Tw Cen MT", Font.PLAIN, 18);

    // Title glow animation
    private float glowPhase = 0f;
    private javax.swing.Timer glowTimer;

    public searchCustomer() {
        initComponents();
        startTitleGlow();
    }

    // ==================== DB Helpers ====================
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost/airline?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String pass = "";
        return DriverManager.getConnection(url, user, pass);
    }

    private void closeQuietly(AutoCloseable c) {
        if (c != null) try { c.close(); } catch (Exception ignored) {}
    }

    // ==================== Image Helpers ====================
    private ImageIcon scaleToLabel(ImageIcon src, javax.swing.JLabel label) {
        if (src == null || src.getIconWidth() <= 0 || src.getIconHeight() <= 0) return null;
        int w = Math.max(1, label.getWidth());
        int h = Math.max(1, label.getHeight());
        Image myImg = src.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(myImg);
    }

    private byte[] readFileToBytes(File imageFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(imageFile);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buff = new byte[4096];
            int read;
            while ((read = fis.read(buff)) != -1) baos.write(buff, 0, read);
            return baos.toByteArray();
        }
    }

    // ==================== UI Init ====================
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setBorder(null);
        setPreferredSize(new Dimension(1176, 689));
        setBackground(COL_BG_DARK);

        // Container with gradient backdrop
        jPanel3 = new GradientPanel();
        jPanel3.setPreferredSize(new Dimension(1176, 689));
        jPanel3.setLayout(null); // we will place two main zones + keep inner panels with GroupLayout

        // Left decorative banner (logo or illustration)
        jLabel7 = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // soft neon ring
                RadialGradientPaint rgp = new RadialGradientPaint(new Point2D.Float(getWidth()*0.4f, getHeight()*0.3f),
                        Math.max(getWidth(), getHeight())/1.8f,
                        new float[]{0f, 1f}, new Color[]{new Color(0, 224, 170, 80), new Color(0,0,0,0)});
                g2.setPaint(rgp);
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        jLabel7.setOpaque(false);
        jLabel7.setBounds(20, 20, 430, 640);
        java.net.URL cdIconURL = getClass().getResource("/graphics/CDicon.png");
        if (cdIconURL != null) jLabel7.setIcon(new ImageIcon(new ImageIcon(cdIconURL).getImage().getScaledInstance(420, 420, Image.SCALE_SMOOTH)));

        // Title
        jLabel12 = new JLabel("Customer Details");
        jLabel12.setFont(FONT_H1);
        jLabel12.setForeground(COL_ACCENT);
        jLabel12.setBounds(470, 30, 600, 56);

        // ID + Search
        jLabel6 = new JLabel("Customer ID");
        jLabel6.setFont(FONT_H2);
        jLabel6.setForeground(COL_MUTED);
        jLabel6.setBounds(470, 96, 150, 32);

        txtcustid = styledTextField();
        txtcustid.setFont(new Font("Tw Cen MT", Font.BOLD, 26));
        txtcustid.setForeground(new Color(255, 99, 99));
        txtcustid.setBounds(630, 94, 170, 38);

        jButton4 = neonButton("Search", "/graphics/search.png");
        jButton4.setBounds(810, 94, 130, 38);
        jButton4.addActionListener(this::jButton4ActionPerformed);

        // Left form card
        jPanel1 = roundedCard();
        jPanel1.setBounds(470, 150, 400, 315);
        jPanel1.setLayout(new GroupLayout(jPanel1));

        jLabel1 = label("First Name");
        jLabel2 = label("Last Name");
        jLabel3 = label("NID No");
        jLabel4 = label("Passport ID");
        jLabel5 = label("Address");

        txtfirstname = styledTextField();
        txtlastname  = styledTextField();
        txtnic       = styledTextField();
        txtpassport  = styledTextField();

        txtaddress = new JTextArea(5, 20);
        txtaddress.setFont(FONT_BODY);
        txtaddress.setForeground(Color.WHITE);
        txtaddress.setBackground(new Color(33, 38, 54));
        txtaddress.setCaretColor(Color.WHITE);
        txtaddress.setBorder(new EmptyBorder(8,10,8,10));
        jScrollPane1 = new JScrollPane(txtaddress);
        jScrollPane1.setBorder(new LineBorder(new Color(48, 56, 80), 1, true));

        // Layout for jPanel1
        GroupLayout gl1 = (GroupLayout) jPanel1.getLayout();
        gl1.setAutoCreateGaps(true);
        gl1.setAutoCreateContainerGaps(true);
        gl1.setHorizontalGroup(
            gl1.createSequentialGroup()
              .addGroup(gl1.createParallelGroup(GroupLayout.Alignment.TRAILING)
                  .addComponent(jLabel1).addComponent(jLabel2).addComponent(jLabel3).addComponent(jLabel4).addComponent(jLabel5))
              .addGroup(gl1.createParallelGroup(GroupLayout.Alignment.LEADING)
                  .addComponent(txtfirstname)
                  .addComponent(txtlastname)
                  .addComponent(txtnic)
                  .addComponent(txtpassport)
                  .addComponent(jScrollPane1))
        );
        gl1.setVerticalGroup(
            gl1.createSequentialGroup()
              .addGroup(gl1.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLabel1).addComponent(txtfirstname))
              .addGroup(gl1.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLabel2).addComponent(txtlastname))
              .addGroup(gl1.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLabel3).addComponent(txtnic))
              .addGroup(gl1.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLabel4).addComponent(txtpassport))
              .addGroup(gl1.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jLabel5).addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
        );

        // Photo area + browse
        txtphoto = new JLabel("No Photo", SwingConstants.CENTER);
        txtphoto.setOpaque(true);
        txtphoto.setBackground(new Color(33, 38, 54));
        txtphoto.setForeground(COL_MUTED);
        txtphoto.setBorder(new CompoundBorder(new LineBorder(new Color(48,56,80), 1, true), new EmptyBorder(8,8,8,8)));
        txtphoto.setBounds(880, 150, 270, 270);

        jButton1 = neonButton("Browse", null);
        jButton1.setBounds(955, 430, 120, 36);
        jButton1.addActionListener(this::jButton1ActionPerformed);

        // Right-lower card: DOB + Gender + Contact + Actions
        jPanel2 = roundedCard();
        jPanel2.setBounds(470, 475, 680, 165);

        jLabel8 = label("Date of Birth");
        jLabel9 = label("Gender");
        jLabel10 = label("Contact");

        txtdob = new JDateChooser();
        txtdob.setDateFormatString("yyyy-MM-dd");
        txtdob.setFont(FONT_BODY);
        txtdob.setBorder(new LineBorder(new Color(48,56,80), 1, true));

        r1 = new JRadioButton("Male");
        r2 = new JRadioButton("Female");
        for (JRadioButton rb : new JRadioButton[]{r1, r2}) {
            rb.setForeground(Color.WHITE);
            rb.setBackground(new Color(0,0,0,0));
            rb.setFont(FONT_BODY);
            rb.setFocusPainted(false);
        }
        genderGroup = new ButtonGroup();
        genderGroup.add(r1);
        genderGroup.add(r2);

        txtcontact = styledTextField();

        jButton2 = actionButton("Update");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jButton3 = dangerButton("Cancel");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        // Layout jPanel2
        GroupLayout gl2 = new GroupLayout(jPanel2);
        jPanel2.setLayout(gl2);
        gl2.setAutoCreateGaps(true);
        gl2.setAutoCreateContainerGaps(true);
        gl2.setHorizontalGroup(
            gl2.createParallelGroup(GroupLayout.Alignment.TRAILING)
              .addGroup(gl2.createSequentialGroup()
                  .addGroup(gl2.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addComponent(jLabel8)
                      .addComponent(jLabel9)
                      .addComponent(jLabel10))
                  .addGap(18)
                  .addGroup(gl2.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                      .addComponent(txtdob, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                      .addGroup(gl2.createSequentialGroup()
                          .addComponent(r1)
                          .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                          .addComponent(r2))
                      .addComponent(txtcontact, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
        );
        gl2.setVerticalGroup(
            gl2.createSequentialGroup()
              .addGroup(gl2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel8).addComponent(txtdob, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                  .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                  .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
              .addGroup(gl2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel9).addComponent(r1).addComponent(r2))
              .addGroup(gl2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel10).addComponent(txtcontact, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
        );

        // Add to container
        jPanel3.add(jLabel7);
        jPanel3.add(jLabel12);
        jPanel3.add(jLabel6);
        jPanel3.add(txtcustid);
        jPanel3.add(jButton4);
        jPanel3.add(jPanel1);
        jPanel3.add(txtphoto);
        jPanel3.add(jButton1);
        jPanel3.add(jPanel2);

        // Put container into frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jPanel3, BorderLayout.CENTER);
        setBounds(0, 0, 1176, 689);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY.deriveFont(Font.BOLD));
        l.setForeground(COL_MUTED);
        return l;
    }

    private JTextField styledTextField() {
        JTextField f = new JTextField();
        f.setFont(FONT_BODY);
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBackground(new Color(33, 38, 54));
        f.setBorder(new CompoundBorder(new LineBorder(new Color(48,56,80), 1, true), new EmptyBorder(6,10,6,10)));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.setBorder(new CompoundBorder(new LineBorder(COL_ACCENT, 1, true), new EmptyBorder(6,10,6,10))); }
            @Override public void focusLost(FocusEvent e) { f.setBorder(new CompoundBorder(new LineBorder(new Color(48,56,80), 1, true), new EmptyBorder(6,10,6,10))); }
        });
        return f;
    }

    private JPanel roundedCard() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D r = new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),24,24);
                // subtle gradient fill
                GradientPaint gp = new GradientPaint(0,0,new Color(28, 32, 48), 0,getHeight(), new Color(22, 26, 40));
                g2.setPaint(gp);
                g2.fill(r);
                // border glow
                g2.setPaint(new GradientPaint(0,0,new Color(0,224,170,80), getWidth(),0,new Color(158,99,232,80)));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(r);
                g2.dispose();
                super.paintComponent(g);
            }
            { setOpaque(false); setLayout(new BorderLayout()); setBorder(new EmptyBorder(14,16,14,16)); }
        };
    }

    private JButton neonButton(String text, String iconPath) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY.deriveFont(Font.BOLD));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(41, 48, 66));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(new LineBorder(new Color(60, 70, 96), 1, true), new EmptyBorder(6,12,6,12)));
        if (iconPath != null) {
            java.net.URL u = getClass().getResource(iconPath);
            if (u != null) b.setIcon(new ImageIcon(u));
        }
        b.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){ b.setBackground(new Color(52, 60, 82)); }
            @Override public void mouseExited(MouseEvent e){ b.setBackground(new Color(41, 48, 66)); }
        });
        return b;
    }

    private JButton actionButton(String text) {
        JButton b = neonButton(text, null);
        b.setBackground(new Color(16, 90, 72));
        b.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){ b.setBackground(new Color(20, 110, 88)); }
            @Override public void mouseExited(MouseEvent e){ b.setBackground(new Color(16, 90, 72)); }
        });
        return b;
    }

    private JButton dangerButton(String text) {
        JButton b = neonButton(text, null);
        b.setBackground(new Color(110, 32, 32));
        b.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){ b.setBackground(new Color(140, 40, 40)); }
            @Override public void mouseExited(MouseEvent e){ b.setBackground(new Color(110, 32, 32)); }
        });
        return b;
    }

    // -------------------- Event Handlers --------------------

    private void txtlastnameActionPerformed(java.awt.event.ActionEvent evt) { /* kept for compatibility */ }
    private void txtpassportActionPerformed(java.awt.event.ActionEvent evt) { /* kept for compatibility */ }

    /** Browse Photo */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            JFileChooser picchooser = new JFileChooser();
            picchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            picchooser.setAcceptAllFileFilterUsed(true);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg");
            picchooser.setFileFilter(filter);

            int result = picchooser.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;

            File pic = picchooser.getSelectedFile();
            if (pic == null || !pic.exists()) {
                JOptionPane.showMessageDialog(this, "Invalid file selected", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            path = pic.getAbsolutePath();
            BufferedImage img = ImageIO.read(pic);
            if (img == null) {
                JOptionPane.showMessageDialog(this, "Unsupported image format", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ImageIcon imageIcon = new ImageIcon(new ImageIcon(img).getImage().getScaledInstance(txtphoto.getWidth(), txtphoto.getHeight(), Image.SCALE_SMOOTH));
            txtphoto.setText("");
            txtphoto.setIcon(imageIcon);

            userimage = readFileToBytes(pic);
        } catch (IOException ex) {
            Logger.getLogger(searchCustomer.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Failed to load image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Update Customer */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        String id = txtcustid.getText().trim();
        String firstname = txtfirstname.getText().trim();
        String lastname = txtlastname.getText().trim();
        String nic = txtnic.getText().trim();
        String passport = txtpassport.getText().trim();
        String address = txtaddress.getText().trim();

        Date dobDate = txtdob.getDate();
        String date = (dobDate != null) ? DATE_FMT.format(dobDate) : null;

        String Gender;
        if (r1.isSelected()) Gender = "Male"; else if (r2.isSelected()) Gender = "Female"; else Gender = null;

        String contact = txtcontact.getText().trim();

        if (id.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter Customer ID before updating.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
        if (firstname.isEmpty() || lastname.isEmpty()) { JOptionPane.showMessageDialog(this, "First name and Last name are required.", "Validation", JOptionPane.WARNING_MESSAGE); return; }

        Connection _con = null; PreparedStatement _pst = null;
        try {
            _con = getConnection();

            boolean updatePhoto = (userimage != null && userimage.length > 0);
            if (!updatePhoto && currentPhotoFromDb == null) {
                currentPhotoFromDb = fetchPhotoBytes(_con, id);
            }

            String sql;
            if (updatePhoto || currentPhotoFromDb != null) {
                sql = "UPDATE customer SET firstname=?, lastname=?, nic=?, passport=?, address=?, dob=?, gender=?, contact=?, photo=? WHERE id=?";
                _pst = _con.prepareStatement(sql);
                _pst.setString(1, firstname);
                _pst.setString(2, lastname);
                _pst.setString(3, nic);
                _pst.setString(4, passport);
                _pst.setString(5, address);
                _pst.setString(6, date);
                _pst.setString(7, Gender);
                _pst.setString(8, contact);
                _pst.setBytes(9, updatePhoto ? userimage : currentPhotoFromDb);
                _pst.setString(10, id);
            } else {
                sql = "UPDATE customer SET firstname=?, lastname=?, nic=?, passport=?, address=?, dob=?, gender=?, contact=? WHERE id=?";
                _pst = _con.prepareStatement(sql);
                _pst.setString(1, firstname);
                _pst.setString(2, lastname);
                _pst.setString(3, nic);
                _pst.setString(4, passport);
                _pst.setString(5, address);
                _pst.setString(6, date);
                _pst.setString(7, Gender);
                _pst.setString(8, contact);
                _pst.setString(9, id);
            }

            int count = _pst.executeUpdate();
            if (count > 0) {
                JOptionPane.showMessageDialog(this, "Registration Updated!!!");
                if (updatePhoto) currentPhotoFromDb = userimage;
            } else {
                JOptionPane.showMessageDialog(this, "No record updated. Check the Customer ID.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(searchCustomer.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeQuietly(_pst); closeQuietly(_con);
        }
    }

    /** Cancel/Close */
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) { this.hide(); }

    /** Search by ID */
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        String id = txtcustid.getText().trim();
        if (id.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter Customer ID", "Validation", JOptionPane.WARNING_MESSAGE); return; }

        Connection _con = null; PreparedStatement _pst = null; ResultSet rs = null;
        try {
            _con = getConnection();
            _pst = _con.prepareStatement("SELECT * FROM customer WHERE id = ?");
            _pst.setString(1, id);
            rs = _pst.executeQuery();

            if (!rs.next()) { JOptionPane.showMessageDialog(this, "Record Not Found"); clearForm(); return; }

            String fname = rs.getString("firstname");
            String lname = rs.getString("lastname");
            String nic = rs.getString("nic");
            String passport = rs.getString("passport");
            String address = rs.getString("address");
            String dob = rs.getString("dob");
            String gender = rs.getString("gender");
            String contact = rs.getString("contact");

            Date date1 = null;
            if (dob != null && !dob.trim().isEmpty()) {
                try { date1 = new SimpleDateFormat("yyyy-MM-dd").parse(dob); } catch (ParseException e) { /* ignore */ }
            }

            currentPhotoFromDb = null;
            ImageIcon newImage = null;
            Blob blob = rs.getBlob("photo");
            if (blob != null) {
                byte[] _imagebytes = blob.getBytes(1, (int) blob.length());
                currentPhotoFromDb = _imagebytes;
                ImageIcon image = new ImageIcon(_imagebytes);
                newImage = scaleToLabel(image, txtphoto);
            }

            txtfirstname.setText(safeTrim(fname));
            txtlastname.setText(safeTrim(lname));
            txtnic.setText(safeTrim(nic));
            txtpassport.setText(safeTrim(passport));
            txtaddress.setText(safeTrim(address));
            txtcontact.setText(safeTrim(contact));
            txtdob.setDate(date1);

            if ("Female".equalsIgnoreCase(gender)) r2.setSelected(true);
            else if ("Male".equalsIgnoreCase(gender)) r1.setSelected(true);
            else genderGroup.clearSelection();

            if (newImage != null) { txtphoto.setText(""); txtphoto.setIcon(newImage); }
            else { txtphoto.setIcon(null); txtphoto.setText("No Photo"); }

            userimage = null; // clear any new selection
        } catch (SQLException ex) {
            Logger.getLogger(searchCustomer.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeQuietly(rs); closeQuietly(_pst); closeQuietly(_con);
        }
    }

    // ==================== Helpers ====================
    private String safeTrim(String s) { return (s == null) ? "" : s.trim(); }

    private void clearForm() {
        txtfirstname.setText("");
        txtlastname.setText("");
        txtnic.setText("");
        txtpassport.setText("");
        txtaddress.setText("");
        txtcontact.setText("");
        txtdob.setDate(null);
        genderGroup.clearSelection();
        txtphoto.setIcon(null);
        txtphoto.setText("No Photo");
        userimage = null;
        currentPhotoFromDb = null;
    }

    private byte[] fetchPhotoBytes(Connection c, String id) {
        PreparedStatement s = null; ResultSet r = null;
        try {
            s = c.prepareStatement("SELECT photo FROM customer WHERE id=?");
            s.setString(1, id);
            r = s.executeQuery();
            if (r.next()) {
                Blob b = r.getBlob(1);
                if (b != null) return b.getBytes(1, (int) b.length());
            }
            return null;
        } catch (SQLException e) {
            return null;
        } finally { closeQuietly(r); closeQuietly(s); }
    }

    // ==================== Fancy Background ====================
    private class GradientPanel extends JPanel {
        GradientPanel(){ setOpaque(true); setBackground(COL_BG_DARK); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // diagonal gradient backdrop
            GradientPaint gp = new GradientPaint(0, 0, new Color(10, 12, 20), getWidth(), getHeight(), new Color(18, 20, 30));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            // neon vignette
            RadialGradientPaint rgp = new RadialGradientPaint(new Point2D.Double(getWidth()*0.85, getHeight()*0.2),
                    Math.max(getWidth(), getHeight())/1.5f,
                    new float[]{0f, 1f}, new Color[]{new Color(158, 99, 232, 70), new Color(0,0,0,0)});
            g2.setPaint(rgp);
            g2.fillRect(0,0,getWidth(),getHeight());
            g2.dispose();
        }
    }

    // ==================== Title Glow Animation ====================
    private void startTitleGlow() {
        glowTimer = new javax.swing.Timer(30, e -> {
            glowPhase += 0.03f;
            float a = (float) ((Math.sin(glowPhase) + 1) / 2.0); // 0..1
            jLabel12.setForeground(blend(COL_ACCENT, COL_ACCENT_2, a));
        });
        glowTimer.start();
        addInternalFrameListener(new InternalFrameAdapter(){
            @Override public void internalFrameClosing(InternalFrameEvent e) { if (glowTimer != null) glowTimer.stop(); }
        });
    }

    private Color blend(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl= (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r,g,bl);
    }

    // ==================== Optional Main for Quick Test ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("searchCustomer Test");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(1200, 720);
            f.setLocationRelativeTo(null);
            f.setLayout(new BorderLayout());
            searchCustomer sc = new searchCustomer();
            f.add(sc, BorderLayout.CENTER);
            sc.setVisible(true);
            f.setVisible(true);
        });
    }
}