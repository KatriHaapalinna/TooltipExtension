package org.vaadin.addons.TooltipExtension;

import org.vaadin.addons.TooltipExtension.client.TooltipExtensionServerRpc;
import org.vaadin.addons.TooltipExtension.client.TooltipExtensionState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.AbstractComponent;

public class TooltipExtension extends AbstractExtension {

    protected TooltipExtension() {
        registerRpc(new TooltipExtensionServerRpc() {
        });
    }

    @Override
    protected TooltipExtensionState getState() {
        return (TooltipExtensionState) super.getState();
    }

    public void extend(AbstractComponent component) {
        if (component.getDescription() != null) {
            component.setDescription("");
        }
        super.extend(component);
    }

}
