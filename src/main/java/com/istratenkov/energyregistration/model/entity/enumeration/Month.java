package com.istratenkov.energyregistration.model.entity.enumeration;

/**
 * Represents month enam stored as number of month in database.
 * Has a specified auto converter com.istratenkov.energyregistration.model.entity.enumeration.MonthConverter.
 */
public enum Month {
    JAN(1), FEB(2), MAR(3), APR(4), MAY(5), JUN(6), JUL(7), AUG(8), SEP(9), OCT(10), NOV(11), DEC(12);
    private Integer number;

    Month(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }
}
