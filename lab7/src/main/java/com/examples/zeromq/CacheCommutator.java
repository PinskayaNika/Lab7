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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isIntersect(String value) {
        int val = Integer.parseInt(value);
        int left = Integer.parseInt(leftBound);
        int right = Integer.parseInt(rightBound);
        return left <= val && val <= right ;
    }
}
