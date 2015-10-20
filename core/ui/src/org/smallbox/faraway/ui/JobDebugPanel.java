package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.ui.panel.debug.BaseDebugPanel;

/**
 * Created by Alex on 14/07/2015.
 */
public class JobDebugPanel extends BaseDebugPanel {
    private JobModel _job;

    @Override
    protected String getTitle() {
        return "JobDetail";
    }

    @Override
    protected void onAddDebug() {
        if (_job != null) {
            addDebugView("Job: " + _job.getLabel());
            addDebugView("Message: " + _job.getMessage());
            addDebugView("Item: " + (_job.getItem() != null ? _job.getItem().getLabel() : "none"));
            addDebugView("Status: " + _job.getStatus());
//            addDebugView("Check: " + _job.onCheck(null));
            addDebugView("Progress: " + _job.getProgress());
        }
    }

    @Override
    public void onSelectCharacter(CharacterModel character) {
        _job = character.getJob();
    }

    @Override
    public void onSelectItem(ItemModel item) {
        _job = item != null && item.getJobs() != null ? item.getJobs().get(0) : null;
    }

    @Override
    public void onSelectConsumable(ConsumableModel consumable) {
        _job = consumable != null && consumable.getJobs() != null && !consumable.getJobs().isEmpty() ? consumable.getJobs().get(0) : null;
    }

    @Override
    public void onSelectStructure(StructureModel structure) {
        _job = structure != null && structure.getJobs() != null && !structure.getJobs().isEmpty() ? structure.getJobs().get(0) : null;
    }

}
