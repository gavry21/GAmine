package sample.oldVer;

import java.lang.reflect.Array;

class Rectangles{
    double x;
    double y;
    double h;
    double w;
    double sum;
    double maxx;
    double maxy;
    double overlap;

    void printRectangle(){
        System.out.println("height " + h);
        System.out.println("width " + w);
        System.out.println("x " + x);
        System.out.println("y " + y);
    }

    public void setOverlap(double overlap) {
        this.overlap = overlap;
    }

    public void setMaxx(double maxx) {
        this.maxx = maxx;
    }

    public void setMaxy(double maxy) {
        this.maxy = maxy;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setH(double h) {
        this.h = h;
    }

    public void setW(double w) {
        this.w = w;
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getH() {
        return h;
    }

    public double getW() {
        return w;
    }

    public double getSum() {
        return sum;
    }

    public double getMaxx() {
        return maxx;
    }

    public double getMaxy() {
        return maxy;
    }

    public double getOverlap() {
        return overlap;
    }
}