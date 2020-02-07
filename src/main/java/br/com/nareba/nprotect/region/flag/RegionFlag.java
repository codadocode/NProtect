package br.com.nareba.nprotect.region.flag;

public class RegionFlag {
    private boolean canBreak;
    private boolean canPlace;
    private boolean canInteract;
    private boolean canUseChest;
    private boolean canMove;
    private boolean canPvp;
    private boolean canWaterBucket;
    private boolean canLavaBucket;
    private boolean canWaterFlow;
    private boolean canLavaFlow;
    public RegionFlag(boolean canBreak, boolean canPlace, boolean canInteract, boolean canUseChest,
                      boolean canMove, boolean canPvp, boolean canWaterBucket, boolean canLavaBucket,
                      boolean canWaterFlow, boolean canLavaFlow)   {
        this.canBreak = canBreak;
        this.canPlace = canPlace;
        this.canInteract = canInteract;
        this.canUseChest = canUseChest;
        this.canMove = canMove;
        this.canPvp = canPvp;
        this.canWaterBucket = canWaterBucket;
        this.canLavaBucket = canLavaBucket;
        this.canWaterFlow = canWaterFlow;
        this.canLavaFlow = canLavaFlow;
    }

    public boolean isCanBreak() {
        return canBreak;
    }

    public void setCanBreak(boolean canBreak) {
        this.canBreak = canBreak;
    }

    public boolean isCanPlace() {
        return canPlace;
    }

    public void setCanPlace(boolean canPlace) {
        this.canPlace = canPlace;
    }

    public boolean isCanInteract() {
        return canInteract;
    }

    public void setCanInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }

    public boolean isCanUseChest() {
        return canUseChest;
    }

    public void setCanUseChest(boolean canUseChest) {
        this.canUseChest = canUseChest;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean isCanPvp() {
        return canPvp;
    }

    public void setCanPvp(boolean canPvp) {
        this.canPvp = canPvp;
    }

    public boolean isCanWaterBucket() {
        return canWaterBucket;
    }

    public void setCanWaterBucket(boolean canWaterBucket) {
        this.canWaterBucket = canWaterBucket;
    }

    public boolean isCanLavaBucket() {
        return canLavaBucket;
    }

    public void setCanLavaBucket(boolean canLavaBucket) {
        this.canLavaBucket = canLavaBucket;
    }

    public boolean isCanWaterFlow() {
        return canWaterFlow;
    }

    public void setCanWaterFlow(boolean canWaterFlow) {
        this.canWaterFlow = canWaterFlow;
    }

    public boolean isCanLavaFlow() {
        return canLavaFlow;
    }

    public void setCanLavaFlow(boolean canLavaFlow) {
        this.canLavaFlow = canLavaFlow;
    }
}
