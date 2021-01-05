package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

@GameObject
public class CharacterDashboardLayer extends DashboardLayerBase {

    @Inject
    private CharacterModule characterModule;

    @Override
    protected void onDraw(GDXRenderer renderer, int frame) {
        characterModule.getAll().forEach(character -> drawDebugCharacter(renderer, character));
    }

    private void drawDebugCharacter(GDXRenderer renderer, CharacterModel character) {
        StringBuilder sb = new StringBuilder();
        sb.append(character.getName()).append(" ").append(character.getParcel().x).append("x").append(character.getParcel().y);
        if (character.getJob() != null) {
            sb.append(" job: ").append(character.getJob().getLabel());
        }
        drawDebug(renderer, "Character", sb.toString());
    }

}
