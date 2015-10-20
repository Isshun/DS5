package org.smallbox.faraway.core.ui.panel.info;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.ui.UserInterface;
import org.smallbox.faraway.core.ui.panel.BaseRightPanel;

/**
 * Created by Alex on 26/06/2015.
 */
public class PanelInfoAnimal extends BaseRightPanel {
    public PanelInfoAnimal(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_animal.yml");
    }
}
