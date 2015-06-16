package org.smallbox.faraway.engine.serializer;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.ui.AreaManager;
import org.smallbox.faraway.ui.AreaModel;
import org.smallbox.faraway.ui.AreaType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alex on 01/06/2015.
 */
public class AreaSerializer implements SerializerInterface {
    public static class AreaSave {
        public static class AreaParcelSave {
            private final int       x;
            private final int       y;

            public AreaParcelSave(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        private final List<AreaParcelSave>  parcels;
        private final String                type;

        public AreaSave(AreaModel area) {
            this.type = area.getType().name();
            this.parcels = area.getParcels().stream().map(parcel -> new AreaParcelSave(parcel.getX(), parcel.getY())).collect(Collectors.toList());
        }
    }

    @Override
    public void save(GameSerializer.GameSave save) {
        save.areas = Game.getAreaManager().getAreas().stream().map(AreaSave::new).collect(Collectors.toList());
    }

    @Override
    public void load(GameSerializer.GameSave save) {
        for (AreaSave areaSave: save.areas) {
            AreaModel area = AreaManager.createArea(AreaType.valueOf(areaSave.type));
            for (AreaSave.AreaParcelSave parcelSave: areaSave.parcels) {
                area.addParcel(Game.getWorldManager().getParcel(parcelSave.x, parcelSave.y));
            }
            Game.getAreaManager().addArea(area);
        }
    }
}
