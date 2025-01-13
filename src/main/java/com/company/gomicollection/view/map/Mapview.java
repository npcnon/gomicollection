package com.company.gomicollection.view.map;

import com.company.gomicollection.entity.CoordinatePosition;
import com.company.gomicollection.view.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.theme.lumo.LumoUtility;
import io.jmix.core.DataManager;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import com.company.gomicollection.entity.Coordinates;
import com.vaadin.flow.dom.DomEvent;
import elemental.json.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Route(value = "mapview", layout = MainView.class)
@ViewController("Mapview")
@ViewDescriptor("MapView.xml")
@org.springframework.stereotype.Component
public class Mapview extends StandardView {

    @Autowired
    private DataManager dataManager;

    private LeafletMap map;
    private List<Coordinates> coordinatesList = new ArrayList<>();
    private com.company.gomicollection.entity.Route currentRoute;
    private Button startRouteButton;
    private Button finishRouteButton;

    public Mapview() {
        // Create main layout
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.getStyle()
                .set("z-index", "9999")
                .set("position", "relative");
        buttonLayout.setSpacing(true);
        buttonLayout.setPadding(true);

        startRouteButton = new Button("Start New Route", e -> startNewRoute());
        finishRouteButton = new Button("Finish Route", e -> finishRoute());
        finishRouteButton.getStyle().set("z-index", "9999");
        finishRouteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Initially hide finish button
        finishRouteButton.setVisible(false);

        // Add buttons to buttonLayout - THIS WAS MISSING
        buttonLayout.add(startRouteButton, finishRouteButton);

        // Add the map component
        map = new LeafletMap();
        map.setHeight("600px");
        map.setWidth("100%");

        // Add the point-added event listener
        map.getElement().addEventListener("point-added", event -> {
            getUI().ifPresent(ui -> ui.access(() -> handlePointAdded(event)));
        }).addEventData("event.detail");

        mainLayout.add(buttonLayout, map);
        mainLayout.getStyle().set("position", "relative");
        mainLayout.getStyle().set("z-index", "1");
        getContent().add(mainLayout);
    }

    private void handlePointAdded(DomEvent event) {
        if (currentRoute != null) {
            JsonObject detail = event.getEventData().getObject("event.detail");
            double lat = detail.getNumber("lat");
            double lng = detail.getNumber("lng");

            Coordinates coordinate = dataManager.create(Coordinates.class);

            if (coordinatesList.isEmpty())
            {
                coordinate.setPosition(CoordinatePosition.START);
            }
            else{
                coordinate.setPosition(CoordinatePosition.NORMAL);
            }
            coordinate.setLatitude(BigDecimal.valueOf(lat));
            coordinate.setLongitude(BigDecimal.valueOf(lng));
            coordinate.setRoute(currentRoute);

            coordinatesList.add(coordinate);

            System.out.println("Points count: " + coordinatesList.size()); // Debug log

            // Show finish button after 2 points - modified approach
            if (coordinatesList.size() >= 2) {
                finishRouteButton.setVisible(true);

                // Force button layout refresh
                finishRouteButton.getParent().ifPresent(parent -> {
                    if (parent instanceof HorizontalLayout) {
                        ((HorizontalLayout) parent).removeAll();
                        ((HorizontalLayout) parent).add(startRouteButton, finishRouteButton);
                    }
                });
            }
        }
    }

    private void startNewRoute() {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);

        dialog.getElement().getStyle().set("z-index", "9999999");
        dialog.addClassName("route-dialog");
        dialog.getElement().executeJs(
                "this.$.overlay.style.zIndex = '9999998';"
        );

        H3 title = new H3("Create New Route");
        title.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.MEDIUM);

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setSpacing(true);
        formLayout.setPadding(true);
        formLayout.setWidth("400px");

        TextField nameField = new TextField("Route Name");
        nameField.setWidth("100%");
        nameField.setRequired(true);
        nameField.setPlaceholder("Enter route name...");

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button createButton = new Button("Create", e -> {
            if (!nameField.isEmpty()) {
                createRoute(nameField.getValue());
                dialog.close();
                map.getElement().callJsFunction("startRouteCreation");
                startRouteButton.setEnabled(false);
            } else {
                nameField.setInvalid(true);
                nameField.setErrorMessage("Route name is required");
            }
        });
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(cancelButton, createButton);
        formLayout.add(title, nameField, buttonLayout);
        dialog.add(formLayout);
        dialog.open();
    }

    private void createRoute(String routeName) {
        currentRoute = dataManager.create(com.company.gomicollection.entity.Route.class);
        currentRoute.setRoute_name(routeName);
        coordinatesList.clear();
    }

    private void finishRoute() {
        if (currentRoute != null && !coordinatesList.isEmpty()) {
            // Save the route
            currentRoute = dataManager.save(currentRoute);

            // Set the last coordinate's position to END
            Coordinates lastCoordinate = coordinatesList.get(coordinatesList.size() - 1);
            lastCoordinate.setPosition(CoordinatePosition.END);
            dataManager.save(lastCoordinate); // Save the updated coordinate

            // Save all other coordinates
            for (int i = 0; i < coordinatesList.size() - 1; i++) {
                Coordinates coordinate = coordinatesList.get(i);
                coordinate.setRoute(currentRoute);
                dataManager.save(coordinate);
            }

            // Stop route creation and clear data
            map.getElement().callJsFunction("stopRouteCreation");
            map.getElement().callJsFunction("clearRoute");

            // Reset route and UI elements
            currentRoute = null;
            coordinatesList.clear();
            startRouteButton.setEnabled(true);
            finishRouteButton.setVisible(false);
        }
    }

    @Tag("leaflet-map")
    @NpmPackage(value = "leaflet", version = "1.9.4")
    @JsModule("./src/leaflet-map.js")
    @CssImport("leaflet/dist/leaflet.css")
    public static class LeafletMap extends Component {
        public LeafletMap() {
            setCenter(51.505, -0.09);
            setZoom(13);
            getElement().getStyle().set("display", "block");
        }

        public void setHeight(String height) {
            getElement().getStyle().set("height", height);
        }

        public void setWidth(String width) {
            getElement().getStyle().set("width", width);
        }

        public void setCenter(double lat, double lon) {
            getElement().callJsFunction("setCenter", lat, lon);
        }

        public void setZoom(int zoom) {
            getElement().callJsFunction("setZoom", zoom);
        }
    }
}