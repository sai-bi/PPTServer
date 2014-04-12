package com.example.slides_controller.app;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private int operation;
    private ArrayList<Float> line_x;
    private ArrayList<Float> line_y;
    private byte[] image;
    private int screen_width;
    private int screen_height;

    public Message() {
        line_x = new ArrayList<Float>();
        line_y = new ArrayList<Float>();
        operation = 0;
        screen_height = 0;
        screen_width = 0;
    }

    public int getScreenWidth() {
        return screen_width;
    }

    public void setScreenWidth(int screen_width) {
        this.screen_width = screen_width;
    }

    public int getScreenHeight() {
        return screen_height;
    }

    public void setScreenHeight(int screen_height) {
        this.screen_height = screen_height;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public ArrayList<Float> getLine_x() {
        return line_x;
    }

    public void setLine_x(ArrayList<Float> line_x) {
        this.line_x = line_x;
    }

    public ArrayList<Float> getLine_y() {
        return line_y;

    }

    public void setLine_y(ArrayList<Float> line_y) {
        this.line_y = line_y;
    }

    public byte[] getImageByteArray() {
        return image;
    }

    public void setImageByteArray(byte[] image) {
        this.image = image.clone();
    }
}
