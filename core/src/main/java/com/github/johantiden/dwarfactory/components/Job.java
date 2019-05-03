package com.github.johantiden.dwarfactory.components;

import com.badlogic.gdx.math.Vector2;

import java.util.Optional;

public interface Job {

    boolean canFinishJob();
    default void finish() {
    }

    Vector2 getWantedSpeed();

    default boolean isJobFailed() {
        return false;
    }
    default void fail() {
    }

    Optional<Vector2> getTargetPosition();
}
