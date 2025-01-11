package com.company.gomicollection.view.map;

import com.company.gomicollection.view.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "mapview", layout = MainView.class)
@ViewController("Mapview")
@ViewDescriptor("MapView.xml")
public class Mapview extends StandardView {

    public Mapview() {
        // Create a Vaadin layout
        VerticalLayout verticalLayout = new VerticalLayout();

        // Create a Vaadin button with a click listener
        Button vaadinButton = new Button("Click Me!",
                event -> Notification.show("Hello from Vaadin!"));

        // Add the button to the Vaadin layout
        verticalLayout.add(vaadinButton);

        // Add the Vaadin layout to the view's content
        getContent().add(verticalLayout);
    }
}