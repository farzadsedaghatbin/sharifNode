package com.isc.npsd.sharif.node.entities;

/**
 * Created by A_Jankeh on 2/26/2017.
 */
public enum FileStatus {

    RECEIVED(Direction.IN),
    REJECTED(Direction.IN),
    ACCEPTED(Direction.IN);
    Direction direction;

    FileStatus(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
