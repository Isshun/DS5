package org.smallbox.faraway.modules.characterNeed.strategy;

import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.character.model.base.NeedEntry;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.job.JobModule;

/**
 * Created by Alex on 11/03/2017.
 */
public interface NeedRestoreStrategy {
    boolean ok(CharacterModel character, NeedEntry need, JobModule jobModule, ConsumableModule consumableModule, ItemModule itemModule);
    boolean done();
}
