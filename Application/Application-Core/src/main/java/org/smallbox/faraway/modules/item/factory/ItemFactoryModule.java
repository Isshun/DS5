package org.smallbox.faraway.modules.item.factory;

import org.smallbox.faraway.core.ModuleInfo;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.ItemModuleObserver;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alex on 26/06/2015.
 */
@ModuleInfo(name = "ItemFactoryModule")
public class ItemFactoryModule extends GameModule {

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private JobModule jobModule;

    @BindModule
    private StructureModule structureModule;

    @BindModule
    private ConsumableModule consumableModule;

//    @BindModule
//    private WorldInteractionModule worldInteractionModule;

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
        _items.forEach(item -> actionCraftJobs(item, item.getFactory()));
        _items.forEach(item -> actionRelease(item, item.getFactory()));
    }

    /**
     * Verifie que la recette en cours ai assez de composants disponibles et accessibles
     *
     * @param item UsableItem
     */
    private void actionCheckComponents(UsableItem item, ItemFactoryModel factory) {
        if (factory.hasRunningReceipt()) {
            if (factory.getRunningReceipt().receiptInfo.inputs.stream().anyMatch(ReceiptInputInfo -> !hasEnoughConsumables(ReceiptInputInfo, item))) {
                item.getFactory().setMessage("not enough component");

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
            item.getFactory().setMessage("seek best receipt");

            factory.getReceipts().stream()
                    .filter(receipt -> receipt.receiptInfo.inputs.stream().allMatch(inputInfo -> hasEnoughConsumables(inputInfo, item)))
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
            for (ReceiptGroupInfo.ReceiptInfo.ReceiptInputInfo receiptInputInfo: factory.getRunningReceipt().receiptInfo.inputs) {
                int currentQuantity = factory.getCurrentQuantity(receiptInputInfo.item);
                if (currentQuantity < receiptInputInfo.quantity) {

                    // Compte le nombre de consomables qui seront rapportés par les jobs existants
                    int quantityInJob = 0;
                    for (BasicHaulJob job: factory.getHaulJobs()) {
                        if (job.getHaulingConsumable().getInfo() == receiptInputInfo.item) {
                            quantityInJob += job.getHaulingQuantity();
                        }
                    }

                    // Ajoute des jobs tant que la quantité de consomable présent dans l'usine et les jobs est inférieur à la quantité requise
                    while (currentQuantity + quantityInJob < receiptInputInfo.quantity) {
                        BasicHaulJob job = consumableModule.createHaulJob(receiptInputInfo.item, item, receiptInputInfo.quantity - (currentQuantity + quantityInJob));

                        // Ajoute la quantity de consomable ammené par ce nouveau job à la quantity existante
                        if (job != null) {
                            Log.info("[Factory] %s -> launch hauling job for component: %s", item, receiptInputInfo);

                            quantityInJob += job.getHaulingQuantity();
                            factory.addHaulJob(job);
                            jobModule.addJob(job);
                        }

                        // Annule la construction s'il n'y à plus suffisament de consomable disponible
                        else {
                            Log.debug("[Factory] %s -> not enough component: %s", item, receiptInputInfo);

                            actionClear(item, factory);
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Crée la tache de création d'objet (BasicCraftJob)
     *
     * @param item
     * @param factory
     */
    private void actionCraftJobs(UsableItem item, ItemFactoryModel factory) {
        if (factory.hasRunningReceipt() && factory.hasEnoughComponents() && factory.getRunningReceipt().getCostRemaining() > 0 && factory.getCraftJob() == null) {
            item.getFactory().setMessage("Create craft job");

            BasicCraftJob job = new BasicCraftJob(item.getParcel()) {
                @Override
                public boolean onCraft() {
                    // Incrémente la variable count de la recette (état d'avancement)
                    return factory.getRunningReceipt().decreaseCostRemaining() == 0;
                }
            };
            job.setLabel("Craft job");

            factory.setCraftJob(job);
            jobModule.addJob(job);
        }
    }

    /**
     * Crée les composants lorsque le craft est terminé
     *
     * @param item
     * @param factory
     */
    private void actionRelease(UsableItem item, ItemFactoryModel factory) {
        if (factory.hasRunningReceipt() && factory.hasEnoughComponents() && factory.getRunningReceipt().getCostRemaining() == 0) {
            // TODO: consomme tous l'inventaire, y compris les objets non utilisés dans la recette
            // Consomme les objets d'entrés
            item.getInventory().clear();

            // Crée les objets de sorties
            factory.getRunningReceipt().receiptInfo.outputs.forEach(output -> consumableModule.putConsumable(item.getParcel(), output.item, output.quantity[0]));

            factory.setMessage("craft " + factory.getRunningReceipt() + " complete");

            actionClear(item, factory);
        }
    }

    /**
     * Nettoie la fabrique en prévision des futurs contructions
     *
     * @param item
     * @param factory
     */
    private void actionClear(UsableItem item, ItemFactoryModel factory) {
        // TODO: Libère les objets non consommés

        // Termine les jobs
        factory.getHaulJobs().forEach(JobModel::cancel);
        factory.getHaulJobs().clear();

        // Retire la recette en cours
        factory.setRunningReceipt(null);

        // Supprime la tache de création d'objet
        factory.setCraftJob(null);
    }

    private boolean hasEnoughConsumables(ReceiptGroupInfo.ReceiptInfo.ReceiptInputInfo inputInfo, UsableItem item) {
        return hasEnoughConsumables(inputInfo.item, inputInfo.quantity, item);
    }

    private boolean hasEnoughConsumables(FactoryReceiptModel.FactoryComponentModel component, UsableItem item) {
        return hasEnoughConsumables(component.itemInfo, component.totalQuantity - component.currentQuantity, item);
    }

    /**
     * Appel le module des consomables et verifie si suffisament d'objets existent et sont accessibles
     *
     * @param itemInfo Composant à tester
     * @param needQuantity Quantity necessaire
     * @param item Fabrique
     *
     * @return true si des composants existent et sont accéssibles
     */
    private boolean hasEnoughConsumables(ItemInfo itemInfo, int needQuantity, UsableItem item) {
        int availableQuantity = 0;

        // Check l'inventaire de la fabrique
        for (ConsumableItem consumable: item.getInventory()) {
            if (consumable.getInfo() == itemInfo) {
                availableQuantity += consumable.getQuantity();
            }
        }
        if (availableQuantity >= needQuantity) {
            return true;
        }

        // Pas assez de composants sur la carte
        if (consumableModule.getTotal(itemInfo) + availableQuantity >= needQuantity) {
            return true;
        }

        // Pas assez de composants accessible
        if (consumableModule.getTotalAccessible(itemInfo, item.getParcel()) + availableQuantity >= needQuantity) {
            return true;
        }

        return false;
    }

}
