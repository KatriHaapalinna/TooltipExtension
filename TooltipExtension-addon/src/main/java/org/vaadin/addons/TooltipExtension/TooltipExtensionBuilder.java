package org.vaadin.addons.TooltipExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.server.Extension;
import com.vaadin.ui.AbstractComponent;

//@formatter:off
/**
 *
 * {@code TooltipExtensionBuilder } is used to build custom tooltips for any
 * Vaadin Components extending {@link AbstractComponent }. The Builder object is
 * reusable by calling the {@link #createTooltip(AbstractComponent) } method for another
 * component.
 * <br/><br/>The following example shows how to add custom tooltips with same formatting to two component.
 * <pre>
 *      TextField field1 = new TextField();
 *      TooltipExtensionBuilder builder = new TooltipExtentionBuilder();
 *      builder.setTooltipText("tooltip")
 *              .setPosition(TooltipPosition.TOP)
 *              .addTooltipStyleName("newStyle")
 *              .build(field1);
 *      TextField field2 = new TextField();
 *      builder.build(field2);
 * </pre>
 *
 */
public class TooltipExtensionBuilder {
  //@formatter:on
    /**
     * Specifies the side of the target component on which the tooltip will
     * appear
     */
    public enum TooltipPosition {
        /**
         * Tooltip will appear centered on top of target component
         */
        TOP("TOP"),
        /**
         * Tooltip will appear vertically centered on left of target component
         */
        LEFT("LEFT"),
        /**
         * Tooltip will appear vertically centered on right of target component
         */
        RIGHT("RIGHT"),
        /**
         * Tooltip will appear centered below target component
         */
        BOTTOM("BOTTOM");

        protected String position;

        private TooltipPosition(final String position) {
            this.position = position;
        }
    }

    private Set<TooltipExtension> createdExtensions = new HashSet<>();
    private String tooltipText = "";
    private List<String> tooltipStylenames;
    private TooltipPosition position = TooltipPosition.RIGHT;
    private int positionTransDurationMs = 250;
    private int opacityTransDurationMs = 250;

    public TooltipExtensionBuilder() {
        tooltipStylenames = new ArrayList<>();
    }

