package com.yauhenii.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileNameDialog extends JDialog {

    private JPanel mainPanel;
    private JLabel fileNameLabel;
    private JTextField fileNameField;
    private JButton acceptButton;
    private JButton cancelButton;

    private MainWindow owner;

    public FileNameDialog(MainWindow owner) {
        super(owner, true);
        this.owner = owner;

        mainPanel = new JPanel(new GridLayout(2, 2));
        fileNameLabel = new JLabel("File name ");
        fileNameField = new JTextField();
        acceptButton = new JButton("Accept");
        cancelButton = new JButton("Cancel");

        addComponents();
        addListeners();
        configureComponents();
        setWindowPreferences();
    }

    private void configureComponents() {
        fileNameLabel.setHorizontalAlignment(JLabel.CENTER);
    }

    private void addListeners() {
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                owner.setCurrentFileName(fileNameField.getText());
                FileNameDialog.this.dispose();
            }
        });
        cancelButton.addActionListener(e -> FileNameDialog.this.dispose());
    }

    private void addComponents() {
        mainPanel.add(fileNameLabel);
        mainPanel.add(fileNameField);
        mainPanel.add(acceptButton);
        mainPanel.add(cancelButton);
    }

    private void setWindowPreferences() {
        setTitle("File name dialog");
//        setIconImage(ResourceLoader.getImage("icon/order-info.png"));
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(WindowConfig.getFileNameDialogScreenWidth(),
            WindowConfig.getFileNameDialogScreenHeight()));
        setResizable(false);
        pack();
    }
}
