package com.example.slides_controller.app;

import java.io.Serializable;

/**
 * Created by saibi on 3/25/14.
 */

public class MyPoint implements Serializable{
    public double x;
    public double y;

    public MyPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public MyPoint(){
        this.x = 0;
        this.y = 0;
    }
}

