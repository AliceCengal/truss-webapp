package edu.vanderbilt.truss.legacy;

class MyCoord {
    public double x;
    public double y;

    public MyCoord(final double x, final double y) {
        super();
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
}
