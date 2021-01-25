package org.smallbox.faraway.game.dig;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.save.GenericGameCollectionSerializer;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.game.job.JobModule;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@GameObject
public class DigJobCollectionSerializer extends GenericGameCollectionSerializer<DigJob> {
    @Inject private DigJobFactory digJobFactory;
    @Inject private JobModule jobModule;

    @Override
    public Collection<DigJob> getEntries() {
        return jobModule.getJobs(DigJob.class).collect(Collectors.toList());
    }

    @Override
    public void onCreateTable(SQLiteConnection db) throws SQLiteException {
        db.exec("CREATE TABLE jobs_dig (id INTEGER, x INTEGER, y INTEGER, z INTEGER)");
    }

    @Override
    public void onSaveEntry(SQLiteConnection db, DigJob digJob) throws SQLiteException {
        SQLiteStatement insertStatement = db.prepare("INSERT INTO jobs_dig (id, x, y, z) VALUES (?, ?, ?, ?)");
        try {
            insertStatement.bind(1, digJob.getId());
            insertStatement.bind(2, digJob.getTargetParcel().x);
            insertStatement.bind(3, digJob.getTargetParcel().y);
            insertStatement.bind(4, digJob.getTargetParcel().z);
            insertStatement.step();
        } finally {
            insertStatement.dispose();
        }
    }

    @Override
    public void onLoadEntry(SQLiteConnection db) throws SQLiteException {
        SQLiteStatement selectStatement = db.prepare("SELECT id, x, y, z FROM jobs_dig");
        try {
            while (selectStatement.step()) {
                Optional.ofNullable(WorldHelper.getParcel(selectStatement.columnInt(1), selectStatement.columnInt(2), selectStatement.columnInt(3)))
                        .ifPresent(parcel -> jobModule.add(digJobFactory.createJob(parcel, DigType.ROCK)));
            }
        } finally {
            selectStatement.dispose();
        }
    }

}
