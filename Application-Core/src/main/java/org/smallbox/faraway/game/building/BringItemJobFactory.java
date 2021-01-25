package org.smallbox.faraway.game.building;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.world.model.BuildableMapObject;
import org.smallbox.faraway.game.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.consumable.ConsumableItem;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.job.task.TechnicalTask;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;

@GameObject
public class BringItemJobFactory {
    @Inject private ConsumableModule consumableModule;
    @Inject private JobModule jobModule;

    public BringItemJob createJob(BuildJob parent, BuildableMapObject targetItem, ItemInfo itemInfo, int quantity) {
        BringItemJob job = new BringItemJob();

        job.setMainLabel("Bring " + itemInfo.label + " to " + targetItem.getInfo().label);
        job.setIcon("[base]/graphics/jobs/ic_haul.png");
        job.setSkillType(CharacterSkillExtra.SkillType.STORE);
        job.setColor(new Color(0xbb391eff));

        // Init
        job.addPrerequisiteTask(() -> {
            job.sourceConsumable = consumableModule.find(itemInfo);

            if (job.sourceConsumable != null) {
                job._targetParcel = job.sourceConsumable.getParcel();
                job.setAcceptedParcel(WorldHelper.getParcelAround(job.sourceConsumable.getParcel(), SurroundedPattern.SQUARE));
                return true;
            }

            return false;
        });

        job.addTask(new TechnicalTask(j -> {
            takeConsumable(parent, targetItem, job.sourceConsumable, job.getCharacter(), quantity);
            job._targetParcel = targetItem.getParcel();
            job.setAcceptedParcel(WorldHelper.getParcelAround(targetItem.getParcel(), SurroundedPattern.SQUARE));
        }));

        job.addTask(new TechnicalTask(j -> targetItem.addInventory(job.getCharacter().getExtra(CharacterInventoryExtra.class).takeInventory(itemInfo)))); // Move consumable to map object

        job.onNewInit();

        return job;
    }

    /**
     * Take consumable from parcel and move them to character's inventory
     */
    private void takeConsumable(BuildJob parent, BuildableMapObject mapObject, ConsumableItem consumable, CharacterModel character, int quantity) {
        int availableQuantity = consumableModule.removeQuantity(consumable, quantity);
        character.getExtra(CharacterInventoryExtra.class).addInventory(consumable.getInfo(), availableQuantity);

        if (availableQuantity < quantity) {
            BringItemJob bringItemJob = createJob(parent, mapObject, consumable.getInfo(), quantity - availableQuantity);
            parent.addSubJob(bringItemJob);
            jobModule.add(bringItemJob);
        }
    }

}
