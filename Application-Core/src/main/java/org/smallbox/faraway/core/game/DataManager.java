package org.smallbox.faraway.core.game;

import org.codehaus.groovy.runtime.metaclass.ConcurrentReaderHashMap;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.game.modelInfo.*;
import org.smallbox.faraway.game.character.CharacterInfo;
import org.smallbox.faraway.game.characterBuff.BuffInfo;
import org.smallbox.faraway.game.characterDisease.DiseaseInfo;
import org.smallbox.faraway.game.planet.PlanetInfo;
import org.smallbox.faraway.game.planet.RegionInfo;
import org.smallbox.faraway.game.weather.WeatherInfo;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationObject
public class DataManager {
    public List<ReceiptGroupInfo> receipts = new ArrayList<>();
    public List<ItemInfo> items = new ArrayList<>();
    public List<NetworkInfo> networks = new ArrayList<>();
    public List<ItemInfo> gatherItems;
    public List<CategoryInfo> categories;
    public List<ItemInfo> equipments;
    public List<PlanetInfo> planets = new ArrayList<>();
    public Map<String, WeatherInfo> weathers = new HashMap<>();
    public HashMap<Integer, String> strings = new HashMap<>();
    public boolean needUIRefresh = false;
    public HashMap<String, CharacterInfo> characters = new HashMap<>();
    //    public Map<String, UICursor>                cursors = new HashMap<>();
    public List<BuffInfo> buffs = new ArrayList<>();
    public List<DiseaseInfo> diseases = new ArrayList<>();
    public List<ItemInfo> consumables;
    public List<BindingInfo> bindings = new ArrayList<>();
    private final List<DataAsyncEntry> _async = new LinkedList<>();
    private final ConcurrentReaderHashMap _all = new ConcurrentReaderHashMap();

    private boolean hasObject(List<NetworkInfo> objects, String name) {
        for (ObjectInfo object : objects) {
            if (object.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private ObjectInfo getObject(List<? extends ObjectInfo> objects, String name) {
        for (ObjectInfo object : objects) {
            if (object.name.equals(name)) {
                return object;
            }
        }
        throw new GameException(DataManager.class, "Unable to find object: " + name);
    }

    //    public UICursor         getCursor(String name) { return this.cursors.get(name); }
    public ReceiptGroupInfo getReceipt(String receiptName) {
        return (ReceiptGroupInfo) getObject(receipts, receiptName);
    }

    public WeatherInfo getWeather(String receiptName) {
        return weathers.get(receiptName);
    }

    public DiseaseInfo getDisease(String receiptName) {
        return (DiseaseInfo) getObject(diseases, receiptName);
    }

    public NetworkInfo getNetwork(String receiptName) {
        return (NetworkInfo) getObject(networks, receiptName);
    }

    public ItemInfo getItemInfo(String receiptName) {
        return (ItemInfo) getObject(items, receiptName);
    }

    public ItemInfo getEquipment(String receiptName) {
        return (ItemInfo) getObject(equipments, receiptName);
    }

    public BindingInfo getBinding(String bindingId) {
        return (BindingInfo) getObject(bindings, bindingId);
    }

    public String getString(int hash) {
        return strings.get(hash);
    }

    public String getString(String str) {
        return strings.getOrDefault(str.hashCode(), str);
    }

    public PlanetInfo getPlanet(String planetName) {
        return (PlanetInfo) getObject(planets, planetName);
    }

    public Collection<ItemInfo> getItems() {
        return items;
    }

    public boolean hasNetwork(String name) {
        return hasObject(networks, name);
    }

    //    public boolean          hasString(int hash) { return strings.containsKey(hash); }
    public boolean hasString(int hash) {
        return false;
    }

    public RegionInfo getRegion(String planetName, String regionName) {
        for (PlanetInfo planet : planets) {
            if (planet.name.equals(planetName))
                for (RegionInfo region : planet.regions) {
                    if (region.name.equals(regionName)) {
                        return region;
                    }
                }
        }
        throw new RuntimeException("Unable to find planet or region (" + planetName + ", " + regionName + ")");
    }

    public void fix() {
        _async.forEach(entry -> {
            Object object = _all.get(entry.name);
            if (entry.listener != null && entry.cls.isInstance(object)) {
                entry.listener.onGetAsync(entry.cls.cast(object));
            }
        });

        this.consumables = this.items.stream().filter(item -> item.isConsumable).collect(Collectors.toList());
    }

    public <T> void getAsync(String itemName, Class<T> cls, DataAsyncListener<T> dataAsyncListener) {
        if (itemName == null) {
            throw new RuntimeException("not null");
        }

        _async.add(new DataAsyncEntry(itemName, cls, dataAsyncListener));
    }

    public void add(String name, Object object) {
        if (!_all.containsKey(name)) {
            _all.put(name, object);
        } else {
            Log.warning("Data: key %s already exists", name);
        }
    }

    public interface DataAsyncListener<T> {
        void onGetAsync(T itemInfo);
    }

    private static class DataAsyncEntry<T> {
        private final Class<T> cls;
        public String name;
        DataAsyncListener listener;

        DataAsyncEntry(String itemName, Class<T> cls, DataAsyncListener dataAsyncListener) {
            this.name = itemName;
            this.cls = cls;
            this.listener = dataAsyncListener;
        }
    }
}