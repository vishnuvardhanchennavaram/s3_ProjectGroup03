package library.models;

import java.util.ArrayList;
import java.util.List;

import library.utils.FileUtil;

public class Users {
    private static final String FILE_PATH = "src/Users.csv";

    private String userType;
    private String firstName;
    private String lastName;
    private String email;
    private String phNo;
    private String userName;
    private String password;

    // Constructor
    public Users(String userType, String firstName, String lastName, String email, String phNo, String userName, String password) {
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phNo = phNo;
        this.userName = userName;
        this.password = password;
    }

    // Getters and setters
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhNo() {
        return phNo;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Method to get user details by username
    public static Users getUserByUsername(List<Users> usersList, String username) {
        for (Users user : usersList) {
            if (user.getUserName().equals(username)) {
                return user;
            }
        }
        return null; // If user with the given username is not found
    }

//    public static Users getUserByFullName(List<Users> usersList, String fullName) {
//        for (Users user : usersList) {
//        	
//            if (user.getUserName().equals(username)) {
//                return user;
//            }
//        }
//        return null; // If user with the given username is not found
//    }
    
    // Method to load users from a CSV file
    public static List<Users> loadUsersFromCSV() {
        List<Users> userList = new ArrayList<>();
        FileUtil fileUtil = FileUtil.getInstance();
        List<String> lines = fileUtil.readFile(FILE_PATH);
        for (String line : lines) {
            String[] data = line.split(",");
            if (data.length >= 7) {
                String userType = data[0].trim();
                String firstName = data[1].trim();
                String lastName = data[2].trim();
                String email = data[3].trim();
                String phNo = data[4].trim();
                String userName = data[5].trim();
                String password = data[6].trim();

                Users user = new Users(userType, firstName, lastName, email, phNo, userName, password);
                userList.add(user);
            }
        }
        return userList;
    }

    // Override toString() method for better representation
    @Override
    public String toString() {
        return "Users{" +
                "userType='" + userType + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phNo='" + phNo + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
