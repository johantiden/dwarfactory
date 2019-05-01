package com.github.johantiden.dwarfactory.components;

import com.badlogic.gdx.math.Vector2;

public interface Job {

    boolean canFinishJob();
    default void finish(){
    }

    Vector2 getWantedAcceleration(ForceContext forceContext);

    default boolean isJobFailed() {
        return false;
    }
    default void fail() {
    }
}
