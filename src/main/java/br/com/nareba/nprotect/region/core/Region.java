package br.com.nareba.nprotect.region.core;

import br.com.nareba.nprotect.region.flag.FlagBuilder;
import br.com.nareba.nprotect.region.flag.RegionFlag;
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import java.util.*;

public class Region {
    private RegionId id;
    private String superRegionId;
    private boolean hasSuperRegion;
    private List<String> subRegionsId;
    private List<String> ownersId;
    private List<String> membersId;
    private RegionFlag regionFlag;
    private Vector3 protectionOrigin;
    private int protection_size;
    private String levelName;
    public Region(Player player, Vector3 protectionOrigin, int protection_size,String levelName)   {
        this.id = new RegionId();
        this.protectionOrigin = protectionOrigin;
        this.protection_size = protection_size;
        this.levelName = levelName;
        this.ownersId = new ArrayList<String>();
        this.subRegionsId = new ArrayList<String>();
        this.membersId = new ArrayList<String>();
        this.regionFlag = FlagBuilder.create();
        this.ownersId.add(player.getUniqueId().toString()); //Add default owner
    }
    public Region(Player player, Vector3 protectionOrigin, int protection_size,String levelName, String superRegionId)   {
        this.id = new RegionId();
        this.superRegionId = superRegionId;
        this.protectionOrigin = protectionOrigin;
        this.protection_size = protection_size;
        this.levelName = levelName;
        this.ownersId = new ArrayList<String>();
        this.subRegionsId = new ArrayList<String>();
        this.membersId = new ArrayList<String>();
        this.regionFlag = FlagBuilder.create();
        this.ownersId.add(player.getUniqueId().toString()); //Add default owner
    }
    private boolean hasSuperRegion()   {
        return this.hasSuperRegion;
    }
    public boolean hasSubRegion()   {
        return (this.subRegionsId.size() > 0);
    }
    public String getId()   {
        return this.id.getValue();
    }
    public Optional<String> addSubRegionId(Region region)   {
        if (!this.subRegionsId.contains(region.getId()))   {
            this.subRegionsId.add(region.getId());
            return Optional.of(region.getId());
        }
        return Optional.empty();
    }
    public Optional<String> addOwner(Player player)   {
        if (!this.ownersId.contains(player.getUniqueId().toString()))   {
            this.ownersId.add(player.getUniqueId().toString());
            return Optional.of(player.getUniqueId().toString());
        }
        return Optional.empty();
    }
    public Optional<String> addMember(Player player)   {
        if (!this.membersId.contains(player.getUniqueId().toString()))   {
            this.ownersId.add(player.getUniqueId().toString());
            return Optional.of(player.getUniqueId().toString());
        }
        return Optional.empty();
    }
    public Optional<String> removeOwner(Player player)   {
        if (this.ownersId.contains(player.getUniqueId().toString()))   {
            this.ownersId.remove(player.getUniqueId().toString());
            return Optional.of(player.getUniqueId().toString());
        }
        return Optional.empty();
    }
    public Optional<String> removeMember(Player player)   {
        if (this.membersId.contains(player.getUniqueId().toString()))   {
            this.membersId.add(player.getUniqueId().toString());
            return Optional.of(player.getUniqueId().toString());
        }
        return Optional.empty();
    }
    public Optional<String> removeSubRegionId(Region region)   {
        if (this.subRegionsId.contains(region.getId()))   {
            this.subRegionsId.remove(region.getId());
            return Optional.of(region.getId());
        }
        return Optional.empty();
    }
    public Optional<RegionStatus> isOwner(Player player)   {
        if (this.ownersId.contains(player.getUniqueId().toString()))   {
            return Optional.of(RegionStatus.IS_OWNER);
        }
        return Optional.of(RegionStatus.NONE);
    }
    public Optional<RegionStatus> isMember(Player player)   {
        if (this.membersId.contains(player.getUniqueId().toString()))   {
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
        return Optional.of(this.subRegionsId);
    }
    public Optional<List<String>> getMembersId()   {
        return Optional.of(this.membersId);
    }
    public Optional<List<String>> getOwnersId()   {
        return Optional.of(this.ownersId);
    }
    public Optional<Boolean> getFlag(String flagName)   {
        switch(flagName)   {
            case "break":
                return Optional.of(this.regionFlag.isCanBreak());
            case "place":
                return Optional.of(this.regionFlag.isCanPlace());
            case "use":
                return Optional.of(this.regionFlag.isCanUse());
            case "chest":
                return Optional.of(this.regionFlag.isCanUseChest());
            case "move":
                return Optional.of(this.regionFlag.isCanMove());
            case "pvp":
                return Optional.of(this.regionFlag.isCanPvp());
            case "waterbucket":
                return Optional.of(this.regionFlag.isCanWaterBucket());
            case "lavabucket":
                return Optional.of(this.regionFlag.isCanLavaBucket());
            case "waterflow":
                return Optional.of(this.regionFlag.isCanWaterFlow());
            case "lavaflow":
                return Optional.of(this.regionFlag.isCanLavaFlow());
        }
        return Optional.empty();
    }
    public Optional<RegionStatus> setFlag(String flagName, Boolean value)   {
        switch(flagName)   {
            case "break":
                this.regionFlag.setCanBreak(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "place":
                this.regionFlag.setCanPlace(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "use":
                this.regionFlag.setCanUse(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "chest":
                this.regionFlag.setCanUseChest(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "move":
                this.regionFlag.setCanMove(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "pvp":
                this.regionFlag.setCanPvp(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "waterbucket":
                this.regionFlag.setCanWaterBucket(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "lavabucket":
                this.regionFlag.setCanLavaBucket(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "waterflow":
                this.regionFlag.setCanWaterFlow(value);
                return Optional.of(RegionStatus.SET_FLAG);
            case "lavaflow":
                this.regionFlag.setCanLavaFlow(value);
                return Optional.of(RegionStatus.SET_FLAG);
        }
        return Optional.of(RegionStatus.NONE);
    }
    private boolean isProtectionBlock(Position position)   {
        Vector3 pos = new Vector3(position.x, position.y, position.z);
        if (this.protectionOrigin.equals(pos) && position.level.getName().equals(this.levelName))   {
            return true;
        }
        return false;
    }
    private boolean insideX(Double x)   {
        if (x > (protectionOrigin.x - protection_size) && x < (protectionOrigin.x + protection_size))   {
            return true;
        }
        return false;
    }
    private boolean insideY(Double y)   {
        if (y > (protectionOrigin.y - protection_size) && y < (protectionOrigin.y + protection_size))   {
            return true;
        }
        return false;
    }
    private boolean insideZ(Double z)   {
        if (z > (protectionOrigin.z - protection_size) && z < (protectionOrigin.z + protection_size))   {
            return true;
        }
        return false;
    }
    private boolean insideLevel(Level level)   {
        if (this.levelName.equals(level.getName()))   {
            return true;
        }
        return false;
    }
}
