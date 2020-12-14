package org.smallbox.faraway.test.unit;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.test.technique.GameTestHelper;
import org.smallbox.faraway.test.technique.HeadlessTestBase;

public class CharacterModuleTest extends HeadlessTestBase {

    @Test
    public void addRandomCharacter() throws InterruptedException {

        GameTestHelper.create(this)
                .runOnGameCreate(() -> characterModule.addRandom())
                .runUntil(10);

        Assert.assertEquals(10, Application.gameManager.getGame().getTick());
        Assert.assertEquals(1, characterModule.getCharacters().size());
    }

}
