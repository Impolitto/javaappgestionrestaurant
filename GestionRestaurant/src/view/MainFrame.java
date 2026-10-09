package view;

import impl.DatabaseConnection;
import javazoom.jl.player.Player;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.imageio.ImageIO;
import javax.swing.plaf.FontUIResource;

public class MainFrame extends JFrame {
    private static final String BASE_FONT_SIZE_KEY = "baseFontSize";
    private static final String BASE_ROW_HEIGHT_KEY = "baseRowHeight";
    private static final String HOME_BACKGROUND_RESOURCE = "/view/assets/accueil-background.jpg";
    private static final String MUSIC_STREAM_URL = "http://worship.lobpreisradio.de:8000/anbetung-adoration-prayer-worship-radio-mp3";
    private boolean highVisibilityEnabled;
    private volatile boolean musicPlaybackEnabled = true;
    private transient Player backgroundMusicPlayer;
    private transient Thread backgroundMusicThread;
    private final transient Object musicLock = new Object();
    
    public MainFrame() {
        // Configuration de la fenêtre
        setTitle(" Système de Gestion de Restaurant");
        setSize(1000, 700);
        setMinimumSize(new Dimension(960, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                stopBackgroundMusic();
            }
        });
        
        // Créer l'interface
        initUI();
        startBackgroundMusic();
        
