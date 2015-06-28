package org.smallbox.faraway.ui.panel.info;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.ui.*;
import org.smallbox.faraway.ui.engine.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoArea extends BaseInfoRightPanel {
    private AreaModel _area;
    private List<UILabel>          _entries;

    public PanelInfoArea(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_area.yml");
        _entries = new ArrayList<>();
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        super.onLayoutLoaded(layout);

        int posX = 0;
        int posY = 0;
        int index = 0;
        FrameLayout frameEntries = (FrameLayout)findById("frame_entries");

        // Add resources
        addTitle(frameEntries, "Resources", posY);
        posY += 40;
        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isResource) {
                addEntry(frameEntries, itemInfo, posX, posY);
                posY = index++ % 2 == 0 ? posY : posY + 20;
                posX = posX == 0 ? 200 : 0;
            }
        }
        posY += 40;

        // Add consumable
        addTitle(frameEntries, "Consumables", posY);
        posX = 0;
        posY += 40;
        index = 0;
        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isConsumable && !itemInfo.isEquipment) {
                addEntry(frameEntries, itemInfo, posX, posY);
                posY = index++ % 2 == 0 ? posY : posY + 20;
                posX = posX == 0 ? 200 : 0;
            }
        }
        posY += 40;

        // Add equipments
        addTitle(frameEntries, "Equipments", posY);
        posX = 0;
        posY += 40;
        index = 0;
        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isEquipment) {
                addEntry(frameEntries, itemInfo, posX, posY);
                posY = index++ % 2 == 0 ? posY : posY + 20;
                posX = posX == 0 ? 200 : 0;
            }
        }
    }

    @Override
    protected void onRefresh(int update) {
    }

    private void addTitle(FrameLayout frameEntries, String title, int posY) {
        UILabel lbTitle = ViewFactory.getInstance().createTextView(180, 20);
        lbTitle.setString(title);
        lbTitle.setCharacterSize(18);
        lbTitle.setPosition(0, posY + 10);
        frameEntries.addView(lbTitle);
    }

    private void addEntry(FrameLayout frameEntries, ItemInfo itemInfo, int posX, int posY) {
        UILabel lbEntry = ViewFactory.getInstance().createTextView(180, 20);
        lbEntry.setString("[x] " + itemInfo.label);
        lbEntry.setCharacterSize(14);
        lbEntry.setPosition(posX, posY);
        lbEntry.setColor(Colors.LINK_INACTIVE);
        lbEntry.setOnFocusListener(new LinkFocusListener());
        lbEntry.setOnClickListener(view -> toggleItem(itemInfo, lbEntry));
        lbEntry.setAlign(Align.CENTER_VERTICAL);
        lbEntry.setData(itemInfo);
        frameEntries.addView(lbEntry);
        _entries.add(lbEntry);
    }

    private void toggleItem(ItemInfo itemInfo, UILabel lbEntry) {
        _area.setAccept(itemInfo, !_area.accept(itemInfo));
        lbEntry.setString((_area.accept(itemInfo) ? "[x] " : "[ ] ") + itemInfo.label);
    }

    public void select(AreaModel area) {
        _area = area;
        ((UILabel)findById("lb_area")).setString(area.getName());
        findById("bt_remove_area").setOnClickListener(view -> ((AreaManager)Game.getInstance().getManager(AreaManager.class)).remove(area));

        for (UILabel lbEntry: _entries) {
            ItemInfo itemInfo = (ItemInfo)lbEntry.getData();
            lbEntry.setString((_area.accept(itemInfo) ? "[x] " : "[ ] ") + itemInfo.label);
        }
    }
}
