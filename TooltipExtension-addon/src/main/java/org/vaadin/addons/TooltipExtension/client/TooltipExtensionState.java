package org.vaadin.addons.TooltipExtension.client;

import java.util.ArrayList;
import java.util.List;

public class TooltipExtensionState
        extends com.vaadin.shared.AbstractComponentState {
    public List<String> tooltipStylenames = new ArrayList<>();
    public String tooltipText = "";
    public String tooltipPositionStyle = "RIGHT";
    public int positionTransDurationMs = 250;
    public int opacityTransDurationMs = 250;
}