        // Test de connexion au démarrage
        testDatabaseConnection();
    }
    
    private void initUI() {
        // Barre de menu
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Fichier
        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Menu Gestion
        JMenu gestionMenu = new JMenu("Gestion");
        JMenuItem platItem = new JMenuItem("Plats");
        JMenuItem clientItem = new JMenuItem("Clients");
        JMenuItem tableItem = new JMenuItem("Tables");

        
        gestionMenu.add(platItem);
        gestionMenu.add(clientItem);
        gestionMenu.add(tableItem);

        
        // Menu Aide
        JMenu helpMenu = new JMenu("Aide");
        JCheckBoxMenuItem highVisibilityItem = new JCheckBoxMenuItem("Mode Haute Visibilité");
        highVisibilityItem.addActionListener(e -> toggleHighVisibility(highVisibilityItem.isSelected()));
        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(highVisibilityItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(gestionMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        
        // Panneau principal avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(248, 249, 251));
        
        // Ajouter les différents panneaux
        tabbedPane.addTab("🏠 Accueil", createHomePanel());
        tabbedPane.addTab("🍽️ Plats", new PlatPanel());
        tabbedPane.addTab("👥 Clients", new ClientPanel());
        tabbedPane.addTab(" Tables", new TablePanel());
        tabbedPane.addTab("📋 Commandes", new CommandePanel());
        
        add(tabbedPane);
        
        // Ajouter des listeners pour les menus
        platItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        clientItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        tableItem.addActionListener(e -> tabbedPane.setSelectedIndex(3));

    }

    private void toggleHighVisibility(boolean enabled) {
        highVisibilityEnabled = enabled;
        applyGlobalSwingStyling(enabled);
        SwingUtilities.updateComponentTreeUI(this);
        applyComponentVisibility(this, enabled);
        revalidate();
        repaint();
    }

    private void applyComponentVisibility(Component component, boolean highVisibility) {
        if (component instanceof JComponent) {
            JComponent jComponent = (JComponent) component;
            Font font = jComponent.getFont();
            if (font != null) {
                Object baseSizeProperty = jComponent.getClientProperty(BASE_FONT_SIZE_KEY);
                int baseSize;
                if (baseSizeProperty instanceof Integer) {
                    baseSize = (Integer) baseSizeProperty;
                } else {
                    baseSize = font.getSize();
                    jComponent.putClientProperty(BASE_FONT_SIZE_KEY, baseSize);
                }
                int targetSize = highVisibility ? baseSize + 2 : baseSize;
                jComponent.setFont(font.deriveFont((float) targetSize));
            }
        }

        if (component instanceof JTable) {
            JTable table = (JTable) component;
            Object baseHeightProperty = table.getClientProperty(BASE_ROW_HEIGHT_KEY);
            int baseHeight;
            if (baseHeightProperty instanceof Integer) {
                baseHeight = (Integer) baseHeightProperty;
            } else {
                baseHeight = table.getRowHeight();
                table.putClientProperty(BASE_ROW_HEIGHT_KEY, baseHeight);
            }
            table.setRowHeight(highVisibility ? baseHeight + 6 : baseHeight);
            table.setSelectionBackground(highVisibility ? new Color(0, 76, 153) : new Color(0, 102, 204));
            table.setSelectionForeground(Color.WHITE);
        }

        if (component instanceof JMenu) {
            JMenu menu = (JMenu) component;
            for (Component child : menu.getMenuComponents()) {
                applyComponentVisibility(child, highVisibility);
            }
        }

        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                applyComponentVisibility(child, highVisibility);
            }
        }
    }
    
    private JPanel createHomePanel() {
        BufferedImage homeBackgroundImage = loadHomeBackgroundImage();
        JPanel homePanel = new BackgroundImagePanel(homeBackgroundImage);
        homePanel.setLayout(new BorderLayout());
        homePanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        homePanel.setBackground(new Color(244, 247, 251));

        Color primaryTextColor = homeBackgroundImage != null ? Color.WHITE : new Color(44, 62, 80);
        Color accentTextColor = homeBackgroundImage != null ? new Color(255, 220, 130) : new Color(32, 98, 169);
        
        // En-tête
        JLabel titleLabel = new JLabel("Système de Gestion de Restaurant", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(primaryTextColor);
        homePanel.add(titleLabel, BorderLayout.NORTH);
        
        // Logo ou image
        JLabel logoLabel = new JLabel(" RESTAURANT", SwingConstants.CENTER);
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 52));
        logoLabel.setForeground(accentTextColor);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        homePanel.add(logoLabel, BorderLayout.CENTER);
        
        // Panel d'informations
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        infoPanel.setOpaque(homeBackgroundImage == null);
        infoPanel.setBackground(new Color(244, 247, 251));
        
        JLabel info1 = new JLabel("Gestion complète de votre restaurant", SwingConstants.CENTER);
        JLabel info2 = new JLabel(" Menu •  Tables •  Clients •  Commandes", SwingConstants.CENTER);
        JLabel info3 = new JLabel(" Interface intuitive •  Base de données MySQL", SwingConstants.CENTER);
        
        info1.setFont(new Font("SansSerif", Font.PLAIN, 18));
        info2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        info3.setFont(new Font("SansSerif", Font.PLAIN, 18));
        info1.setForeground(primaryTextColor);
        info2.setForeground(primaryTextColor);
        info3.setForeground(primaryTextColor);

        JButton toggleMusicButton = new JButton();
        toggleMusicButton.setFocusPainted(false);
        toggleMusicButton.setForeground(Color.WHITE);
        toggleMusicButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        updateMusicButtonAppearance(toggleMusicButton, musicPlaybackEnabled);
        toggleMusicButton.addActionListener(e -> {
            if (musicPlaybackEnabled) {
                stopBackgroundMusic();
                updateMusicButtonAppearance(toggleMusicButton, false);
            } else {
                startBackgroundMusic();
                updateMusicButtonAppearance(toggleMusicButton, true);
            }
        });
        
        infoPanel.add(info1);
        infoPanel.add(info2);
        infoPanel.add(info3);
        infoPanel.add(toggleMusicButton);
        
        homePanel.add(infoPanel, BorderLayout.SOUTH);
        
        return homePanel;
    }

    private BufferedImage loadHomeBackgroundImage() {
        try (InputStream imageStream = getClass().getResourceAsStream(HOME_BACKGROUND_RESOURCE)) {
            if (imageStream != null) {
                return ImageIO.read(imageStream);
            }
        } catch (IOException ex) {
            System.err.println("Impossible de charger l'image d'accueil: " + ex.getMessage());
        }
        return null;
    }

    private void startBackgroundMusic() {
        synchronized (musicLock) {
            musicPlaybackEnabled = true;
            if (backgroundMusicThread != null && backgroundMusicThread.isAlive()) {
                return;
            }

            backgroundMusicThread = new Thread(() -> {
                Player player = null;
                try (BufferedInputStream stream = new BufferedInputStream(new URL(MUSIC_STREAM_URL).openStream())) {
                    synchronized (musicLock) {
                        if (!musicPlaybackEnabled) {
                            return;
                        }
                    }

                    player = new Player(stream);
                    synchronized (musicLock) {
                        if (!musicPlaybackEnabled) {
                            player.close();
                            return;
                        }
                        backgroundMusicPlayer = player;
                    }

                    player.play();
                } catch (Exception ex) {
                    if (musicPlaybackEnabled) {
                        System.err.println("Lecture du flux audio impossible: " + ex.getMessage());
                    }
                } finally {
                    synchronized (musicLock) {
                        if (backgroundMusicPlayer == player) {
                            backgroundMusicPlayer = null;
                        }
                        backgroundMusicThread = null;
                    }
                }
            }, "music-stream-player");
            backgroundMusicThread.setDaemon(true);
            backgroundMusicThread.start();
        }
    }

    private void stopBackgroundMusic() {
        synchronized (musicLock) {
            musicPlaybackEnabled = false;
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.close();
                backgroundMusicPlayer = null;
            }
            if (backgroundMusicThread != null) {
                backgroundMusicThread.interrupt();
            }
        }
    }

    private void updateMusicButtonAppearance(JButton button, boolean enabled) {
        if (enabled) {
            button.setText("Desactiver la musique");
            button.setBackground(new Color(191, 57, 43));
        } else {
            button.setText("Activer la musique");
            button.setBackground(new Color(35, 124, 78));
        }
    }

    private static class BackgroundImagePanel extends JPanel {
        private final transient Image backgroundImage;

        BackgroundImagePanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // Add a soft overlay to keep text readable on bright areas.
            g2.setColor(new Color(0, 0, 0, 90));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
    
    private void testDatabaseConnection() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection == null || !connection.isValid(3)) {
                throw new SQLException("Connexion invalide.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "❌ Erreur de connexion à la base de données !\n" +
                "Vérifiez MySQL et vos identifiants.\n\n" +
                "Détail: " + e.getMessage(),
                "Erreur de connexion",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Système de Gestion de Restaurant\n" +
            "Version 1.0\n" +
            "Développé par Wissem Dakhli et Alaaeddine Kerkeni\n" +
            "MySQL + Java Swing",
            "À propos",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private static void configureLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep default look and feel if a custom one cannot be applied.
        }
    }

    private static void applyGlobalSwingStyling(boolean highVisibility) {
        int baseSize = highVisibility ? 16 : 14;
        int headingSize = highVisibility ? 16 : 14;
        FontUIResource globalFont = new FontUIResource("SansSerif", Font.PLAIN, baseSize);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, globalFont);
            }
        }

        UIManager.put("Menu.font", new FontUIResource("SansSerif", Font.BOLD, headingSize));
        UIManager.put("MenuItem.font", new FontUIResource("SansSerif", Font.PLAIN, baseSize));
        UIManager.put("TabbedPane.font", new FontUIResource("SansSerif", Font.BOLD, headingSize));
        UIManager.put("Button.font", new FontUIResource("SansSerif", Font.BOLD, baseSize));
        UIManager.put("Table.font", new FontUIResource("SansSerif", Font.PLAIN, baseSize));
        UIManager.put("Table.rowHeight", highVisibility ? 38 : 32);
        UIManager.put("TextField.font", new FontUIResource("SansSerif", Font.PLAIN, baseSize));
        UIManager.put("ComboBox.font", new FontUIResource("SansSerif", Font.PLAIN, baseSize));
        UIManager.put("OptionPane.messageFont", new FontUIResource("SansSerif", Font.PLAIN, baseSize));
        UIManager.put("OptionPane.buttonFont", new FontUIResource("SansSerif", Font.BOLD, highVisibility ? 15 : 13));

        UIManager.put("Panel.background", new Color(244, 247, 251));
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.selectionBackground", highVisibility ? new Color(0, 76, 153) : new Color(0, 102, 204));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextArea.background", Color.WHITE);
    }
    
    public static void main(String[] args) {
        configureLookAndFeel();
        applyGlobalSwingStyling(false);
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}