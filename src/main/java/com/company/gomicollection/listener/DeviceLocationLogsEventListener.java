package com.company.gomicollection.listener;

import com.company.gomicollection.entity.DeviceLocationLogs;
import io.jmix.core.event.EntityChangedEvent;
import io.jmix.core.event.EntityLoadingEvent;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DeviceLocationLogsEventListener {

    @EventListener
    public void onDeviceLocationLogsLoading(final EntityLoadingEvent<DeviceLocationLogs> event) {

    }

    @EventListener
    public void onDeviceLocationLogsSaving(final EntitySavingEvent<DeviceLocationLogs> event) {
        DeviceLocationLogs entity = event.getEntity();
        if (entity.getLatitude() != null) {
            entity.setLatitude(entity.getLatitude()
                    .setScale(6, RoundingMode.HALF_UP));
        }
        if (entity.getLongitude() != null) {
            entity.setLongitude(entity.getLongitude()
                    .setScale(6, RoundingMode.HALF_UP));
        }
    }

    @EventListener
    public void onDeviceLocationLogsChangedBeforeCommit(final EntityChangedEvent<DeviceLocationLogs> event) {

    }

    @TransactionalEventListener
    public void onDeviceLocationLogsChangedAfterCommit(final EntityChangedEvent<DeviceLocationLogs> event) {

    }
}