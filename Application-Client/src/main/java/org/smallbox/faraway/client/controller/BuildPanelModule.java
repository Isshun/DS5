package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.ModuleLayer;
import org.smallbox.faraway.client.render.layer.BuildLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.engine.module.GameModule;

@ModuleLayer(BuildLayer.class)
@GameObject
public class BuildPanelModule extends GameModule {
}
