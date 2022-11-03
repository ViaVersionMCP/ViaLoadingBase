package de.florianmichael.viaprotocolhack.util;
import java.util.ArrayList;

public class Scheduler extends Thread {

	private static int taskId;
	public final ArrayList<Task> tasks = new ArrayList<>();

	@Override
	public void run() {
		super.run();

		while (true) {
			tasks.forEach(Task::tick);
		}
	}

	public Task runTask(Runnable runnable) {
		return runTaskLater(runnable, 0);
	}

	public Task runTaskLater(Runnable runnable, long delay) {
		return runTaskTimerLater(runnable, -1, delay);
	}

	public Task runTaskTimer(Runnable runnable, long period) {
		return runTaskTimerLater(runnable, period, 0);
	}

	public Task runTaskTimerLater(Runnable runnable, long period, long delay) {
		final Task task = new Task(this, runnable, delay, period);
		tasks.add(task);

		return task;
	}

	public int getTaskId() {
		return taskId++;
	}

	public void cancel(int taskId) {
		tasks.removeIf(t -> t.taskId == taskId);
	}

	public static class Task {

		private final Scheduler scheduler;
		public Runnable runnable;

		public int taskId;
		public long delay;
		public long tickCount = 0;
		public long period;
		public long periodTickCount;

		public Task(final Scheduler scheduler, Runnable runnable) {
			this(scheduler, runnable, 0, -1);
		}

		public Task(final Scheduler scheduler, Runnable runnable, long delay) {
			this(scheduler, runnable, delay, -1);
		}

		public Task(final Scheduler scheduler, Runnable runnable, long delay, long period) {
			this.scheduler = scheduler;
			this.runnable = runnable;

			this.delay = delay < 0 ? 0 : delay;
			this.period = period < 1 ? -1 : period;
			this.periodTickCount = this.period;
			this.taskId = scheduler.getTaskId();
		}

		public void tick() {
			if (tickCount++ < delay) return;
			if (period == -1) {
				this.runnable.run();
				this.cancel();
			} else if (++periodTickCount >= period) {
				periodTickCount = 0;
				this.runnable.run();
			}
		}

		public void cancel() {
			scheduler.cancel(taskId);
		}
	}
}
