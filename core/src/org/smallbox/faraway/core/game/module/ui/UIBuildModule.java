package org.smallbox.faraway.core.game.module.ui;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.*;

public class UIBuildModule extends GameModule {
    private UIList      _mainList;
    private ItemInfo    _currentStructureParent;
    private ItemInfo    _currentStructure;
    private ItemInfo    _currentItem;
    private ItemInfo    _currentNetwork;

    @Override
    protected void onGameStart(Game game) {
        createDisplay();
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
    }

    @Override
    public void onReloadUI() {
        createDisplay();
    }

    private void createDisplay() {
        UIFrame view = new UIFrame(this);
        view.setSize(Application.getInstance().getConfig().screen.resolution[0], Application.getInstance().getConfig().screen.resolution[1]);
        view.setPosition(Application.getInstance().getConfig().screen.resolution[0] - 372, 38);
        view.setBackgroundColor(0xff0000);
        view.setId("base.ui.panel_build");
        view.setVisible(false);
        view.setBackgroundColor(0x121c1e);

        UIGrid topGrid = new UIGrid(this);
        topGrid.setSize(372, 32);
        topGrid.setColumns(3);
        topGrid.setColumnWidth(80);
        topGrid.setRowHeight(32);
        topGrid.addView(UILabel.create(this).setText("Structure").setSize(80, 32).setPadding(10).setOnClickListener(this::openStructures));
        topGrid.addView(UILabel.create(this).setText("Item").setSize(80, 32).setPadding(10).setOnClickListener(this::openItems));
        topGrid.addView(UILabel.create(this).setText("Network").setSize(80, 32).setPadding(10).setOnClickListener(this::openNetworks));
        view.addView(topGrid);

        _mainList = new UIList(this);
        _mainList.setSize(372, 100);
        _mainList.setPosition(0, 32);
        view.addView(_mainList);

        UserInterface.getInstance().addView(view);
    }

    private void openNetworks() {

    }

    private void openItems() {

    }

    private void openStructures() {
        _mainList.clear();
        for (String parentName: new String[] {"base.wall", "base.door", "base.floor"}) {
            UIFrame listEntry = new UIFrame(this);
            listEntry.setSize(372, 32);
            listEntry.addView(UILabel.create(this).setText(Data.getData().getItemInfo(parentName).label).setSize(372, 32).setPadding(10));
            UIGrid categoryGrid = new UIGrid(this);
            categoryGrid.setSize(200, 32);
            categoryGrid.setPosition(200, 0);
            categoryGrid.setColumns(10);
            categoryGrid.setColumnWidth(32);
            categoryGrid.setRowHeight(32);
            for (ItemInfo itemInfo: Data.getData().items) {
                if (itemInfo.receipts != null && !itemInfo.receipts.isEmpty() && parentName.equals(itemInfo.parentName)) {
                    View icStructure = UIImage.create(this).setImage(itemInfo.receipts.get(0).icon).setSize(32, 32);
                    icStructure.setOnClickListener(() -> selectStructure(icStructure, itemInfo));
                    categoryGrid.addView(icStructure);
                }
                if (itemInfo == _currentStructure) {
                    listEntry.setBackgroundColor(0x349394);
                }
            }
            listEntry.addView(categoryGrid);
            _mainList.addView(listEntry);
        }
    }

    private void selectStructure(View icStructure, ItemInfo structureInfo) {
        icStructure.getParent().getViews().forEach(view -> view.setBackgroundColor(0x34939400));
        icStructure.setBackgroundColor(0x349394);
        _currentStructureParent = structureInfo.parent;
        _currentStructure = structureInfo;
    }
}