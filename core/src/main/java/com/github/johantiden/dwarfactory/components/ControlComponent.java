package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.Objects;
import java.util.function.Supplier;

public class ControlComponent implements Component {
    public static final int RANGE_SQUARED = 400;

    private final Supplier<Job> jobSelector;

    private Job job;

    private final ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);

    public ControlComponent(Supplier<Job> jobSelector) {this.jobSelector = jobSelector;}

    public boolean isInRange(PositionComponent entityPosition) {
        Objects.requireNonNull(job, "job == null. Call hasJob first!");
        PositionComponent targetPosition = getTargetPosition();
        return entityPosition
                .cpy()
                .sub(targetPosition)
                .len2() < RANGE_SQUARED;
    }

    private PositionComponent getTargetPosition() {
        Entity target = job.target;
        return positionMapper.get(target);
    }

    public Entity getTarget() {
        Objects.requireNonNull(job, "job == null. Call hasJob first!");
        return job.target;
    }

    public boolean hasJob() {
        return job != null;
    }

    public void finishJob() {
        Objects.requireNonNull(job, "job == null. Call hasJob first!");
        job.finishCallback.run();
        job = null;
    }

    public void trySelectNewJob() {
        Job job = jobSelector.get();
        this.job = job;
    }

    public static class Job {
        private final Entity target;
        private final Runnable finishCallback;

        public Job(Entity target, Runnable finishCallback) {
            this.target = target;
            this.finishCallback = finishCallback;
        }
    }
}
