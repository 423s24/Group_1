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

    public MockForm() {
    }

    public static void main(String[] args) {


        //controller instantiation
        //new GuestController(this, new GuestModel());
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
