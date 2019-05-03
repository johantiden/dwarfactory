package com.github.johantiden.dwarfactory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.github.johantiden.dwarfactory.game.entities.SelectJobContext;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;

public class ControlComponent implements Component {

    private final Function<SelectJobContext, Collection<Job>> jobSelector;

    private final Queue<Job> jobQueue = new LinkedList<>();

    public ControlComponent(Function<SelectJobContext, Collection<Job>> jobSelector) {this.jobSelector = jobSelector;}


    public boolean hasJob() {
        return !jobQueue.isEmpty();
    }

    public void finishJob() {
        assert !jobQueue.isEmpty();
        Job job = jobQueue.remove();
        job.finish();
    }

    public void trySelectNewJob(SelectJobContext selectJobContext) {
        Collection<Job> job = jobSelector.apply(selectJobContext);
        this.jobQueue.addAll(job);
    }

    public boolean canFinishJob() {
        assert !jobQueue.isEmpty();
        return jobQueue.peek().canFinishJob();
    }

    public boolean isJobFailed() {
        assert !jobQueue.isEmpty();
        return jobQueue.stream()
                .anyMatch(Job::isJobFailed);
    }

    public void fail() {
        jobQueue.forEach(Job::fail);
        jobQueue.clear();
    }

    private Vector2 getWantedSpeed() {
        assert !jobQueue.isEmpty();
        return jobQueue.peek().getWantedSpeed();
    }

    public Vector2 asSpeed() {
        if (!jobQueue.isEmpty()) {
            return getWantedSpeed();
        } else {
            return new Vector2(0, 0);
        }
    }

    public Iterable<Job> getJobQueue() {
        return jobQueue;
    }
}
