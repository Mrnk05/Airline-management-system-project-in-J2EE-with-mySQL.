package airline.managment.system;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class Main extends JFrame {

    private Image bg;
    private JDesktopPane jDesktopPane1;
    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5;
    private JButton jButton1;
    private JMenuBar jMenuBar1;
    private JMenu jMenu1, jMenu2, jMenu3;
    private Color ROYAL_NAVY;
    private Color ROYAL_GOLD;

    public Main() {
        initComponents();
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/graphics/icon.png")));
        } catch (Exception ex) {
            System.err.println("App icon missing");
        }
    }

    private void initComponents() {

        jDesktopPane1 = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };

        URL bgURL = getClass().getResource("/graphics/royal_airplane_bg.jpg");
        if (bgURL != null) {
            bg = new ImageIcon(bgURL).getImage();
        } else {
            System.err.println("Background image not found!");
        }

        jLabel1 = new JLabel();
        URL homeURL = getClass().getResource("/graphics/home.png");
        if (homeURL != null) jLabel1.setIcon(new ImageIcon(homeURL));
        else jLabel1.setText("");

        // 1. A.N. Wings - Stylish Royal Design Change
       jLabel2 = new JLabel("A.N. Wings Airline");
        jLabel2.setFont(new Font("Serif", Font.BOLD, 80));
        // Changed to Navy Blue for best readability against the dark background
        jLabel2.setForeground(ROYAL_NAVY); 
        
        // New Tagline
        jLabel3 = new JLabel("your sky our wings");
        jLabel3.setFont(new Font("Tw Cen MT", Font.BOLD | Font.ITALIC, 36));
        jLabel3.setForeground(ROYAL_GOLD);

        jLabel4 = new JLabel("System");
        jLabel4.setFont(new Font("Tw Cen MT", Font.BOLD, 50));
        jLabel4.setForeground(new Color(135, 206, 250));

        jLabel5 = new JLabel("Developed by ");
        jLabel5.setFont(new Font("Tw Cen MT", Font.BOLD, 28));
        jLabel5.setForeground(Color.WHITE);

        jButton1 = new JButton("Developers");
        jButton1.setFont(new Font("Tw Cen MT", Font.BOLD, 24));
        URL logoURL = getClass().getResource("/graphics/logo.png");
        if (logoURL != null) jButton1.setIcon(new ImageIcon(logoURL));
        jButton1.setBackground(new Color(10, 88, 160));
        jButton1.setForeground(Color.WHITE);
        jButton1.setFocusPainted(false);
        jButton1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jButton1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                new EmptyBorder(6, 12, 6, 12)
        ));
        jButton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { jButton1.setBackground(new Color(0, 120, 215)); }
            @Override
            public void mouseExited(MouseEvent e) { jButton1.setBackground(new Color(10, 88, 160)); }
        });
        jButton1.addActionListener(evt -> {
            Developers devInfo = new Developers();
            showInternalFrame(devInfo);
        });

        GroupLayout jDesktopPane1Layout = new GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setAutoCreateGaps(true);
        jDesktopPane1Layout.setAutoCreateContainerGaps(true);

        jDesktopPane1Layout.setHorizontalGroup(
                jDesktopPane1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jDesktopPane1Layout.createSequentialGroup()
                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 540, GroupLayout.PREFERRED_SIZE)
                                .addGap(40)
                                .addGroup(jDesktopPane1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4)
                                        .addGroup(jDesktopPane1Layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton1))
                                )
                        )
        );

        jDesktopPane1Layout.setVerticalGroup(
                jDesktopPane1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jDesktopPane1Layout.createSequentialGroup()
                                .addGap(50)
                                .addComponent(jLabel2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                                .addGroup(jDesktopPane1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jButton1))
                                .addGap(30)
                        )
        );

        // ----- Menus -----
        jMenuBar1 = new JMenuBar();
        jMenuBar1.setBackground(new Color(10, 88, 160));
        jMenuBar1.setForeground(Color.WHITE);
        jMenuBar1.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));

        jMenu1 = createMenu("Customer",
                new String[]{"Add Customer", "Search Customer"},
                new ActionListener[]{
                        evt -> showInternalFrame(new D_addCustomer()),
                        evt -> showInternalFrame(new searchCustomer())
                });
        jMenu1.setForeground(new Color(255, 215, 0)); // Set menu name color to Gold

        jMenu2 = createMenu("Tickets",
                new String[]{"Book Ticket", "Ticket Report"},
                new ActionListener[]{
                        evt -> showInternalFrame(new C_bookTicket()),
                        evt -> showInternalFrame(new E_ticketReport())
                });
        jMenu2.setForeground(new Color(255, 215, 0)); // Set menu name color to Gold

        jMenu3 = createMenu("Flight",
                new String[]{"Add Flight"},
                new ActionListener[]{
                        evt -> showInternalFrame(new B_addFlight())
                });
        jMenu3.setForeground(new Color(255, 215, 0)); // Set menu name color to Gold

        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenu2);
        jMenuBar1.add(jMenu3);
        setJMenuBar(jMenuBar1);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jDesktopPane1));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jDesktopPane1));

        setSize(new Dimension(1204, 776));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void showInternalFrame(JInternalFrame frame) {
        jDesktopPane1.add(frame);
        frame.setVisible(true);

        // --- Simple center placement (animation removed for stability) ---
        Dimension desktopSize = jDesktopPane1.getSize();
        Dimension frameSize = frame.getSize();
        int x = (desktopSize.width - frameSize.width) / 2;
        int y = (desktopSize.height - frameSize.height) / 2;
        frame.setLocation(Math.max(0, x), Math.max(0, y));
    }

    // 2. Menu Bar/Items - Stylish Royal Dashboard Design Change
    private JMenu createMenu(String name, String[] items, ActionListener[] actions) {
        JMenu menu = new JMenu(name);
        menu.setFont(new Font("Century Gothic", Font.BOLD, 22)); // Bolder Menu Name Font

        // JMenu foreground is now set outside this method (in initComponents)

        for (int i = 0; i < items.length; i++) {
            JMenuItem mi = new JMenuItem(items[i]);
            mi.setFont(new Font("Century Gothic", Font.PLAIN, 18)); // Slightly larger item font

            // Set initial colors to match the dark theme
            final Color defaultBg = new Color(10, 88, 160); // Deep Blue
            final Color defaultFg = Color.WHITE;
            final Color hoverBg = new Color(255, 215, 0); // Gold
            final Color hoverFg = new Color(10, 88, 160); // Deep Blue

            mi.setBackground(defaultBg);
            mi.setForeground(defaultFg);
            mi.setBorder(new EmptyBorder(5, 15, 5, 15)); // Add padding

            // Add royal-looking hover effect: Gold background, Deep Blue text
            mi.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    mi.setBackground(hoverBg);
                    mi.setForeground(hoverFg);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    mi.setBackground(defaultBg);
                    mi.setForeground(defaultFg);
                }
            });

            mi.addActionListener(actions[i]);
            menu.add(mi);
        }
        return menu;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("Look and Feel load failed");
        }
        EventQueue.invokeLater(() -> new Main().setVisible(true));
    }
}