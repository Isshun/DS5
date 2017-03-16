package org.smallbox.faraway.modules.itemFactory;

import org.smallbox.faraway.core.ModuleInfoAnnotation;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.ReceiptGroupInfo;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 26/06/2015.
 */
@ModuleInfoAnnotation(name = "ItemFactoryModule", updateInterval = 1)
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

    @Override
    protected void onModuleUpdate(Game game) {
        itemModule.getItems().stream()
                .filter(item -> item.getFactory() != null)
                .forEach(item -> {
                    actionCheckComponents(item, item.getFactory());
                    actionFindBestReceipt(item, item.getFactory());
                    actionHaulingJobs(item, item.getFactory());
                    actionCraftJobs(item, item.getFactory());
                    actionRelease(item, item.getFactory());
                });
    }

    /**
     * Verifie que la recette en cours ai assez de composants disponibles et accessibles
     *
     * @param item UsableItem
     */
    private void actionCheckComponents(UsableItem item, ItemFactoryModel factory) {
        if (factory.hasRunningReceipt()) {
            if (factory.getRunningReceipt().receiptInfo.inputs.stream().anyMatch(ReceiptInputInfo -> !hasEnoughConsumables(ReceiptInputInfo, item))) {
                if (factory.getRunningReceipt().receiptInfo.inputs.stream().anyMatch(ReceiptInputInfo -> !hasEnoughConsumables(ReceiptInputInfo, item))) {
                    factory.setMessage("not enough component");

                    actionClear(item, factory);
                }
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
            factory.setMessage("seek best receipt");

            factory.getReceipts().stream()
                    .filter(receipt -> receipt.receiptInfo.inputs.stream().allMatch(inputInfo -> hasEnoughConsumables(inputInfo, item)))
                    .findFirst()
                    .ifPresent(factory::setRunningReceipt);

            if (factory.hasRunningReceipt()) {
                factory.setMessage("{blue,icon;" + factory.getRunningReceipt() + "}: waiting components");
            }
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
                    if (!consumableModule.createHaulToFactoryJobs(item, receiptInputInfo.item, receiptInputInfo.quantity - currentQuantity)) {
                        // Annule la construction s'il n'y à plus suffisament de consomable disponible
                        actionClear(item, factory);
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
            factory.setMessage("{red,icon;" + factory.getRunningReceipt() + "}: crafting");
            BasicCraftJob.create(jobModule, item.getParcel(), factory.getRunningReceipt().receiptInfo, factory);
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
            item.getInventory().forEach(consumable -> consumableModule.removeConsumable(consumable));
            item.getInventory().clear();

            // Crée les objets de sorties
            factory.getRunningReceipt().receiptInfo.outputs.forEach(output -> consumableModule.addConsumable(output.item, output.quantity, item.getParcel()));

            factory.setMessage(factory.getRunningReceipt() + ": craft complete");

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
        jobModule.getJobs().stream()
                .filter(job -> job instanceof BasicHaulJob)
                .map(job -> (BasicHaulJob)job)
                .filter(job -> job.getItem() == item)
                .forEach(JobModel::close);

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
        availableQuantity += item.getInventory().stream()
                .filter(consumable -> consumable.getInfo().instanceOf(itemInfo))
                .mapToInt(ConsumableItem::getFreeQuantity)
                .sum();

//        // Check l'inventaire des personnages sur les hauling jobs de la fabrique
//        availableQuantity += jobModule.getJobs().stream()
//                .filter(job -> job instanceof BasicHaulJob)
//                .map(job -> (BasicHaulJob)job)
//                .filter(job -> job.getFactory() == item.getFactory())
//                .filter(job -> job.getCharacter() != null)
//                .mapToInt(job -> job.getCharacter().getInventoryQuantity(itemInfo))
//                .sum();

        // TODO: inutile avec le system de lock
        // Check la quantité réservée par les job
        availableQuantity += jobModule.getJobs().stream()
                .filter(job -> job instanceof BasicHaulJob)
                .map(job -> (BasicHaulJob)job)
                .filter(job -> job.getItem() == item)
                .mapToInt(BasicHaulJob::getHaulingQuantity)
                .sum();

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
