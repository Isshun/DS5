package org.smallbox.faraway.data.serializer;

import com.ximpleware.*;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.util.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Alex on 11/07/2015.
 */
public class ParamSerializer implements SerializerInterface {

    @Override
    public void load(VTDNav vn) throws XPathParseException, NavException, XPathEvalException {
        AutoPilot ap = new AutoPilot(vn);
        ap.selectXPath("/save/params/*");

        int viewportX = 0;
        int viewportY = 0;

        while (ap.evalXPath() != -1) {
            vn.push();

            switch (vn.toString(vn.getAttrVal("name"))) {
                case "viewportX":
                    viewportX = vn.parseInt(vn.getAttrVal("value"));
                    break;
                case "viewportY":
                    viewportY = vn.parseInt(vn.getAttrVal("value"));
                    break;
            }
            vn.pop();
        }

        Game.getInstance().getViewport().setPosition(viewportX, viewportY);
    }

    @Override
    public void save(FileOutputStream fos) throws IOException {
        FileUtils.write(fos, "<params>");
        FileUtils.write(fos, "<param name='viewportX' value='" + Game.getInstance().getViewport().getPosX() + "' />");
        FileUtils.write(fos, "<param name='viewportY' value='" + Game.getInstance().getViewport().getPosY() + "' />");
        FileUtils.write(fos, "</params>");
    }

}
