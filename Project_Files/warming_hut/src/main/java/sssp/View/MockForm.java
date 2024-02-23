package sssp.View;

import sssp.Control.GuestController;
import sssp.Model.GuestModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MockForm {

    private JTextField firstNameTextField;
    public JTextField getFirstNameTextField() {
        return firstNameTextField;
    }
    private JPasswordField passwordTextField;
    public JPasswordField getPasswordTextField() {
        return passwordTextField;
    }

    private JButton submitButton;
    public JButton getSubmitButton() {
        return submitButton;
    }

    private JPanel MockPanel;
    private JTextField lastNameTextField;
    public JTextField getLastNameTextField() {
        return lastNameTextField;
    }

    public JPanel getMockPanel() {
        return MockPanel;
    }

    private MainMenuMockup mainMenuMockup;
    public MockForm(MainMenuMockup mainMenuMockup) {
        this.mainMenuMockup = mainMenuMockup;
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameTextField.getText();
                String lastName = lastNameTextField.getText();
                String password = new String(passwordTextField.getPassword());
                String date = "1/1/2000"; // Placeholder date

                mainMenuMockup.addRowToTable(firstName, lastName, date); // Sends to menu table

                // Just some console checking stuff
                System.out.println("First Name: " + firstName);
                System.out.println("Last Name: " + lastName);
                System.out.println("Password: " + password);

                // Clear fields after submission
                firstNameTextField.setText("");
                lastNameTextField.setText("");
                passwordTextField.setText("");
            }
        });
    }

    public static void main(String[] args) {


        //controller instantiation
        //new GuestController(this, new GuestModel());
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
