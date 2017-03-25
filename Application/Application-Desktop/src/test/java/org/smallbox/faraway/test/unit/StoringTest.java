package org.smallbox.faraway.test.unit;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.modules.consumable.StorageArea;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.storing.BasicStoreJob;
import org.smallbox.faraway.test.TestHelper;
import org.smallbox.faraway.test.technique.GameTestHelper;
import org.smallbox.faraway.test.technique.HeadlessTestBase;

import java.util.*;

public class StoringTest extends HeadlessTestBase {

    @Test
    public void storingWithoutAreaTest() throws InterruptedException {

        GameTestHelper.create(this)
                .runOnGameCreate(() -> consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 5, 5, 1))
                .runOnGameTick(1, () -> Assert.assertEquals(0, jobModule.getJobs().size()))
                .runUntil(100);

    }

    @Test
    public void storingTest() throws InterruptedException {

        GameTestHelper.create(this)
                .runOnGameCreate(() -> {
                    consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 5, 5, 1);
                    areaModule.addArea(StorageArea.class, Collections.singletonList(WorldHelper.getParcel(8, 8, 1)));
                })
                .runOnGameTick(1, () -> {
                    Assert.assertEquals(1, jobModule.getJobs().size());

                    JobModel job = jobModule.getJobs().stream().findFirst().get();
                    Assert.assertEquals(BasicStoreJob.class, job.getClass());

                    Collection<ConsumableItem> consumables = ((BasicStoreJob)job).getConsumables().keySet();
                    Assert.assertEquals(1, consumables.size());

                    Assert.assertEquals("base.consumable.vegetable.rice", consumables.stream().findAny().get().getInfo().name);
                })
                .runUntil(100);

    }

    @Test
    public void storingTest2() throws InterruptedException {

        GameTestHelper.create(this)
                .runOnGameCreate(() -> {
                    areaModule.addArea(StorageArea.class, Collections.singletonList(WorldHelper.getParcel(8, 8, 1)));
                    consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 5, 5, 1);
                    consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 6, 5, 1);
                })
                .runOnGameTick(1, () -> {
                    Assert.assertEquals(1, jobModule.getJobs().size());

                    Collection<ConsumableItem> consumables = ((BasicStoreJob)jobModule.getJobs().stream().findFirst().get()).getConsumables().keySet();
                    Assert.assertEquals(2, consumables.size());
                })
                .runUntil(100);

    }

    /**
     * Mise à jour d'un StoringJob (via maj de la quantité d'un consomable)
     */
    @Test
    public void storingTest3() throws InterruptedException {

        GameTestHelper.create(this)
                .runOnGameCreate(() -> areaModule.addArea(StorageArea.class, Collections.singletonList(WorldHelper.getParcel(8, 8, 1))))

                // Ajoute un consomable (quantité = 10)
                .runOnGameTick(1, () -> consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 5, 5, 1))

                // Test le nombre de job (attendu = 1)
                .runOnGameTick(2, () -> Assert.assertEquals(1, jobModule.getJobs().size()))

                // Ajoute un consomable (quantité = 5)
                .runOnGameTick(3, () -> consumableModule.addConsumable("base.consumable.vegetable.rice", 5, 5, 5, 1))

                .runOnGameTick(4, () -> {
                    // Test le nombre de job (attendu = 1)
                    Assert.assertEquals(1, jobModule.getJobs().size());

                    // Test la quantité de consomable dans le job (attendu = 10 + 5 = 15)
                    BasicStoreJob job = TestHelper.getFirstJob(jobModule, BasicStoreJob.class);
                    Assert.assertEquals(1, job.getConsumables().size());
                    Assert.assertEquals(20, (int)job.getConsumables().entrySet().stream().findFirst().orElse(null).getValue());
                })
                .run();

    }

}
