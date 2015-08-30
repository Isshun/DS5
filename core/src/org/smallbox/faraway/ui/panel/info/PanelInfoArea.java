package org.smallbox.faraway.ui.panel.info;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.world.AreaModule;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.area.AreaModel;
import org.smallbox.faraway.game.model.area.GardenAreaModel;
import org.smallbox.faraway.game.model.area.StorageAreaModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.planet.PlanetInfo;
import org.smallbox.faraway.ui.LinkFocusListener;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.Colors;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoArea extends BaseInfoRightPanel {
    private AreaModel       _area;
    private ParcelModel     _parcel;
    private List<UILabel>   _entries;

    public PanelInfoArea(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_area.yml");
        _entries = new ArrayList<>();
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

    private void addToggle(FrameLayout frameEntries, int posY, StorageAreaModel storage, Predicate<ItemInfo> predicate) {
        UILabel lbToggle = ViewFactory.getInstance().createTextView(180, 20);
        lbToggle.setString("select all");
        lbToggle.setData(true);
        lbToggle.setCharacterSize(14);
        lbToggle.setPosition(300, posY + 10);
        lbToggle.setOnFocusListener(new LinkFocusListener());
        lbToggle.setOnClickListener(view -> {
            frameEntries.getViews().stream()
                    .filter(v -> v.getData() instanceof ItemInfo && predicate.test((ItemInfo) v.getData()))
                    .forEach(v -> storage.setAccept((ItemInfo) v.getData(), (boolean) lbToggle.getData()));
            lbToggle.setData(!(boolean) lbToggle.getData());
            refreshItem(frameEntries, (StorageAreaModel)_area);
        });
        frameEntries.addView(lbToggle);
    }

    private void addItemEntry(FrameLayout frameEntries, ItemInfo info, int posX, int posY, String label, OnClickListener clickListener) {
        UILabel lbEntry = ViewFactory.getInstance().createTextView(180, 20);
        lbEntry.setString(label);
        lbEntry.setData(info);
        lbEntry.setCharacterSize(14);
        lbEntry.setPosition(posX, posY);
        lbEntry.setColor(Colors.LINK_INACTIVE);
        lbEntry.setOnFocusListener(new LinkFocusListener());
        lbEntry.setOnClickListener(clickListener);
        lbEntry.setAlign(Align.CENTER_VERTICAL);
        frameEntries.addView(lbEntry);
        _entries.add(lbEntry);
    }

    public void select(AreaModel area, ParcelModel parcel) {
        super.select(parcel);

        _area = area;
        _parcel = parcel;
        ((UILabel)findById("lb_area")).setString(area.getName());
        findById("bt_remove_area").setOnClickListener(view -> ((AreaModule) Game.getInstance().getManager(AreaModule.class)).remove(area));

        findById("frame_info_garden").setVisible(false);
        findById("frame_info_storage").setVisible(false);
        findById("frame_entries").setVisible(false);

        switch (area.getType()) {
            case STORAGE:
                createStorageEntries((StorageAreaModel)area);
                break;
            case GARDEN:
                createGardenEntries((GardenAreaModel)area, parcel);
                break;
        }
    }

    private void createGardenEntries(GardenAreaModel garden, ParcelModel parcel) {
        int posX = 0;
        int posY = 0;
        int index = 0;
        FrameLayout frameEntries = (FrameLayout)findById("frame_entries");
        frameEntries.removeAllViews();
        frameEntries.setVisible(true);

        // Add resources
        addTitle(frameEntries, "Vegetables", posY);
        posY += 40;
        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isResource && itemInfo.actions != null && !itemInfo.actions.isEmpty() && "gather".equals(itemInfo.actions.get(0).type)) {
                addItemEntry(frameEntries, itemInfo, posX, posY, (garden.accept(itemInfo) ? "[x] " : "[ ] ") + itemInfo.label, view -> {
                    garden.setAccept(itemInfo, true);
                    select(garden, parcel);
                });
                posY = index++ % 2 == 0 ? posY : posY + 20;
                posX = posX == 0 ? 200 : 0;
            }
        }

        findById("frame_info_garden").setVisible(true);

        PlanetInfo planetInfo = Game.getInstance().getPlanet().getInfo();
        ((UILabel)findById("lb_growing_period")).setString("Growing period: " + planetInfo.farming.growing[0] + " to " + planetInfo.farming.growing[1]);

        if (parcel.getResource() != null) {
            double growRate = parcel.getResource().getGrowRate();
            UILabel lbGrowRate = (UILabel)findById("lb_growing_status");
            if (growRate < 0) { lbGrowRate.setString("Status: plant is dying"); }
            if (growRate > 0.025) { lbGrowRate.setString("Status: partial grow"); }
            if (growRate < 0.05) { lbGrowRate.setString("Status: regular grow"); }
            if (growRate < 0.075) { lbGrowRate.setString("Status: exceptional grow"); }
        }
    }

    private void createStorageEntries(StorageAreaModel storage) {
        int posX = 0;
        int posY = 0;
        int index = 0;
        FrameLayout frameEntries = (FrameLayout)findById("frame_entries");
        frameEntries.removeAllViews();
        frameEntries.setVisible(true);

        // Add consumable
        addTitle(frameEntries, "Consumables", posY);
        addToggle(frameEntries, posY, storage, itemInfo -> itemInfo.isConsumable && !itemInfo.isEquipment);
//        addToggle(frameEntries, posY, itemInfo -> {
//            frameEntries.getViews().forEach(v -> {
//                if (v.getData() != null && ((ItemInfo)v.getData()).isConsumable && !((ItemInfo)v.getData()).isEquipment) {
//                    storage.setAccept((ItemInfo)v.getData(), true);
//                }
//            });
//        });
        posX = 0;
        posY += 40;
        index = 0;
        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isConsumable && !itemInfo.isEquipment) {
                addItemEntry(frameEntries, itemInfo, posX, posY, (storage.accept(itemInfo) ? "[x] " : "[ ] ") + itemInfo.label, view -> {
                    storage.setAccept(itemInfo, !storage.accept(itemInfo));
                    ((UILabel)view).setString((storage.accept(itemInfo) ? "[x] " : "[ ] ") + itemInfo.label);
                });
                posY = index++ % 2 == 0 ? posY : posY + 20;
                posX = posX == 0 ? 200 : 0;
            }
        }
        posY += 40;

        // Add equipments
        addTitle(frameEntries, "Equipments", posY);
        addToggle(frameEntries, posY, storage, itemInfo -> itemInfo.isEquipment);
        posX = 0;
        posY += 40;
        index = 0;
        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isEquipment) {
                addItemEntry(frameEntries, itemInfo, posX, posY, (storage.accept(itemInfo) ? "[x] " : "[ ] ") + itemInfo.label, view -> {
                    storage.setAccept(itemInfo, !storage.accept(itemInfo));
                    ((UILabel)view).setString((storage.accept(itemInfo) ? "[x] " : "[ ] ") + itemInfo.label);
                });
                posY = index++ % 2 == 0 ? posY : posY + 20;
                posX = posX == 0 ? 200 : 0;
            }
        }
    }

    private void refreshItem(FrameLayout frameEntries, StorageAreaModel storage) {
        frameEntries.getViews().stream().filter(view -> view.getData() instanceof ItemInfo).forEach(view -> {
            ItemInfo itemInfo = (ItemInfo) view.getData();
            ((UILabel)view).setString((storage.accept(itemInfo) ? "[x] " : "[ ] ") + itemInfo.label);
        });
    }

}
