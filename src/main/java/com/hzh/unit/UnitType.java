package com.hzh.unit;

import lombok.Getter;

@Getter
public enum UnitType {
    CAVE(1, "穴"),
    TRAP(2, "陷"),
    RIVER(3, "河"),
    CHESS(4, "棋");

    private final int value;
    private final String name;

    UnitType(int value, String name){
        this.value = value;
        this.name = name;
    }
}
