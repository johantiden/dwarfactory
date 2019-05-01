package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TaskComponent implements Component {

    private final float totalTaskTime;
    private final Consumer<TaskContext> finishCallback;
    private final Predicate<TaskContext> canRun;

    private float timeSpent;

    public TaskComponent(float totalTaskTime, Consumer<TaskContext> finishCallback, Predicate<TaskContext> canRun) {
        this.totalTaskTime = totalTaskTime;
        this.finishCallback = finishCallback;
        this.canRun = canRun;
    }

    public void addTime(TaskContext taskContext, float deltaTime) {
        if (canRun(taskContext)) {
            timeSpent += deltaTime;
        }
    }

    public Boolean canRun(TaskContext taskContext) {
        return canRun.test(taskContext);
    }

    public boolean isComplete(TaskContext taskContext) {
        return canRun(taskContext) && timeSpent > totalTaskTime;
    }

    public void finish(TaskContext taskContext) {
        finishCallback.accept(taskContext);
        if (!canRun(taskContext)) {
            // We can't continue immediately, discard the remaining time spent.
            timeSpent = 0;
        } else {
            // We can continue, we should let the time spill over to the next task.
            timeSpent -= totalTaskTime;
        }
    }

    public float getProgressRatio() {
        return timeSpent / totalTaskTime;
    }
}
