package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PersonApp extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> genderComboBox;
    private DefaultTableModel tableModel;
    private JTable personTable;

    public PersonApp() {
        setTitle("Gestion des Personnes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création des champs du formulaire
        nameField = new JTextField(15);
        emailField = new JTextField(15);
        genderComboBox = new JComboBox<>(new String[]{"Homme", "Femme", "Autre"});

        // Création du modèle de table et du JTable
        tableModel = new DefaultTableModel(new String[]{"Nom", "Email", "Genre"}, 0);
        personTable = new JTable(tableModel);

        // Chargement des données de la base de données
        loadDataFromDatabase();

        // Création des boutons
        JButton addButton = new JButton("Ajouter");
        JButton detailButton = new JButton("Détails");

        // Disposition du formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(new JLabel("Nom :"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email :"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Genre :"), gbc);
        gbc.gridx = 1;
        formPanel.add(genderComboBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(addButton, gbc);

        // Ajout du tableau et des boutons au layout principal
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(personTable), BorderLayout.CENTER);
        add(detailButton, BorderLayout.SOUTH);

        // Action du bouton "Ajouter"
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String gender = (String) genderComboBox.getSelectedItem();

                if (!name.isEmpty() && !email.isEmpty()) {
                    addPersonToDatabase(name, email, gender);
                    tableModel.addRow(new Object[]{name, email, gender});
                    nameField.setText("");
                    emailField.setText("");
                    genderComboBox.setSelectedIndex(0);
                } else {
                    JOptionPane.showMessageDialog(PersonApp.this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action du bouton "Détails"
        detailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = personTable.getSelectedRow();
                if (selectedRow != -1) {
                    String name = (String) tableModel.getValueAt(selectedRow, 0);
                    String email = (String) tableModel.getValueAt(selectedRow, 1);
                    String gender = (String) tableModel.getValueAt(selectedRow, 2);

                    // Afficher les détails dans une JDialog
                    showPersonDetails(name, email, gender);
                } else {
                    JOptionPane.showMessageDialog(PersonApp.this, "Veuillez sélectionner une personne", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadDataFromDatabase() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT name, email, gender FROM persons")) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String gender = resultSet.getString("gender");
                tableModel.addRow(new Object[]{name, email, gender});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement des données : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPersonToDatabase(String name, String email, String gender) {
        String query = "INSERT INTO persons (name, email, gender) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, gender);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur d'ajout de personne : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPersonDetails(String name, String email, String gender) {
        JDialog detailDialog = new JDialog(this, "Détails de la personne", true);
        detailDialog.setSize(300, 200);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setLayout(new GridLayout(4, 1));

        detailDialog.add(new JLabel("Nom : " + name));
        detailDialog.add(new JLabel("Email : " + email));
        detailDialog.add(new JLabel("Genre : " + gender));

        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> detailDialog.dispose());
        detailDialog.add(closeButton);

        detailDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PersonApp app = new PersonApp();
            app.setVisible(true);
        });
    }
}
