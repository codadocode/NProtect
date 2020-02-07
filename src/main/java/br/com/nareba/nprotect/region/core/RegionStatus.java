package br.com.nareba.nprotect.region.core;

public enum RegionStatus {
    NONE( 0),
    CANT(1),
    CAN(2),
    PROTECTION_BLOCK(3),
    INSIDE_BLOCK(4),
    IS_OWNER(5),
    IS_MEMBER(6),
    REGION_CREATED(7),
    SUBREGION_CREATED(8),
    SET_FLAG(9),
    NO_REGION_SELECTED(10);

    private int value;
    RegionStatus(int value)   {
        this.value = value;
    }
    public int getValue()   {
        return this.value;
    }
}
