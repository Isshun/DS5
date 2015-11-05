package org.smallbox.faraway.core.game.model;

import org.smallbox.faraway.core.data.BindingInfo;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.data.loader.*;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.module.character.model.BuffInfo;
import org.smallbox.faraway.core.game.module.character.model.DiseaseInfo;
import org.smallbox.faraway.core.game.module.world.model.ReceiptGroupInfo;
import org.smallbox.faraway.ui.UICursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Data {
    public static Data _data;
    public static GameConfig                    config;

    public List<ReceiptGroupInfo>               receipts = new ArrayList<>();
    public List<ItemInfo>                       items = new ArrayList<>();
    public List<NetworkInfo>                    networks = new ArrayList<>();
    public List<ItemInfo>                       gatherItems;
    public List<CategoryInfo>                   categories;
    public List<ItemInfo>                       equipments;
    public List<PlanetInfo>                     planets = new ArrayList<>();
    public Map<String, WeatherInfo>            weathers = new HashMap<>();
    public HashMap<Integer, String>             strings = new HashMap<>();
    public boolean                              needUIRefresh;
    public List<IDataLoader>                    _loaders;
    public HashMap<String, CharacterTypeInfo>   characters;
    public Map<String, UICursor>                cursors = new HashMap<>();
    public List<BuffInfo>                      buffs = new ArrayList<>();
    public List<DiseaseInfo>                   diseases = new ArrayList<>();
    public List<ItemInfo>                       consumables;
    public List<BindingInfo>                    bindings = new ArrayList<>();

    public Data() {
        _data = this;

        _loaders = new ArrayList<>();

        _loaders.add(new ConfigLoader());
        _loaders.add(new EquipmentLoader());
        _loaders.add(new CategoryLoader());
        _loaders.add(new CharacterLoader());
    }

    public static Data getData() {
        return _data;
    }

    private boolean hasObject(List<NetworkInfo> objects, String name) {
        for (ObjectInfo object: objects) {
            if (object.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private ObjectInfo getObject(List<? extends ObjectInfo> objects, String name) {
        for (ObjectInfo object: objects) {
            if (object.name.equals(name)) {
                return object;
            }
        }
        throw new RuntimeException("Unable to find object \"" + name + "\"");
//        return null;
    }

    public UICursor         getCursor(String name) { return this.cursors.get(name); }
    public ReceiptGroupInfo getReceipt(String receiptName) { return (ReceiptGroupInfo) getObject(receipts, receiptName); }
    public DiseaseInfo      getDisease(String receiptName) { return (DiseaseInfo) getObject(diseases, receiptName); }
    public NetworkInfo      getNetwork(String receiptName) { return (NetworkInfo) getObject(networks, receiptName); }
    public ItemInfo         getItemInfo(String receiptName) { return (ItemInfo) getObject(items, receiptName); }
    public ItemInfo         getEquipment(String receiptName) { return (ItemInfo) getObject(equipments, receiptName); }
    public String           getString(int hash) { return _data.strings.get(hash); }
    public static String    getString(String str) { return _data.strings.containsKey(str.hashCode()) ? _data.strings.get(str.hashCode()) : str; }

    public boolean          hasNetwork(String name) { return hasObject(networks, name); }
    public boolean          hasString(int hash) { return _data.strings.containsKey(hash); }

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

    public void fix() {
        this.items.stream()
                .forEach(item -> {
                    if (item.parentName != null) {
                        item.parent = getItemInfo(item.parentName);
                    }
                    if (item.networks != null) {
                        item.networks.forEach(network -> network.network = getNetwork(network.name));
                    }
                    if (item.factory != null && item.factory.receipts != null) {
                        item.factory.receipts.forEach(receipt -> receipt.receipt = getReceipt(receipt.receiptName));
                    }
                    if (item.actions != null) {
                        item.actions.stream().filter(action -> action.products != null)
                                .forEach(action -> {
                                    if (action.networkNames != null) {
                                        action.networks = action.networkNames.stream().map(this::getNetwork).collect(Collectors.toList());
                                    }
                                    if (action.products != null) {
                                        action.products.forEach(product -> product.item = getItemInfo(product.itemName));
                                    }
                                });
                    }
                    if (item.receipts != null) {
                        item.receipts.stream().filter((receipt -> receipt.components != null))
                                .forEach(receipt -> receipt.components
                                        .forEach(component -> component.item = getItemInfo(component.itemName)));
                    }
                });

        this.receipts.forEach(receipt -> receipt.receipts.forEach(productInfo -> {
            if (productInfo.outputs != null) {
                productInfo.outputs.forEach(product -> product.item = getItemInfo(product.itemName));
            }
            if (productInfo.inputs != null) {
                productInfo.inputs.forEach(component -> component.item = getItemInfo(component.itemName));
            }
        }));

        this.networks.forEach(network -> network.items = network.itemNames.stream().map(this::getItemInfo).collect(Collectors.toList()));

        this.consumables = this.items.stream().filter(item -> item.isConsumable).collect(Collectors.toList());
    }
}