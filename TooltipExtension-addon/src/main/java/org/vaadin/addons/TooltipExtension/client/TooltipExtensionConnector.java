package org.vaadin.addons.TooltipExtension.client;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addons.TooltipExtension.TooltipExtension;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@Connect(TooltipExtension.class)
public class TooltipExtensionConnector extends AbstractExtensionConnector {
    private enum TooltipPosition {
        TOP("customTooltipComponentTop", "bottom"),
        LEFT("customTooltipComponentLeft", "right"),
        RIGHT("customTooltipComponentRight", "left"),
        BOTTOM("customTooltipComponentBottom", "top");
        public String styleName;
        public String position;

        private TooltipPosition(final String styleName, final String position) {
            this.styleName = styleName;
            this.position = position;
        }
    }

    private TooltipPosition position = TooltipPosition.RIGHT;
    private boolean manualAttach = false;
    private AttachEvent.Handler handler = new AttachEvent.Handler() {

        @Override
        public void onAttachOrDetach(AttachEvent event) {
            if (event.isAttached() && !manualAttach) {
                handleAttach();
            }
        }
    };

    private Element parent = DOM.createDiv();
    private Element originalParent;
    private Element tooltip = DOM.createSpan();
    private Element tooltipTextSpan = DOM.createSpan();
    private Widget baseWidget;
    private String id;
    private String tooltipText = "";
    private List<String> tooltipStylenames = new ArrayList<>();
    private int mvmntTransitionMs = 250;
    private int opacityTransitionMs = 250;
    // ServerRpc is used to send events to server. Communication implementation
    // is automatically created here
    TooltipExtensionServerRpc rpc = RpcProxy
            .create(TooltipExtensionServerRpc.class, this);

    public TooltipExtensionConnector() {
    }

    @Override
    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);
        if (event.hasPropertyChanged("description")
                && !getState().description.equals("")) {
            getState().description = "";
        } else if (event.hasPropertyChanged("tooltipStylenames")) {
            tooltipStylenames = getState().tooltipStylenames;
            updateStyles();
        }
    }

    @Override
    public TooltipExtensionState getState() {
        return (TooltipExtensionState) super.getState();
    }

    private native void alert(String s) /*-{
                                        alert('extended '+s);
                                        }-*/;

    @Override
    protected void extend(ServerConnector target) {
        id = getState().id;
        tooltipText = getState().tooltipText;
        mvmntTransitionMs = getState().positionTransDurationMs;
        opacityTransitionMs = getState().opacityTransDurationMs;
        position = TooltipPosition.valueOf(getState().tooltipPositionStyle);
        tooltipStylenames = getState().tooltipStylenames;
        if (baseWidget == null) {
            baseWidget = ((AbstractComponentConnector) target).getWidget();
            if (baseWidget.isAttached()) {
                handleAttach();
            }
            baseWidget.addAttachHandler(handler);
        }
    }

    private void handleAttach() {
        originalParent = baseWidget.getElement().getParentElement();
        if (originalParent.hasClassName("customTooltipWidget")) {
            parent = originalParent;
            tooltip = parent.getFirstChildElement();
            tooltipTextSpan = tooltip.getFirstChildElement();
            addTooltip();
        } else {
            parent.removeFromParent();
            parent = DOM.createDiv();
            tooltip = DOM.createSpan();
            tooltipTextSpan = DOM.createSpan();
            addTooltip();
            manualAttach = true;
            originalParent.replaceChild(parent, baseWidget.getElement());
            parent.appendChild(baseWidget.getElement());
            manualAttach = false;
        }
        originalParent = parent.getParentElement();
    }

    private void addTooltip() {
        tooltipTextSpan.setInnerHTML(tooltipText);
        tooltipTextSpan.addClassName("customTooltipText");
        if (id != null && !id.trim().isEmpty()) {
            tooltip.setId(id);
        }
        if (!tooltip.isOrHasChild(tooltipTextSpan)) {
            tooltip.appendChild(tooltipTextSpan);
        }
        for (TooltipPosition t : TooltipPosition.values()) {
            tooltip.removeClassName(t.styleName);
        }
        tooltipTextSpan.setAttribute("role", "tooltip");

        if (!parent.isOrHasChild(tooltip)) {
            parent.appendChild(tooltip);
        }
        parent.addClassName("v-widget customTooltipWidget");
        updateStyles();
        setDelays();
    }

    private void updateStyles() {
        String[] l = tooltip.getClassName().split(" ");
        for (int i = 0; i < l.length; i++) {
            tooltip.removeClassName(l[i]);
        }

        tooltip.addClassName("customTooltip");
        tooltip.addClassName(position.styleName);
        for (String style : tooltipStylenames) {
            tooltip.addClassName(style);
        }
    }

    private void setDelays() {
        String transition = position.position + " " + mvmntTransitionMs
                + "ms, opacity " + opacityTransitionMs + "ms ease-in";

        tooltip.getStyle().setProperty("-webkit-transition", transition);
        tooltip.getStyle().setProperty("-ms-transition", transition);
        tooltip.getStyle().setProperty("-moz-transition", transition);
        tooltip.getStyle().setProperty("-o-transition", transition);
        tooltip.getStyle().setProperty("transition", transition);
    }
}