    /**
     * Sets text for tooltip. If null, text is set to empty String
     *
     * @param tooltipText
     *            String
     * @return TooltipExtensionBuilder
     */
    public TooltipExtensionBuilder setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText == null ? "" : tooltipText;
        return this;
    }

    /**
     * Sets position of tooltip. Default position is on the right of the target
     * component
     *
     * @param position
     *            {@link #TooltipPosition }
     * @return TooltipExtensionBuilder
     */
    public TooltipExtensionBuilder setPosition(TooltipPosition position) {
        this.position = position == null ? TooltipPosition.RIGHT : position;

        return this;
    }

    /**
     * Sets position (top, bottom, left or right depending on
     * {@link #TooltipPosition } set for Builder) transition duration in
     * milliseconds
     *
     * Defaults to 250 milliseconds, if value provided is less than zero,
     * duration is set to zero
     *
     * @param durationMs
     *            int duration of transition
     * @return TooltipExtensionBuilder
     */
    public TooltipExtensionBuilder setPositionTransitionDuration(
            int durationMs) {
        positionTransDurationMs = durationMs < 0 ? 0 : durationMs;
        return this;
    }

    /**
     * Sets opacity transition duration in milliseconds
     *
     * Defaults to 250 milliseconds, if value provided is less than zero,
     * duration is set to zero
     *
     * @param durationMs
     *            int duration of transition
     * @return TooltipExtensionBuilder
     */
    public TooltipExtensionBuilder setOpacityTransitionDuration(
            int durationMs) {
        opacityTransDurationMs = durationMs < 0 ? 0 : durationMs;
        return this;
    }

    /**
     * Add custom style name to Builder. If style is already present, or null or
     * empty, this method has no effect.
     *
     * Note that this method adds the style name to all components extended with
     * this Builder.
     *
     * @param stylename
     *            String
     * @return TooltipExtensionBuilder
     */
    public TooltipExtensionBuilder addTooltipStyleName(String stylename) {
        if (stylename != null && !stylename.trim().isEmpty()
                && !tooltipStylenames.contains(stylename)) {
            tooltipStylenames.add(stylename);
            for (TooltipExtension t : createdExtensions) {
                t.getState().tooltipStylenames = tooltipStylenames;
            }
        }
        return this;
    }

    /**
     * Remove style name (class) if it has been added to the builder, if the
     * name is not present this method has no effect.
     *
     * Note that this method removes the style name from all components extended
     * with this Builder. Cannot remove position defining styles, only user
     * added custom style names.
     *
     * @param stylename
     *            String
     * @return TooltipExtensionBuilder
     */
    public TooltipExtensionBuilder removeTooltipStyleName(String stylename) {
        if (stylename != null && !stylename.trim().isEmpty()
                && tooltipStylenames.contains(stylename)) {
            tooltipStylenames.remove(stylename);
            for (TooltipExtension t : createdExtensions) {
                t.getState().tooltipStylenames = tooltipStylenames;
            }
        }
        return this;
    }

    /**
     * Returns user added style names
     *
     * Does not contain position defining default styles
     *
     * @return List<String> style names added to tooltip
     */
    public List<String> getTooltipStyleNames() {
        return tooltipStylenames;
    }

    /**
     * Builds the custom tooltip according to Builder properties, and adds it to
     * the target component
     *
     * @param component
     *            target component
     */
    public void createTooltip(AbstractComponent component) {
        TooltipExtension te = new TooltipExtension();
        if (isExtended(component)) {
            te = getExtension(component);
        } else {
            createdExtensions.add(te);
        }
        te.getState().tooltipText = tooltipText;
        te.getState().tooltipPositionStyle = position.position;
        te.getState().tooltipStylenames = tooltipStylenames;
        te.getState().positionTransDurationMs = positionTransDurationMs;
        te.getState().opacityTransDurationMs = opacityTransDurationMs;
        te.extend(component);
        createdExtensions.add(te);
    }

    /**
     * Builds the custom tooltip according to Builder properties and specified
     * tooltip text, and adds it to the target component
     *
     * Note that the tooltip text specified in the arguments is NOT set as the
     * Builder default tooltip text
     *
     * @param component
     *            target component
     * @param tooltipText
     *            String
     */
    public void createTooltip(AbstractComponent component, String tooltipText) {
        TooltipExtension te = new TooltipExtension();
        if (isExtended(component)) {
            te = getExtension(component);
        } else {
            createdExtensions.add(te);
        }
        te.getState().tooltipText = tooltipText == null ? "" : tooltipText;
        te.getState().tooltipStylenames = tooltipStylenames;
        te.getState().tooltipPositionStyle = position.position;
        te.getState().positionTransDurationMs = positionTransDurationMs;
        te.getState().opacityTransDurationMs = opacityTransDurationMs;
        te.extend(component);
        createdExtensions.add(te);
    }

    /**
     * Builds the custom tooltip according to Builder properties and specified
     * id for tooltip, and adds it to the target component
     *
     * Note that the id is NOT checked for uniqueness automatically
     *
     * @param component
     *            target component
     * @param id
     *            String id for the tooltip
     */
    public void createTooltipWithId(AbstractComponent component, String id) {
        TooltipExtension te = new TooltipExtension();
        if (isExtended(component)) {
            te = getExtension(component);
        } else {
            createdExtensions.add(te);
        }
        te.getState().tooltipText = tooltipText == null ? "" : tooltipText;
        te.getState().tooltipStylenames = tooltipStylenames;
        te.getState().tooltipPositionStyle = position.position;
        te.getState().positionTransDurationMs = positionTransDurationMs;
        te.getState().opacityTransDurationMs = opacityTransDurationMs;
        te.getState().id = id;
        te.extend(component);
        createdExtensions.add(te);
    }

    /**
     * Builds the custom tooltip according to Builder properties and specified
     * id and text for tooltip, and adds it to the target component
     *
     * Note that the id is NOT checked for uniqueness automatically, and that the tooltip text specified in the arguments is NOT set as the
     * Builder default tooltip text
     *
     * @param component
     *            target component
     * @param id
     *            String id for the tooltip
     * @param tooltipText
     *            String
     */
    public void createTooltipWithId(AbstractComponent component, String id,
            String tooltipText) {
        TooltipExtension te = new TooltipExtension();
        if (isExtended(component)) {
            te = getExtension(component);
        } else {
            createdExtensions.add(te);
        }
        te.getState().tooltipText = tooltipText == null ? "" : tooltipText;
        te.getState().tooltipStylenames = tooltipStylenames;
        te.getState().tooltipPositionStyle = position.position;
        te.getState().positionTransDurationMs = positionTransDurationMs;
        te.getState().opacityTransDurationMs = opacityTransDurationMs;
        te.getState().id = id;

        te.extend(component);
    }

    private boolean isExtended(AbstractComponent component) {
        Collection<Extension> c = component.getExtensions();
        for (Extension e : c) {
            if (e instanceof TooltipExtension) {
                return true;
            }
        }
        return false;
    }

    private TooltipExtension getExtension(AbstractComponent component) {
        Collection<Extension> c = component.getExtensions();
        for (Extension e : c) {
            if (e instanceof TooltipExtension) {
                return (TooltipExtension) e;
            }
        }
        return null;
    }

    /**
     * Return position transition duration set by
     * {@link #setPositionTransitionDuration(int)} to Builder
     *
     * @return int duration of transition in milliseconds
     */
    public int getPositionTransitionDuration() {
        return positionTransDurationMs;
    }

    public int getSize() {
        return createdExtensions.size();
    }

    /**
     * Return opacity transition duration set by
     * {@link #setOpacityTransitionDuration(int)} to Builder
     *
     * @return int duration of transition in milliseconds
     */
    public int getOpacityTransitionDuration() {
        return opacityTransDurationMs;
    }

    /**
     * Returns TooltipPosition set by {@link #setPosition(TooltipPosition)} to
     * Builder
     *
     * @return TooltipPosition
     */
    public TooltipPosition getTooltipPosition() {
        return position;
    }

    /**
     * Returns tooltip text set by {@link #setTooltipText(String)} method to
     * this Builder
     *
     * @return String
     */
    public String getTooltipText() {
        return tooltipText;
    }

    /**
     * Returns the tooltip position for the component specified. If component
     * does not have tooltip extension, returns null
     *
     * Note that position can be different from the Builder tooltip position
     * which is returned by {@link #getTooltipPosition()}
     *
     * @return TooltipPosition
     */
    public TooltipPosition getTooltipPositionFor(AbstractComponent component) {
        TooltipPosition p = null;
        Collection<Extension> extensions = component.getExtensions();
        for (Extension e : extensions) {
            if (e instanceof TooltipExtension) {
                if (((TooltipExtension) e).getState().tooltipPositionStyle
                        .equals("TOP")) {
                    p = TooltipPosition.TOP;
                } else if (((TooltipExtension) e)
                        .getState().tooltipPositionStyle.equals("BOTTOM")) {
                    p = TooltipPosition.BOTTOM;
                } else if (((TooltipExtension) e)
                        .getState().tooltipPositionStyle.equals("LEFT")) {
                    p = TooltipPosition.LEFT;
                } else {
                    p = TooltipPosition.RIGHT;
                }
            }
        }
        return p;
    }

    /**
     * Returns the tooltip opacity transition duration for the component
     * specified. If component does not have tooltip extension, returns -1. If
     * transition duration has been changed in CSS file, this value is erroneous
     *
     * Note that value can be different from the Builder value which is returned
     * by {@link #getOpacityTransitionDuration()}
     *
     * @return int
     */
    public int getOpacityTransitionFor(AbstractComponent component) {
        int i = -1;
        Collection<Extension> extensions = component.getExtensions();
        for (Extension e : extensions) {
            if (e instanceof TooltipExtension) {
                i = ((TooltipExtension) e).getState().opacityTransDurationMs;
            }
        }
        return i;
    }

    /**
     * Returns the tooltip id for the component specified. If id is not set,
     * returns null.
     *
     * @return String
     */
    public String getTooltipIdFor(AbstractComponent component) {
        String id = null;
        Collection<Extension> extensions = component.getExtensions();
        for (Extension e : extensions) {
            if (e instanceof TooltipExtension) {
                id = ((TooltipExtension) e).getState().id;
            }
        }
        return id;
    }

    /**
     * Returns the tooltip position transition duration for the component
     * specified. If component does not have tooltip extension, returns -1. If
     * transition duration has been changed in CSS file, this value is erroneous
     *
     * Note that value can be different from the Builder value which is returned
     * by {@link #getPositionTransitionDuration()}
     *
     * @return int
     */
    public int getPositionTransitionFor(AbstractComponent component) {
        int i = -1;
        Collection<Extension> extensions = component.getExtensions();
        for (Extension e : extensions) {
            if (e instanceof TooltipExtension) {
                i = ((TooltipExtension) e).getState().positionTransDurationMs;
            }
        }
        return i;
    }

    /**
     * Returns the tooltip text for the component specified. If component does
     * not have tooltip extension, returns null
     *
     * Note that text can be different from the Builder tooltip text which is
     * returned by {@link #getTooltipText()}
     *
     * @return String
     */
    public String getTooltipTextFor(AbstractComponent component) {
        String text = null;
        Collection<Extension> extensions = component.getExtensions();
        for (Extension e : extensions) {
            if (e instanceof TooltipExtension) {
                text = ((TooltipExtension) e).getState().tooltipText;
            }
        }
        return text;
    }
}
