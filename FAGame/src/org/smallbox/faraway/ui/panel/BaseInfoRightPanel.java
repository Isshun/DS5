package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.model.item.ParcelModel;
import org.smallbox.faraway.model.room.RoomModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

import java.util.Map;

/**
 * Created by Alex on 13/06/2015.
 */
public class BaseInfoRightPanel extends BaseRightPanel {
    private View        _frame_room_info;
    private ParcelModel _area;

    public BaseInfoRightPanel(UserInterface.Mode mode, GameEventListener.Key shortcut, String path) {
        super(mode, shortcut, path);
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        if (findById("frame_room_info") != null) {
            ViewFactory.getInstance().load("data/ui/panels/view_room_info.yml", view -> {
                _frame_room_info = view;
                ((FrameLayout) findById("frame_room_info")).addView(view);
            });
        }
    }

    public void select(ParcelModel area) {
        _area = area;
        if (area != null && area.getRoom() != null && _frame_room_info != null) {
            ((TextView)_frame_room_info.findById("lb_room")).setString(area.getRoom().isExterior() ? "Exterior" : "Room");
            ((TextView)_frame_room_info.findById("lb_room_size")).setString("Size: " + area.getRoom().getParcels().size());
            ((TextView)_frame_room_info.findById("lb_room_temperature")).setString("Temperature: " + (int)area.getRoom().getTemperatureInfo().temperature + "°");
            ((TextView)_frame_room_info.findById("lb_room_light")).setString("Light: " + area.getRoom().getLight());

            ((TextView)_frame_room_info.findById("lb_heat_potency")).setString("HP: " + area.getRoom().getTemperatureInfo().heatPotency);
            ((TextView)_frame_room_info.findById("lb_cold_potency")).setString("CP: " + area.getRoom().getTemperatureInfo().coldPotency);
            ((TextView)_frame_room_info.findById("lb_heat")).setString("H: " + area.getRoom().getTemperatureInfo().targetHeat);
            ((TextView)_frame_room_info.findById("lb_cold")).setString("C: " + area.getRoom().getTemperatureInfo().targetCold);
            ((TextView)_frame_room_info.findById("lb_heat_left")).setString("HL: " + area.getRoom().getTemperatureInfo().heatPotencyLeft);
            ((TextView)_frame_room_info.findById("lb_cold_left")).setString("CL: " + area.getRoom().getTemperatureInfo().coldPotencyLeft);

            int count = 0;
            Map<RoomModel, Integer> neighborhoods = area.getRoom().getNeighborhoods();
            for (Integer i: neighborhoods.values()) {
                count += i;
            }
            ((TextView)_frame_room_info.findById("lb_neighborhood")).setString("Neighborhood: " + count);
        }
    }

    @Override
    protected void onRefresh(int update) {
        select(_area);
    }

}
