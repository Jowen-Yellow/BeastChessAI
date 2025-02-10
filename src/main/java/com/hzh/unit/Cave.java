package com.hzh.unit;

import lombok.Getter;

@Getter
public class Cave extends AbstractUnit{
    private final boolean maximizer;
    public Cave(int x, int y, boolean maximizer) {
        super(x, y);
        this.maximizer=maximizer;
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.CAVE;
    }
}
