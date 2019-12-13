package com.examples.zeromq;

public class CacheCommutator {

    private String leftBound;
    private String rightBound;
    private long time;

    public CacheCommutator (String left, String right, long time) {
        leftBound = left;
        rightBound = right;
        this.time = time;
    }

    public String getLeftBound() {
        return leftBound;
    }

    public String getRightBound() {
        return rightBound;
    }
}
