package com.company.gomicollection.view.devicelocationlogs;

import com.company.gomicollection.entity.DeviceLocationLogs;
import com.company.gomicollection.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import io.jmix.core.validation.group.UiCrossFieldChecks;
import io.jmix.flowui.action.SecuredBaseAction;
import io.jmix.flowui.component.UiComponentUtils;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.validation.ValidationErrors;
import io.jmix.flowui.kit.action.Action;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.model.InstanceLoader;
import io.jmix.flowui.view.*;
import com.vaadin.flow.data.renderer.NumberRenderer;

import javax.swing.*;
import java.util.Objects;

@Route(value = "deviceLocationLogs", layout = MainView.class)
@ViewController(id = "DeviceLocationLogs.list2")
@ViewDescriptor(path = "device-location-logs-list-view2.xml")
@LookupComponent("deviceLocationLogsesDataGrid")
@DialogMode(width = "64em")
public class DeviceLocationLogsListView2 extends StandardListView<DeviceLocationLogs> {

    @ViewComponent
    private DataContext dataContext;

    @ViewComponent
    private CollectionContainer<DeviceLocationLogs> deviceLocationLogsesDc;

    @ViewComponent
    private InstanceContainer<DeviceLocationLogs> deviceLocationLogsDc;

    @ViewComponent
    private InstanceLoader<DeviceLocationLogs> deviceLocationLogsDl;

    @ViewComponent
    private VerticalLayout listLayout;

    @ViewComponent
    private DataGrid<DeviceLocationLogs> deviceLocationLogsesDataGrid;

    @ViewComponent
    private FormLayout form;

    @ViewComponent
    private HorizontalLayout detailActions;

    @ViewComponent
    private TextField latitudeField;

    @ViewComponent
    private TextField longhitudeField;
    @Subscribe
    public void onInit(final InitEvent event) {
        deviceLocationLogsesDataGrid.getActions().forEach(action -> {
            if (action instanceof SecuredBaseAction secured) {
                secured.addEnabledRule(() -> listLayout.isEnabled());
            }
        });

        Objects.requireNonNull(deviceLocationLogsesDataGrid.getColumnByKey("latitude"))
                .setRenderer(new NumberRenderer<>(
                        DeviceLocationLogs::getLatitude,
                        "%.6f"
                ));

        Objects.requireNonNull(deviceLocationLogsesDataGrid.getColumnByKey("longitude"))
                .setRenderer(new NumberRenderer<>(
                        DeviceLocationLogs::getLongitude,
                        "%.6f"
                ));



    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        updateControls(false);
    }

    @Subscribe("deviceLocationLogsesDataGrid.create")
    public void onDeviceLocationLogsesDataGridCreate(final ActionPerformedEvent event) {
        dataContext.clear();
        DeviceLocationLogs entity = dataContext.create(DeviceLocationLogs.class);
        deviceLocationLogsDc.setItem(entity);
        updateControls(true);



    }

    @Subscribe("deviceLocationLogsesDataGrid.edit")
    public void onDeviceLocationLogsesDataGridEdit(final ActionPerformedEvent event) {
        updateControls(true);
    }

    @Subscribe("saveButton")
    public void onSaveButtonClick(final ClickEvent<JmixButton> event) {
        DeviceLocationLogs item = deviceLocationLogsDc.getItem();
        ValidationErrors validationErrors = validateView(item);
        if (!validationErrors.isEmpty()) {
            ViewValidation viewValidation = getViewValidation();
            viewValidation.showValidationErrors(validationErrors);
            viewValidation.focusProblemComponent(validationErrors);
            return;
        }
        dataContext.save();
        deviceLocationLogsesDc.replaceItem(item);
        updateControls(false);
    }

    @Subscribe("cancelButton")
    public void onCancelButtonClick(final ClickEvent<JmixButton> event) {
        dataContext.clear();
        deviceLocationLogsDl.load();
        updateControls(false);
    }

    @Subscribe(id = "deviceLocationLogsesDc", target = Target.DATA_CONTAINER)
    public void onDeviceLocationLogsesDcItemChange(final InstanceContainer.ItemChangeEvent<DeviceLocationLogs> event) {
        DeviceLocationLogs entity = event.getItem();
        dataContext.clear();
        if (entity != null) {
            deviceLocationLogsDl.setEntityId(entity.getId());
            deviceLocationLogsDl.load();
        } else {
            deviceLocationLogsDl.setEntityId(null);
            deviceLocationLogsDc.setItem(null);
        }
        updateControls(false);
    }




    protected ValidationErrors validateView(DeviceLocationLogs entity) {
        ViewValidation viewValidation = getViewValidation();
        ValidationErrors validationErrors = viewValidation.validateUiComponents(form);
        if (!validationErrors.isEmpty()) {
            return validationErrors;
        }
        validationErrors.addAll(viewValidation.validateBeanGroup(UiCrossFieldChecks.class, entity));
        return validationErrors;
    }

    private void updateControls(boolean editing) {
        UiComponentUtils.getComponents(form).forEach(component -> {
            if (component instanceof HasValueAndElement<?, ?> field) {
                field.setReadOnly(!editing);
            }
        });

        detailActions.setVisible(editing);
        listLayout.setEnabled(!editing);
        deviceLocationLogsesDataGrid.getActions().forEach(Action::refreshState);
    }

    private ViewValidation getViewValidation() {
        return getApplicationContext().getBean(ViewValidation.class);
    }
}