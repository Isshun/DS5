package org.smallbox.faraway.module.itemFactory;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.module.consumable.BasicHaulJob;
import org.smallbox.faraway.module.consumable.ConsumableModule;
import org.smallbox.faraway.module.item.UsableItem;
import org.smallbox.faraway.module.item.ItemModule;
import org.smallbox.faraway.module.item.ItemModuleObserver;
import org.smallbox.faraway.module.job.JobModule;
import org.smallbox.faraway.module.structure.StructureModule;
import org.smallbox.faraway.module.world.WorldInteractionModule;
import org.smallbox.faraway.module.world.WorldModule;
import org.smallbox.faraway.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alex on 26/06/2015.
 */
public class ItemFactoryModule extends GameModule {

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private StructureModule structureModule;

    @BindModule
    private ConsumableModule consumableModule;

    @BindModule
    private WorldInteractionModule worldInteractionModule;

    @BindModule
    private ItemModule itemModule;

    private List<UsableItem> _items;

    @Override
    public void onGameCreate(Game game) {
        _items = new LinkedList<>();

        itemModule.addObserver(new ItemModuleObserver() {
            @Override
            public void onAddItem(ParcelModel parcel, UsableItem item) {
                if (item.hasFactory()) {
                    _items.add(item);
                }
            }

            @Override
            public void onRemoveItem(ParcelModel parcel, UsableItem item) {
                _items.remove(item);
            }
        });
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        _items.forEach(item -> actionCheckComponents(item, item.getFactory()));
        _items.forEach(item -> actionFindBestReceipt(item, item.getFactory()));
        _items.forEach(item -> actionHaulingJobs(item, item.getFactory()));
        _items.forEach(item -> actionCraftJob(item, item.getFactory()));
    }

    /**
     * Verifie que la recette en cours ai assez de composants disponibles et accessibles
     *
     * @param item UsableItem
     */
    private void actionCheckComponents(UsableItem item, ItemFactoryModel factory) {
        if (factory.hasRunningReceipt()) {
            if (factory.getRunningReceipt().getComponents().stream().anyMatch(component -> !hasEnoughConsumables(component, item.getParcel()))) {
                Log.debug("[Factory] %s -> not enough component", item);

                actionClear(item, factory);
            }
        }
    }

    /**
     * Lance la meilleur recette en fonction des composants disponibles
     *
     * @param item UsableItem
     */
    private void actionFindBestReceipt(UsableItem item, ItemFactoryModel factory) {
        if (!factory.hasRunningReceipt()) {
            Log.info("[Factory] %s -> seek best receipt", item);

            factory.getReceipts().stream()
                    .filter(receipt -> receipt.receiptInfo.inputs.stream().allMatch(inputInfo -> hasEnoughConsumables(inputInfo, item.getParcel())))
                    .findFirst()
                    .ifPresent(factory::setRunningReceipt);
        }
    }

    /**
     * Lance les hauling jobs necessaire pour apporter tous les composants
     *
     * @param item UsableItem
     */
    private void actionHaulingJobs(UsableItem item, ItemFactoryModel factory) {
        if (factory.hasRunningReceipt()) {
            for (FactoryReceiptModel.FactoryComponentModel component: factory.getRunningReceipt().getComponents()) {
                if (component.currentQuantity < component.totalQuantity) {

                    // Compte le nombre de consomables qui seront rapportés par les job existants
                    int quantityInJob = 0;
                    for (BasicHaulJob job: factory.getHaulJobs()) {
                        if (job.getHaulingConsumable().getInfo() == component.itemInfo) {
                            quantityInJob += job.getHaulingQuantity();
                        }
                    }

                    // Ajoute des jobs tant que la quantité de consomable présent dans l'usine et les jobs est inférieur à la quantité requise
                    while (component.currentQuantity + quantityInJob < component.totalQuantity) {
                        BasicHaulJob job = consumableModule.createHaulJob(component.itemInfo, item, component.totalQuantity - (component.currentQuantity + quantityInJob));

                        // Ajoute la quantity de consomable ammené par ce nouveau job à la quantity existante
                        if (job != null) {
                            Log.info("[Factory] %s -> launch hauling job for component: %s", item, component);

                            quantityInJob += job.getHaulingQuantity();
                            factory.addHaulJob(job);
                            jobModule.addJob(job);
                        }

                        // Annule la construction s'il n'y à plus suffisament de consomable disponible
                        else {
                            Log.debug("[Factory] %s -> not enough component: %s", item, component);

                            actionClear(item, factory);
                            return;
                        }
                    }
                }
            }
        }
    }

    // TODO
    private void actionCraftJob(UsableItem item, ItemFactoryModel factory) {
        if (factory.hasRunningReceipt() && factory.getRunningReceipt().hasEnoughComponents()) {
            Log.info("[Factory] %s -> craft %s", item, factory.getRunningReceipt());

            // Consomme les objets d'entrée
            factory.getRunningReceipt().getComponents().forEach(component -> component.currentQuantity = 0);

            // Crée les objets de sortie
            factory.getRunningReceipt().receiptInfo.outputs.forEach(output -> consumableModule.putConsumable(item.getParcel(), output.item, output.quantity[0]));

            Log.info("[Factory] %s -> craft %s complete", item, factory.getRunningReceipt());

            actionClear(item, factory);
        }
    }

    private void actionClear(UsableItem item, ItemFactoryModel factory) {
        // Libère les objets non consommés
        factory.getRunningReceipt().getComponents().forEach(component -> component.currentQuantity = 0);

        // Termine les jobs
        factory.getHaulJobs().forEach(JobModel::cancel);
        factory.getHaulJobs().clear();

        // Retire la recette en cours
        factory.setRunningReceipt(null);
    }

    private boolean hasEnoughConsumables(ReceiptGroupInfo.ReceiptInfo.ReceiptInputInfo inputInfo, ParcelModel parcel) {
        return hasEnoughConsumables(inputInfo.item, inputInfo.quantity, parcel);
    }

    private boolean hasEnoughConsumables(FactoryReceiptModel.FactoryComponentModel component, ParcelModel parcel) {
        return hasEnoughConsumables(component.itemInfo, component.totalQuantity - component.currentQuantity, parcel);
    }

    /**
     * Appel le module des consomables et verifie si suffisament d'objets existent et sont accessible
     *
     * @param itemInfo Composant à tester
     * @param needQuantity Quantity necessaire
     * @param parcel Parcel de l'usine
     *
     * @return true si des composants existent et sont accéssibles
     */
    private boolean hasEnoughConsumables(ItemInfo itemInfo, int needQuantity, ParcelModel parcel) {

        // Pas assez de composants sur la carte
        if (consumableModule.getTotal(itemInfo) < needQuantity) {
            return false;
        }

        // Pas assez de composants accessible
        if (consumableModule.getTotalAccessible(itemInfo, parcel) < needQuantity) {
            return false;
        }

        return true;
    }

}
