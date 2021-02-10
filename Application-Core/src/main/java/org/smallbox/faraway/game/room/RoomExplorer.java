package org.smallbox.faraway.game.room;

import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.room.model.RoomModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.game.world.WorldModule;

import java.util.*;

import static org.smallbox.faraway.core.game.model.MovableModel.Direction.*;

@GameObject
public class RoomExplorer {
    @Inject private ApplicationConfig applicationConfig;
    @Inject private WorldModule worldModule;
    @Inject private ItemModule itemModule;
    @Inject private RoomModule roomModule;

    public void refresh() {
        Collection<RoomModel> newRooms = new ArrayList<>();

        itemModule.getAll().stream().filter(usableItem -> usableItem.getInfo().name.contains("door")).forEach(door -> {

            if (isWalkable(door.getParcel(), LEFT) && isWalkable(door.getParcel(), RIGHT) && !isWalkable(door.getParcel(), TOP) && !isWalkable(door.getParcel(), BOTTOM)) {
                createRoom(newRooms, worldModule.getParcel(door.getParcel(), LEFT));
                createRoom(newRooms, worldModule.getParcel(door.getParcel(), RIGHT));
            }

            if (isWalkable(door.getParcel(), TOP) && isWalkable(door.getParcel(), BOTTOM) && !isWalkable(door.getParcel(), LEFT) && !isWalkable(door.getParcel(), RIGHT)) {
                createRoom(newRooms, worldModule.getParcel(door.getParcel(), TOP));
                createRoom(newRooms, worldModule.getParcel(door.getParcel(), BOTTOM));
            }

        });

        roomModule.replaceAll(newRooms, RoomModel::buildKey, room -> room);
    }

    private void createRoom(Collection<RoomModel> newRooms, Parcel parcel) {
        RoomModel room = Optional.ofNullable(roomModule.getRoom(parcel)).orElse(new RoomModel(RoomModel.RoomType.NONE, parcel.z, parcel));

        Set<Parcel> parcels = new HashSet<>();
        Set<Parcel> doors = new HashSet<>();

        discoverRoom(parcels, doors, parcel);

        if (room.getParcels().size() <= applicationConfig.game.roomMaxSize) {
            room.setParcels(parcels);
            room.setDoors(doors);
            newRooms.add(room);
        }
    }

    private void discoverRoom(Set<Parcel> parcels, Set<Parcel> doors, Parcel parcel) {
        boolean isDoor = parcel.getItems().stream().anyMatch(item -> item.getInfo().name.contains("door"));

        if (isDoor) {
            doors.add(parcel);
        }

        if (parcels.size() <= applicationConfig.game.roomMaxSize && !isDoor && !parcels.contains(parcel) && parcel.isWalkable()) {
            parcels.add(parcel);
            WorldHelper.getParcelAround(parcel, SurroundedPattern.SQUARE, parcelAround -> discoverRoom(parcels, doors, parcelAround));
        }
    }

    private boolean isWalkable(Parcel parcel, MovableModel.Direction direction) {
        return Optional.ofNullable(worldModule.getParcel(parcel, direction)).filter(Parcel::isWalkable).isPresent();
    }

}