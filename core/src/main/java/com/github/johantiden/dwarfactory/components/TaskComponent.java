package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;

import java.util.function.Supplier;

public class TaskComponent implements Component {

    private final float totalTaskTime;
    private final Runnable finishCallback;
    private final Supplier<Boolean> canRun;

    private float timeSpent;

    public TaskComponent(float totalTaskTime, Runnable finishCallback, Supplier<Boolean> canRun) {
        this.totalTaskTime = totalTaskTime;
        this.finishCallback = finishCallback;
        this.canRun = canRun;
    }

    public void addTime(float deltaTime) {
        if (canRun()) {
            timeSpent += deltaTime;
        }
    }

    public Boolean canRun() {
        return canRun.get();
    }

    public boolean isComplete() {
        return canRun() && timeSpent > totalTaskTime;
    }

    public void finish() {
        finishCallback.run();
        if (!canRun()) {
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
