import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import 'leaflet/dist/leaflet.js';

class LeafletMap extends PolymerElement {
    static get is() {
        return 'leaflet-map';
    }

    constructor() {
        super();
        this._map = null;
        this._markers = [];
        this._polyline = null;
        this._isRouteCreationActive = false;
        this._routeCreationOverlay = null;
    }

    displayExistingRoute(coordinates) {
        console.log('Received coordinates:', coordinates);
        this.clearRoute();

        if (!coordinates || coordinates.length === 0) {
            console.log('No coordinates received');
            return;
        }

        // Create markers and polyline
        coordinates.forEach(coord => {
            const [lat, lng, type] = coord;

            // Create marker with appropriate icon based on type
            let icon;
            if (type === 0) { // START
                icon = L.divIcon({
                    html: '<div style="background-color: green; width: 10px; height: 10px; border-radius: 50%;"></div>',
                    className: 'start-marker'
                });
            } else if (type === 2) { // END
                icon = L.divIcon({
                    html: '<div style="background-color: #eeff00; width: 10px; height: 10px; border-radius: 50%;"></div>',
                    className: 'end-marker'
                });
            } else { // NORMAL
                icon = L.divIcon({
                    html: '<div style="background-color: blue; width: 8px; height: 8px; border-radius: 50%;"></div>',
                    className: 'normal-marker'
                });
            }

            const marker = L.marker([lat, lng], { icon }).addTo(this._map);
            this._markers.push(marker);
        });

        // Create polyline from all points
        const points = coordinates.map(coord => [coord[0], coord[1]]);
        this._polyline = L.polyline(points, { color: 'red' }).addTo(this._map);

        // Fit map bounds to show the entire route
        if (points.length > 0) {
            const bounds = L.latLngBounds(points);
            this._map.fitBounds(bounds, { padding: [50, 50] });
        }
    }

    connectedCallback() {
        super.connectedCallback();

        this._map = L.map(this);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(this._map);

        this._map.setView([51.505, -0.09], 13);

        // In the click event handler:
        this._map.on('click', (e) => {
            if (this._isRouteCreationActive) {
                const lat = e.latlng.lat;
                const lng = e.latlng.lng;

                console.log('Adding point:', lat, lng); // Debug log

                // Add marker
                const marker = L.marker([lat, lng]).addTo(this._map);
                this._markers.push(marker);

                // Update polyline
                const points = this._markers.map(marker => marker.getLatLng());
                if (this._polyline) {
                    this._polyline.setLatLngs(points);
                } else {
                    // In LeafletMap.js, change the polyline color from red to something like blue
                    this._polyline = L.polyline(points, { color: 'blue', weight: 3 }).addTo(this._map);
                }

                // Update start/end markers
                this.updateStartEndMarkers();

                // Dispatch event with coordinates
                const event = new CustomEvent('point-added', {
                    detail: {
                        lat: lat,
                        lng: lng
                    },
                    bubbles: true,
                    composed: true
                });
                console.log('Dispatching event:', event); // Debug log
                this.dispatchEvent(event);
            }
        });

        setTimeout(() => {
            this._map.invalidateSize();
        }, 100);
    }


    updateStartEndMarkers() {
        if (this._markers.length > 0) {
            // Update first marker icon
            const startIcon = L.divIcon({
                html: '<div style="background-color: green; width: 10px; height: 10px; border-radius: 50%;"></div>',
                className: 'start-marker'
            });
            this._markers[0].setIcon(startIcon);

            // Update last marker icon if there are multiple points
            if (this._markers.length > 1) {
                const endIcon = L.divIcon({
                    html: '<div style="background-color: red; width: 10px; height: 10px; border-radius: 50%;"></div>',
                    className: 'end-marker'
                });
                this._markers[this._markers.length - 1].setIcon(endIcon);
            }
        }
    }

    startRouteCreation() {
        this._isRouteCreationActive = true;
        this.clearRoute();

        // Add an overlay with instructions
        this._routeCreationOverlay = L.control({position: 'topright'});
        this._routeCreationOverlay.onAdd = (map) => {
            const div = L.DomUtil.create('div', 'route-creation-overlay');
            div.style.cssText = `
            background-color: white;
            padding: 10px;
            border: 2px solid #666;
            border-radius: 4px;
            z-index: 1000;
            position: relative;
        `;
            div.innerHTML = 'Click on the map to add route points';
            return div;
        };
        this._routeCreationOverlay.addTo(this._map);
    }

    stopRouteCreation() {
        this._isRouteCreationActive = false;
        if (this._routeCreationOverlay) {
            this._routeCreationOverlay.remove();
            this._routeCreationOverlay = null;
        }
    }

    clearRoute() {
        // Clear existing markers
        this._markers.forEach(marker => this._map.removeLayer(marker));
        this._markers = [];

        // Clear polyline
        if (this._polyline) {
            this._map.removeLayer(this._polyline);
            this._polyline = null;
        }
    }

    setCenter(lat, lon) {
        if (this._map) {
            this._map.setView([lat, lon], this._map.getZoom());
        }
    }

    setZoom(zoom) {
        if (this._map) {
            this._map.setZoom(zoom);
        }
    }
}

customElements.define(LeafletMap.is, LeafletMap);