package org.smallbox.faraway.data.serializer;

import com.ximpleware.*;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.WorldManager;
import org.smallbox.faraway.game.model.character.AndroidModel;
import org.smallbox.faraway.game.model.character.DroidModel;
import org.smallbox.faraway.game.model.character.HumanModel;
import org.smallbox.faraway.game.model.character.base.CharacterInfoModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;

/**
 * Created by Alex on 01/07/2015.
 */
public class NewCharacterSerializer implements SerializerInterface {
    @Override
    public void save(GameSerializer.GameSave save) {

    }

    @Override
    public void load(VTDNav vn) throws XPathParseException, NavException, XPathEvalException {
        AutoPilot ap = new AutoPilot(vn);
        ap.selectXPath("/org.smallbox.faraway.data.serializer.GameSerializer_-GameSave/characters/*");

        AutoPilot ap2 = new AutoPilot(vn);
        ap2.selectXPath("*");

        AutoPilot ap3 = new AutoPilot(vn);
        ap3.selectXPath("*");

        while (ap.evalXPath() != -1) {
            createCharacter(ap2, ap3, vn);
        }
    }

    private void createCharacter(AutoPilot ap2, AutoPilot ap3, VTDNav vn) throws NavException, XPathEvalException {
        CharacterModel character;

        switch (vn.toString(vn.getCurrentIndex())) {
            default:
                character = new HumanModel(0, 0, 0, null, null, 0);
                break;
            case "android":
                character = new AndroidModel(0, 0, 0, null, null, 0);
                break;
            case "droid":
                character = new DroidModel(0, 0, 0, null, null, 0);
                break;
        }

        vn.push();
        while (ap2.evalXPath() != -1) {
            switch (vn.toString(vn.getCurrentIndex())) {
                case "id":
                    character.setId(vn.parseInt(vn.getText()));
                    break;
                case "lastname":
                    character.getInfo().setLastName(vn.toString(vn.getText()));
                    break;
                case "firstname":
                    character.getInfo().setFirstName(vn.toString(vn.getText()));
                    break;
                case "old":
                    character.setOld((int)vn.parseDouble(vn.getText()));
                    break;
                case "x":
                    character.setX(vn.parseInt(vn.getText()));
                    break;
                case "y":
                    character.setY(vn.parseInt(vn.getText()));
                    break;
                case "gender":
                    character.getInfo().setGender(CharacterInfoModel.Gender.valueOf(vn.toString(vn.getText())));
                    break;
                case "needs":
                    while (ap3.evalXPath() != -1) {
                        switch (vn.toString(vn.getCurrentIndex())) {
                            case "isSleeping":
                                character.getNeeds().setSleeping("true".equals(vn.toString(vn.getText())));
                                break;
                            case "drinking":
                                character.getNeeds().drinking = vn.parseDouble(vn.getText());
                                break;
                            case "socialize":
                                character.getNeeds().socialize = vn.parseDouble(vn.getText());
                                break;
                            case "food":
                                character.getNeeds().food = vn.parseDouble(vn.getText());
                                break;
                            case "happiness":
                                character.getNeeds().happiness = vn.parseDouble(vn.getText());
                                break;
                            case "relation":
                                character.getNeeds().relation = vn.parseDouble(vn.getText());
                                break;
                            case "security":
                                character.getNeeds().security = vn.parseDouble(vn.getText());
                                break;
                            case "oxygen":
                                character.getNeeds().oxygen = vn.parseDouble(vn.getText());
                                break;
                            case "energy":
                                character.getNeeds().energy = vn.parseDouble(vn.getText());
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
                            case "satiety":
                                character.getNeeds().satiety = vn.parseDouble(vn.getText());
                                break;
                            case "joy":
                                character.getNeeds().joy = vn.parseDouble(vn.getText());
                                break;
                        }
                    }
                    ap3.resetXPath();
                    break;
            }
        }
        ap2.resetXPath();
        vn.pop();

        Game.getCharacterManager().add(character);
    }
}
