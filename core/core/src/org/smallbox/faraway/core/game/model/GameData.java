package org.smallbox.faraway.core.game.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.data.loader.*;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.module.character.model.BuffModel;
import org.smallbox.faraway.core.game.module.character.model.DiseaseModel;
import org.smallbox.faraway.core.game.module.world.model.ReceiptGroupInfo;
import org.smallbox.faraway.ui.UICursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameData {
    public static GameData                      _data;
    public static GameConfig                    config;

    public List<ReceiptGroupInfo>               receipts = new ArrayList<>();
    public List<ItemInfo>                       items = new ArrayList<>();
    public List<ItemInfo>                       gatherItems;
    public List<CategoryInfo>                   categories;
    public List<ItemInfo>                       equipments;
    public List<PlanetInfo>                     planets = new ArrayList<>();
    public Map<String, WeatherModel>            weathers;
    public HashMap<Integer, String>             strings = new HashMap<>();
    public boolean                              needUIRefresh;
    public List<IDataLoader>                    _loaders;
    public HashMap<String, CharacterTypeInfo>   characters;
    public Map<String, UICursor>                cursors = new HashMap<>();
    public List<BuffModel>                      buffs = new ArrayList<>();
    public List<DiseaseModel>                   diseases = new ArrayList<>();
    public List<ItemInfo>                       consumables;

    public GameData() {
        _data = this;

        _loaders = new ArrayList<>();

        _loaders.add(new ConfigLoader());
        _loaders.add(new WeatherLoader());
        _loaders.add(new EquipmentLoader());
        _loaders.add(new CategoryLoader());
        _loaders.add(new CharacterLoader());
    }

    public ItemInfo getItemInfo(String name) {
        for (ItemInfo info: items) {
            if (info.name.equals(name)) {
                return info;
            }
        }
        throw new RuntimeException("item not exists: " + name);
    }

    public static GameData getData() {
        return _data;
    }

    public void reloadConfig() {
        _loaders.forEach(dataLoader -> dataLoader.reloadIfNeeded(this));
    }

    public ItemInfo getEquipment(String name) {
        for (ItemInfo equipment: equipments) {
            if (equipment.name.equals(name)) {
                return equipment;
            }
        }
        return null;
    }

    public void loadAll() {
        _loaders.forEach(dataLoader -> dataLoader.load(this));
    }

    public RegionInfo getRegion(String planetName, String regionName) {
        for (PlanetInfo planet: planets) {
            if (planet.name.equals(planetName))
            for (RegionInfo region: planet.regions) {
                if (region.name.equals(regionName)) {
                    return region;
                }
            }
        }
        throw new RuntimeException("Unable to find planet or region (" + planetName + ", " + regionName + ")");
    }

    public boolean hasString(int hash) {
        return _data.strings.containsKey(hash);
    }

    public String getString(int hash) {
        return _data.strings.get(hash);
    }

    public void fix() {
        this.items.stream()
                .forEach(item -> {
                    if (item.parentName != null) {
                        item.parent = getItemInfo(item.parentName);
                    }
                    if (item.factory != null && item.factory.receiptNames != null) {
                        item.factory.receipts = item.factory.receiptNames.stream().map(this::getReceipt).collect(Collectors.toList());
                    }
                    if (item.actions != null) {
                        item.actions.stream().filter(action -> action.products != null)
                                .forEach(action -> action.products
                                        .forEach(product -> product.item = getItemInfo(product.itemName)));
                    }
                    if (item.receipts != null) {
                        item.receipts.stream().filter((receipt -> receipt.components != null))
                                .forEach(receipt -> receipt.components
                                        .forEach(component -> component.item = getItemInfo(component.itemName)));
                    }
                });
        this.receipts.forEach(receipt -> receipt.receipts.forEach(productInfo -> {
            productInfo.outputs.forEach(product -> product.item = getItemInfo(product.itemName));
            productInfo.inputs.forEach(component -> component.item = getItemInfo(component.itemName));
        }));

        this.consumables = this.items.stream().filter(item -> item.isConsumable).collect(Collectors.toList());
    }

    private ReceiptGroupInfo getReceipt(String receiptName) {
        for (ReceiptGroupInfo receipt: this.receipts) {
            if (receipt.name.equals(receiptName)) {
                return receipt;
            }
        }
        return null;
    }

    public UICursor getCursor(String name) { return this.cursors.get(name); }

    public DiseaseModel getDisease(String name) {
        for (DiseaseModel disease: diseases) {
            if (disease.name.equals(name)) {
                return disease;
            }
        }
        return null;
    }
}
