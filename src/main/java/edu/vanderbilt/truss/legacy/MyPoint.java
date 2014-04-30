package edu.vanderbilt.truss.legacy;

class MyPoint {
    public int x;
    public int y;

    public MyPoint(final int x, final int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
}
