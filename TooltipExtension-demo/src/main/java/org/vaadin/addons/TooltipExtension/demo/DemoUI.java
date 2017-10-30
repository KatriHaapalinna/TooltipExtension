package org.vaadin.addons.TooltipExtension.demo;

import javax.servlet.annotation.WebServlet;

import org.vaadin.addons.TooltipExtension.TooltipExtensionBuilder;
import org.vaadin.addons.TooltipExtension.TooltipExtensionBuilder.TooltipPosition;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("TooltipExtension Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        final TextField component1 = new TextField();
        final TooltipExtensionBuilder tb = new TooltipExtensionBuilder();
        tb.setTooltipText("this is a tooltip").setPosition(TooltipPosition.TOP)
                .addTooltipStyleName("newStyle");
        tb.createTooltipWithId(component1, "newComponent");

        final ComboBox component2 = new ComboBox();
        tb.setPosition(TooltipPosition.RIGHT);
        tb.createTooltip(component2, "another tooltip");

        Button button = new Button("click this to change tooltip text");
        final VerticalLayout layout = new VerticalLayout();
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                tb.setOpacityTransitionDuration(0);
                tb.createTooltip(component1, "new tooltip text");
            }

        });

        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeFull();
        layout.addComponents(component1, component2, button);
        layout.setComponentAlignment(component1, Alignment.BOTTOM_CENTER);
        layout.setComponentAlignment(component2, Alignment.TOP_CENTER);
        setContent(layout);
    }
}
