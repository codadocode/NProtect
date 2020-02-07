package br.com.nareba.nprotect.region.core;

import cn.nukkit.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerRegion {
    private String playerId;
    private Map<String, String> regionOwnerId;
    private Map<String, String> regionMemberId;
    public PlayerRegion(Player player)   {
        this.playerId = player.getUniqueId().toString();
        this.regionOwnerId = new HashMap<String, String>();
        this.regionMemberId = new HashMap<String, String>();
    }
    public boolean isRegionOwner(Region region)   {
        if (hasMemberRegion())   {
            if (this.regionOwnerId.containsKey(region.getId()))   {
                return true;
            }
        }
        return false;
    }
    public boolean isRegionMember(Region region)   {
        if (hasMemberRegion())   {
            if (this.regionMemberId.containsKey(region.getId()))   {
                return true;
            }
        }
        return false;
    }
    public boolean hasOwnerRegion()   {
        if (this.regionOwnerId.size() > 0)   {
            return true;
        }
        return false;
    }
    public boolean hasMemberRegion()   {
        if (this.regionMemberId.size() > 0)   {
            return true;
        }
        return false;
    }
    public String getPlayerId()   {
        return this.playerId;
    }
}
