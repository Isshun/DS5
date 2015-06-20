package org.smallbox.faraway.ui.panel;

import com.badlogic.gdx.ai.pfa.Connection;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.ui.engine.FrameLayout;
import org.smallbox.faraway.ui.engine.TextView;
import org.smallbox.faraway.ui.engine.View;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.room.NeighborModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

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

    public void select(ParcelModel parcel) {
        _area = parcel;
        if (parcel != null && parcel.getRoom() != null && _frame_room_info != null) {
            ((TextView)_frame_room_info.findById("lb_room")).setString(parcel.getRoom().isExterior() ? "Exterior" : "Room");
            ((TextView)_frame_room_info.findById("lb_room_size")).setString("Size: " + parcel.getRoom().getParcels().size());
            ((TextView)_frame_room_info.findById("lb_room_temperature")).setString("Temperature: " + (int)parcel.getRoom().getTemperatureInfo().temperature + "°");
            ((TextView)_frame_room_info.findById("lb_room_light")).setString("Light: " + parcel.getRoom().getLight());

            ((TextView)_frame_room_info.findById("lb_heat_potency")).setString("HP: " + parcel.getRoom().getTemperatureInfo().heatPotency);
            ((TextView)_frame_room_info.findById("lb_cold_potency")).setString("CP: " + parcel.getRoom().getTemperatureInfo().coldPotency);
            ((TextView)_frame_room_info.findById("lb_heat")).setString("H: " + parcel.getRoom().getTemperatureInfo().targetHeat);
            ((TextView)_frame_room_info.findById("lb_cold")).setString("C: " + parcel.getRoom().getTemperatureInfo().targetCold);
            ((TextView)_frame_room_info.findById("lb_heat_left")).setString("HL: " + parcel.getRoom().getTemperatureInfo().heatPotencyLeft);
            ((TextView)_frame_room_info.findById("lb_cold_left")).setString("CL: " + parcel.getRoom().getTemperatureInfo().coldPotencyLeft);
            ((TextView)_frame_room_info.findById("lb_oxygen")).setString("O2: " + parcel.getRoom().getOxygen());

            String strConnexion = "";
            for (Connection<ParcelModel> connection: parcel.getConnections()) {
                strConnexion += strConnexion.isEmpty() ? "Connexion: " : ", ";
                strConnexion += connection.getToNode().getX() + "x" + connection.getToNode().getY();
            }
            ((TextView)_frame_room_info.findById("lb_connexion")).setString(strConnexion);

            int count = 0;
            for (NeighborModel neighbor: parcel.getRoom().getNeighbors()) {
                count += neighbor.parcels.size();
            }
            ((TextView)_frame_room_info.findById("lb_neighborhood")).setString("Neighborhood: " + count);
        }
    }

    @Override
    protected void onRefresh(int update) {
        select(_area);
    }

}
