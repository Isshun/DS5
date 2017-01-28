package org.smallbox.faraway.core.task;

import java.util.concurrent.*;

/**
 * Created by Alex on 19/11/2016.
 */
public class LoaderThreadExecutor extends ThreadPoolExecutor {

    public LoaderThreadExecutor() {
        super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

//    @Override
//    public Future<?> submit(LoadTask task) {
//        if (task == null) throw new NullPointerException();
//        RunnableFuture<Void> ftask = newTaskFor(task, null);
//        execute(ftask);
//        return ftask;
//    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null) {
            System.out.println(t);
        }
    }
}
