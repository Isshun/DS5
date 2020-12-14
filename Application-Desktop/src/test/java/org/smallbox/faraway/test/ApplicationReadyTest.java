//package org.smallbox.faraway.test;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.test.base.TestBase;
//
//public class ApplicationReadyTest extends TestBase {
//
//    @Test
//    public void test1() throws InterruptedException {
//        launchApplication(() -> {
//            Assert.assertNotNull(Application.moduleManager);
//            Assert.assertFalse(Application.data.items.isEmpty());
//            complete();
//        });
//    }
//}
