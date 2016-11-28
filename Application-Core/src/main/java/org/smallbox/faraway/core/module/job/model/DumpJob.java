package org.smallbox.faraway.core.module.job.model;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.MoveListener;

public class DumpJob extends JobModel {
    private MapObjectModel  _item;
    private int             _current;

    private DumpJob(ParcelModel jobParcel) {
        super(null, jobParcel, new IconDrawable("data/res/ic_dump.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 128, 32, 32, 7, 10));
    }

    public static JobModel create(MapObjectModel item) {
        assert item != null;

        DumpJob job = new DumpJob(item.getParcel());

        job.setLabel(Application.data.getString("Dump") + " " + Application.data.getString(item.getLabel()));
        job._item = item;
        job._cost = item.getInfo().cost;
        job.setOnActionListener(() -> {
            if (job.getCharacter().getType().needs.joy != null) {
                job.getCharacter().getNeeds().addValue("entertainment", job.getCharacter().getType().needs.joy.change.work);
            }
        });

        return job;
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        // TODO
        //        // Item is no longer exists
//        if (_item != _item.getParcel().getItem() && _item != _item.getParcel().getStructure()) {
//            _reason = JobAbortReason.INVALID;
//            return JobCheckReturn.ABORT;
//        }

        // No path to item
        if (!Application.pathManager.hasPath(character.getParcel(), _item.getParcel(), true, false)) {
            return JobCheckReturn.STAND_BY;
        }

        return JobCheckReturn.OK;
    }

    @Override
    protected void onStart(CharacterModel character) {
        _targetParcel = character.moveApprox(_item.getParcel(), new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel character) {
            }

            @Override
            public void onFail(CharacterModel character) {
                _reason = JobAbortReason.BLOCKED;
                quit(character);
            }
        });
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (_current++ < _cost) {
            _progress = _current / _cost;
            return JobActionReturn.CONTINUE;
        }
        return JobActionReturn.COMPLETE;
    }

    @Override
    protected void onComplete() {
        throw new NotImplementedException("");

//        ModuleHelper.getWorldModule().remove(_item);
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.BUILD;
    }
}
