package org.smallbox.faraway.ui.panel.info;

import com.badlogic.gdx.ai.pfa.Connection;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.module.room.model.NeighborModel;
import org.smallbox.faraway.core.game.module.room.model.RoomModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.UILabel;
import org.smallbox.faraway.ui.engine.views.View;
import org.smallbox.faraway.ui.panel.BaseRightPanel;

/**
 * Created by Alex on 13/06/2015.
 */
public class BaseInfoRightPanel extends BaseRightPanel {
    private View        _frame_parcel_info;
    private View        _frame_room_info;
    private ParcelModel _parcel;

    private static Object[][] TEMPERATURE_COLOR = new Object[][] {
            new Object[] { 80,  0,   new Color(0xf3a000) },
            new Object[] { 60,  25,  new Color(0xce0201) },
            new Object[] { 40,  50,  new Color(0x991d32) },
            new Object[] { 20,  75,  new Color(0x653963) },
            new Object[] {  0,  100, new Color(0x305594) },
            new Object[] { -20, 125, new Color(0x0e67b3) },
            new Object[] { -40, 150, new Color(0x4b8dc6) },
            new Object[] { -60, 175, new Color(0x8cb6db) },
            new Object[] { -80, 200, new Color(0xcbdeee) },
    };

    public BaseInfoRightPanel(UserInterface.Mode mode, GameEventListener.Key shortcut, String path) {
        super(mode, shortcut, path);
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout, UIFrame panel) {
        // Load parcel layout
        if (findById("frame_parcel_info") != null) {
            ViewFactory.getInstance().load("data/ui/panels/view_parcel_info.yml", view -> {
                _frame_parcel_info = view;
                ((UIFrame) findById("frame_parcel_info")).addView(view);
            });
        }

        // Load room layout
        if (findById("frame_room_info") != null) {
            ViewFactory.getInstance().load("data/ui/panels/view_room_info.yml", view -> {
                _frame_room_info = view;
                ((UIFrame) findById("frame_room_info")).addView(view);
            });
        }
    }

    public void select(ParcelModel parcel) {
        _parcel = parcel;

        // Refresh parcel info
        if (_frame_parcel_info != null) {
            if (parcel != null) {
                _frame_parcel_info.setVisible(true);
                boolean isBuildGround = parcel.getStructure() != null && parcel.getStructure().isFloor();
                ((UILabel) _frame_parcel_info.findById("lb_parcel_name")).setText(isBuildGround ? "Build ground" : "Ground");
                ((UILabel) _frame_parcel_info.findById("lb_parcel_pos")).setText(parcel.x + "x" + parcel.y);
                ((UILabel) _frame_parcel_info.findById("lb_light")).setText("light: " + parcel.getLight());
                ((UILabel) _frame_parcel_info.findById("lb_oxygen")).setText("oxygen: " + parcel.getOxygen());
                ((UILabel) _frame_parcel_info.findById("lb_type")).setText("type: " + parcel.getType());

                if (parcel.getEnvironment() != null) {
                    ((UILabel) _frame_parcel_info.findById("lb_blood")).setText("blood: " + parcel.getEnvironment().blood);
                    ((UILabel) _frame_parcel_info.findById("lb_dirt")).setText("dirt: " + parcel.getEnvironment().dirt);
                    ((UILabel) _frame_parcel_info.findById("lb_rubble")).setText("rubble: " + parcel.getEnvironment().rubble);
                    ((UILabel) _frame_parcel_info.findById("lb_snow")).setText("snow: " + parcel.getEnvironment().snow);
                }

                String strConnexion = "";
                for (Connection<ParcelModel> connection : parcel.getConnections()) {
                    strConnexion += strConnexion.isEmpty() ? "Connexion: " : ", ";
                    strConnexion += connection.getToNode().x + "x" + connection.getToNode().y;
                }
                ((UILabel) _frame_parcel_info.findById("lb_connexion")).setText(strConnexion);
            } else {
                _frame_parcel_info.setVisible(false);
            }
        }

        // Refresh room info
        if (_frame_room_info != null) {
            if (parcel != null && parcel.getRoom() != null) {
                RoomModel room = parcel.getRoom();

                _frame_room_info.setVisible(true);
                ((UILabel) _frame_room_info.findById("lb_room")).setText(room.isExterior() ? "Exterior" : room.getName());
                ((UILabel) _frame_room_info.findById("lb_room_size")).setText("Size: " + (room.getParcels().size() / 2) + "m²");
                ((UILabel) _frame_room_info.findById("lb_room_temperature")).setText("Temperature: " + (int) room.getTemperatureInfo().temperature + "°");

                ((UILabel) _frame_room_info.findById("lb_heat_potency")).setText("HP: " + room.getTemperatureInfo().heatPotency);
                ((UILabel) _frame_room_info.findById("lb_cold_potency")).setText("CP: " + room.getTemperatureInfo().coldPotency);
                ((UILabel) _frame_room_info.findById("lb_heat")).setText("H: " + room.getTemperatureInfo().targetHeat);
                ((UILabel) _frame_room_info.findById("lb_cold")).setText("C: " + room.getTemperatureInfo().targetCold);
                ((UILabel) _frame_room_info.findById("lb_heat_left")).setText("HL: " + room.getTemperatureInfo().heatPotencyLeft);
                ((UILabel) _frame_room_info.findById("lb_cold_left")).setText("CL: " + room.getTemperatureInfo().coldPotencyLeft);
                ((UILabel) _frame_room_info.findById("lb_oxygen")).setText("O2: " + room.getOxygen());

                // Temperature cursor
                int temperature = (int) room.getTemperatureInfo().temperature;
                int position = 100 - Math.min(80, Math.max(-80, temperature)) * 80 / 100;
                ((UILabel) _frame_room_info.findById("lb_temperature_cursor")).setText(temperature + "°");
                for (Object[] obj : TEMPERATURE_COLOR) {
                    if (temperature + 10 > (int) obj[0]) {
                        _frame_room_info.findById("temperature_cursor").setPosition(0, position - 10);
                        _frame_room_info.findById("bg_1_temperature_cursor").setBackgroundColor((Color) obj[2]);
                        _frame_room_info.findById("bg_2_temperature_cursor").setBackgroundColor((Color) obj[2]);
                        ((UILabel) _frame_room_info.findById("lb_temperature_cursor")).setTextColor(Color.WHITE);
                        break;
                    }
                }


                if (room.hasNeighbors()) {
                    int count = 0;
                    for (NeighborModel neighbor : room.getNeighbors()) {
                        count += neighbor.parcels.size();
                    }
                    ((UILabel) _frame_room_info.findById("lb_neighborhood")).setText("Neighborhood: " + count);
                }
            } else {
                _frame_room_info.setVisible(false);
            }
        }
    }

    @Override
    protected void onRefresh(int update) {
        select(_parcel);
    }

//    protected void addJobOrder(UIFrame frame, OldReceiptModel.OrderModel order, int index) {
//        UILabel lbOrder = ViewFactory.getInstance().createTextView();
//        lbOrder.setTextSize(14);
//        lbOrder.setPosition(0, index * 20);
//
//        String str = order.consumable.getInfo().label;
//        switch (order.status) {
//            case NONE: lbOrder.setDashedString(str, "waiting", 42); break;
//            case CARRY: lbOrder.setDashedString(str, "carrying", 42); break;
//            case STORED: lbOrder.setDashedString(str, "ok", 42); break;
//        }
//
//
//        frame.addView(lbOrder);
//    }

}
