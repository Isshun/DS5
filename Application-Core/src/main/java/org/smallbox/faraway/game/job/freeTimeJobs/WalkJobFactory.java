//package org.smallbox.faraway.game.job.freeTimeJobs;
//
//import org.smallbox.faraway.core.config.ApplicationConfig;
//import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
//import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
//import org.smallbox.faraway.core.path.PathManager;
//import org.smallbox.faraway.game.character.model.base.CharacterModel;
//import org.smallbox.faraway.game.consumable.ConsumableModule;
//import org.smallbox.faraway.game.job.JobFactory;
//import org.smallbox.faraway.game.job.JobModel;
//import org.smallbox.faraway.game.job.task.ActionDurationTask;
//import org.smallbox.faraway.game.job.task.TechnicalTask;
//import org.smallbox.faraway.game.world.WorldHelper;
//
//import java.util.concurrent.TimeUnit;
//import java.util.stream.IntStream;
//
//@GameObject
//public class WalkJobFactory implements JobFactory {
//    private final static int MAX_DISTANCE = 10;
//
//    @Inject private ApplicationConfig applicationConfig;
//    @Inject private ConsumableModule consumableModule;
//    @Inject private PathManager pathManager;
//
//    @Override
//    public JobModel createJob(CharacterModel character) {
//        JobModel job = new JobModel(character.getParcel());
//
//        job.setMainLabel("Walks");
//        job.setOptional(true);
//        job.setMoveSpeed(0.25f);
//        job.addAcceptedParcel(WorldHelper.getRandomParcel(character.getParcel(), MAX_DISTANCE));
//
//        IntStream.range(0, 10).forEach(value -> {
//            job.addTask(new ActionDurationTask("Walks", 5, TimeUnit.MINUTES, (c, hourInterval, localDateTime) -> {}));
//            job.addTask(new TechnicalTask(j -> {
//                job.getAcceptedParcels().clear();
//                job.addAcceptedParcel(WorldHelper.getRandomParcel(character.getParcel(), MAX_DISTANCE));
//            }));
//        });
//
//        return job;
//    }
//
//}
