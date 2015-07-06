package org.smallbox.faraway.data.serializer;

import com.ximpleware.*;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.AreaManager;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.area.*;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.util.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Alex on 01/06/2015.
 */
public class AreaSerializer implements SerializerInterface {
    @Override
    public void save(FileOutputStream fos) throws IOException {
        FileUtils.write(fos, "<areas>");
        for (AreaModel area: ((AreaManager) Game.getInstance().getManager(AreaManager.class)).getAreas()) {
            FileUtils.write(fos, "<area id='" + 0 + "' name='" + area.getName() + "' type='" + area.getType() + "'>");

            // Write accepted items
            if (area.isStorage()) {
                FileUtils.write(fos, "<accept>");
                for (Map.Entry<ItemInfo, Boolean> entry : ((StorageAreaModel) area).getItemsAccepts().entrySet()) {
                    if (entry.getValue()) {
                        FileUtils.write(fos, "<item>" + entry.getKey().name + "</item>");
                    }
                }
                FileUtils.write(fos, "</accept>");
            }

            // Write parcels
            FileUtils.write(fos, "<parcels>");
            for (ParcelModel parcel: area.getParcels()) {
                FileUtils.write(fos, "<parcel x='" + parcel.getX() + "' y='" + parcel.getY() + "' />");
            }
            FileUtils.write(fos, "</parcels>");

            FileUtils.write(fos, "</area>");
        }
        FileUtils.write(fos, "</areas>");
    }

    @Override
    public void load(VTDNav vn) throws XPathParseException, NavException, XPathEvalException {
        AutoPilot ap = new AutoPilot(vn);
        ap.selectXPath("/save/areas/*");

        AutoPilot ap2 = new AutoPilot(vn);
        ap2.selectXPath("*");

        AutoPilot ap3 = new AutoPilot(vn);
        ap3.selectXPath("*");

        while (ap.evalXPath() != -1) {
            readArea(ap2, ap3, vn);
        }
    }

    private void readArea(AutoPilot ap2, AutoPilot ap3, VTDNav vn) throws NavException, XPathEvalException {
        vn.push();

        AreaType type = AreaType.valueOf(vn.toString(vn.getAttrVal("type")));
        AreaModel area;
        switch (type) {
            case STORAGE: area = new StorageAreaModel(); break;
            case GARDEN: area = new GardenAreaModel(); break;
            case HOME: area = new HomeAreaModel(); break;
            default: area = new AreaModel(type); break;
        }

        while (ap2.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {

                case "accept":
                    while (ap3.evalXPath() != -1) {
                        area.setAccept(GameData.getData().getItemInfo(vn.toString(vn.getText())), true);
                    }
                    ap3.resetXPath();
                    break;

                case "parcels":
                    while (ap3.evalXPath() != -1) {
                        area.addParcel(Game.getWorldManager().getParcel(vn.parseInt(vn.getAttrVal("x")), vn.parseInt(vn.getAttrVal("y"))));
                    }
                    ap3.resetXPath();
                    break;
            }
        }
        ap2.resetXPath();

        ((AreaManager)Game.getInstance().getManager(AreaManager.class)).addArea(area);

        vn.pop();
    }
}
