package com.imfine.ngs.game.enums;

public enum GameStatusType {
    ACTIVE("게임 활성화", 1),
    INACTIVE("게임 비활성화", 2);

    private final String description;
    private final int number;

    GameStatusType(String description, int number) {
        this.description = description;
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public int getNumber() {
        return number;
    }
}
