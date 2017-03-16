package org.smallbox.faraway.modules.characterNeed;

import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.CharacterInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.CharacterTimetableExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra;
import org.smallbox.faraway.modules.character.model.base.NeedEntry;
import org.smallbox.faraway.modules.characterBuff.CharacterBuffModule;
import org.smallbox.faraway.modules.characterRelation.CharacterRelationModule;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.consumable.ConsumeJob;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.item.job.UseJob;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.smallbox.faraway.modules.character.model.base.CharacterNeedsExtra.*;

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

    @BindModule
    private ItemModule itemModule;

    private Map<NeedEntry, JobModel> _jobs = new ConcurrentHashMap<>();

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    @Override
    public void onModuleUpdate(Game game) {
        characterModule.getCharacters().forEach(character -> {
            CharacterNeedsExtra needs = character.getExtra(CharacterNeedsExtra.class);
            decreaseNeeds(character, needs);
            checkRegularSleep(game, character, needs);
            tryToRestoreNeeds(character, needs);
        });
    }

    /**
     * Envoi le personnage se coucher en fonction de son emploi du temps
     *
     * @param game Game
     * @param character CharacterModel
     * @param needs CharacterNeedsExtra
     */
    private void checkRegularSleep(Game game, CharacterModel character, CharacterNeedsExtra needs) {

        // Récupère le besoin
        NeedEntry sleepNeed = needs.get(TAG_ENERGY);

        // Récupère le job
        JobModel sleepJob = _jobs.get(sleepNeed);
        boolean hasSleepJob = (sleepJob != null && !sleepJob.isClose());

        // Récupère l'emploi du temps
        CharacterTimetableExtra.State state = character.getExtra(CharacterTimetableExtra.class).getState(game.getHour());

        // Check: le personnage est en période SLEEP mais le job n'est pas lancé
        // Envoi le personnage ne coucher
        if (state == CharacterTimetableExtra.State.SLEEP && !hasSleepJob) {
            tryToRestoreNeedWithItem(character, needs.get(TAG_ENERGY));
            return;
        }

        // Check: le personnage est en période WORK et un job est lancé
        // Arrêt du job uniquement si le personnage à son énergie au minimum à la moitier du niveau warning
        double workWakeUpThreshold  = (sleepNeed.critical + (sleepNeed.warning - sleepNeed.critical) / 2);
        if (state == CharacterTimetableExtra.State.WORK && hasSleepJob && sleepNeed.value() >= workWakeUpThreshold) {
            sleepJob.close();
            return;
        }

        // Check: le personnage est en période FREE et un job est lancé
        // Arrêt du job uniquement si le personnage à son énergie au minimum du niveau optimal
        if (state == CharacterTimetableExtra.State.FREE && hasSleepJob && sleepNeed.value() >= sleepNeed.optimal) {
            sleepJob.close();
        }

    }

    /**
     * Mise à jour des besoins
     *
     * @param character CharacterModel
     * @param needs CharacterNeedsExtra
     */
    private void decreaseNeeds(CharacterModel character, CharacterNeedsExtra needs) {
        CharacterInfo.NeedsInfo needsInfo = character.getType().needs;
        needs.addValue(TAG_FOOD, character.isSleeping() ? needsInfo.food.change.sleep : needsInfo.food.change.rest);
        needs.addValue(TAG_DRINK, character.isSleeping() ? needsInfo.drink.change.sleep : needsInfo.drink.change.rest);
        needs.addValue(TAG_ENERGY, character.isSleeping() ? needsInfo.energy.change.sleep : needsInfo.energy.change.rest);
        needs.addValue(TAG_ENTERTAINMENT, character.isSleeping() ? needsInfo.entertainment.change.sleep : needsInfo.entertainment.change.rest);
        needs.addValue(TAG_HAPPINESS, buffModule.getMood(character));
        needs.addValue(TAG_RELATION, characterRelationModule.getScore(character));
    }

    /**
     * Lance les taches pour restaurer les besoins
     *
     * @param character CharacterModel
     * @param needs CharacterNeedsExtra
     */
    private void tryToRestoreNeeds(CharacterModel character, CharacterNeedsExtra needs) {

        // Ajoute les jobs consume
        needs.getAll().stream()
                .filter(need -> need.value() < need.warning)
                .filter(need -> !_jobs.containsKey(need) || _jobs.get(need).isClose())
                .forEach(need -> {

                    if (tryToRestoreNeedWithItem(character, need)) {
                        return;
                    }

                    if (tryToRestoreNeedWithConsumable(character, need)) {
                        return;
                    }

                });
    }

    private boolean tryToRestoreNeedWithItem(CharacterModel character, NeedEntry need) {

        // Find best item
        UsableItem bestItem = itemModule.getItems().stream()
                .filter(item -> need.hasEffect(item.getInfo().use))
                .findAny().orElse(null);
        if (bestItem == null) {
            return false;
        }

        // Create use job
        UseJob job = itemModule.createUseJob(bestItem, bestItem.getInfo().use.duration, (consumable, durationLeft) -> {
            character.getExtra(CharacterNeedsExtra.class).use(consumable.getInfo().use);
        });
        if (job == null) {
            return false;
        }

        _jobs.put(need, job);
        return true;
    }

    private boolean tryToRestoreNeedWithConsumable(CharacterModel character, NeedEntry need) {

        // Find best consumable
        ConsumableItem bestConsumable = consumableModule.getConsumables().stream()
                .filter(consumable -> consumable.getFreeQuantity() > 0)
                .filter(item -> need.hasEffect(item.getInfo().consume))
                .findAny().orElse(null);
        if (bestConsumable == null) {
            return false;
        }

        // Create consume job
        ConsumeJob job = consumableModule.createConsumeJob(bestConsumable, bestConsumable.getInfo().consume.duration, (consumable, durationLeft) -> {
            ItemInfo itemInfo = bestConsumable.getInfo();
            character.getExtra(CharacterNeedsExtra.class).use(itemInfo.consume);
        });
        if (job == null) {
            return false;
        }

        _jobs.put(need, job);
        return true;
    }
}
