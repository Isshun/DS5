package org.smallbox.farpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import org.smallbox.faraway.client.GDXApplication;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.awt.*;

public class DesktopLauncher {

    public static void main (String[] arg) {
        FileUtils.createRoamingDirectory();

        // Get native screen resolution
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        double ratio = (double)width / height;
        Log.info("Screen resolution: " + width + "x" + height + " (" + ratio + ")");

        new LwjglApplication(new GDXApplication(new GDXApplication.GameTestCallback() {
            @Override
            public void onApplicationReady() {
                Application.gameManager.createGame(GameInfo.create(Application.data.getRegion("base.planet.corrin", "mountain"), 12, 16, 2), new GameManager.GameCreateListener() {
                    @Override
                    public void onGameCreate(Game game) {
                        Application.moduleManager.getModule(CharacterModule.class).addRandom();
                        Application.moduleManager.getModule(ItemModule.class).addItem(Application.data.getItemInfo("base.cooker"), true, 2, 2, 1);
                        Application.moduleManager.getModule(ConsumableModule.class).addConsumable(Application.data.getItemInfo("base.vegetable_rice"), 10, 4, 4, 1);
                        Application.moduleManager.getModule(ConsumableModule.class).addConsumable(Application.data.getItemInfo("base.vegetable_carrot"), 10, 4, 6, 1);
                    }
                });
            }

            @Override
            public void onGameUpdate(long tick) {

            }
        }), LwjglConfig.from(Application.APPLICATION_CONFIG));
    }

}
