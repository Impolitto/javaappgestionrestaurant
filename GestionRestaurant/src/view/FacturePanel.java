package view;

import impl.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FacturePanel extends JPanel {
    private JTable tableFactures;
    private DefaultTableModel modelTable;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnAfficher;
    private FactureDAOImpl factureDAO;
    private static final Color TITLE_COLOR = new Color(32, 56, 100);
    
    public FacturePanel() {
        this.factureDAO = new FactureDAOImpl();
        initComponents();
        chargerFactures();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(244, 247, 251));

        JLabel titleLabel = new JLabel("Gestion des Factures");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBoutons.setBackground(new Color(244, 247, 251));
        btnAjouter = new JButton("Ajouter");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnAfficher = new JButton("Afficher");

        styleButton(btnAjouter, 130);
        styleButton(btnModifier, 130);
        styleButton(btnSupprimer, 130);
        styleButton(btnAfficher, 130);
        
        btnAjouter.addActionListener(e -> ajouterFacture());
        btnModifier.addActionListener(e -> modifierFacture());
        btnSupprimer.addActionListener(e -> supprimerFacture());
        btnAfficher.addActionListener(e -> afficherFacture());
        
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        panelBoutons.add(btnAfficher);
        
        // Table
        String[] colonnes = {"ID", "Commande", "Date", "Montant HT", "TVA", "Montant TTC", "Statut"};
        modelTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableFactures = new JTable(modelTable);
        styleTable(tableFactures);
        JScrollPane scrollPane = new JScrollPane(tableFactures);

        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setBackground(new Color(244, 247, 251));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(panelBoutons, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
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
    
    private void chargerFactures() {
        modelTable.setRowCount(0);
        List<Facture> factures = factureDAO.findAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Facture f : factures) {
            modelTable.addRow(new Object[]{
                f.getIdFacture(),
                f.getIdCommande(),
                sdf.format(f.getDateFacture()),
                String.format("%.2f", f.getMontantHT()),
                String.format("%.2f", f.getMontantTVA()),
                String.format("%.2f", f.getMontantTTC()),
                f.getStatut()
            });
        }
    }
    
    private void ajouterFacture() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Nouvelle Facture");
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(244, 247, 251));
        
        JLabel lblCommande = new JLabel("Commande ID:");
        JSpinner spinCommande = new JSpinner();
        
        JLabel lblMontantHT = new JLabel("Montant HT:");
        JTextField txtMontantHT = new JTextField();
        
        JLabel lblTVA = new JLabel("Taux TVA (%):");
        JSpinner spinTVA = new JSpinner();
        spinTVA.setValue(20.0);
        
        JLabel lblNotes = new JLabel("Notes:");
        JTextArea txtNotes = new JTextArea();
        
        JLabel lblStatut = new JLabel("Statut:");
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"EMISE", "PAYEE", "ANNULEE"});
        
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnAnnuler = new JButton("Annuler");
        styleButton(btnSauvegarder, 140);
        styleButton(btnAnnuler, 120);
        
        btnSauvegarder.addActionListener(e -> {
            try {
                int idCommande = (Integer) spinCommande.getValue();
                double montantHT = Double.parseDouble(txtMontantHT.getText());
                double tauxTVA = (Double) spinTVA.getValue();
                String notes = txtNotes.getText();
                String statut = (String) cmbStatut.getSelectedItem();
                
                Facture facture = new Facture(idCommande, new Date(), montantHT, tauxTVA, statut);
                facture.setNotes(notes);
                
                if (factureDAO.create(facture)) {
                    JOptionPane.showMessageDialog(dialog, "Facture créée avec succès!");
                    chargerFactures();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de la création", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAnnuler.addActionListener(e -> dialog.dispose());
        
        panel.add(lblCommande);
        panel.add(spinCommande);
        panel.add(lblMontantHT);
        panel.add(txtMontantHT);
        panel.add(lblTVA);
        panel.add(spinTVA);
        panel.add(lblNotes);
        panel.add(new JScrollPane(txtNotes));
        panel.add(lblStatut);
        panel.add(cmbStatut);
        panel.add(btnSauvegarder);
        panel.add(btnAnnuler);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void modifierFacture() {
        int selectedRow = tableFactures.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une facture à modifier");
            return;
        }
        
        int idFacture = (Integer) modelTable.getValueAt(selectedRow, 0);
        Facture facture = factureDAO.read(idFacture);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Modifier Facture");
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(244, 247, 251));
        
        JLabel lblMontantHT = new JLabel("Montant HT:");
        JTextField txtMontantHT = new JTextField(String.valueOf(facture.getMontantHT()));
        
        JLabel lblTVA = new JLabel("Taux TVA (%):");
        JSpinner spinTVA = new JSpinner();
        spinTVA.setValue(facture.getTauxTVA());
        
        JLabel lblNotes = new JLabel("Notes:");
        JTextArea txtNotes = new JTextArea(facture.getNotes() != null ? facture.getNotes() : "");
        
        JLabel lblStatut = new JLabel("Statut:");
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"EMISE", "PAYEE", "ANNULEE"});
        cmbStatut.setSelectedItem(facture.getStatut());
        
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnAnnuler = new JButton("Annuler");
        styleButton(btnSauvegarder, 140);
        styleButton(btnAnnuler, 120);
        
        btnSauvegarder.addActionListener(e -> {
            try {
                facture.setMontantHT(Double.parseDouble(txtMontantHT.getText()));
                facture.setTauxTVA((Double) spinTVA.getValue());
                facture.setNotes(txtNotes.getText());
                facture.setStatut((String) cmbStatut.getSelectedItem());
                
                if (factureDAO.update(facture)) {
                    JOptionPane.showMessageDialog(dialog, "Facture modifiée avec succès!");
                    chargerFactures();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAnnuler.addActionListener(e -> dialog.dispose());
        
        panel.add(lblMontantHT);
        panel.add(txtMontantHT);
        panel.add(lblTVA);
        panel.add(spinTVA);
        panel.add(lblNotes);
        panel.add(new JScrollPane(txtNotes));
        panel.add(lblStatut);
        panel.add(cmbStatut);
        panel.add(btnSauvegarder);
        panel.add(btnAnnuler);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void supprimerFacture() {
        int selectedRow = tableFactures.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une facture à supprimer");
            return;
        }
        
        int idFacture = (Integer) modelTable.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cette facture?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (factureDAO.delete(idFacture)) {
                JOptionPane.showMessageDialog(this, "Facture supprimée avec succès!");
                chargerFactures();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void afficherFacture() {
        int selectedRow = tableFactures.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une facture à afficher");
            return;
        }
        
        int idFacture = (Integer) modelTable.getValueAt(selectedRow, 0);
        Facture facture = factureDAO.read(idFacture);
        
        StringBuilder details = new StringBuilder();
        details.append("FACTURE N°").append(facture.getIdFacture()).append("\n\n");
        details.append("Commande N°: ").append(facture.getIdCommande()).append("\n");
        details.append("Date: ").append(new SimpleDateFormat("dd/MM/yyyy").format(facture.getDateFacture())).append("\n\n");
        details.append("Montant HT: ").append(String.format("%.2f", facture.getMontantHT())).append(" €\n");
        details.append("TVA (").append(facture.getTauxTVA()).append("%): ").append(String.format("%.2f", facture.getMontantTVA())).append(" €\n");
        details.append("-----------------------------------\n");
        details.append("Montant TTC: ").append(String.format("%.2f", facture.getMontantTTC())).append(" €\n\n");
        details.append("Statut: ").append(facture.getStatut()).append("\n");
        if (facture.getNotes() != null && !facture.getNotes().isEmpty()) {
            details.append("Notes: ").append(facture.getNotes());
        }
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Détails Facture", JOptionPane.INFORMATION_MESSAGE);
    }
}
