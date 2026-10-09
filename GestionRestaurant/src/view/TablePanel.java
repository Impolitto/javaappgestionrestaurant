package view;

import impl.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TablePanel extends JPanel {
    private JTable tableRestaurant;
    private DefaultTableModel modelTable;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnLiberer;
    private TableDAOImpl tableDAO;
    private static final Color TITLE_COLOR = new Color(32, 56, 100);
    
    public TablePanel() {
        this.tableDAO = new TableDAOImpl();
        initComponents();
        chargerTables();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(244, 247, 251));
        
        JLabel titleLabel = new JLabel("Gestion des Tables");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBoutons.setBackground(new Color(244, 247, 251));
        btnAjouter = new JButton("Ajouter Table");
        btnModifier = new JButton("Modifier");
        btnSupprimer = new JButton("Supprimer");
        btnLiberer = new JButton("Libérer");

        styleButton(btnAjouter, 150);
        styleButton(btnModifier, 130);
        styleButton(btnSupprimer, 130);
        styleButton(btnLiberer, 130);
        
        btnAjouter.addActionListener(e -> ajouterTable());
        btnModifier.addActionListener(e -> modifierTable());
        btnSupprimer.addActionListener(e -> supprimerTable());
        btnLiberer.addActionListener(e -> libererTable());
        
        panelBoutons.add(btnAjouter);
        panelBoutons.add(btnModifier);
        panelBoutons.add(btnSupprimer);
        panelBoutons.add(btnLiberer);
        
        // Table
        String[] colonnes = {"ID", "N° Table", "Capacité", "Emplacement", "Statut"};
        modelTable = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableRestaurant = new JTable(modelTable);
        styleTable(tableRestaurant);
        JScrollPane scrollPane = new JScrollPane(tableRestaurant);

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
    
    private void chargerTables() {
        modelTable.setRowCount(0);
        List<TableRestaurant> tables = tableDAO.findAll();
        
        for (TableRestaurant t : tables) {
            String couleur = "";
            if (t.getStatut().equals("LIBRE")) {
                couleur = "🟢 ";
            } else if (t.getStatut().equals("OCCUPEE")) {
                couleur = "🔴 ";
            } else if (t.getStatut().equals("RESERVEE")) {
                couleur = "🟡 ";
            }
            
            modelTable.addRow(new Object[]{
                t.getIdTable(),
                t.getNumeroTable(),
                t.getCapacite(),
                t.getEmplacement(),
                couleur + t.getStatut()
            });
        }
    }
    
    private void ajouterTable() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Ajouter une Table");
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(244, 247, 251));
        
        JLabel lblNumero = new JLabel("Numéro Table:");
        JTextField txtNumero = new JTextField();
        
        JLabel lblCapacite = new JLabel("Capacité:");
        JSpinner spinCapacite = new JSpinner();
        spinCapacite.setValue(4);
        
        JLabel lblEmplacement = new JLabel("Emplacement:");
        JTextField txtEmplacement = new JTextField();
        
        JLabel lblStatut = new JLabel("Statut:");
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"LIBRE", "OCCUPEE", "RESERVEE"});
        cmbStatut.setSelectedItem("LIBRE");
        
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnAnnuler = new JButton("Annuler");
        styleButton(btnSauvegarder, 140);
        styleButton(btnAnnuler, 120);
        
        btnSauvegarder.addActionListener(e -> {
            try {
                String numero = txtNumero.getText().trim();
                if (numero.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Veuillez entrer un numéro de table");
                    return;
                }
                
                int capacite = (Integer) spinCapacite.getValue();
                String emplacement = txtEmplacement.getText().trim();
                String statut = (String) cmbStatut.getSelectedItem();
                
                TableRestaurant table = new TableRestaurant(numero, capacite, emplacement, statut);
                
                if (tableDAO.create(table)) {
                    JOptionPane.showMessageDialog(dialog, "Table ajoutée avec succès!");
                    chargerTables();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de l'ajout", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAnnuler.addActionListener(e -> dialog.dispose());
        
        panel.add(lblNumero);
        panel.add(txtNumero);
        panel.add(lblCapacite);
        panel.add(spinCapacite);
        panel.add(lblEmplacement);
        panel.add(txtEmplacement);
        panel.add(lblStatut);
        panel.add(cmbStatut);
        panel.add(btnSauvegarder);
        panel.add(btnAnnuler);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void modifierTable() {
        int selectedRow = tableRestaurant.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une table à modifier");
            return;
        }
        
        int idTable = (Integer) modelTable.getValueAt(selectedRow, 0);
        TableRestaurant table = tableDAO.read(idTable);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Modifier Table");
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(244, 247, 251));
        
        JLabel lblNumero = new JLabel("Numéro Table:");
        JTextField txtNumero = new JTextField(table.getNumeroTable());
        
        JLabel lblCapacite = new JLabel("Capacité:");
        JSpinner spinCapacite = new JSpinner();
        spinCapacite.setValue(table.getCapacite());
        
        JLabel lblEmplacement = new JLabel("Emplacement:");
        JTextField txtEmplacement = new JTextField(table.getEmplacement());
        
        JLabel lblStatut = new JLabel("Statut:");
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"LIBRE", "OCCUPEE", "RESERVEE"});
        cmbStatut.setSelectedItem(table.getStatut());
        
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnAnnuler = new JButton("Annuler");
        styleButton(btnSauvegarder, 140);
        styleButton(btnAnnuler, 120);
        
        btnSauvegarder.addActionListener(e -> {
            try {
                String numero = txtNumero.getText().trim();
                if (numero.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Veuillez entrer un numéro de table");
                    return;
                }
                
                table.setNumeroTable(numero);
                table.setCapacite((Integer) spinCapacite.getValue());
                table.setEmplacement(txtEmplacement.getText().trim());
                table.setStatut((String) cmbStatut.getSelectedItem());
                
                if (tableDAO.update(table)) {
                    JOptionPane.showMessageDialog(dialog, "Table modifiée avec succès!");
                    chargerTables();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAnnuler.addActionListener(e -> dialog.dispose());
        
        panel.add(lblNumero);
        panel.add(txtNumero);
        panel.add(lblCapacite);
        panel.add(spinCapacite);
        panel.add(lblEmplacement);
        panel.add(txtEmplacement);
        panel.add(lblStatut);
        panel.add(cmbStatut);
        panel.add(btnSauvegarder);
        panel.add(btnAnnuler);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void supprimerTable() {
        int selectedRow = tableRestaurant.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une table à supprimer");
            return;
        }
        
        int idTable = (Integer) modelTable.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cette table?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (tableDAO.delete(idTable)) {
                JOptionPane.showMessageDialog(this, "Table supprimée avec succès!");
                chargerTables();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void libererTable() {
        int selectedRow = tableRestaurant.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une table à libérer");
            return;
        }
        
        int idTable = (Integer) modelTable.getValueAt(selectedRow, 0);
        TableRestaurant table = tableDAO.read(idTable);
        
        table.setStatut("LIBRE");
        if (tableDAO.update(table)) {
            JOptionPane.showMessageDialog(this, "Table libérée!");
            chargerTables();
        } else {
            JOptionPane.showMessageDialog(this, "Erreur lors de la libération", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
