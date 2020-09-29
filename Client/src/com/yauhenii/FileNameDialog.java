package com.yauhenii;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileNameDialog extends JDialog {

    private String fileName;

    private JPanel mainPanel;
    private JTextField fileNameField;
    private JButton acceptButton;
    private JButton cancelButton;

    private MainWindow owner;

    public FileNameDialog(MainWindow owner) {
        super(owner,true);
        this.owner=owner;
        fileName=null;

        mainPanel=new JPanel(new GridLayout(2,2));
        fileNameField=new JTextField();
        acceptButton=new JButton("Accept");
        cancelButton=new JButton("Cancel");

        addComponents();
        addListeners();
        setWindowPreferences();
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
        mainPanel.add(new JLabel("File name:"));
        mainPanel.add(fileNameField);
        mainPanel.add(acceptButton);
        mainPanel.add(cancelButton);
    }

    private void setWindowPreferences() {
        setTitle("File name dialog");
//        setIconImage(ResourceLoader.getImage("icon/order-info.png"));
        setContentPane(mainPanel);
//        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setResizable(false);
        pack();
    }
}
