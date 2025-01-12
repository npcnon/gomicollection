package com.company.gomicollection.view.map;

import com.company.gomicollection.view.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
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
        // Create main layout
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        // Add the map component
        LeafletMap map = new LeafletMap();
        map.setHeight("600px");
        map.setWidth("100%");

        mainLayout.add(map);
        getContent().add(mainLayout);
    }

    @Tag("leaflet-map")
    @NpmPackage(value = "leaflet", version = "1.9.4")
    @JsModule("./src/leaflet-map.js")
    @CssImport("leaflet/dist/leaflet.css")
    public static class LeafletMap extends Component {
        public LeafletMap() {
            // Initialize with default center coordinates
            setCenter(51.505, -0.09);
            setZoom(13);
            getElement().getStyle().set("display", "block");
        }

        // Add these methods for sizing
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