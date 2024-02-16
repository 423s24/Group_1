package sssp.Control;

import sssp.Model.GuestModel;
import sssp.View.MockForm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GuestController extends Controller implements PropertyChangeListener {

    private MockForm view;
    private GuestModel model;

    public GuestController(MockForm view, GuestModel model){
        this.view = view;
        this.model = model;
        model.addEventListener(this);
        setupViewEvents();
    }

    private void setupViewEvents() {
        view.getSubmitButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addNewGuest(view.getFirstNameTextField().getText(), view.getLastNameTextField().getText(), view.getPasswordTextField().getText());
            }
        });
    }

    private void onGuestAdded(){
        view.getSubmitButton().setBackground(Color.red);
        System.out.println("Successfully added new guest!");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        GuestModel.Events event = GuestModel.Events.valueOf(evt.getPropertyName());
        switch (event){
            case GuestAdded -> onGuestAdded();
        }
    }
}
