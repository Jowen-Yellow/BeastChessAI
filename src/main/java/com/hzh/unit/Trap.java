package com.hzh.unit;

import lombok.Getter;

@Getter
public class Trap extends AbstractUnit{
    private final boolean maximizer;
    public Trap(int x, int y, boolean maximizer) {
        super(x, y);
        this.maximizer=maximizer;
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.TRAP;
    }
}
