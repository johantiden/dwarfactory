package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.game.entities.SelectJobContext;
import com.github.johantiden.dwarfactory.systems.physics.Force;

import java.util.Objects;
import java.util.function.Function;

public class ControlComponent implements Component {

    private final Function<SelectJobContext, Job> jobSelector;

    private Job job;

    public ControlComponent(Function<SelectJobContext, Job> jobSelector) {this.jobSelector = jobSelector;}


    public boolean hasJob() {
        return job != null;
    }

    public void finishJob() {
        Objects.requireNonNull(job, "job == null. Call hasJob first!");
        job.finish();
        job = null;
    }

    public void trySelectNewJob(SelectJobContext selectJobContext) {
        Job job = jobSelector.apply(selectJobContext);
        this.job = job;
    }

    public boolean canFinishJob() {
        Objects.requireNonNull(job, "job == null. Call hasJob first!");
        return job.canFinishJob();
    }

    public boolean isJobFailed() {
        Objects.requireNonNull(job, "job == null. Call hasJob first!");
        return job.isJobFailed();
    }


    public void fail() {
        Objects.requireNonNull(job, "job == null. Call hasJob first!");
        job.fail();
        job = null;
    }

    public Vector2 getWantedForce(ForceContext forceContext) {
        Objects.requireNonNull(job, "job == null. Call hasJob first!");
        return job.getWantedAcceleration(forceContext);
    }

    public Force asForce() {
        return forceContext -> {
            if (job != null) {
                return getWantedForce(forceContext);
            } else {
                return new Vector2(0, 0);
            }
        };
    }
}
