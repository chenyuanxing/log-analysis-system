package com.cad.web.domain;


/**
 * 包括panel，和panel的位置以及大小
 */
public class Chart{
    private Panel panel;
    private int x_axis;
    private int y_axis;
    private int width;
    private int height;

    public Panel getPanel() {
        return panel;
    }

    public void setPanel(Panel panel) {
        this.panel = panel;
    }

    public int getX_axis() {
        return x_axis;
    }

    public void setX_axis(int x_axis) {
        this.x_axis = x_axis;
    }

    public int getY_axis() {
        return y_axis;
    }

    public void setY_axis(int y_axis) {
        this.y_axis = y_axis;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    @Override
    public String toString() {
        return "ChartParam{" +
                "panel=" + panel +
                ", x_axis=" + x_axis +
                ", y_axis=" + y_axis +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
