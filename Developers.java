package airline.managment.system;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Developers extends JInternalFrame {

    public Developers() {
        initComponents();
    }

    private void initComponents() {
        // Main panel with image background
        JPanel mainPanel = new JPanel() {
            private Image backgroundImage;

            {
                // Load the background image once
                try {
                    URL imgURL = getClass().getResource("/graphics/DEVimage.jpg"); // <--- REPLACE WITH YOUR IMAGE PATH
                    if (imgURL != null) {
                        backgroundImage = new ImageIcon(imgURL).getImage();
                    } else {
                        System.err.println("Background image not found: /graphics/background.jpg");
                        // Fallback: keep the super.paintComponent(g) to use default background
                    }
                } catch (Exception e) {
                    System.err.println("Error loading background image: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // Draw the image, scaling it to cover the entire panel
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback to a solid dark color if image is not found
                    g.setColor(new Color(2, 17, 54)); 
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(1176, 689));

        // Header
        JLabel header = new JLabel("A.N Wings Airline - Developers", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI Black", Font.BOLD, 42));
        header.setForeground(new Color(220, 230, 255));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        // Content Panel
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 100, 40, 100));
        content.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Developer Panels
        JPanel dev1 = createDevPanel("/graphics/dev1.jpg", "Nizamudin Khan");
        JPanel dev2 = createDevPanel("/graphics/dev2.jpg", "Adnan Bukhari");
        JPanel dev3 = createDevPanel("/graphics/default-avatar.jpg", "Aadil Khan");

        content.add(dev1);
        content.add(Box.createRigidArea(new Dimension(80, 0)));
        content.add(dev2);
        content.add(Box.createRigidArea(new Dimension(80, 0)));
        content.add(dev3);

        mainPanel.add(content, BorderLayout.CENTER);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        JButton btnOk = new JButton("Acknowledged!");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btnOk.setForeground(Color.WHITE);
        btnOk.setBackground(new Color(180, 0, 50));
        btnOk.setFocusPainted(false);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 70, 90), 2, true),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)));
        btnOk.addActionListener(e -> this.dispose()); // Close the JInternalFrame
        btnOk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnOk.setBackground(new Color(255, 70, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnOk.setBackground(new Color(180, 0, 50));
            }
        });
        btnPanel.add(btnOk);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        // Final frame setup
        this.setContentPane(mainPanel);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setClosable(true);
        this.setMaximizable(true);
        this.setIconifiable(true);
        this.setResizable(false);
        this.setTitle("Developer Information");
        this.pack();
    }

    private JPanel createDevPanel(String imgResourcePath, String devName) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 70, 70), 3, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        ImageIcon icon = null;

        try {
            URL imgURL = getClass().getResource(imgResourcePath);
            if (imgURL != null) {
                icon = new ImageIcon(imgURL);
            } else {
                System.err.println("Developer image not found: " + imgResourcePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + imgResourcePath);
        }

        // Safe fallback if image is null
        Image img;
        if (icon != null && icon.getImage() != null) {
            img = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        } else {
            BufferedImage fallback = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = fallback.createGraphics();
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, 300, 300);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("No Image", 90, 150);
            g2.dispose();
            img = fallback;
        }

        JLabel iconLabel = new JLabel(new ImageIcon(img));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); // Add space below image

        JLabel nameLabel = new JLabel(devName, SwingConstants.CENTER); // Center text within the label
        nameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));
        
        // --- MODIFIED CODE START ---
        // Change text color to White
        nameLabel.setForeground(Color.WHITE); 
        // Set a dark, slightly transparent background for the name
        nameLabel.setOpaque(true);
        nameLabel.setBackground(new Color(0, 0, 0, 180)); // Black with 180/255 opacity
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding around the text
        // --- MODIFIED CODE END ---
        
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create a wrapper panel to hold the name label and apply the background only to the name
        JPanel nameWrapper = new JPanel();
        nameWrapper.setOpaque(false); // Make sure wrapper doesn't obscure the main panel
        nameWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
        nameWrapper.add(nameLabel);

        p.add(iconLabel);
        p.add(nameWrapper);

        return p;
    }
}