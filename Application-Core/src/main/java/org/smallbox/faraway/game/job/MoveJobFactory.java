package org.smallbox.faraway.game.job;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.dig.DigJob;
import org.smallbox.faraway.game.job.task.TechnicalTask;
import org.smallbox.faraway.game.world.Parcel;

@GameObject
public class MoveJobFactory {

    public JobModel createJob(Parcel targetParcel, CharacterModel character) {
        JobModel job = new DigJob(targetParcel);

        job.addAcceptedParcel(targetParcel);
        job.setMainLabel("Move");
        job.setCharacterRequire(character);
        job.setIcon("data/graphics/jobs/ic_mining.png");
        job.setColor(new Color(0x80391eff));

        // Move character to parcel
        job.addAcceptedParcel(targetParcel);
        job.addTask(new TechnicalTask(j -> {}));

        job.onNewInit();

        return job;
    }
}
