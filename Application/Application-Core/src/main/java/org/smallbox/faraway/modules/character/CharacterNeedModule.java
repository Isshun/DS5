package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.ConsumeJob;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;

import static org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra.TAG_FOOD;

@ModuleSerializer(CharacterModuleSerializer.class)
public class CharacterNeedModule extends GameModule {

    @BindComponent
    private Data data;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private CharacterModule characterModule;

    @BindModule
    private ConsumableModule consumableModule;

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    @Override
    public void onModuleUpdate(Game game) {
        characterModule.getCharacters().forEach(this::updateCharacter);
    }

    private void updateCharacter(CharacterModel character) {

        // Ajoute les besoins pour les personnages manquants
        if (character.getExtra(CharacterNeedsExtra.class) == null) {
            character.addExtra(new CharacterNeedsExtra(character, null));
        }

        CharacterNeedsExtra needsExtra = character.getExtra(CharacterNeedsExtra.class);

        // Met à jour les besoins
        needsExtra.update();

        // Ajoute les jobs consume food
        if (jobModule.getJobs().stream().noneMatch(job -> job instanceof ConsumeJob)) {
            if (needsExtra.get(TAG_FOOD) < 0.8) {

                ConsumableItem consumableMeal = consumableModule.getConsumables().stream()
                        .filter(consumable -> consumable.getInfo().instanceOf(data.getItemInfo("base.consumable.easy_meal")))
                        .filter(consumable -> consumable.getFreeQuantity() > 0)
                        .findAny().orElse(null);

                if (consumableMeal != null) {

                    jobModule.createJob(ConsumeJob.class, null, consumableMeal.getParcel(), job -> {
                        ConsumableModule.ConsumableJobLock lock = consumableModule.lock(job, consumableMeal, 1);
                        job.addTask("Move", c -> c.moveTo(WorldHelper.getParcel(2, 2, 1)) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);
                        job.addTask("Eat", c -> {
                            needsExtra.consume(consumableModule.takeConsumable(lock));
                            return JobTaskReturn.COMPLETE;
                        });
                        return true;
                    });

                }

            }
        }
    }

}
