package org.smallbox.faraway.core.game.module.character;

import com.ximpleware.*;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.AndroidModel;
import org.smallbox.faraway.core.game.module.character.model.DroidModel;
import org.smallbox.faraway.core.game.module.character.model.HumanModel;
import org.smallbox.faraway.core.game.module.character.model.TimeTableModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterPersonalsExtra;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Alex on 01/07/2015.
 */
public class CharacterModuleSerializer implements SerializerInterface {
    @Override
    public void save(FileOutputStream fos) throws IOException {
        FileUtils.write(fos, "<characters>");
        for (CharacterModel character: ModuleHelper.getCharacterModule().getCharacters()) {
            writeCharacter(fos, character);
        }
        FileUtils.write(fos, "</characters>");
    }

    private void writeCharacter(FileOutputStream fos, CharacterModel character) throws IOException {
        FileUtils.write(fos, "<characters id='" + character.getId() + "' type='" + character.getType().name + "'>");

        FileUtils.write(fos, "<lastname>" + character.getPersonals().getLastName().trim() + "</lastname>");
        FileUtils.write(fos, "<firstname>" + character.getPersonals().getFirstName().trim() + "</firstname>");
        FileUtils.write(fos, "<old>" + character.getPersonals().getLastName() + "</old>");
        FileUtils.write(fos, "<x>" + character.getParcel().x + "</x>");
        FileUtils.write(fos, "<y>" + character.getParcel().y + "</y>");
        FileUtils.write(fos, "<gender>" + character.getPersonals().getGender() + "</gender>");

        writeCharacterNeeds(fos, character.getNeeds());
        writeCharacterTimetable(fos, character.getTimetable());

        FileUtils.write(fos, "</characters>");
    }

    private void writeCharacterTimetable(FileOutputStream fos, TimeTableModel timetable) throws IOException {
        FileUtils.write(fos, "<timetable>");

        for (Map.Entry<Integer, Integer> entry: timetable.getHours().entrySet()) {
            FileUtils.write(fos, "<period h='" + entry.getKey() + "'>" + entry.getValue() + "</period>");
        }

        FileUtils.write(fos, "</timetable>");
    }

    private void writeCharacterNeeds(FileOutputStream fos, CharacterNeedsExtra needs) throws IOException {
        FileUtils.write(fos, "<needs>");

//        FileUtils.write(fos, "<isSleeping>" + needs.isSleeping() + "</isSleeping>");
        FileUtils.write(fos, "<water>" + needs.get("water") + "</water>");
        FileUtils.write(fos, "<socialize>" + needs.socialize + "</socialize>");
        FileUtils.write(fos, "<food>" + needs.get("food") + "</food>");
        FileUtils.write(fos, "<happiness>" + needs.get("happiness") + "</happiness>");
        FileUtils.write(fos, "<relation>" + needs.get("relation") + "</relation>");
        FileUtils.write(fos, "<security>" + needs.get("security") + "</security>");
        FileUtils.write(fos, "<oxygen>" + needs.get("oxygen") + "</oxygen>");
        FileUtils.write(fos, "<energy>" + needs.get("energy") + "</energy>");
        FileUtils.write(fos, "<health>" + needs.health + "</health>");
        FileUtils.write(fos, "<sickness>" + needs.sickness + "</sickness>");
        FileUtils.write(fos, "<injuries>" + needs.injuries + "</injuries>");
        FileUtils.write(fos, "<entertainment>" + needs.get("entertainment") + "</entertainment>");

        FileUtils.write(fos, "</needs>");
    }

    @Override
    public void load(GameInfo gameInfo, VTDNav vn, GameSerializer.GameSerializerInterface gameSerializerInterface) throws XPathParseException, NavException, XPathEvalException {
        AutoPilot ap = new AutoPilot(vn);
        ap.selectXPath("/save/characters/*");

        AutoPilot ap2 = new AutoPilot(vn);
        ap2.selectXPath("*");

        AutoPilot ap3 = new AutoPilot(vn);
        ap3.selectXPath("*");

        while (ap.evalXPath() != -1) {
            readCharacter(ap2, ap3, vn);
        }
    }

    private void readCharacter(AutoPilot ap2, AutoPilot ap3, VTDNav vn) throws NavException, XPathEvalException {
        CharacterModel character;
        int x = 0;
        int y = 0;

        switch (vn.toString(vn.getCurrentIndex())) {
            default:
                character = new HumanModel(0, WorldHelper.getParcel(10, 10), null, null, 0);
                break;
            case "android":
                character = new AndroidModel(0, WorldHelper.getParcel(10, 10), null, null, 0);
                break;
            case "droid":
                character = new DroidModel(0, WorldHelper.getParcel(10, 10), null, null, 0);
                break;
        }

        vn.push();
        while (ap2.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "id":
                    character.setId(vn.parseInt(vn.getText()));
                    break;
                case "lastname":
                    character.getPersonals().setLastName(vn.toString(vn.getText()));
                    break;
                case "firstname":
                    character.getPersonals().setFirstName(vn.toString(vn.getText()));
                    break;
                case "old":
                    character.getPersonals().setOld((int) vn.parseDouble(vn.getText()));
                    break;
                case "x":
                    x = vn.parseInt(vn.getText());
                    break;
                case "y":
                    y = vn.parseInt(vn.getText());
                    break;
                case "gender":
                    character.getPersonals().setGender(CharacterPersonalsExtra.Gender.valueOf(vn.toString(vn.getText())));
                    break;
                case "timetable":
                    while (ap3.evalXPath() != -1) {
                        character.getTimetable().set(vn.parseInt(vn.getAttrVal("h")), vn.parseInt(vn.getText()));
                    }
                    ap3.resetXPath();
                    break;
                case "needs":
                    while (ap3.evalXPath() != -1) {
                        switch (vn.toString(vn.getCurrentIndex())) {
//                            case "isSleeping":
//                                character.getNeeds().setSleeping("true".equals(vn.toString(vn.getText())));
//                                break;
                            case "water":
                                character.getNeeds().setValue("water", vn.parseDouble(vn.getText()));
                                break;
                            case "socialize":
                                character.getNeeds().socialize = vn.parseDouble(vn.getText());
                                break;
                            case "food":
                                character.getNeeds().setValue("food", vn.parseDouble(vn.getText()));
                                break;
                            case "happiness":
                                character.getNeeds().setValue("happiness", vn.parseDouble(vn.getText()));
                                break;
                            case "relation":
                                character.getNeeds().setValue("relation", vn.parseDouble(vn.getText()));
                                break;
                            case "security":
                                character.getNeeds().setValue("security", vn.parseDouble(vn.getText()));
                                break;
                            case "oxygen":
                                character.getNeeds().setValue("oxygen", vn.parseDouble(vn.getText()));
                                break;
                            case "energy":
                                character.getNeeds().setValue("energy", vn.parseDouble(vn.getText()));
                                break;
                            case "health":
                                character.getNeeds().health = vn.parseDouble(vn.getText());
                                break;
                            case "sickness":
                                character.getNeeds().sickness = vn.parseDouble(vn.getText());
                                break;
                            case "injuries":
                                character.getNeeds().injuries = vn.parseDouble(vn.getText());
                                break;
                            case "entertainment":
                                character.getNeeds().setValue("entertainment", vn.parseDouble(vn.getText()));
                                break;
                        }
                    }
                    ap3.resetXPath();
                    break;
            }
        }
        ap2.resetXPath();
        vn.pop();

        character.setParcel(WorldHelper.getParcel(x, y));
        ModuleHelper.getCharacterModule().add(character);
    }
}
