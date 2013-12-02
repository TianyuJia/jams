package jams.worldwind.events;

/**
 *
 * @author Ronny Berndt <ronny.berndt@uni-jena.de>
 */
public interface Events {
    public static final String ACTIVE_LAYER_CHANGED = "jams.worldwind.events.ActiveLayerChanged";
    public static final String FOUND_RENDERABLE_UNDER_CURSOR = "jams.worldwind.events.FoundRenderableUnderCursor";
    public static final String LAYER_ADDED = "jams.worldwind.events.LayerAdded";
    public static final String LAYER_CHANGED = "jams.worldwind.events.LayerChanged";
    public static final String LAYER_REMOVED = "jams.worldwind.events.LayerRemoved";
}
