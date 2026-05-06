package com.ldtteam.structurize.util;

/** [1.7.10 stub] PlacementSettings */
public class PlacementSettings {
    public Mirror mirror = Mirror.NONE;
    public Object rotation = null;

    public PlacementSettings() {}

    public PlacementSettings(Mirror mirror, Object rotation) {
        this.mirror = mirror;
        this.rotation = rotation;
    }

    public Mirror getMirror() { return mirror; }
    public Object getRotation() { return rotation; }
}

