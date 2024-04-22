package library.views;

import java.awt.*;
import library.utils.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import library.views.LoginPanel.BackgroundPanel;
import library.views.SignUpPanel;
public class LoginPanel extends JFrame {

	private JPanel cards; // a panel that uses CardLayout
	private final String LOGINPANEL = "Login Card";
	private final String SIGNUPPANEL = "Sign Up Card";
	private static final String CSV_FILE = "src/users.csv";

	public LoginPanel() {
		initializeUI();
		setTitle("Library Management System");
		setSize(800, 650);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void initializeUI() {
		
		    cards = new JPanel(new CardLayout());
		    JPanel loginPanel = createLoginPanel();
		    JPanel signUpPanel = new SignUpPanel(cards, LOGINPANEL);  // Assuming SignUpPanel constructor is appropriate

		    cards.add(loginPanel, LOGINPANEL);
		    cards.add(signUpPanel, SIGNUPPANEL);  // Ensure this key matches the one used in the ActionListener

		    add(cards);
		

	}

	private JPanel createLoginPanel() {
		BufferedImage backgroundImage = null;
		try {
			backgroundImage = ImageIO.read(new File("src/bookImage.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		BackgroundPanel panel = new BackgroundPanel(backgroundImage);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(5, 5, 5, 5);

		JLabel userTypeLabel = new JLabel("User Type:");
		userTypeLabel.setForeground(Color.WHITE);
		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(userTypeLabel, constraints);
		JComboBox<String> userTypeDropdown = new JComboBox<>(new String[] { "Member", "Librarian" });
		constraints.gridx = 1;
		panel.add(userTypeDropdown, constraints);

		JLabel usernameLabel = new JLabel("Username:");
		usernameLabel.setForeground(Color.WHITE);
		constraints.gridx = 0;
		constraints.gridy = 1;
		panel.add(usernameLabel, constraints);
		JTextField usernameField = new JTextField(20);
		constraints.gridx = 1;
		panel.add(usernameField, constraints);

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setForeground(Color.WHITE);
		constraints.gridx = 0;
		constraints.gridy = 2;
		panel.add(passwordLabel, constraints);
		JPasswordField passwordField = new JPasswordField(20);
		constraints.gridx = 1;
		panel.add(passwordField, constraints);

		JButton loginButton = new JButton("Login");
		constraints.gridx = 0;
		constraints.gridy = 3;
		panel.add(loginButton, constraints);
		JButton signUpButton = new JButton("Sign Up");
		signUpButton.addActionListener(e -> ((CardLayout) cards.getLayout()).show(cards, SIGNUPPANEL));
		constraints.gridx = 1;
		panel.add(signUpButton, constraints);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> System.exit(0));
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.gridx = 0;
		panel.add(cancelButton, constraints);

		loginButton.addActionListener(e -> {
			String userType = userTypeDropdown.getSelectedItem().toString();
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			if (validateCredentials(userType, username, password)) {
				JPanel userPanel = userType.equals("Librarian") ? new LibrarianPanel(username, cards, LOGINPANEL): new MemberPanel(username, cards, LOGINPANEL);
				cards.add(userPanel, userType.toUpperCase() + "_PANEL");
				((CardLayout) cards.getLayout()).show(cards, userType.toUpperCase() + "_PANEL");
			} else {
				JOptionPane.showMessageDialog(this, "Username not found or password incorrect");
			}
		});
        
		signUpButton.addActionListener(e -> {
			CardLayout cl = (CardLayout) cards.getLayout(); // Access the CardLayout
	        cl.show(cards, SIGNUPPANEL);
		});
		return panel;
	}

	public static boolean validateCredentials(String userType, String username, String password) {
		FileUtil fileUtil = FileUtil.getInstance();  // Using singleton instance
	    List<String> csvLines = fileUtil.readFile(CSV_FILE);
	    Map<String, String[]> userMap = new HashMap<>();

        for (String line : csvLines) {
            String[] data = line.split(",");
            // Assume CSV format: User Type, Username, Password, etc.
            if (data.length >= 7) { // Check for sufficient data
                String key = data[0] + "_" + data[5];
                userMap.put(key, new String[]{data[0], data[6]}); // key -> [userType, password]
            }
        }

		String keyToValidate = userType + "_" + username;

		if (userMap.containsKey(keyToValidate)) {
			String[] details = userMap.get(keyToValidate);
			return details[1].equals(password);
		}
		return false;
	}

	class BackgroundPanel extends JPanel {
		private BufferedImage image;

		public BackgroundPanel(BufferedImage image) {
			this.image = image;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new LoginPanel().setVisible(true));
	}

}
