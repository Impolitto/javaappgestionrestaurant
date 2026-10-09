package view;

import impl.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandePanel extends JPanel {
    private CommandeDAOImpl commandeDAO;
    private ClientDAOImpl clientDAO;
    private TableDAOImpl tableDAO;
    private PlatDAOImpl platDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;
    private static final Color TITLE_COLOR = new Color(32, 56, 100);
    
    public CommandePanel() {
        commandeDAO = new CommandeDAOImpl();
        clientDAO = new ClientDAOImpl();
        tableDAO = new TableDAOImpl();
        platDAO = new PlatDAOImpl();
        initUI();
        loadData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(244, 247, 251));
        
        // Titre
        JLabel titleLabel = new JLabel("Gestion des Commandes");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);
        
        // Table des commandes
        String[] columns = {"ID", "Table", "Client", "Date", "Heure", "Statut", "Montant (€)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailsPanel();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Panel des détails (en bas)
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails de la commande sélectionnée"));
        
        detailsArea = new JTextArea(5, 50);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsPanel.add(detailsScroll, BorderLayout.CENTER);
        
        // Panel centre avec table et détails
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(detailsPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton addButton = new JButton("➕ Nouvelle Commande");
        JButton editButton = new JButton("✏️ Modifier");
        JButton deleteButton = new JButton("🗑️ Supprimer");
        JButton refreshButton = new JButton("🔄 Actualiser");

        styleButton(addButton, 190);
        styleButton(editButton, 140);
        styleButton(deleteButton, 140);
        styleButton(refreshButton, 140);
        
        addButton.addActionListener(e -> creerNouvelleCommande());
        editButton.addActionListener(e -> modifierCommande());
        deleteButton.addActionListener(e -> supprimerCommande());
        refreshButton.addActionListener(e -> loadData());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
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
    
    private void updateDetailsPanel() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            detailsArea.setText("");
            return;
        }
        
        int idCommande = (int) tableModel.getValueAt(selectedRow, 0);
        Commande commande = commandeDAO.read(idCommande);
        
        if (commande != null) {
            StringBuilder details = new StringBuilder();
            details.append("Commande #").append(commande.getIdCommande()).append("\n");
            details.append("Client: ").append(getClientName(commande.getIdClient())).append("\n");
            details.append("Table: ").append(getTableInfo(commande.getIdTable())).append("\n");
            details.append("Statut: ").append(commande.getStatutCommande()).append("\n");
            if (commande.getDescriptionCommande() != null && !commande.getDescriptionCommande().trim().isEmpty()) {
                details.append("Description: ").append(commande.getDescriptionCommande()).append("\n");
            }
            details.append("Total: ").append(String.format("%.2f", commande.getMontantTotal())).append("€\n");
            details.append("\nDétails des plats:\n");
            
            if (commande.getDetails() != null) {
                for (DetailCommande detail : commande.getDetails()) {
                    Plat plat = platDAO.read(detail.getIdPlat());
                    if (plat != null) {
                        details.append("- ").append(detail.getQuantite()).append("x ")
                               .append(plat.getNomPlat()).append(" (")
                               .append(detail.getPrixUnitaire()).append("€) = ")
                               .append(detail.getSousTotal()).append("€");
                        
                        if (detail.getNotesSpeciales() != null && !detail.getNotesSpeciales().isEmpty()) {
                            details.append(" [").append(detail.getNotesSpeciales()).append("]");
                        }
                        details.append("\n");
                    }
                }
            }
            
            detailsArea.setText(details.toString());
        }
    }
    
    private String getClientName(int idClient) {
        Client client = clientDAO.read(idClient);
        return client != null ? client.getNom() + " " + client.getPrenom() : "Inconnu";
    }
    
    private String getTableInfo(int idTable) {
        TableRestaurant tableRest = tableDAO.read(idTable);
        return tableRest != null ? tableRest.getNumeroTable() + " (" + tableRest.getEmplacement() + ")" : "Inconnue";
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Commande> commandes = commandeDAO.findAll();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        for (Commande commande : commandes) {
            Object[] row = {
                commande.getIdCommande(),
                getTableInfo(commande.getIdTable()),
                getClientName(commande.getIdClient()),
                dateFormat.format(commande.getDateCommande()),
                timeFormat.format(commande.getHeureCommande()),
                commande.getStatutCommande(),
                String.format("%.2f", commande.getMontantTotal())
            };
            tableModel.addRow(row);
        }
    }
    
    private void creerNouvelleCommande() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Nouvelle Commande");
        dialog.setSize(620, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(244, 247, 251));
        
        // Sélecteur de client
        JLabel lblClient = new JLabel("Client:");
        JComboBox<Client> cmbClient = new JComboBox<>();
        for (Client c : clientDAO.findAll()) {
            cmbClient.addItem(c);
        }
        
        // Sélecteur de table
        JLabel lblTable = new JLabel("Table:");
        JComboBox<TableRestaurant> cmbTable = new JComboBox<>();
        for (TableRestaurant t : tableDAO.findAll()) {
            cmbTable.addItem(t);
        }
        
        // Montant total
        JLabel lblMontant = new JLabel("Montant Total (€):");
        JTextField txtMontant = new JTextField("0.00");

        JLabel lblDescription = new JLabel("Description:");
        JTextArea txtDescription = new JTextArea(4, 24);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(txtDescription);
        descriptionScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        descriptionScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Statut
        JLabel lblStatut = new JLabel("Statut:");
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"EN_COURS", "PREPARATION", "SERVIE", "REGLEE"});
        
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnAnnuler = new JButton("Annuler");
        styleButton(btnSauvegarder, 150);
        styleButton(btnAnnuler, 150);
        
        btnSauvegarder.addActionListener(e -> {
            try {
                if (cmbClient.getSelectedIndex() == -1 || cmbTable.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Veuillez sélectionner un client et une table", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Client client = (Client) cmbClient.getSelectedItem();
                TableRestaurant tableRest = (TableRestaurant) cmbTable.getSelectedItem();
                double montant = Double.parseDouble(txtMontant.getText());
                String statut = (String) cmbStatut.getSelectedItem();
                
                Commande commande = new Commande();
                commande.setIdClient(client.getIdClient());
                commande.setIdTable(tableRest.getIdTable());
                commande.setDateCommande(new Date());
                commande.setHeureCommande(new java.sql.Time(System.currentTimeMillis()));
                commande.setDescriptionCommande(txtDescription.getText().trim());
                commande.setMontantTotal(montant);
                commande.setStatutCommande(statut);
                
                if (commandeDAO.create(commande)) {
                    JOptionPane.showMessageDialog(dialog, "Commande créée avec succès!");
                    loadData();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de la création de la commande.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de la création de la commande:\n" + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAnnuler.addActionListener(e -> dialog.dispose());
        
        panel.add(lblClient);
        panel.add(cmbClient);
        panel.add(lblTable);
        panel.add(cmbTable);
        panel.add(lblMontant);
        panel.add(txtMontant);
        panel.add(lblDescription);
        panel.add(descriptionScroll);
        panel.add(lblStatut);
        panel.add(cmbStatut);
        panel.add(btnSauvegarder);
        panel.add(btnAnnuler);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void modifierCommande() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une commande à modifier");
            return;
        }
        
        int idCommande = (int) tableModel.getValueAt(selectedRow, 0);
        Commande commande = commandeDAO.read(idCommande);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Modifier Commande");
        dialog.setSize(620, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(244, 247, 251));
        
        JLabel lblMontant = new JLabel("Montant Total (€):");
        JTextField txtMontant = new JTextField(String.valueOf(commande.getMontantTotal()));

        JLabel lblDescription = new JLabel("Description:");
        JTextArea txtDescription = new JTextArea(commande.getDescriptionCommande() == null ? "" : commande.getDescriptionCommande(), 4, 24);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(txtDescription);
        descriptionScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        descriptionScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        JLabel lblStatut = new JLabel("Statut:");
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"EN_COURS", "PREPARATION", "SERVIE", "REGLEE"});
        cmbStatut.setSelectedItem(commande.getStatutCommande());
        
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnAnnuler = new JButton("Annuler");
        styleButton(btnSauvegarder, 150);
        styleButton(btnAnnuler, 150);
        
        btnSauvegarder.addActionListener(e -> {
            try {
                commande.setMontantTotal(Double.parseDouble(txtMontant.getText()));
                commande.setDescriptionCommande(txtDescription.getText().trim());
                commande.setStatutCommande((String) cmbStatut.getSelectedItem());
                
                if (commandeDAO.update(commande)) {
                    JOptionPane.showMessageDialog(dialog, "Commande modifiée avec succès!");
                    loadData();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAnnuler.addActionListener(e -> dialog.dispose());
        
        panel.add(lblMontant);
        panel.add(txtMontant);
        panel.add(lblDescription);
        panel.add(descriptionScroll);
        panel.add(lblStatut);
        panel.add(cmbStatut);
        panel.add(btnSauvegarder);
        panel.add(btnAnnuler);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void supprimerCommande() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une commande à supprimer");
            return;
        }

        try {
            int idCommande = (int) tableModel.getValueAt(selectedRow, 0);
            Object montantCell = tableModel.getValueAt(selectedRow, 6);
            double montant = 0.0;
            if (montantCell != null) {
                montant = Double.parseDouble(montantCell.toString().replace(',', '.'));
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer la commande #" + idCommande + " (" + String.format("%.2f", montant) + "€) ?");

            if (confirm == JOptionPane.YES_OPTION) {
                if (commandeDAO.delete(idCommande)) {
                    JOptionPane.showMessageDialog(this, "Commande supprimée avec succès!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la suppression:\n" + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}