package org.smallbox.faraway.game.module.base;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.BuildableMapObject;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.StructureModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.path.PathManager;
import org.smallbox.faraway.util.MoveListener;

/**
 * Created by Alex on 09/10/2015.
 */
public class BuildJob extends BaseJobModel {
    private final BuildableMapObject _buildItem;
    private JobActionReturn _return = JobActionReturn.CONTINUE;

    public BuildJob(BuildableMapObject item) {
        super(null, item.getParcel(), new IconDrawable("data/res/ic_build.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 64, 32, 32, 7, 10));
        _buildItem = item;
        _buildItem.setBuildJob(this);
    }

    @Override
    public String getShortLabel() {
        return "Build " + _buildItem.getInfo().label;
    }

    @Override
    public ParcelModel getActionParcel() {
        return null;
    }

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.BUILD;
    }

    @Override
    public boolean onCheck(CharacterModel character) {
        return _buildItem.hasAllComponents();
    }

    @Override
    protected void onFinish() {
    }

    @Override
    protected void onStart(CharacterModel character) {
        _message = "Build " + _buildItem.getInfo().label;

        GraphPath<ParcelModel> path = PathManager.getInstance().getBestApprox(character.getParcel(), _jobParcel);
        if (path != null) {
            _targetParcel = path.get(path.getCount() - 1);
            System.out.println("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getInfo().getFirstName() + ")");
            character.move(path);
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (WorldHelper.getApproxDistance(_character.getParcel(), _buildItem.getParcel()) <= 2) {
            if (_buildItem.build()) {
                _buildItem.setBuildJob(null);
                _return = JobActionReturn.FINISH;
                if (_buildItem instanceof ItemModel) {
                    Game.getInstance().notify(observer -> observer.onRefreshItem((ItemModel) _buildItem));
                } else if (_buildItem instanceof StructureModel) {
                    Game.getInstance().notify(observer -> observer.onRefreshStructure((StructureModel)_buildItem));
                }
            }
            _progress = (double)_buildItem.getCurrentBuild() / _buildItem.getTotalBuild();
        }
        return _return;
    }
}
