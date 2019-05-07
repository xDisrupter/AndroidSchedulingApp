package com.group5.atoms;

import org.joda.time.Interval;

import java.util.Comparator;

public class IntervalComparator implements Comparator<Interval> {
    @Override
    public int compare(Interval x, Interval y) {
        return x.getStart().compareTo(y.getStart());
    }
}