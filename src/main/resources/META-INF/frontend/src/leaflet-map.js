import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import 'leaflet/dist/leaflet.js';

class LeafletMap extends PolymerElement {
    static get is() {
        return 'leaflet-map';
    }

    constructor() {
        super();
        this._map = null;
    }

    connectedCallback() {
        super.connectedCallback();

        // Initialize the map
        this._map = L.map(this);

        // Add OpenStreetMap tiles
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(this._map);

        // Set default view
        this._map.setView([51.505, -0.09], 13);

        // Force a resize after the map is added to ensure proper rendering
        setTimeout(() => {
            this._map.invalidateSize();
        }, 100);
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