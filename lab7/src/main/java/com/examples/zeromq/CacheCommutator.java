package com.examples.zeromq;


class CacheCommutator {

    private String leftBound;
    private String rightBound;
    private long time;

    CacheCommutator (String left, String right, long time) {
        leftBound = left;
        rightBound = right;
        this.time = time;
    }

    String getLeftBound() {
        return leftBound;
    }

    String getRightBound() {
        return rightBound;
    }

    long getTime() {
        return time;
    }

    void setTime(long time) {
        this.time = time;
    }

    boolean isIntersect(String value) {
        int val = Integer.parseInt(value);
        int left = Integer.parseInt(leftBound);
        int right = Integer.parseInt(rightBound);
        return left <= val && val <= right ;
    }
}
