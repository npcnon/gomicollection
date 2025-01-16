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
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import com.company.gomicollection.entity.Coordinates;
import com.vaadin.flow.dom.DomEvent;
import elemental.json.JsonObject;
import com.vaadin.flow.component.combobox.ComboBox;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.jmix.flowui.view.View;
import io.jmix.core.common.event.Subscription;
import com.vaadin.flow.component.AttachEvent;
import io.jmix.flowui.view.Subscribe;
import jakarta.annotation.PostConstruct;
import elemental.json.Json;
import elemental.json.JsonArray;
import java.util.ArrayList;
import java.util.Arrays;
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
    private ComboBox<com.company.gomicollection.entity.Route> routeComboBox;

    @PostConstruct
    public void init() {
        // Create main layout
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        // Create top controls layout
        HorizontalLayout topControls = new HorizontalLayout();
        topControls.setWidthFull();
        topControls.setSpacing(true);
        topControls.setPadding(true);
        topControls.getStyle().set("z-index", "9999").set("position", "relative");

        // Create route selector
        routeComboBox = new ComboBox<>("Select Route");
        routeComboBox.setPlaceholder("Search routes...");
        routeComboBox.setItemLabelGenerator(route -> route.getRoute_name());
        routeComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                displayRoute(event.getValue());
                startRouteButton.setEnabled(false);
            } else {
                clearMap();
                startRouteButton.setEnabled(true);
            }
        });
        updateRouteComboBox();

        // Create button layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        startRouteButton = new Button("Start New Route", e -> startNewRoute());
        finishRouteButton = new Button("Finish Route", e -> finishRoute());
        finishRouteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        finishRouteButton.setVisible(false);

        buttonLayout.add(startRouteButton, finishRouteButton);

        // Add components to top controls
        topControls.add(routeComboBox, buttonLayout);
        topControls.setFlexGrow(1, routeComboBox);

        // Add the map component
        map = new LeafletMap();
        map.setHeight("600px");
        map.setWidth("100%");

        // Add the point-added event listener
        map.getElement().addEventListener("point-added", event -> {
            getUI().ifPresent(ui -> ui.access(() -> handlePointAdded(event)));
        }).addEventData("event.detail");

        mainLayout.add(topControls, map);
        mainLayout.getStyle().set("position", "relative").set("z-index", "1");
        getContent().add(mainLayout);
    }
    private void updateRouteComboBox() {
        List<com.company.gomicollection.entity.Route> routes = dataManager.load(com.company.gomicollection.entity.Route.class)
                .all()
                .list();
        routeComboBox.setItems(routes);
    }
    private void displayRoute(com.company.gomicollection.entity.Route route) {
        clearMap();
        List<Coordinates> coordinates = dataManager.load(Coordinates.class)
                .query("select c from Coordinates c where c.route = :route order by c.id")
                .parameter("route", route)
                .list();

        // Create a list to hold coordinate arrays
        List<List<Double>> coordArrays = new ArrayList<>();

        for (Coordinates coord : coordinates) {
            List<Double> point = Arrays.asList(
                    coord.getLatitude().doubleValue(),
                    coord.getLongitude().doubleValue(),
                    (double) getPositionType(coord.getPosition())
            );
            coordArrays.add(point);
        }

        // Convert to JsonArray for proper serialization
        JsonArray jsonArray = Json.createArray();
        for (int i = 0; i < coordArrays.size(); i++) {
            JsonArray point = Json.createArray();
            List<Double> coords = coordArrays.get(i);
            point.set(0, coords.get(0));
            point.set(1, coords.get(1));
            point.set(2, coords.get(2));
            jsonArray.set(i, point);
        }

        // Call the JavaScript function with the JsonArray
        map.getElement().callJsFunction("displayExistingRoute", jsonArray);
    }

    private int getPositionType(CoordinatePosition position) {
        if (position == CoordinatePosition.START) return 0;
        if (position == CoordinatePosition.END) return 2;
        return 1;
    }

    private void clearMap() {
        map.getElement().callJsFunction("clearRoute");
        currentRoute = null;
        coordinatesList.clear();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        updateRouteComboBox(); // Refresh routes list when entering the view
    }

    private void onRouteFinished() {
        clearMap();
        updateRouteComboBox();
        routeComboBox.setValue(null);
        startRouteButton.setEnabled(true);
    }

    private void handlePointAdded(DomEvent event) {
        if (currentRoute != null) {
            JsonObject detail = event.getEventData().getObject("event.detail");
            double lat = detail.getNumber("lat");
            double lng = detail.getNumber("lng");

            Coordinates coordinate = dataManager.create(Coordinates.class);

            // Set position based on point order
            if (coordinatesList.isEmpty()) {
                coordinate.setPosition(CoordinatePosition.START);
            } else if (coordinatesList.size() >= 2) {
                // Update previous "normal" point if it exists
                Coordinates previousCoordinate = coordinatesList.get(coordinatesList.size() - 1);
                if (previousCoordinate.getPosition() == CoordinatePosition.NORMAL) {
                    previousCoordinate.setPosition(CoordinatePosition.END);
                    coordinate.setPosition(CoordinatePosition.END);
                } else {
                    coordinate.setPosition(CoordinatePosition.NORMAL);
                }
            } else {
                coordinate.setPosition(CoordinatePosition.NORMAL);
            }

            coordinate.setLatitude(BigDecimal.valueOf(lat));
            coordinate.setLongitude(BigDecimal.valueOf(lng));
            coordinate.setRoute(currentRoute);

            coordinatesList.add(coordinate);

            // Show finish button after 2 points
            if (coordinatesList.size() >= 2) {
                finishRouteButton.setVisible(true);
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
            onRouteFinished();
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