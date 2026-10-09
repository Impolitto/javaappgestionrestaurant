package view;

import impl.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ClientDAOImpl clientDAO;
    private static final Color TITLE_COLOR = new Color(32, 56, 100);
    
    public ClientPanel() {
        this.clientDAO = new ClientDAOImpl();
        initUI();
        loadData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(244, 247, 251));
        
        // Titre
        JLabel titleLabel = new JLabel(" Gestion des Clients");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);
        
        // Table des clients
        String[] columns = {"ID", "Nom", "Prénom", "Téléphone", "Email", "Visites"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton addButton = new JButton("➕ Ajouter");
        JButton editButton = new JButton("✏️ Modifier");
        JButton deleteButton = new JButton("🗑️ Supprimer");
        JButton refreshButton = new JButton("🔄 Actualiser");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(refreshButton);
        
        addButton.addActionListener(e -> ajouterClient());
        editButton.addActionListener(e -> modifierClient());
        deleteButton.addActionListener(e -> supprimerClient());
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

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(140, 36));
        button.setFocusPainted(false);
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Client> clients = clientDAO.findAll();
        
        for (Client client : clients) {
            tableModel.addRow(new Object[]{
                client.getIdClient(),
                client.getNom(),
                client.getPrenom(),
                client.getTelephone(),
                client.getEmail(),
                client.getNbVisites()
            });
        }
    }
    
    private void ajouterClient() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Ajouter un Client");
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(244, 247, 251));
        
        JLabel lblNom = new JLabel("Nom:");
        JTextField txtNom = new JTextField();
        
        JLabel lblPrenom = new JLabel("Prénom:");
        JTextField txtPrenom = new JTextField();
        
        JLabel lblTelephone = new JLabel("Téléphone:");
        JTextField txtTelephone = new JTextField();
        
        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField();
        
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnAnnuler = new JButton("Annuler");
        styleButton(btnSauvegarder);
        styleButton(btnAnnuler);
        
        btnSauvegarder.addActionListener(e -> {
            try {
                String nom = txtNom.getText().trim();
                String prenom = txtPrenom.getText().trim();
                String telephone = txtTelephone.getText().trim();
                String email = txtEmail.getText().trim();
                
                if (nom.isEmpty() || prenom.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Nom et Prénom sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Client client = new Client();
                client.setNom(nom);
                client.setPrenom(prenom);
                client.setTelephone(telephone);
                client.setEmail(email);
                client.setNbVisites(0);
                
                if (clientDAO.create(client)) {
                    JOptionPane.showMessageDialog(dialog, "Client ajouté avec succès!");
                    loadData();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur lors de l'ajout", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnAnnuler.addActionListener(e -> dialog.dispose());
        
        panel.add(lblNom);
        panel.add(txtNom);
        panel.add(lblPrenom);
        panel.add(txtPrenom);
        panel.add(lblTelephone);
        panel.add(txtTelephone);
        panel.add(lblEmail);
        panel.add(txtEmail);
        panel.add(btnSauvegarder);
        panel.add(btnAnnuler);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void modifierClient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un client à modifier");
            return;
        }
        
        int idClient = (Integer) tableModel.getValueAt(selectedRow, 0);
        Client client = clientDAO.read(idClient);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Modifier Client");
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(244, 247, 251));
        
        JLabel lblNom = new JLabel("Nom:");
        JTextField txtNom = new JTextField(client.getNom());
        
        JLabel lblPrenom = new JLabel("Prénom:");
        JTextField txtPrenom = new JTextField(client.getPrenom());
        
        JLabel lblTelephone = new JLabel("Téléphone:");
        JTextField txtTelephone = new JTextField(client.getTelephone());
        
        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField(client.getEmail());

        JLabel lblVisites = new JLabel("Visites:");
        JTextField txtVisites = new JTextField(String.valueOf(client.getNbVisites()));
        
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnAnnuler = new JButton("Annuler");
        styleButton(btnSauvegarder);
        styleButton(btnAnnuler);
        
        btnSauvegarder.addActionListener(e -> {
            try {
                String nom = txtNom.getText().trim();
                String prenom = txtPrenom.getText().trim();
                
                if (nom.isEmpty() || prenom.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Nom et Prénom sont obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                client.setNom(nom);
                client.setPrenom(prenom);
                client.setTelephone(txtTelephone.getText().trim());
                client.setEmail(txtEmail.getText().trim());
                int nbVisites;
                try {
                    nbVisites = Integer.parseInt(txtVisites.getText().trim());
                    if (nbVisites < 0) {
                        JOptionPane.showMessageDialog(dialog, "Le nombre de visites doit etre positif", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(dialog, "Le nombre de visites doit etre un entier valide", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                client.setNbVisites(nbVisites);
                
                if (clientDAO.update(client)) {
                    JOptionPane.showMessageDialog(dialog, "Client modifié avec succès!");
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
        
        panel.add(lblNom);
        panel.add(txtNom);
        panel.add(lblPrenom);
        panel.add(txtPrenom);
        panel.add(lblTelephone);
        panel.add(txtTelephone);
        panel.add(lblEmail);
        panel.add(txtEmail);
        panel.add(lblVisites);
        panel.add(txtVisites);
        panel.add(btnSauvegarder);
        panel.add(btnAnnuler);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void supprimerClient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un client à supprimer");
            return;
        }
        
        int idClient = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce client?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (clientDAO.delete(idClient)) {
                JOptionPane.showMessageDialog(this, "Client supprimé avec succès!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}