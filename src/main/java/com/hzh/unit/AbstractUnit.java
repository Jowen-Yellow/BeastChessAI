package com.hzh.unit;

import com.hzh.game.Point;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractUnit implements Unit{
    private final Point point;

    public AbstractUnit(int x, int y){
        this.point=new Point(x,y);
    }

    public int getX(){
        return point.getX();
    }

    public int getY(){
        return point.getY();
    }
}
