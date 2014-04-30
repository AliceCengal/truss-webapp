package edu.vanderbilt.truss.legacy;

class Member {
    public int j1;
    public int j2;
    public double area;
    public double elasticity;
    public double cosx;
    public double cosy;
    public double length;
    public double sinx;

    public Member() {
        super();
        this.j1 = 0;
        this.j2 = 0;
        this.area = 0.0;
        this.elasticity = 0.0;
        this.cosx = 0.0;
        this.cosy = 0.0;
        this.sinx = 0.0;
        this.length = 0.0;
    }

    public String toString() {
        return "(" + this.j1 + "-" + this.j2 + " " + this.area + "," + this.elasticity + "," + this.length + ")";
    }
}
