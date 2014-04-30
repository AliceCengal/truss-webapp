package edu.vanderbilt.truss.legacy;

class Joint {
    public double x;
    public double y;
    public int jrx;
    public int jry;
    public double wx;
    public double wy;
    public boolean load;
    public boolean restraint;

    public Joint() {
        super();
        this.x = 0.0;
        this.y = 0.0;
        this.jrx = 0;
        this.jry = 0;
        this.wx = 0.0;
        this.wy = 0.0;
        this.load = false;
        this.restraint = false;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + "," + this.jrx + "," + this.jry + "," + this.wx + "," + this.wy + ")";
    }
}
