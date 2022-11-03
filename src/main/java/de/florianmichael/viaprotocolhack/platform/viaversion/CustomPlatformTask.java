package de.florianmichael.viaprotocolhack.platform.viaversion;

import com.viaversion.viaversion.api.platform.PlatformTask;
import de.florianmichael.viaprotocolhack.util.Scheduler;

public class CustomPlatformTask implements PlatformTask<Scheduler.Task> {

    private final Scheduler.Task task;

    public CustomPlatformTask(final Scheduler.Task task) {
        this.task = task;
    }

    @Override
    public Scheduler.Task getObject() {
        return this.task;
    }

    @Override
    public void cancel() {
        this.task.cancel();
    }
}
