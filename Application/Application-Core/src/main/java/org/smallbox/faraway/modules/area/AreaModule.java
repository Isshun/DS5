package org.smallbox.faraway.modules.area;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by Alex on 13/06/2015.
 */
@ModuleSerializer(AreaSerializer.class)
@SuppressWarnings("Duplicates")
public class AreaModule extends GameModule {

    private Collection<AreaModel> _areas = new LinkedBlockingQueue<>();

    private Collection<Class<? extends AreaModel>> _areaClasses = new LinkedBlockingQueue<>();

    public Collection<AreaModel> getAreas() {
        return _areas;
    }

    public List<ParcelModel> getParcelsByType(Class<? extends AreaModel> cls) {
        return _areas.stream()
                .filter(cls::isInstance)
                .flatMap(area -> area.getParcels().stream())
                .collect(Collectors.toList());
    }

    public <T extends AreaModel> List<T> getAreasByType(Class<T> cls) {
        return _areas.stream()
                .filter(cls::isInstance)
                .map(area -> (T)area)
                .collect(Collectors.toList());
    }

    public void addAreaClass(Class<? extends AreaModel> cls) {
        _areaClasses.add(cls);
    }

    public <T extends AreaModel> T addArea(Class<T> cls, Collection<ParcelModel> parcels) {
        T existingArea = _areas.stream()
                .filter(cls::isInstance)
                .filter(area -> area.getParcels().stream().anyMatch(parcels::contains))
                .map(cls::cast)
                .findAny().orElse(null);

        // Add parcel to existing area
        if (existingArea != null) {
            parcels.forEach(existingArea::addParcel);
            return existingArea;
        }

        // Create new area
        try {
            T area = cls.getConstructor().newInstance();
            parcels.forEach(area::addParcel);
            _areas.add(area);
            return area;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Collection<Class<? extends AreaModel>> getAreaClasses() {
        return _areaClasses;
    }

    public AreaModel getArea(ParcelModel parcel) {
        return _areas.stream()
                .filter(area -> area.getParcels().contains(parcel))
                .findFirst()
                .orElse(null);
    }

    public void removeArea(List<ParcelModel> parcels) {
        _areas.forEach(area -> parcels.forEach(area::removeParcel));
        _areas.removeIf(area -> area.getParcels().isEmpty());
    }

}