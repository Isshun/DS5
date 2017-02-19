package org.smallbox.faraway.test;

import org.junit.Assert;
import org.junit.Test;
import org.smallbox.faraway.core.Application;
import org.smallbox.farpoint.desktop.GdxTestApplication;

public class ApplicationReadyTest extends TestBase {

    @Test
    public void test1() throws InterruptedException {
        launchGame(new GdxTestApplication.GameTestCallback() {
            @Override
            public void onApplicationReady() {
                Assert.assertNotNull(Application.moduleManager);
                Assert.assertFalse(Application.data.items.isEmpty());
                quit();
            }

            @Override
            public void onGameUpdate(long tick) {
            }
        });
    }
}
