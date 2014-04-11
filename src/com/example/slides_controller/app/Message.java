package com.example.slides_controller.app;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private int operation;
    private ArrayList<Integer> line_x;
    private ArrayList<Integer> line_y;
    private byte[] image;

    public Message() {
        line_x = new ArrayList<Integer>();
        line_y = new ArrayList<Integer>();
        operation = 0;
    }

    public void addPoint(int x, int y) {
        line_x.add(x);
        line_y.add(y);
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public ArrayList<Integer> getLine_x() {
        return line_x;
    }

    public ArrayList<Integer> getLine_y() {
        return line_y;
    }

    public byte[] getImageByteArray() {
        return image;
    }

    public void setImageByteArray(byte[] image) {
        this.image = image.clone();
    }
}
