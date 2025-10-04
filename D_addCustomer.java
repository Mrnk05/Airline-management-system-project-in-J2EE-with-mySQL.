package airline.managment.system;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

public class D_addCustomer extends JInternalFrame {

    // Form fields
    private final JTextField txtFirstName = new JTextField();
    private final JTextField txtLastName = new JTextField();
    private final JTextField txtPassport = new JTextField();
    private final JTextField txtNIC = new JTextField();
    private final JTextField txtContact = new JTextField();
    private final JSpinner spDateOfBirth = new JSpinner(new SpinnerDateModel());
    private BufferedImage logoImage = null; // optional airline logo loaded by user

    // Preview components
    private final TicketPreview panelPreview = new TicketPreview();

    // Buttons
    private final JButton btnBrowseLogo = new JButton("Browse Logo");
    private final JButton btnPreview = new JButton("Preview Ticket");
    private final JButton btnDownload = new JButton("Download PDF");

    public D_addCustomer() {
        super("Add Customer / Boarding Pass", true, true, true, true); // Added super() constructor
        setSize(1100, 640);
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // Root gradient background (royal blue shades)
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = new Color(5, 37, 83); // dark royal blue
                Color c2 = new Color(10, 70, 140); // royal blue lighter
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        add(root, BorderLayout.CENTER);

        // Top header
        JLabel header = new JLabel("<html><span style='font-size:22pt;font-weight:900;color:#FFD700;'>A.N Airlines</span> &nbsp;&nbsp; " +
                "<span style='font-size:14pt;color:#E0E0E0;'>Passenger Registration — Boarding Pass</span></html>");
        header.setBorder(new EmptyBorder(8, 8, 18, 8));
        root.add(header, BorderLayout.NORTH);

        // Split pane: left form, right preview
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(540);
        split.setResizeWeight(0.5);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        // LEFT: Form panel with glass card style
        JPanel formCard = new JPanel(null);
        formCard.setBackground(new Color(255, 255, 255, 230));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 215, 0), 2, true), // golden border
                new EmptyBorder(16, 16, 16, 16)
        ));
        split.setLeftComponent(formCard);

        int ly = 16, lx = 20, labelW = 130, fieldX = lx + labelW + 12, fw = 340, rowH = 46;

        JLabel lblTitle = new JLabel("Passenger Details");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(5, 37, 83));
        lblTitle.setBounds(lx, ly, 400, 32);
        formCard.add(lblTitle);

        ly += 48;
        addFormRow(formCard, "First Name", lx, ly, labelW, txtFirstName, fieldX, fw);
        ly += rowH;
        addFormRow(formCard, "Last Name", lx, ly, labelW, txtLastName, fieldX, fw);
        ly += rowH;
        addFormRow(formCard, "Passport #", lx, ly, labelW, txtPassport, fieldX, fw);
        ly += rowH;
        addFormRow(formCard, "NID / NIC", lx, ly, labelW, txtNIC, fieldX, fw);
        ly += rowH;
        addFormRow(formCard, "Contact Number", lx, ly, labelW, txtContact, fieldX, fw);
        ly += rowH;

        // Date of Birth
        JLabel lblDob = makeLabel("Date of Birth");
        lblDob.setBounds(lx, ly + 8, labelW, 24);
        formCard.add(lblDob);
        JSpinner.DateEditor de = new JSpinner.DateEditor(spDateOfBirth, "yyyy-MM-dd");
        spDateOfBirth.setEditor(de);
        spDateOfBirth.setBounds(fieldX, ly + 2, 180, 32);
        spDateOfBirth.getEditor().getComponent(0).setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formCard.add(spDateOfBirth);
        ly += rowH;

        // Logo browse button
        btnBrowseLogo.setBounds(lx, ly, 180, 40);
        styleActionButton(btnBrowseLogo, new Color(255, 215, 0), new Color(30, 30, 30)); // golden button with dark text
        formCard.add(btnBrowseLogo);

        JLabel lblInfo = new JLabel("<html><i style='font-size:12px;color:#444;'>Optional: Airline logo for boarding pass (PNG/JPG recommended)</i></html>");
        lblInfo.setBounds(fieldX, ly + 6, 360, 30);
        formCard.add(lblInfo);

        ly += 56;

        // Preview and Download buttons
        btnPreview.setBounds(lx, ly, 180, 44);
        styleActionButton(btnPreview, new Color(0, 100, 180), Color.WHITE);
        formCard.add(btnPreview);

        btnDownload.setBounds(fieldX, ly, 180, 44);
        styleActionButton(btnDownload, new Color(0, 130, 80), Color.WHITE);
        formCard.add(btnDownload);

        // RIGHT: Preview panel
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(12, 12, 12, 12));
        split.setRightComponent(right);

        JLabel previewTitle = new JLabel("Boarding Pass Preview");
        previewTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        previewTitle.setForeground(new Color(255, 215, 0)); // golden
        previewTitle.setBorder(new EmptyBorder(6, 6, 12, 6));
        right.add(previewTitle, BorderLayout.NORTH);

        panelPreview.setPreferredSize(new Dimension(540, 230));
        JScrollPane previewScroll = new JScrollPane(panelPreview, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        previewScroll.setBorder(null);
        previewScroll.setOpaque(false);
        previewScroll.getViewport().setOpaque(false);
        right.add(previewScroll, BorderLayout.CENTER);

        // Add listeners
        btnBrowseLogo.addActionListener(e -> onBrowseLogo());
        btnPreview.addActionListener(e -> refreshPreview());
        btnDownload.addActionListener(e -> onDownloadPDF());

        // Live update on typing
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshPreview(); }
            public void removeUpdate(DocumentEvent e) { refreshPreview(); }
            public void changedUpdate(DocumentEvent e) { refreshPreview(); }
        };
        txtFirstName.getDocument().addDocumentListener(dl);
        txtLastName.getDocument().addDocumentListener(dl);
        txtPassport.getDocument().addDocumentListener(dl);
        txtNIC.getDocument().addDocumentListener(dl);
        txtContact.getDocument().addDocumentListener(dl);

        // Initial preview
        refreshPreview();
    }

    private void addFormRow(JPanel p, String label, int lx, int ly, int labelW, JTextField field, int fx, int fw) {
        JLabel lbl = makeLabel(label + ":");
        lbl.setBounds(lx, ly + 8, labelW, 24);
        p.add(lbl);
        field.setBounds(fx, ly + 4, fw, 32);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 180, 30), 2, true),
                new EmptyBorder(6, 10, 6, 10)));
        p.add(field);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(new Color(10, 30, 70));
        return l;
    }

    private void styleActionButton(JButton b, Color bg, Color fg) {
        b.setFocusPainted(false);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 2, true),
                new EmptyBorder(8, 20, 8, 20)));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground(bg.brighter());
                b.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(bg.brighter().darker(), 2, true),
                    new EmptyBorder(8, 20, 8, 20)));
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
                b.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(bg.darker(), 2, true),
                    new EmptyBorder(8, 20, 8, 20)));
            }
        });
    }

    private void onBrowseLogo() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "png", "jpg", "jpeg"));
        int ret = fc.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fc.getSelectedFile();
                BufferedImage img = ImageIO.read(f);
                logoImage = resizeToFit(img, 200, 70);
                JOptionPane.showMessageDialog(this, "Logo loaded. Click Preview Ticket to view it on boarding pass.", "Logo", JOptionPane.INFORMATION_MESSAGE);
                refreshPreview();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not load logo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private BufferedImage resizeToFit(BufferedImage src, int maxW, int maxH) {
        int w = src.getWidth(), h = src.getHeight();
        double scale = Math.min((double) maxW / w, (double) maxH / h);
        if (scale >= 1) return src;
        int nw = (int)(w * scale), nh = (int)(h * scale);
        Image tmp = src.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(tmp, 0, 0, null);
        g2.dispose();
        return resized;
    }

    private void refreshPreview() {
        String fname = txtFirstName.getText().trim();
        String lname = txtLastName.getText().trim();
        String name = (fname.isEmpty() && lname.isEmpty()) ? "Passenger Name" : (fname + " " + lname).trim();
        String passport = txtPassport.getText().trim();
        String nid = txtNIC.getText().trim();
        String contact = txtContact.getText().trim();

        String ticketId = "TCK" + System.currentTimeMillis() % 100000;
        String flight = "AN" + (100 + (int)(Math.random()*900));
        String seat = (char)('A' + (int)(Math.random()*6)) + String.valueOf(1 + (int)(Math.random()*30));
        String gate = "G" + (1 + (int)(Math.random()*9));
        String boardingTime = computeBoardingInMinutes(45);
        String dob = new SimpleDateFormat("yyyy-MM-dd").format((Date) spDateOfBirth.getValue());

        String qrPayload = String.format("T:%s|N:%s|P:%s|F:%s|S:%s|G:%s", ticketId, name, passport, flight, seat, gate);

        BufferedImage qr = generateQRImage(qrPayload, 140, 140);

        panelPreview.setTicketData(logoImage, name, passport, nid, contact, dob, ticketId, flight, seat, gate, boardingTime, qr);
        panelPreview.repaint();
    }

    private String computeBoardingInMinutes(int minsFromNow) {
        long now = System.currentTimeMillis();
        Date d = new Date(now + minsFromNow * 60L * 1000L);
        return new SimpleDateFormat("HH:mm").format(d);
    }

    private BufferedImage generateQRImage(String text, int w, int h) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, w, h);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException ex) {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, w, h);
            g.dispose();
            return img;
        }
    }

    private void onDownloadPDF() {
        if (txtFirstName.getText().trim().isEmpty() && txtLastName.getText().trim().isEmpty()) {
            int ok = JOptionPane.showConfirmDialog(this, "No passenger name entered. Continue anyway?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
        }

        refreshPreview();

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("boarding_pass.pdf"));
        int ret = fc.showSaveDialog(this);
        if (ret != JFileChooser.APPROVE_OPTION) return;
        File outFile = fc.getSelectedFile();

        float pdfW = 700f;
        float pdfH = 220f;

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(pdfW, pdfH));
            doc.addPage(page);

            BufferedImage img = panelPreview.createTicketImage((int) pdfW, (int) pdfH);

            PDImageXObject pdImage = LosslessFactory.createFromImage(doc, img);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.drawImage(pdImage, 0, 0, pdfW, pdfH);
            }

            doc.save(outFile);
            JOptionPane.showMessageDialog(this, "PDF saved: " + outFile.getAbsolutePath(), "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ***** This method was removed as it caused an error *****
    // private void setLocationRelativeTo(Object object) {
    //    throw new UnsupportedOperationException("Not supported yet.");
    // }

    private static class TicketPreview extends JPanel {
        private BufferedImage logo;
        private String name = "Passenger Name";
        private String passport = "Passport#";
        private String nid = "";
        private String contact = "";
        private String dob = "";
        private String ticketId = "TCK000";
        private String flight = "AN101";
        private String seat = "A1";
        private String gate = "G1";
        private String boardingTime = "00:00";
        private BufferedImage qr;

        TicketPreview() {
            setPreferredSize(new Dimension(680, 240));
            setOpaque(false);
        }

        void setTicketData(BufferedImage logo, String name, String passport, String nid, String contact, String dob,
                           String ticketId, String flight, String seat, String gate, String boardingTime, BufferedImage qr) {
            this.logo = logo;
            this.name = name;
            this.passport = passport;
            this.nid = nid;
            this.contact = contact;
            this.dob = dob;
            this.ticketId = ticketId;
            this.flight = flight;
            this.seat = seat;
            this.gate = gate;
            this.boardingTime = boardingTime;
            this.qr = qr;
        }

      @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            int arc = 28;
            Color bg1 = new Color(0, 50, 100);
            Color bg2 = new Color(0, 85, 155);
            GradientPaint gp = new GradientPaint(0, 0, bg1, 0, h, bg2);
            g2.setPaint(gp);
            g2.fillRoundRect(12, 12, w - 24, h - 24, arc, arc);

            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(12, 12, w - 24, h - 24, arc, arc);

            if (logo != null) {
                g2.drawImage(logo, 24, 24, null);
            } else {
                g2.setFont(new Font("Segoe UI Black", Font.BOLD, 30));
                g2.setColor(new Color(255, 215, 0));
                g2.drawString("A.N Airlines", 24, 70);
            }

            int xText = 260, yText = 40;
            int lineHeight = 28;

            g2.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
            g2.setColor(Color.WHITE);
            g2.drawString("Name: " + name, xText, yText);
            g2.drawString("Passport: " + passport, xText, yText + lineHeight);
            g2.drawString("NID / NIC: " + nid, xText, yText + lineHeight * 2);
            g2.drawString("Contact: " + contact, xText, yText + lineHeight * 3);
            g2.drawString("DOB: " + dob, xText, yText + lineHeight * 4);

            g2.setFont(new Font("Segoe UI Black", Font.BOLD, 18));
            g2.drawString("Ticket ID: " + ticketId, xText, yText + lineHeight * 6);

            g2.setFont(new Font("Segoe UI Bold", Font.PLAIN, 22));
            g2.drawString("Flight: " + flight, xText, yText + lineHeight * 8);
            g2.drawString("Seat: " + seat, xText + 220, yText + lineHeight * 8);
            g2.drawString("Gate: " + gate, xText, yText + lineHeight * 10);
            g2.drawString("Boarding: " + boardingTime, xText + 220, yText + lineHeight * 10);

            if (qr != null) {
                int qrSize = 140;
                int qrX = w - qrSize - 40;
                int qrY = h - qrSize - 40;
                g2.drawImage(qr, qrX, qrY, qrSize, qrSize, null);
            }

            g2.setFont(new Font("Segoe UI Italic", Font.PLAIN, 11));
            g2.setColor(new Color(255, 255, 255, 150));
            g2.drawString("Generated by A.N Airlines System", 18, h - 18);
            g2.dispose();
        }


        BufferedImage createTicketImage(int width, int height) {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            setSize(width, height);
            paintComponent(g2);
            g2.dispose();
            return img;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) {
            }
            // To test this single file, you would run it in a JFrame like this
            JFrame frame = new JFrame();
            D_addCustomer addCustomerPanel = new D_addCustomer();
            frame.add(addCustomerPanel);
            frame.setSize(1100, 640);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            addCustomerPanel.setVisible(true);
            frame.setVisible(true);
        });
    }
}