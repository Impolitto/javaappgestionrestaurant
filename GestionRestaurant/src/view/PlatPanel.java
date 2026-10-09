package view;

import impl.PlatDAOImpl;
import impl.CategoriePlatDAOImpl;
import model.CategoriePlat;
import model.Plat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PlatPanel extends JPanel {
    private PlatDAOImpl platDAO;
    private CategoriePlatDAOImpl categorieDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private static final Color TITLE_COLOR = new Color(32, 56, 100);
    
    public PlatPanel() {
        platDAO = new PlatDAOImpl();
        categorieDAO = new CategoriePlatDAOImpl();
        initUI();
        loadData();
    }

    private boolean isValidCategoryId(int categoryId) {
        return categoryId > 0 && categorieDAO.read(categoryId) != null;
    }

    private String getCategoryHint() {
        List<CategoriePlat> categories = categorieDAO.findAll();
        if (categories.isEmpty()) {
            return "Aucune categorie trouvee.";
        }
        return categories.stream()
            .map(c -> c.getIdCategorie() + "=" + c.getNomCategorie())
            .collect(Collectors.joining(" | "));
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(244, 247, 251));

        JLabel titleLabel = new JLabel("Gestion des Plats");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(244, 247, 251));
        searchPanel.add(new JLabel(" "));
        searchPanel.add(new JLabel("Rechercher: "));
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(260, 34));
        searchField.addActionListener(e -> searchPlats());
        JButton searchButton = new JButton("🔍");
        styleButton(searchButton, 56);
        searchButton.addActionListener(e -> searchPlats());
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setBackground(new Color(244, 247, 251));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        
        // Table des plats
        String[] columns = {"ID", "Nom", "Description", "Prix (€)", "Catégorie", "Disponible", "Préparation (min)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton addButton = new JButton("➕ Ajouter");
        JButton editButton = new JButton("✏️ Modifier");
        JButton deleteButton = new JButton("🗑️ Supprimer");
        JButton refreshButton = new JButton("🔄 Actualiser");
        JButton statsButton = new JButton("📊 Statistiques");

        styleButton(addButton, 140);
        styleButton(editButton, 140);
        styleButton(deleteButton, 140);
        styleButton(refreshButton, 140);
        styleButton(statsButton, 150);
        
        addButton.addActionListener(e -> addPlat());
        editButton.addActionListener(e -> editPlat());
        deleteButton.addActionListener(e -> deletePlat());
        refreshButton.addActionListener(e -> loadData());
        statsButton.addActionListener(e -> showStats());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(statsButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setGridColor(new Color(220, 225, 232));
        table.setShowVerticalLines(true);
        table.setSelectionBackground(new Color(0, 102, 204));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 236, 245));
        table.getTableHeader().setForeground(new Color(28, 44, 68));
    }

    private void styleButton(JButton button, int width) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(width, 36));
        button.setFocusPainted(false);
    }
    
    private void loadData() {
        SwingWorker<List<Plat>, Void> worker = new SwingWorker<List<Plat>, Void>() {
            @Override
            protected List<Plat> doInBackground() throws Exception {
                return platDAO.findAll();
            }
            
            @Override
            protected void done() {
                try {
                    List<Plat> plats = get();
                    tableModel.setRowCount(0);
                    
                    for (Plat plat : plats) {
                        Object[] row = {
                            plat.getIdPlat(),
                            plat.getNomPlat(),
                            plat.getDescription(),
                            String.format("%.2f", plat.getPrix()),
                            plat.getIdCategorie(),
                            plat.isDisponible() ? "✅ Oui" : "❌ Non",
                            plat.getTempsPreparation() + " min"
                        };
                        tableModel.addRow(row);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PlatPanel.this,
                        "Erreur lors du chargement des plats: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void searchPlats() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadData();
            return;
        }
        
        try {
            List<Plat> allPlats = platDAO.findAll();
            tableModel.setRowCount(0);
            
            for (Plat plat : allPlats) {
                if (plat.getNomPlat().toLowerCase().contains(searchText) ||
                    plat.getDescription().toLowerCase().contains(searchText)) {
                    
                    Object[] row = {
                        plat.getIdPlat(),
                        plat.getNomPlat(),
                        plat.getDescription(),
                        String.format("%.2f", plat.getPrix()),
                        plat.getIdCategorie(),
                        plat.isDisponible() ? "✅ Oui" : "❌ Non",
                        plat.getTempsPreparation() + " min"
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addPlat() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un plat", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(520, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(244, 247, 251));
        
        JTextField nomField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(260, 80));
        descScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        descScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JTextField prixField = new JTextField();
        JTextField categorieField = new JTextField();
        JCheckBox dispoCheck = new JCheckBox("Disponible", true);
        JTextField tempsField = new JTextField();
        
        formPanel.add(new JLabel("Nom du plat:"));
        formPanel.add(nomField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descScroll);
        formPanel.add(new JLabel("Prix (€):"));
        formPanel.add(prixField);
        formPanel.add(new JLabel("ID Catégorie:"));
        formPanel.add(categorieField);
        formPanel.add(new JLabel("Disponible:"));
        formPanel.add(dispoCheck);
        formPanel.add(new JLabel("Temps (min):"));
        formPanel.add(tempsField);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(244, 247, 251));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        styleButton(saveButton, 140);
        styleButton(cancelButton, 120);
        
        saveButton.addActionListener(e -> {
            try {
                if (nomField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Le nom du plat est obligatoire.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int idCategorie = Integer.parseInt(categorieField.getText().trim());
                if (!isValidCategoryId(idCategorie)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Categorie invalide. Utilisez un ID existant.\n\nCategories disponibles: " + getCategoryHint(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Plat plat = new Plat();
                plat.setNomPlat(nomField.getText().trim());
                plat.setDescription(descArea.getText().trim());
                plat.setPrix(Double.parseDouble(prixField.getText().trim()));
                plat.setIdCategorie(idCategorie);
                plat.setDisponible(dispoCheck.isSelected());
                plat.setTempsPreparation(Integer.parseInt(tempsField.getText().trim()));
                
                if (platDAO.create(plat)) {
                    JOptionPane.showMessageDialog(dialog, "Plat ajouté avec succès !");
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de l'ajout. Veuillez vérifier les données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur de format: Vérifiez les champs numériques (Prix, Catégorie, Temps).\n" + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur lors de l'ajout: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void editPlat() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un plat à modifier.",
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idPlat = (int) tableModel.getValueAt(selectedRow, 0);
        Plat plat = platDAO.read(idPlat);
        
        if (plat == null) {
            JOptionPane.showMessageDialog(this, "Plat non trouvé.");
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier le plat", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(520, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(244, 247, 251));
        
        JTextField nomField = new JTextField(plat.getNomPlat());
        JTextArea descArea = new JTextArea(plat.getDescription(), 3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(260, 80));
        descScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        descScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JTextField prixField = new JTextField(String.valueOf(plat.getPrix()));
        JTextField categorieField = new JTextField(String.valueOf(plat.getIdCategorie()));
        JCheckBox dispoCheck = new JCheckBox("Disponible", plat.isDisponible());
        JTextField tempsField = new JTextField(String.valueOf(plat.getTempsPreparation()));
        
        formPanel.add(new JLabel("Nom du plat:"));
        formPanel.add(nomField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descScroll);
        formPanel.add(new JLabel("Prix (€):"));
        formPanel.add(prixField);
        formPanel.add(new JLabel("ID Catégorie:"));
        formPanel.add(categorieField);
        formPanel.add(new JLabel("Disponible:"));
        formPanel.add(dispoCheck);
        formPanel.add(new JLabel("Temps (min):"));
        formPanel.add(tempsField);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(244, 247, 251));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        styleButton(saveButton, 140);
        styleButton(cancelButton, 120);
        
        saveButton.addActionListener(e -> {
            try {
                if (nomField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Le nom du plat est obligatoire.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int idCategorie = Integer.parseInt(categorieField.getText().trim());
                if (!isValidCategoryId(idCategorie)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Categorie invalide. Utilisez un ID existant.\n\nCategories disponibles: " + getCategoryHint(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                plat.setNomPlat(nomField.getText().trim());
                plat.setDescription(descArea.getText().trim());
                plat.setPrix(Double.parseDouble(prixField.getText().trim()));
                plat.setIdCategorie(idCategorie);
                plat.setDisponible(dispoCheck.isSelected());
                plat.setTempsPreparation(Integer.parseInt(tempsField.getText().trim()));
                
                if (platDAO.update(plat)) {
                    JOptionPane.showMessageDialog(dialog, "Plat modifié avec succès !");
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de la modification. Veuillez vérifier les données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    System.err.println("Erreur: platDAO.update() a retourné false pour le plat ID: " + plat.getIdPlat());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur de format: Vérifiez les champs numérique (Prix, Catégorie, Temps).\n" + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur lors de la modification: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deletePlat() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un plat à supprimer.",
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idPlat = (int) tableModel.getValueAt(selectedRow, 0);
        String nomPlat = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer le plat :\n\"" + nomPlat + "\" ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (platDAO.delete(idPlat)) {
                JOptionPane.showMessageDialog(this,
                    "Plat supprimé avec succès !",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showStats() {
        try {
            List<Plat> plats = platDAO.findAll();
            int total = plats.size();
            int disponibles = 0;
            double prixMoyen = 0;
            
            for (Plat plat : plats) {
                if (plat.isDisponible()) disponibles++;
                prixMoyen += plat.getPrix();
            }
            prixMoyen = total > 0 ? prixMoyen / total : 0;
            
            String stats = String.format(
                "📊 Statistiques des plats:\n\n" +
                "• Total des plats: %d\n" +
                "• Plats disponibles: %d\n" +
                "• Plats indisponibles: %d\n" +
                "• Prix moyen: %.2f €\n" +
                "• Taux de disponibilité: %.1f%%",
                total, disponibles, total - disponibles, prixMoyen,
                total > 0 ? (disponibles * 100.0 / total) : 0
            );
            
            JOptionPane.showMessageDialog(this, stats, "Statistiques", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du calcul des statistiques.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}