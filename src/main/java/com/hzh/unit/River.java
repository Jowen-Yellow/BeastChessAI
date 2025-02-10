package com.hzh.unit;

public class River extends AbstractUnit {
    public River(int x, int y) {
        super(x, y);
    }

    @Override
    public UnitType getUnitType() {
        return UnitType.RIVER;
    }
}
