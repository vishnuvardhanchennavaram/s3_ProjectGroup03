package library.views;

import javax.swing.*;
import library.utils.FileUtil;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream; 

@SuppressWarnings("serial")
public class SignUpPanel extends JPanel {

    private JComboBox<String> userTypeDropdown;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneNumberField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel cards;
    private String loginPanelKey;
    private static final String FILE_PATH = "src/users.csv";
//    private final String LOGINPANEL = "LoginPanel";
//    private final String SIGNUPPANEL = "SignUpPanel";

    public SignUpPanel(JPanel cards, String loginPanelKey) {
        this.cards = cards;
        this.loginPanelKey = loginPanelKey;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);
        // User type selection
        addLabelAndComponent("User Type:", new JComboBox<>(new String[]{"Member", "Librarian"}), constraints, 0);

        // Text fields setup
        firstNameField = addLabelAndTextField("First Name:", constraints, 1);
        lastNameField = addLabelAndTextField("Last Name:", constraints, 2);
        emailField = addLabelAndTextField("Email ID:", constraints, 3);
        phoneNumberField = addLabelAndTextField("Phone Number:", constraints, 4);
        usernameField = addLabelAndTextField("Username:", constraints, 5);
        passwordField = new JPasswordField(20);
        addLabelAndComponent("Password:", passwordField, constraints, 6);

        // Navigation buttons
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            resetFields();
            ((CardLayout) cards.getLayout()).show(cards, loginPanelKey);
        });
        
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> performRegistration());

        constraints.gridy = 7;
        constraints.gridx = 0;
        add(backButton, constraints);
        constraints.gridx = 1;
        add(registerButton, constraints);
    }

    private JTextField addLabelAndTextField(String labelText, GridBagConstraints constraints, int gridy) {
        JLabel label = new JLabel(labelText);
        constraints.gridx = 0;
        constraints.gridy = gridy;
        add(label, constraints);
        JTextField textField = new JTextField(20);
        constraints.gridx = 1;
        add(textField, constraints);
        return textField;
    }

    @SuppressWarnings("unchecked")
	private void addLabelAndComponent(String labelText, JComponent component, GridBagConstraints constraints, int gridy) {
        JLabel label = new JLabel(labelText);
        constraints.gridx = 0;
        constraints.gridy = gridy;
        add(label, constraints);
        constraints.gridx = 1;
        add(component, constraints);
        if (component instanceof JComboBox) {
            userTypeDropdown = (JComboBox<String>) component;
        }
    }

    
    private void performRegistration() {
    	Map<String, String> userData = new LinkedHashMap<>();
        userData.put("User Type", userTypeDropdown.getSelectedItem().toString());
        userData.put("First Name", firstNameField.getText());
        userData.put("Last Name", lastNameField.getText());
        userData.put("Email ID", emailField.getText());
        userData.put("Phone Number", phoneNumberField.getText());
        userData.put("Username", usernameField.getText());
        userData.put("Password", new String(passwordField.getPassword())); // Convert char[] to String
        if (registerUser(userData)) {
        	JOptionPane.showMessageDialog(this, "Registration Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
        	((CardLayout) cards.getLayout()).show(cards, loginPanelKey);
//            JOptionPane.showMessageDialog(this, "Registration Successful");
            
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed");
        }
        
    }
    private void resetFields() {
        userTypeDropdown.setSelectedIndex(0); // Reset to the first option
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneNumberField.setText("");
        usernameField.setText("");
        passwordField.setText("");
    }
    
    public static boolean registerUser(Map<String, String> userData) {
        if (!hasMandatoryFields(userData)) {
            System.out.println("Mandatory fields are missing. User data not written to CSV.");
            return false;
        }

        if (isDuplicate(userData)) {
            System.out.println("Duplicate user data detected. User data not written to CSV.");
            return false;
        }

        // Prepare the CSV data line
        StringBuilder csvData = new StringBuilder();
        userData.forEach((key, value) -> csvData.append(value).append(","));

        // Remove the last comma
        if (csvData.length() > 0) csvData.setLength(csvData.length() - 1);

        // Write to CSV using utility class
        return FileUtil.writeToCSV1(FILE_PATH, Collections.singletonList(csvData.toString()));
    }


    private static boolean hasMandatoryFields(Map<String, String> userData) {
        String[] mandatoryFields = {"First Name", "Email ID", "User Type", "Username", "Password"};
        for (String field : mandatoryFields) {
            if (!userData.containsKey(field) || userData.get(field).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    public static boolean isDuplicate(Map<String, String> userData) {
        FileUtil fileUtil = FileUtil.getInstance(); // Use singleton instance of FileUtil
        try (Stream<String> lines = Files.lines(Paths.get(FILE_PATH))) {
            return lines.anyMatch(line -> {
                String[] fields = line.split(",");
                return fields.length >= 7 && 
                       fields[0].equals(userData.get("User Type")) && 
                       fields[5].equals(userData.get("Username")) &&
                       fields[3].equals(userData.get("Email ID"));
            });
        } catch (IOException e) {
            System.err.println("An error occurred while reading from the CSV file: " + e.getMessage());
            return false;
        }
    }
}
