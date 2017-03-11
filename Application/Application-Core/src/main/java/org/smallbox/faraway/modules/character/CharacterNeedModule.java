package org.smallbox.faraway.modules.character;

import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.module.ModuleSerializer;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.characterBuff.CharacterBuffModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.ConsumeJob;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;

import static org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra.*;

@ModuleSerializer(CharacterModuleSerializer.class)
public class CharacterNeedModule extends GameModule {

    @BindComponent
    private Data data;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private CharacterModule characterModule;

    @BindModule
    private CharacterRelationModule characterRelationModule;

    @BindModule
    private CharacterBuffModule buffModule;

    @BindModule
    private ConsumableModule consumableModule;

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    @Override
    public void onModuleUpdate(Game game) {
        characterModule.getCharacters().forEach(character -> {
            CharacterNeedsExtra needs = character.getExtra(CharacterNeedsExtra.class);

            // Ajoute les besoins au personnage si manquants
            if (needs == null) {
                needs = character.addExtra(new CharacterNeedsExtra(character, null));
            }

            decreaseNeeds(character, needs);
            tryToRestoreNeeds(character, needs);
        });
    }

    /**
     * Mise à jour les besoins
     *
     * @param character CharacterModel
     * @param needs CharacterNeedsExtra
     */
    private void decreaseNeeds(CharacterModel character, CharacterNeedsExtra needs) {
        boolean isSleeping = false;
        CharacterInfo.Needs needsInfo = character.getType().needs;
        needs.addValue(TAG_FOOD, isSleeping ? needsInfo.food.change.sleep : needsInfo.food.change.rest);
        needs.addValue(TAG_DRINK, isSleeping ? needsInfo.water.change.sleep : needsInfo.water.change.rest);
        needs.addValue(TAG_ENERGY, isSleeping ? needsInfo.energy.change.sleep : needsInfo.energy.change.rest);
        needs.addValue(TAG_ENTERTAINMENT, isSleeping ? needsInfo.joy.change.sleep : needsInfo.joy.change.rest);
        needs.addValue(TAG_HAPPINESS, buffModule.getMood(character) / 100.0);
        needs.addValue(TAG_RELATION, characterRelationModule.getScore(character) / 100.0);
    }

    /**
     * Lance les taches pour restaurer les besoins
     *
     * @param character CharacterModel
     * @param needs CharacterNeedsExtra
     */
    private void tryToRestoreNeeds(CharacterModel character, CharacterNeedsExtra needs) {

        // Ajoute les jobs consume
        needs.getAll().forEach((name, need) -> {
            if (need.value() < need.warning) {

                String jobData = "characterNeedModule-consume-" + name + "-character-" + character.getId();
                if (jobModule.getJobs().stream().noneMatch(job -> jobData.equals(job.getData()))) {

                    // Find best item
                    ConsumableItem bestConsumable = consumableModule.getConsumables().stream()
                            .filter(consumable -> consumable.getFreeQuantity() > 0)
                            .filter(consumable -> needs.hasEffect(need, consumable))
                            .findAny().orElse(null);

                    // Create consume job
                    if (bestConsumable != null) {

                        jobModule.createJob(ConsumeJob.class, null, bestConsumable.getParcel(), job -> {
                            job.setData(jobData);
                            ConsumableModule.ConsumableJobLock lock = consumableModule.lock(job, bestConsumable, 1);
                            job.addTask("Move", c -> c.moveTo(WorldHelper.getParcel(2, 2, 1)) ? JobTaskReturn.COMPLETE : JobTaskReturn.CONTINUE);
                            job.addTask("Drink", c -> {
                                needs.use(consumableModule.takeConsumable(lock));
                                return JobTaskReturn.COMPLETE;
                            });
                            return true;
                        });

                    }

                }

            }
        });
    }
}
