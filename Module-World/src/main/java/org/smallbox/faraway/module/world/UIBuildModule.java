package org.smallbox.faraway.module.world;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.BindLuaAction;
import org.smallbox.faraway.core.game.BindLuaController;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.module.world.controller.BuildController;
import org.smallbox.faraway.ui.MouseEvent;
import org.smallbox.faraway.ui.engine.views.widgets.*;

public class UIBuildModule extends GameModule {
    @BindLuaController
    private BuildController _controller;

    @BindModule("")
    private WorldModule _world;

    @BindModule("")
    private WorldInteractionModule _worldInteraction;

    private UIList      _mainList;
    private ItemInfo    _currentStructureParent;
    private ItemInfo    _currentStructure;
    private ItemInfo    _currentItem;
    private ItemInfo    _currentNetwork;
    private ItemInfo    _selected;
    private BuildRenderer render;

    @Override
    public void onMouseMove(MouseEvent event) {
//        if (_selected != null) {
//            render.setCursor(_selected, x, y);
//        }
    }

    @Override
    protected void onGameCreate(Game game) {
        render = new BuildRenderer(this, _world);
        game.getRenders().add(render);

        _world.addObserver(new WorldModuleObserver() {
            @Override
            public void onMouseMove(MouseEvent event, int parcelX, int parcelY, int floor) {
                if (_selected != null) {
                    render.setCursor(_selected, parcelX, parcelY);
                }
            }
        });
    }

    @Override
    protected void onGameStart(Game game) {
        _controller.create(this);
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
    }

    @Override
    public void onReloadUI() {
    }

    private void createDisplay() {
//        UIFrame view = new UIFrame(this);
//        view.setSize(Application.getInstance().getConfig().screen.resolution[0], Application.getInstance().getConfig().screen.resolution[1]);
//        view.setPosition(Application.getInstance().getConfig().screen.resolution[0] - 372, 38);
//        view.setBackgroundColor(0xff0000);
//        view.setId("base.ui.panel_build");
//        view.setVisible(false);
//        view.setBackgroundColor(0x121c1e);
//
//        UIGrid topGrid = new UIGrid(this);
//        topGrid.setSize(372, 32);
//        topGrid.setColumns(3);
//        topGrid.setColumnWidth(80);
//        topGrid.setRowHeight(32);
//        topGrid.addView(UILabel.create(this).setText("Structure").setSize(80, 32).setPadding(10).setOnClickListener(this::openStructures));
//        topGrid.addView(UILabel.create(this).setText("Item").setSize(80, 32).setPadding(10).setOnClickListener(this::openItems));
//        topGrid.addView(UILabel.create(this).setText("Network").setSize(80, 32).setPadding(10).setOnClickListener(this::openNetworks));
//        view.addView(topGrid);
//
//        _mainList = new UIList(this);
//        _mainList.setSize(372, 100);
//        _mainList.setPosition(0, 32);
//        view.addView(_mainList);
//
//        UserInterface.getInstance().addView(view);
    }

    private void selectStructure(View icStructure, ItemInfo structureInfo) {
        icStructure.getParent().getViews().forEach(view -> view.setBackgroundColor(0x34939400));
        icStructure.setBackgroundColor(0x349394);
        _currentStructureParent = structureInfo.parent;
        _currentStructure = structureInfo;
    }

    public void selectItem(ItemInfo item) {
        _selected = item;
    }
}