package org.smallbox.faraway.game.item.job;

import org.smallbox.faraway.game.item.UsableItem;

public interface OnUseCallback {
    /**
     * Methode appelée à chaque tick tant que l'action n'est pas terminée
     *
     * @param item         l'item
     * @param durationLeft la durée restante
     */
    void onUse(UsableItem item, double durationLeft);
}
