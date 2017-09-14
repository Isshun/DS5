package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.GameSerializer;
import org.smallbox.faraway.GameTask;
import org.smallbox.faraway.GameTaskSerializer;
import org.smallbox.faraway.common.GameTaskDeserializer;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import static org.smallbox.faraway.common.util.Utils.second;

/**
 * Created by 300206 on 14/09/2017.
 */
@GameDeserializer(GameTaskDeserializer.class)
@GameSerializer(GameTaskSerializer.class)
public class RandomMoveTask extends GameTask {

    private final ParcelModel initialParcel;
    private final ParcelModel targetParcel;
    private final CharacterModel character;

    public RandomMoveTask(CharacterModel character) {
        super("CHARACTER_MOVE", "Random move", second(2));
        this.character = character;
        this.initialParcel = character.getParcel();
        this.targetParcel = WorldHelper.getRandomParcel(character.getParcel(), 20);
    }

    @Override
    public void onStart() {
        this.character._task = this;
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onClose() {
        assert this.character._task == this;

        this.character._task = null;
        this.character._parcel = targetParcel;
    }

}
