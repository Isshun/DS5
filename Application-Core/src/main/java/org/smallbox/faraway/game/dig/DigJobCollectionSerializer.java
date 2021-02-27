package org.smallbox.faraway.game.dig;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.save.GenericGameCollectionSerializer;
import org.smallbox.faraway.game.dig.factory.DigRockJobFactory;
import org.smallbox.faraway.game.job.JobModule;
import org.smallbox.faraway.game.world.WorldHelper;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@GameObject
public class DigJobCollectionSerializer extends GenericGameCollectionSerializer<DigJob> {
    private final static String CREATE_TABLE_CMD = "CREATE TABLE jobs_dig (id INTEGER, x INTEGER, y INTEGER, z INTEGER)";
    private final static String INSERT_CMD = "INSERT INTO jobs_dig (id, x, y, z) VALUES (?, ?, ?, ?)";
    private final static String SELECT_CMD = "SELECT id, x, y, z FROM jobs_dig";

    @Inject private DigRockJobFactory digRockJobFactory;
    @Inject private JobModule jobModule;

    public DigJobCollectionSerializer() {
        super(CREATE_TABLE_CMD, INSERT_CMD, SELECT_CMD);
    }

    @Override
    public Collection<DigJob> getEntries() {
        return jobModule.getJobs(DigJob.class).collect(Collectors.toList());
    }

    @Override
    public void onSaveEntry(SQLiteStatement statement, DigJob digJob) throws SQLiteException {
        statement.bind(1, digJob.getId());
        statement.bind(2, digJob.getTargetParcel().x);
        statement.bind(3, digJob.getTargetParcel().y);
        statement.bind(4, digJob.getTargetParcel().z);
        statement.step();
    }

    @Override
    public void onLoadEntry(SQLiteStatement statement) throws SQLiteException {
        Optional.ofNullable(WorldHelper.getParcel(statement.columnInt(1), statement.columnInt(2), statement.columnInt(3)))
                .ifPresent(parcel -> jobModule.add(digRockJobFactory.createJob(parcel)));
    }

}
