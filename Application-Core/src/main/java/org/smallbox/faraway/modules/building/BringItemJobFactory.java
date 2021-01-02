package org.smallbox.faraway.modules.building;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.character.model.CharacterInventoryExtra;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;

@GameObject
public class BringItemJobFactory {

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private JobModule jobModule;

    public BringItemJob createJob(BuildJob parent, BuildableMapObject mapObject, ItemInfo itemInfo, int quantity) {
        BringItemJob job = new BringItemJob();

        job.setMainLabel("Bring " + itemInfo.label + " to " + mapObject.getInfo().label);
        job._targetParcel = mapObject.getParcel();
        job.setIcon("[base]/graphics/jobs/ic_haul.png");
        job.setSkillType(CharacterSkillExtra.SkillType.STORE);
        job.setColor(new Color(0xbb391eff));

        // Init
        job.addPrerequisiteTask(() -> (job.sourceConsumable = consumableModule.find(itemInfo)) != null);

        // Job
        job.addMoveTask("Move to consumable", () -> job.sourceConsumable.getParcel()); // Move character to sourceConsumable
        job.addTechnicalTask(() -> takeConsumable(parent, mapObject, job.sourceConsumable, job.getCharacter(), quantity)); // Move consumable to character's inventory
        job.addMoveTask("Move to storage", mapObject::getParcel); // Move character to map object
        job.addTechnicalTask(() -> mapObject.addInventory(job.getCharacter().getExtra(CharacterInventoryExtra.class).takeInventory(itemInfo))); // Move consumable to map object

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
            jobModule.addJob(bringItemJob);
        }
    }

}
