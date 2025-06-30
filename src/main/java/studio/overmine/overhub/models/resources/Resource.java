package studio.overmine.overhub.models.resources;

import studio.overmine.overhub.OverHub;

public abstract class Resource {

    protected final OverHub plugin;

    public Resource(OverHub plugin) {
        this.plugin = plugin;
    }

    public abstract void initialize();
}
