package org.smallbox.faraway.core.game.module.area;

import com.ximpleware.*;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.area.model.*;
import org.smallbox.faraway.core.game.module.world.model.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Alex on 01/06/2015.
 */
public class AreaModuleSerializer implements SerializerInterface {
    @Override
    public void save(FileOutputStream fos) throws IOException {
        FileUtils.write(fos, "<areas>");
        for (AreaModel area: ((AreaModule) ModuleManager.getInstance().getModule(AreaModule.class)).getAreas()) {
            FileUtils.write(fos, "<model id='" + 0 + "' name='" + area.getName() + "' type='" + area.getType() + "'>");

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
                FileUtils.write(fos, "<parcel x='" + parcel.x + "' y='" + parcel.y + "' />");
            }
            FileUtils.write(fos, "</parcels>");

            FileUtils.write(fos, "</model>");
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
                        area.addParcel(ModuleHelper.getWorldModule().getParcel(vn.parseInt(vn.getAttrVal("x")), vn.parseInt(vn.getAttrVal("y"))));
                    }
                    ap3.resetXPath();
                    break;
            }
        }
        ap2.resetXPath();

        ((AreaModule)ModuleManager.getInstance().getModule(AreaModule.class)).addArea(area);

        vn.pop();
    }
}
