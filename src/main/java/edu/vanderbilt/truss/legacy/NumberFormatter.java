package edu.vanderbilt.truss.legacy;

class NumberFormatter {
    int len;
    int dec;

    public NumberFormatter(final int len, final int dec) {
        super();
        this.len = len;
        this.dec = dec;
    }

    public String round(final double n) {
        String s = Double.toString(Math.floor(n * Math.pow(10.0, this.dec) + 0.5) / Math.pow(10.0, this.dec));
        for (
                int n2 = this.len - s.length(),
                        n3 = 0;
                n3 < n2
                        && (s.length() <= this.len - this.dec
                        || s.charAt(this.len - this.dec - 1) != '.');
                s = " " + s,
                        ++n3) {
        }

        while (s.length() < this.len) {
            s += " ";
        }
        return s;
    }

    public String round(final int i) {
        String str = Integer.toString(i);
        for (int n = this.len - str.length(), j = 0; j < n; ++j) {
            str = " " + str;
        }
        return str;
    }
}
