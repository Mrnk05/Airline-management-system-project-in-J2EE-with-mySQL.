package airline.managment.system;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Date;

/**
 * C_bookTicket.java - Final polished + gradient-compatible.
 * DB: set to an_airline (user root / blank password).
 */
public class C_bookTicket extends JInternalFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/an_airline";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private JComboBox<String> cmbSource;
    private JComboBox<String> cmbDestination;
    private JButton btnSearchFlights;
    private JTable tblFlights;
    private DefaultTableModel flightsTableModel;
    private JLabel lblTicketNo;
    private JTextField txtCustomerId;
    private JButton btnSearchCustomer;
    private JLabel lblCustFirst, lblCustLast, lblPassport;

    private JLabel lblFlightNo, lblFlightName, lblDeptTime;
    private JComboBox<String> cmbClass;
    private JTextField txtPrice;
    private JSpinner spSeats;
    private JLabel lblTotal;
    private JSpinner dateSpinner;
    private JButton btnBook, btnCancel;

    private TicketPreviewPanel previewPanel;

    private Connection con;
    private PreparedStatement pst;

    private final Color ROYAL_BLUE = new Color(10, 93, 156);
    private final Color GOLD = new Color(247, 190, 61);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public C_bookTicket() {
        super("Book Ticket", true, true, true, true);
        setSize(1150, 680);
        setLayout(new BorderLayout());
        initUI();
        autoTicketID();
        loadSourceDestinationOptions();
    }

    private void initUI() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(620);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setResizeWeight(0.6);

        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(12, 12, 12, 12));

        left.add(createSearchPanel(), BorderLayout.NORTH);
        left.add(createFlightsTablePanel(), BorderLayout.CENTER);
        left.add(createTicketInfoPanel(), BorderLayout.SOUTH);

        JPanel right = new JPanel(null);
        right.setPreferredSize(new Dimension(520, 680));
        right.setOpaque(false);

        JPanel bookingCard = new JPanel(null);
        bookingCard.setBounds(18, 18, 485, 420);
        bookingCard.setBorder(new LineBorder(new Color(230,230,230),1,true));
        right.add(bookingCard);

        JLabel bkTitle = new JLabel("Book Ticket");
        bkTitle.setFont(TITLE_FONT);
        bkTitle.setForeground(ROYAL_BLUE);
        bkTitle.setBounds(20, 14, 200, 30);
        bookingCard.add(bkTitle);

        int ybase = 60;
        lblFlightNo = labeledValue(bookingCard, "Flight No", 20, ybase);
        lblFlightName = labeledValue(bookingCard, "Flight", 20, ybase + 50);
        lblDeptTime = labeledValue(bookingCard, "Dep Time", 20, ybase + 100);

        JLabel lblClass = new JLabel("Class:");
        lblClass.setFont(FIELD_FONT);
        lblClass.setBounds(20, ybase + 160, 80, 24);
        bookingCard.add(lblClass);

        cmbClass = new JComboBox<>(new String[]{"Economy", "Business"});
        cmbClass.setFont(FIELD_FONT);
        cmbClass.setBounds(110, ybase + 160, 140, 28);
        bookingCard.add(cmbClass);

        JLabel lblSeats = new JLabel("Seats:");
        lblSeats.setFont(FIELD_FONT);
        lblSeats.setBounds(270, ybase + 160, 50, 24);
        bookingCard.add(lblSeats);

        spSeats = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spSeats.setBounds(320, ybase + 160, 60, 28);
        spSeats.setFont(FIELD_FONT);
        bookingCard.add(spSeats);

        JLabel lblP = new JLabel("Price (per seat):");
        lblP.setFont(FIELD_FONT);
        lblP.setBounds(20, ybase + 210, 140, 24);
        bookingCard.add(lblP);

        txtPrice = new JTextField();
        txtPrice.setBounds(160, ybase + 210, 140, 28);
        txtPrice.setFont(FIELD_FONT);
        txtPrice.setEditable(false);
        bookingCard.add(txtPrice);

        JLabel lblDate = new JLabel("Date:");
        lblDate.setFont(FIELD_FONT);
        lblDate.setBounds(20, ybase + 260, 60, 24);
        bookingCard.add(lblDate);

        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        dateSpinner.setBounds(80, ybase + 260, 160, 28);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        bookingCard.add(dateSpinner);

        JLabel lblTot = new JLabel("Total:");
        lblTot.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTot.setBounds(260, ybase + 260, 60, 24);
        bookingCard.add(lblTot);

        lblTotal = new JLabel("0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(Color.RED);
        lblTotal.setBounds(320, ybase + 256, 140, 28);
        bookingCard.add(lblTotal);

        btnBook = new JButton("Book Ticket");
        btnBook.setBounds(40, ybase + 320, 170, 42);
        bookingCard.add(btnBook);

        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(240, ybase + 320, 170, 42);
        bookingCard.add(btnCancel);

        previewPanel = new TicketPreviewPanel();
        previewPanel.setBounds(18, 450, 485, 180);
        right.add(previewPanel);

        spSeats.addChangeListener(e -> recalcTotal());
        cmbClass.addActionListener(e -> recalcTotal());
        dateSpinner.addChangeListener(e -> previewPanel.setDate((Date) dateSpinner.getValue()));
        btnBook.addActionListener(e -> bookTicket());
        btnCancel.addActionListener(e -> clearBookingForm());

        split.setLeftComponent(left);
        split.setRightComponent(right);
        add(split, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new GridBagLayout());
        p.setBorder(new CompoundBorder(new EmptyBorder(6, 6, 6, 6),
                new LineBorder(new Color(220, 220, 220), 1, true)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Search Flights");
        title.setFont(TITLE_FONT);
        title.setForeground(ROYAL_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        p.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        JLabel lblSrc = new JLabel("Source:");
        lblSrc.setFont(FIELD_FONT);
        gbc.gridx = 0;
        p.add(lblSrc, gbc);

        cmbSource = new JComboBox<>(new String[]{"Loading..."});
        cmbSource.setFont(FIELD_FONT);
        gbc.gridx = 1;
        p.add(cmbSource, gbc);

        JLabel lblDest = new JLabel("Destination:");
        lblDest.setFont(FIELD_FONT);
        gbc.gridx = 2;
        p.add(lblDest, gbc);

        cmbDestination = new JComboBox<>(new String[]{"Loading..."});
        cmbDestination.setFont(FIELD_FONT);
        gbc.gridx = 3;
        p.add(cmbDestination, gbc);

        gbc.gridy++;
        gbc.gridx = 3;
        btnSearchFlights = new JButton("Search");
        btnSearchFlights.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearchFlights.addActionListener(e -> searchFlights());
        p.add(btnSearchFlights, gbc);

        return p;
    }

    private JScrollPane createFlightsTablePanel() {
        flightsTableModel = new DefaultTableModel(new String[]{
                "Flight No", "Flight Name", "Source", "Destination", "Date", "DepTime", "ArrTime", "Charge"
        }, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        tblFlights = new JTable(flightsTableModel);
        tblFlights.setRowHeight(26);
        tblFlights.setFont(FIELD_FONT);
        tblFlights.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblFlights.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblFlights.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int sel = tblFlights.getSelectedRow();
                if (sel >= 0) {
                    String flightNo = flightsTableModel.getValueAt(sel, 0).toString();
                    String fname = flightsTableModel.getValueAt(sel, 1).toString();
                    String deptime = flightsTableModel.getValueAt(sel, 5).toString();
                    String charge = flightsTableModel.getValueAt(sel, 7).toString();
                    lblFlightNo.setText(flightNo);
                    lblFlightName.setText(fname);
                    lblDeptTime.setText(deptime);
                    txtPrice.setText(charge);
                    previewPanel.setFlightInfo(flightNo, fname, deptime);
                    recalcTotal();
                }
            }
        });

        JScrollPane sp = new JScrollPane(tblFlights);
        sp.setPreferredSize(new Dimension(580, 260));
        sp.setBorder(new EmptyBorder(10, 10, 10, 10));
        return sp;
    }

    private JPanel createTicketInfoPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(null);
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(580, 140));
        top.setBorder(new CompoundBorder(new EmptyBorder(8, 8, 8, 8),
                new LineBorder(new Color(230, 230, 230), 1, true)));

        JLabel lbl = new JLabel("Ticket Number");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(ROYAL_BLUE);
        lbl.setBounds(10, 6, 200, 24);
        top.add(lbl);

        lblTicketNo = new JLabel("TO001");
        lblTicketNo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTicketNo.setForeground(new Color(200, 50, 50));
        lblTicketNo.setBounds(10, 36, 200, 36);
        top.add(lblTicketNo);

        JLabel cl = new JLabel("Customer ID:");
        cl.setFont(FIELD_FONT);
        cl.setBounds(230, 8, 100, 24);
        top.add(cl);

        txtCustomerId = new JTextField();
        txtCustomerId.setBounds(330, 8, 150, 28);
        txtCustomerId.setFont(FIELD_FONT);
        top.add(txtCustomerId);

        btnSearchCustomer = new JButton("Find");
        btnSearchCustomer.setBounds(490, 8, 80, 28);
        top.add(btnSearchCustomer);

        JLabel fLbl = new JLabel("First Name:");
        fLbl.setBounds(230, 46, 100, 24);
        fLbl.setFont(FIELD_FONT);
        top.add(fLbl);
        lblCustFirst = new JLabel("—");
        lblCustFirst.setBounds(330, 46, 200, 24);
        top.add(lblCustFirst);

        JLabel lLbl = new JLabel("Last Name:");
        lLbl.setBounds(230, 76, 100, 24);
        lLbl.setFont(FIELD_FONT);
        top.add(lLbl);
        lblCustLast = new JLabel("—");
        lblCustLast.setBounds(330, 76, 200, 24);
        top.add(lblCustLast);

        JLabel pLbl = new JLabel("Passport:");
        pLbl.setBounds(230, 106, 100, 24);
        pLbl.setFont(FIELD_FONT);
        top.add(pLbl);
        lblPassport = new JLabel("—");
        lblPassport.setBounds(330, 106, 200, 24);
        top.add(lblPassport);

        p.add(top, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        bottom.add(new JLabel("Tip: select flight and set seats & date on the right to preview."));
        p.add(bottom, BorderLayout.SOUTH);

        btnSearchCustomer.addActionListener(e -> searchCustomer());
        return p;
    }

    private JLabel labeledValue(JPanel parent, String labelText, int x, int y) {
        JLabel lbl = new JLabel(labelText + ":");
        lbl.setFont(FIELD_FONT);
        lbl.setForeground(new Color(80, 80, 80));
        lbl.setBounds(x, y, 100, 22);
        parent.add(lbl);

        JLabel val = new JLabel("—");
        val.setFont(new Font("Segoe UI", Font.BOLD, 16));
        val.setForeground(new Color(190, 30, 30));
        val.setBounds(x + 110, y, 260, 22);
        parent.add(val);
        return val;
    }

    private void recalcTotal() {
        try {
            int price = Integer.parseInt(txtPrice.getText().isEmpty() ? "0" : txtPrice.getText());
            int qty = (Integer) spSeats.getValue();
            String cls = (String) cmbClass.getSelectedItem();
            double factor = cls.equalsIgnoreCase("Business") ? 1.6 : 1.0;
            int tot = (int) Math.round(price * factor * qty);
            lblTotal.setText(String.valueOf(tot));
            previewPanel.setSeatsAndTotal(qty, tot);
            previewPanel.setPricePerSeat((int) Math.round(price * factor));
        } catch (NumberFormatException ex) {
            lblTotal.setText("0");
            previewPanel.setSeatsAndTotal(0, 0);
        }
    }

    private void searchFlights() {
        String source = (String) cmbSource.getSelectedItem();
        String dest = (String) cmbDestination.getSelectedItem();
        flightsTableModel.setRowCount(0);

        String sql = "SELECT id, flightname, source, depart, date, deptime, arrtime, flightcharge FROM flight WHERE 1=1";
        boolean useSource = source != null && !source.toLowerCase().contains("all") && !source.toLowerCase().contains("select");
        boolean useDest = dest != null && !dest.toLowerCase().contains("all") && !dest.toLowerCase().contains("select");

        try {
            openConnection();
            if (useSource && useDest) {
                sql += " AND source = ? AND depart = ?";
                pst = con.prepareStatement(sql);
                pst.setString(1, source);
                pst.setString(2, dest);
            } else if (useSource) {
                sql += " AND source = ?";
                pst = con.prepareStatement(sql);
                pst.setString(1, source);
            } else if (useDest) {
                sql += " AND depart = ?";
                pst = con.prepareStatement(sql);
                pst.setString(1, dest);
            } else {
                pst = con.prepareStatement(sql);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("flightname"));
                row.add(rs.getString("source"));
                row.add(rs.getString("depart"));
                row.add(rs.getString("date"));
                row.add(rs.getString("deptime"));
                row.add(rs.getString("arrtime"));
                row.add(rs.getString("flightcharge"));
                flightsTableModel.addRow(row);
            }
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching flights: " + ex.getMessage());
        } finally {
            closeStatement();
        }
    }

    private void searchCustomer() {
        String cid = txtCustomerId.getText().trim();
        if (cid.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter Customer ID."); return; }
        String sql = "SELECT firstname, lastname, passport FROM customer WHERE id = ?";
        try {
            openConnection();
            pst = con.prepareStatement(sql);
            pst.setString(1, cid);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                lblCustFirst = new JLabel(rs.getString("firstname"));
                lblCustLast = new JLabel(rs.getString("lastname"));
                lblPassport = new JLabel(rs.getString("passport"));
            } else { JOptionPane.showMessageDialog(this, "Customer not found."); }
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching customer: " + ex.getMessage());
        } finally { closeStatement(); }
    }

    private void bookTicket() {
        String ticketId = lblTicketNo.getText();
        String flightId = lblFlightNo.getText();
        String custId = txtCustomerId.getText().trim();
        String fclass = (String) cmbClass.getSelectedItem();
        String price = String.valueOf(previewPanel.getPricePerSeat());
        String seats = String.valueOf(spSeats.getValue());
        Date date = (Date) dateSpinner.getValue();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = df.format(date);

        if (flightId.equals("—") || flightId.trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Please select a flight from table."); return; }
        if (custId.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter Customer ID and press Find."); return; }

        String sql = "INSERT INTO ticket(id, flightid, custid, class, price, seats, date) VALUES(?,?,?,?,?,?,?)";
        try {
            openConnection();
            pst = con.prepareStatement(sql);
            pst.setString(1, ticketId);
            pst.setString(2, flightId);
            pst.setString(3, custId);
            pst.setString(4, fclass);
            pst.setString(5, price);
            pst.setString(6, seats);
            pst.setString(7, dateStr);
            int rows = pst.executeUpdate();
            if (rows > 0) { JOptionPane.showMessageDialog(this, "Ticket Booked Successfully!\nTicket ID: " + ticketId); autoTicketID(); clearBookingForm(); }
            else { JOptionPane.showMessageDialog(this, "Failed to book ticket."); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error booking ticket: " + ex.getMessage()); }
        finally { closeStatement(); }
    }

    private void autoTicketID() {
        String sql = "SELECT MAX(id) AS maxid FROM ticket";
        try {
            openConnection();
            Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) {
                String maxid = rs.getString("maxid");
                if (maxid == null) { lblTicketNo.setText("TO001"); }
                else {
                    long id = Long.parseLong(maxid.substring(2));
                    id++;
                    lblTicketNo.setText("TO" + String.format("%03d", id));
                }
            }
            rs.close(); s.close();
        } catch (Exception ex) { lblTicketNo.setText("TO001"); }
        finally { closeStatement(); }
    }

    private void loadSourceDestinationOptions() {
        try {
            openConnection();
            Vector<String> sources = new Vector<>();
            Vector<String> dests = new Vector<>();
            sources.add("All"); dests.add("All");
            Statement s = con.createStatement();
            ResultSet rs1 = s.executeQuery("SELECT DISTINCT source FROM flight");
            while (rs1.next()) sources.add(rs1.getString(1));
            rs1.close();
            ResultSet rs2 = s.executeQuery("SELECT DISTINCT depart FROM flight");
            while (rs2.next()) dests.add(rs2.getString(1));
            rs2.close(); s.close();
            cmbSource.setModel(new DefaultComboBoxModel<>(sources));
            cmbDestination.setModel(new DefaultComboBoxModel<>(dests));
        } catch (Exception ex) {
            String[] defaults = {"All", "Bangladesh", "USA", "UK", "Japan", "Germany", "Australia", "China"};
            cmbSource.setModel(new DefaultComboBoxModel<>(defaults));
            cmbDestination.setModel(new DefaultComboBoxModel<>(defaults));
        } finally { closeStatement(); }
    }

    private void openConnection() throws ClassNotFoundException, SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException ex) { Class.forName("com.mysql.jdbc.Driver"); }
        con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private void closeStatement() {
        try { if (pst != null) pst.close(); } catch (SQLException ignored) {}
        try { if (con != null) con.close(); } catch (SQLException ignored) {}
        pst = null; con = null;
    }

    private void clearBookingForm() {
        lblFlightNo.setText("—");
        lblFlightName.setText("—");
        lblDeptTime.setText("—");
        txtPrice.setText("");
        spSeats.setValue(1);
        lblTotal.setText("0");
        txtCustomerId.setText("");
        lblCustFirst.setText("—");
        lblCustLast.setText("—");
        lblPassport.setText("—");
        previewPanel.reset();
    }

    private static class TicketPreviewPanel extends JPanel {
        private JLabel lblPaxName = new JLabel("Passenger: —");
        private JLabel lblPaxFlight = new JLabel("Flight: —");
        private JLabel lblPaxDetails = new JLabel("Depart: —");
        private JLabel lblPaxSeats = new JLabel("Seats: —");
        private JLabel lblPaxTotal = new JLabel("Total: 0");
        private JLabel lblDate = new JLabel("Date: —");
        private int pricePerSeat = 0;
        TicketPreviewPanel() {
            setLayout(new GridLayout(6,1));
            add(lblPaxName); add(lblPaxFlight); add(lblPaxDetails); add(lblPaxSeats); add(lblPaxTotal); add(lblDate);
        }
        void setCustomerName(String name) { lblPaxName.setText("Passenger: " + name); }
        void setFlightInfo(String flightNo, String flightName, String deptime) { lblPaxFlight.setText("Flight: " + flightNo + " — " + flightName); lblPaxDetails.setText("Depart: " + deptime); }
        void setSeatsAndTotal(int seats, int total) { lblPaxSeats.setText("Seats: " + seats); lblPaxTotal.setText("Total: " + total); }
        void setDate(Date d) { java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd"); lblDate.setText("Date: " + df.format(d)); }
        void setPricePerSeat(int p) { pricePerSeat = p; }
        int getPricePerSeat() { return pricePerSeat; }
        void reset() { setCustomerName("—"); setFlightInfo("—","—","—"); setSeatsAndTotal(0,0); setDate(new Date()); setPricePerSeat(0); }
    }
}