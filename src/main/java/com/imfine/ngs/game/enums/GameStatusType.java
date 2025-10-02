package com.imfine.ngs.game.enums;

public enum GameStatusType {
    ACTIVE("게임 활성화"),
    INACTIVE("게임 비활성화");

    private final String description;

    GameStatusType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}