package org.smallbox.faraway.test;

import org.junit.Test;
import org.smallbox.faraway.core.Application;

public class FactoryTest extends TestBase {

    @Test
    public void test1() throws InterruptedException {
        launchGame(new GameTestCallback() {
            @Override
            public void onApplicationReady() {
                Application.gameManager.loadLastGame();
            }

            @Override
            public void onGameUpdate(long tick) {
                System.out.println("Game update: " + tick);

//                quit();
            }
        });
    }
}
