package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameRenderer;

/**
 * Created by Alex on 24/11/2016.
 */
@GameRenderer(level = 1, visible = true)
public class UIRenderer extends BaseRenderer {

    public View _debugView;

    @Override
    protected void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        ApplicationClient.uiManager.draw(renderer, Application.gameManager.isLoaded());

        if (_debugView != null) {
            _debugView.draw(renderer, 0, 0);
        }
    }

//    @Override
//    public void onGameUpdate(Game game) {
//        Globals globals = JsePlatform.standardGlobals();
//        globals.load("function main(a, u, d)\n application = a\n data = d\n ui = u\n math.round = function(num, idp)\n local mult = 10^(idp or 0)\n return math.floor(num * mult + 0.5) / mult\n end end", "main").call();
//        globals.get("main").call(
//                CoerceJavaToLua.coerce(new LuaApplicationModel(null, new LuaEventsModel())),
//                CoerceJavaToLua.coerce(new LuaUIBridge(null) {
//
//                    @Override
//                    public void extend(LuaValue values) {
//                        Log.info("Load lua ui: " + values.get("name").toString());
//                        if (!values.get("type").isnil()) {
//                            RootView rootView = new LuaUIExtend().debug(globals, values);
//                            rootView.getView().setVisible(true);
//                            _debugView = rootView.getView();
//                        }
//                    }
//                }),
//                CoerceJavaToLua.coerce(new LuaDataModel() {
//                    @Override
//                    public void extend(LuaValue values) {
//                    }
//                }));
//
//
//        File f = new File("W:\\projects\\desktop\\FarAway\\Application\\Application-Core\\src\\main\\resources\\modules\\plant\\info_plant.lua");
//        try (FileReader fileReader = new FileReader(f)) {
//            globals.load(fileReader, f.getName()).call();
//        } catch (LuaError | IOException e) {
//            e.printStackTrace();
//        }
//    }
}
