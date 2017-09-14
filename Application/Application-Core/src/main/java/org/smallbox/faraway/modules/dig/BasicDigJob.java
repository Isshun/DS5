package org.smallbox.faraway.modules.dig;

import org.smallbox.faraway.common.modelInfo.ItemInfo;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 28/02/2017.
 */
public class BasicDigJob extends JobModel {

    private ParcelModel _digParcel;
    private double _time;

    public static BasicDigJob create(ConsumableModule consumableModule, JobModule jobModule, WorldModule worldModule, ParcelModel digParcel) {

        ParcelModel targetParcel = WorldHelper.searchAround(digParcel, 1, WorldHelper.SearchStrategy.FREE);
        ItemInfo rockInfo = digParcel.getRockInfo();

        if (targetParcel != null && rockInfo != null) {
            return jobModule.createJob(BasicDigJob.class, null, targetParcel, job -> {

                job.setDigParcel(digParcel);

                // Déplace le personnage à l'emplacement des composants
                job.addMoveTask("Move to rock", targetParcel);

                // Déplace le personnage à l'emplacement des composants
                job.addTask("Dig", (character, hourInterval) -> {
                    job._time += hourInterval;
                    job.setProgress(job._time, Application.config.game.digTime);
                    return job._time >= Application.config.game.digTime ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE;
                });

                // Retire les rochers de la carte
                job.addTechnicalTask("Remove rock", character -> {
                    digParcel.setRockInfo(null);
                    Application.moduleManager.getModule(PathManager.class).refreshConnections(digParcel);
                });

                // Crée les gravats
                job.addTechnicalTask("Create components", character ->
                        rockInfo.actions.stream()
                                .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.MINE)
                                .flatMap(action -> action.products.stream())
                                .forEach(product -> consumableModule.addConsumable(product.item, product.quantity, digParcel)));

                return true;
            });
        }

        return null;
    }

    private void setDigParcel(ParcelModel digParcel) {
        _digParcel = digParcel;
        _startParcel = digParcel;
    }

    public ParcelModel getDigParcel() {
        return _digParcel;
    }

    public BasicDigJob(ItemInfo.ItemInfoAction actionInfo, ParcelModel targetParcel) {
        super(actionInfo, targetParcel);
        _mainLabel = "Dig";
        _targetParcel = targetParcel;
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public boolean checkCharacterAccepted(CharacterModel character) {

        // Character have no skill
        if (!character.hasExtra(CharacterSkillExtra.class) || !character.getExtra(CharacterSkillExtra.class).hasSkill(CharacterSkillExtra.SkillType.DIG)) {
            return false;
        }

        // Character is qualified for job
        return true;

    }

    @Override
    public String toString() { return "Dig"; }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return CharacterSkillExtra.SkillType.DIG;
    }

}
