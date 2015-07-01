package org.smallbox.faraway.data.serializer;

import com.ximpleware.VTDNav;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.ui.AreaManager;
import org.smallbox.faraway.ui.AreaModel;
import org.smallbox.faraway.ui.AreaType;

import java.util.List;
import java.util.Map;
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
        private final List<String>          accepts;
        private final String                type;

        public AreaSave(AreaModel area) {
            this.type = area.getType().name();
            this.accepts = area.getItemsAccepts().entrySet().stream().filter(Map.Entry::getValue).map(entry -> entry.getKey().name).collect(Collectors.toList());
            this.parcels = area.getParcels().stream().map(parcel -> new AreaParcelSave(parcel.getX(), parcel.getY())).collect(Collectors.toList());
        }
    }

    @Override
    public void save(GameSerializer.GameSave save) {
        save.areas = ((AreaManager)Game.getInstance().getManager(AreaManager.class)).getAreas().stream().map(AreaSave::new).collect(Collectors.toList());
    }

    @Override
    public void load(VTDNav save) {
//        for (AreaSave areaSave: save.areas) {
//            AreaModel area = AreaManager.createArea(AreaType.valueOf(areaSave.type));
//
//            // Add parcel
//            for (AreaSave.AreaParcelSave parcelSave: areaSave.parcels) {
//                area.addParcel(Game.getWorldManager().getParcel(parcelSave.x, parcelSave.y));
//            }
//
//            // Add accepted items
//            for (String itemName: areaSave.accepts) {
//                area.setAccept(GameData.getData().getItemInfo(itemName), true);
//            }
//
//            ((AreaManager)Game.getInstance().getManager(AreaManager.class)).addArea(area);
//        }
    }
}
