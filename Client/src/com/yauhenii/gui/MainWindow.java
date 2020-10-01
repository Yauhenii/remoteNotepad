package com.yauhenii.gui;

import com.yauhenii.client.Client;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import lombok.Setter;

public class MainWindow extends JFrame {

    private Client client;
    private String userName;
    @Setter
    private String currentFileName;

    private JScrollPane mainScrollPane;

    private JPanel mainPanel;
    private JPanel authPanel;

    private JButton enterButton;
    private JButton exitButton;

    JTextArea mainTextArea;

    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameTextField;
    private JPasswordField passwordField;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem deleteItem;
    private JMenuItem saveAsItem;
    private JMenu userMenu;
    private JMenuItem newKeyItem;
    private JMenuItem showKeyItem;
    private JMenuItem logOutItem;

    public MainWindow(Client client) {
        //client
        this.client = client;
        //authPanel
        authPanel = new JPanel(new GridLayout(3, 2));
        enterButton = new JButton("Log in");
        exitButton = new JButton("Exit");
        usernameLabel = new JLabel("Username");
        usernameTextField = new JTextField();
        passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField();
        addComponentsToAuthPanel();
//        configureAuthPanelComponents();
        //mainPanel
        mainPanel = new JPanel(new BorderLayout());
        mainTextArea = new JTextArea();
        mainScrollPane = new JScrollPane(mainTextArea);
        addComponentsToMainPanel();
        configureMainPanelComponents();
        //menu
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        openItem = new JMenuItem("Open...");
        deleteItem = new JMenuItem("Delete file...");
        saveAsItem = new JMenuItem("Save as...");
        userMenu = new JMenu("User");
        newKeyItem = new JMenuItem("Generate new key");
        showKeyItem = new JMenuItem("Show key");
        logOutItem = new JMenuItem("Log out");
        configureMenu();
        addMenuListeners();
        setWindowPreferences();
    }

    private void addComponentsToAuthPanel() {
        authPanel.add(usernameLabel);
        authPanel.add(usernameTextField);
        authPanel.add(passwordLabel);
        authPanel.add(passwordField);
        authPanel.add(enterButton);
        authPanel.add(exitButton);
    }
//
//    private void configureAuthPanelComponents() {
//        usernameLabel.setFont(WindowConfig.getTextFont());
//        enterButton.setFont(WindowConfig.getTextFont());
//        exitButton.setFont(WindowConfig.getTextFont());
//    }

    private void addComponentsToMainPanel() {
        mainPanel.add(mainScrollPane, BorderLayout.CENTER);
    }

    private void configureMainPanelComponents() {

    }

    private void setWindowPreferences() {
        showAuthPanel();
//        showMainPanel();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void showAuthPanel() {
        setTitle("Log in");
//        setIconImage(ResourceLoader.getImage("icon/who-are-you.png"));

        JComponent contentPane = (JPanel) MainWindow.this.getContentPane();
        contentPane.removeAll();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(authPanel, BorderLayout.CENTER);
        contentPane.revalidate();
        contentPane.repaint();

        menuBar.setVisible(false);
        setBounds(0, 0, WindowConfig.getAuthScreenWidth(), WindowConfig.getAuthScreenHeight());
    }

    private void showMainPanel() {
        setTitle("Client app: " + userName);
//        setIconImage(ResourceLoader.getImage("icon/client.png"));

        JComponent contentPane = (JPanel) MainWindow.this.getContentPane();
        contentPane.removeAll();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.revalidate();
        contentPane.repaint();

        menuBar.setVisible(true);
        MainWindow.this
            .setBounds(0, 0, WindowConfig.getScreenWidth(), WindowConfig.getScreenHeight());
    }

    private void configureMenu() {
        menuBar = new JMenuBar();
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(deleteItem);
        fileMenu.add(saveAsItem);

        userMenu.add(newKeyItem);
        userMenu.add(showKeyItem);
        userMenu.addSeparator();
        userMenu.add(logOutItem);

        menuBar.add(fileMenu);
        menuBar.add(userMenu);
        setJMenuBar(menuBar);

        menuBar.setVisible(false);
    }

    private void addMenuListeners() {
        openItem.addActionListener(e -> {
            try {
                FileNameDialog fileNameDialog = new FileNameDialog(MainWindow.this);
                fileNameDialog.setVisible(true);
                if (currentFileName != null) {
                    byte[] bytes = client.sendRequestForFileMessage(currentFileName);
                    mainTextArea.setText(new String(bytes));
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(MainWindow.this,
                    "File is not found", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        saveAsItem.addActionListener(e -> {
            if (!mainTextArea.getText().trim().equals("")) {
                try {
                    FileNameDialog fileNameDialog = new FileNameDialog(MainWindow.this);
                    fileNameDialog.setVisible(true);
                    if (currentFileName != null) {
                        byte[] bytes = mainTextArea.getText().getBytes();
                        client.sendSaveAsMessage(currentFileName, bytes);
                    }
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(MainWindow.this,
                        "File is not found", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(MainWindow.this,
                    "File is empty", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        logOutItem.addActionListener(e -> {
            try {
                client.sendEndMessage();
                MainWindow.this
                    .dispatchEvent(new WindowEvent(MainWindow.this, WindowEvent.WINDOW_CLOSING));
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(MainWindow.this,
                    "Can not log out", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        newItem.addActionListener(e -> {
            Object[] options = {"Cancel", "No", "Yes"};
            int option;
            if (!mainTextArea.getText().trim().equals("")) {
                option = JOptionPane.showOptionDialog(MainWindow.this,
                    "Do you want to save your work?",
                    "New file",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);
                if (option == 1) {
                    mainTextArea.setText("");
                } else if (option == 2) {
                    saveAsItem.doClick();
                    mainTextArea.setText("");
                }
            }
        });
        newKeyItem.addActionListener(e -> {
            try {
                client.sendGenerateMessage();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(MainWindow.this,
                    "Can not log out", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        showKeyItem.addActionListener(e -> JOptionPane.showMessageDialog(MainWindow.this,
            client.getSessionKey(), "Info", JOptionPane.INFORMATION_MESSAGE));
        enterButton.addActionListener(e -> {
            try {
                String userName = usernameTextField.getText();
                MainWindow.this.userName = userName;
                String password = new String(passwordField.getPassword());
                if (client.sendLogInMessage(userName + "-" + password)) {
                    showMainPanel();
                } else {
                    JOptionPane.showMessageDialog(MainWindow.this,
                        "Invalid credentials", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(MainWindow.this,
                    "Can log in", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        exitButton.addActionListener(e -> MainWindow.this
            .dispatchEvent(new WindowEvent(MainWindow.this, WindowEvent.WINDOW_CLOSING)));
        deleteItem.addActionListener(e -> {
            try {
                FileNameDialog fileNameDialog = new FileNameDialog(MainWindow.this);
                fileNameDialog.setVisible(true);
                if (currentFileName != null) {
                    client.sendDeleteFileMessage(currentFileName);
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(MainWindow.this,
                    "File is not found", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
