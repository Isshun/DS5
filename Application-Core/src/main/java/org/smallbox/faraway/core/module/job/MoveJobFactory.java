package org.smallbox.faraway.core.module.job;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.dig.DigJob;
import org.smallbox.faraway.modules.job.JobModel;

@GameObject
public class MoveJobFactory {

    public JobModel createJob(ParcelModel targetParcel, CharacterModel character) {

        JobModel job = new DigJob();

        job._targetParcel = targetParcel;
        job.setMainLabel("Move");
        job.setCharacterRequire(character);
        job.setIcon("[base]/graphics/jobs/ic_mining.png");
        job.setColor(new Color(0x80391eff));

        // Move character to parcel
        job.addMoveTask("Move to parcel", () -> targetParcel);

        job.onNewInit();

        return job;
    }
}
