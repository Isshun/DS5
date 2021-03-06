package org.smallbox.faraway.game.area;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.module.SuperGameModule;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.util.GameException;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@GameObject
public class AreaModule extends SuperGameModule {

    private final Collection<AreaModel> _areas = new LinkedBlockingQueue<>();

    private final Collection<Class<? extends AreaModel>> _areaClasses = new LinkedBlockingQueue<>();

    public Collection<AreaModel> getAreas() {
        return _areas;
    }

    public <T extends AreaModel> Stream<T> getAreas(Class<T> cls) {
        return _areas.stream()
                .filter(cls::isInstance)
                .map(cls::cast);
    }

    public Stream<Parcel> getAreasParcels(Class<? extends AreaModel> cls) {
        return _areas.stream()
                .filter(cls::isInstance)
                .map(cls::cast)
                .flatMap(area -> area.getParcels().stream());
    }

    public List<Parcel> getParcelsByType(Class<? extends AreaModel> cls) {
        return _areas.stream()
                .filter(cls::isInstance)
                .flatMap(area -> area.getParcels().stream())
                .collect(Collectors.toList());
    }

    public <T extends AreaModel> List<T> getAreasByType(Class<T> cls) {
        return _areas.stream()
                .filter(cls::isInstance)
                .map(cls::cast)
                .collect(Collectors.toList());
    }

    public void addAreaClass(Class<? extends AreaModel> cls) {
        _areaClasses.add(cls);
    }

    public <T extends AreaModel> T addArea(Class<T> cls, Collection<Parcel> parcels) {
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

    public AreaModel getArea(Parcel parcel) {
        throw new GameException(AreaModule.class, "Not implemented");
    }

    public <T extends AreaModel> Stream<T> getArea(Class<T> cls) {
        return _areas.stream()
                .filter(cls::isInstance)
                .map(cls::cast);
    }

    public <T extends AreaModel> T getArea(Class<T> cls, Parcel parcel) {
        AreaModel area = getArea(parcel);
        return cls.isInstance(area) ? cls.cast(area) : null;
    }

    public void removeArea(List<Parcel> parcels) {
        _areas.forEach(area -> parcels.forEach(area::removeParcel));
        _areas.removeIf(area -> area.getParcels().isEmpty());
    }

}