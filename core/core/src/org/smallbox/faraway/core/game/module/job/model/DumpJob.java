package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.MoveListener;

public class DumpJob extends JobModel {
    private MapObjectModel     _dumpObject;

    private DumpJob(ParcelModel jobParcel) {
        super(null, jobParcel, new IconDrawable("data/res/ic_dump.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 128, 32, 32, 7, 10));
    }

    public static JobModel create(MapObjectModel objectModel) {
        if (objectModel == null) {
            return null;
        }

        DumpJob job = new DumpJob(objectModel.getParcel());
        job.setDumpObject(objectModel);
        job.setCost(objectModel.getInfo().cost);
        job.setStrategy(j -> {
            if (j.getCharacter().getType().needs.joy != null) {
                j.getCharacter().getNeeds().joy += j.getCharacter().getType().needs.joy.change.work;
            }
        });
        return job;
    }

    public void setDumpObject(MapObjectModel dumpObject) {
        _dumpObject = dumpObject;
    }

    @Override
    public boolean onCheck(CharacterModel character) {
        // Item is null
        if (_dumpObject == null) {
            _reason = JobAbortReason.INVALID;
            return false;
        }

        // Item is no longer exists
        if (_dumpObject != _jobParcel.getItem() && _dumpObject != _jobParcel.getStructure()) {
            _reason = JobAbortReason.INVALID;
            return false;
        }

        return true;
    }

    @Override
    protected void onFinish() {
        ModuleHelper.getWorldModule().remove(_dumpObject);
    }

    @Override
    protected void onStart(CharacterModel character) {
        PathModel path = PathManager.getInstance().getBestApprox(character.getParcel(), _jobParcel);

        if (path != null) {
            _targetParcel = path.getLastParcel();

            System.out.println("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getPersonals().getFirstName() + ")");
            character.move(path, new MoveListener<CharacterModel>() {
                @Override
                public void onReach(CharacterModel character) {
                }

                @Override
                public void onFail(CharacterModel character) {
                }

                @Override
                public void onSuccess(CharacterModel character) {
                }
            });
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        _dumpObject.addProgress(-character.getTalents().get(CharacterTalentExtra.TalentType.BUILD).work());
        _progress = _cost - _dumpObject.getProgress();
        return _dumpObject.isDump() ? JobActionReturn.FINISH : JobActionReturn.CONTINUE;
    }

    @Override
    public boolean canBeResume() {
        return false;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.BUILD;
    }

    @Override
    public String getLabel() {
        return "Dump " + _dumpObject.getLabel();
    }

    @Override
    public String getShortLabel() {
        return "Dump " + _dumpObject.getLabel();
    }

    @Override
    public ParcelModel getActionParcel() {
        return _dumpObject.getParcel();
    }

}
