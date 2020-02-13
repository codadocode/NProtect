package br.com.nareba.nprotect.region.flag;

public abstract class FlagBuilder {
    private static final boolean canBreak = false;
    private static final boolean canPlace = false;
    private static final boolean canUse = false;
    private static final boolean canUseChest = false;
    private static final boolean canMove = true;
    private static final boolean canPvp = false;
    private static final boolean canWaterBucket = true;
    private static final boolean canLavaBucket = false;
    private static final boolean canWaterFlow = false;
    private static final boolean canLavaFlow = false;
    private static final boolean canPutFire = false;
    public static RegionFlag create()   {
        return new RegionFlag(canBreak, canPlace, canUse, canUseChest, canMove, canPvp, canWaterBucket,
                canLavaBucket, canWaterFlow, canLavaFlow, canPutFire);
    }
}
