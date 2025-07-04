package studio.overmine.overhub.controllers;

import java.util.HashSet;
import java.util.Set;

import studio.overmine.overhub.OverHub;
import studio.overmine.overhub.models.resources.Resource;
import studio.overmine.overhub.models.resources.types.ConfigResource;
import studio.overmine.overhub.models.resources.types.LanguageResource;
import studio.overmine.overhub.models.resources.types.ScoreboardResource;
import studio.overmine.overhub.models.resources.types.SelectorResource;
import lombok.Getter;

@Getter
public class ResourceController {

    private final OverHub plugin;
    private final Set<Resource> resources;

    public ResourceController(OverHub plugin) {
        this.plugin = plugin;
        this.resources = new HashSet<>();
        this.resources.add(new ConfigResource(plugin));
        this.resources.add(new LanguageResource(plugin));
        this.resources.add(new SelectorResource(plugin));
        this.resources.add(new ScoreboardResource(plugin));
        this.onReload();
    }

    public final void onReload() {
        resources.forEach(Resource::initialize);
    }
}
