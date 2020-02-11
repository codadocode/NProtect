package br.com.nareba.nprotect.region.core;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import java.util.*;

public class Region {
    private RegionInfo regionInfo;
    public Region(RegionInfo regionInfo)   {
        this.regionInfo = regionInfo;
    }
    public boolean hasSuperRegion()   {
        return this.regionInfo.isHasSuperRegion();
    }
    public boolean hasSubRegion()   {
        return (this.regionInfo.getSubRegionsId().size() > 0);
    }
    public String getId()   {
        return this.regionInfo.getId().getValue();
    }
    public RegionInfo getRegionInfo()   {
        return this.regionInfo;
    }
    public void setRegionInfo(RegionInfo regionInfo)   {
        this.regionInfo = regionInfo;
    }
    public Optional<String> addSubRegionId(Region region)   {
        if (!this.regionInfo.getSubRegionsId().contains(region.getId()))   {
            this.regionInfo.getSubRegionsId().add(region.getId());
            return Optional.of(region.getId());
        }
        return Optional.empty();
    }
    public Optional<String> addOwner(Player player)   {
        if (!this.regionInfo.getOwnersId().contains(player.getUniqueId().toString()))   {
            this.regionInfo.getOwnersId().add(player.getUniqueId().toString());
            return Optional.of(player.getUniqueId().toString());
        }
        return Optional.empty();
    }
    public Optional<String> addMember(Player player)   {
        if (!this.regionInfo.getMembersId().contains(player.getUniqueId().toString()))   {
            this.regionInfo.getMembersId().add(player.getUniqueId().toString());
            return Optional.of(player.getUniqueId().toString());
        }
        return Optional.empty();
    }
    public Optional<String> removeOwner(Player player)   {
        if (this.regionInfo.getOwnersId().contains(player.getUniqueId().toString()))   {
            this.regionInfo.getOwnersId().remove(player.getUniqueId().toString());
            return Optional.of(player.getUniqueId().toString());
        }
        return Optional.empty();
    }
    public Optional<String> removeMember(Player player)   {
        if (this.regionInfo.getMembersId().contains(player.getUniqueId().toString()))   {
            this.regionInfo.getMembersId().remove(player.getUniqueId().toString());
            return Optional.of(player.getUniqueId().toString());
        }
        return Optional.empty();
    }
    public Optional<String> removeSubRegionId(Region region)   {
        if (this.regionInfo.getSubRegionsId().contains(region.getId()))   {
            this.regionInfo.getSubRegionsId().remove(region.getId());
            return Optional.of(region.getId());
        }
        return Optional.empty();
    }
    public Optional<RegionStatus> isOwner(Player player)   {
        if (this.regionInfo.getOwnersId().contains(player.getUniqueId().toString()))   {
            return Optional.of(RegionStatus.IS_OWNER);
        }
        return Optional.of(RegionStatus.NONE);
    }
    public Optional<RegionStatus> isMember(Player player)   {
        if (this.regionInfo.getMembersId().contains(player.getUniqueId().toString()))   {
            return Optional.of(RegionStatus.IS_MEMBER);
        }
        return Optional.of(RegionStatus.NONE);
    }
    public Optional<RegionStatus> isInside(Position position)   {
        if (insideX(position.x) && insideY(position.y) && insideZ(position.z) && insideLevel(position.getLevel()))   {
            if (isProtectionBlock(position))   {
                return Optional.of(RegionStatus.PROTECTION_BLOCK);
            }
            return Optional.of(RegionStatus.INSIDE_BLOCK);
        }
        return Optional.of(RegionStatus.NONE);
    }
    public Optional<List<String>> getSubRegionsId()   {
        return Optional.of(this.regionInfo.getSubRegionsId());
    }
    public Optional<String> getSuperRegionId()   {
        if (hasSuperRegion())   {
            return Optional.of(this.regionInfo.getSuperRegionId());
        }
        return Optional.empty();
    }
    public Optional<List<String>> getMembersId()   {
        return Optional.of(this.regionInfo.getMembersId());
    }
    public Optional<List<String>> getOwnersId()   {
        return Optional.of(this.regionInfo.getOwnersId());
    }
    public Optional<Boolean> getFlag(String flagName)   {
        switch(flagName)   {
            case "break":
                return Optional.of(this.regionInfo.getRegionFlag().isCanBreak());
            case "place":
                return Optional.of(this.regionInfo.getRegionFlag().isCanPlace());
            case "use":
                return Optional.of(this.regionInfo.getRegionFlag().isCanUse());
            case "chest":
                return Optional.of(this.regionInfo.getRegionFlag().isCanUseChest());
            case "move":
                return Optional.of(this.regionInfo.getRegionFlag().isCanMove());
            case "pvp":
                return Optional.of(this.regionInfo.getRegionFlag().isCanPvp());
            case "waterbucket":
                return Optional.of(this.regionInfo.getRegionFlag().isCanWaterBucket());
            case "lavabucket":
                return Optional.of(this.regionInfo.getRegionFlag().isCanLavaBucket());
            case "waterflow":
                return Optional.of(this.regionInfo.getRegionFlag().isCanWaterFlow());
            case "lavaflow":
                return Optional.of(this.regionInfo.getRegionFlag().isCanLavaFlow());
        }
        return Optional.empty();
    }
    public Optional<RegionStatus> setFlag(String flagName, Boolean value)   {
        switch(flagName)   {
            case "break":
                this.regionInfo.getRegionFlag().setCanBreak(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "place":
                this.regionInfo.getRegionFlag().setCanPlace(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "use":
                this.regionInfo.getRegionFlag().setCanUse(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "chest":
                this.regionInfo.getRegionFlag().setCanUseChest(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "move":
                this.regionInfo.getRegionFlag().setCanMove(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "pvp":
                this.regionInfo.getRegionFlag().setCanPvp(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "waterbucket":
                this.regionInfo.getRegionFlag().setCanWaterBucket(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "lavabucket":
                this.regionInfo.getRegionFlag().setCanLavaBucket(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "waterflow":
                this.regionInfo.getRegionFlag().setCanWaterFlow(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "lavaflow":
                this.regionInfo.getRegionFlag().setCanLavaFlow(value);
                return Optional.of(RegionStatus.SET_FLAG);
        }
        return Optional.of(RegionStatus.NONE);
    }
    private boolean isProtectionBlock(Position position)   {
        Vector3 pos = new Vector3(position.x, position.y, position.z);
        if (this.regionInfo.getProtectionOrigin().equals(pos) && position.level.getName().equals(this.regionInfo.getLevelName()))   {
            return true;
        }
        return false;
    }
    private boolean insideX(Double x)   {
        if (x > (this.regionInfo.getProtectionOrigin().x - this.regionInfo.getProtection_size()) && x < (this.regionInfo.getProtectionOrigin().x + this.regionInfo.getProtection_size()))   {
            return true;
        }
        return false;
    }
    private boolean insideY(Double y)   {
        if (y > (this.regionInfo.getProtectionOrigin().y - this.regionInfo.getProtection_size()) && y < (this.regionInfo.getProtectionOrigin().y + this.regionInfo.getProtection_size()))   {
            return true;
        }
        return false;
    }
    private boolean insideZ(Double z)   {
        if (z > (this.regionInfo.getProtectionOrigin().z - this.regionInfo.getProtection_size()) && z < (this.regionInfo.getProtectionOrigin().z + this.regionInfo.getProtection_size()))   {
            return true;
        }
        return false;
    }
    private boolean insideLevel(Level level)   {
        if (this.regionInfo.getLevelName().equals(level.getName()))   {
            return true;
        }
        return false;
    }
}
