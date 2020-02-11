package br.com.nareba.nprotect.region.core;

import br.com.nareba.nprotect.region.flag.FlagBuilder;
import br.com.nareba.nprotect.region.flag.RegionFlag;
import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class RegionInfo {
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
    public RegionInfo(Player player, Vector3 protectionOrigin, int protection_size, String levelName)   {
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
    public RegionInfo(Player player, Vector3 protectionOrigin, int protection_size,String levelName, String superRegionId)   {
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
    public RegionId getId() {
        return this.id;
    }
    public void setId(RegionId id) {
        this.id = id;
    }
    public String getSuperRegionId() {
        return superRegionId;
    }
    public void setSuperRegionId(String superRegionId) {
        this.superRegionId = superRegionId;
    }
    public boolean isHasSuperRegion() {
        return hasSuperRegion;
    }
    public void setHasSuperRegion(boolean hasSuperRegion) {
        this.hasSuperRegion = hasSuperRegion;
    }
    public List<String> getSubRegionsId() {
        return subRegionsId;
    }
    public void setSubRegionsId(List<String> subRegionsId) {
        this.subRegionsId = subRegionsId;
    }
    public List<String> getOwnersId() {
        return ownersId;
    }
    public void setOwnersId(List<String> ownersId) {
        this.ownersId = ownersId;
    }
    public List<String> getMembersId() {
        return membersId;
    }
    public void setMembersId(List<String> membersId) {
        this.membersId = membersId;
    }
    public RegionFlag getRegionFlag() {
        return regionFlag;
    }
    public void setRegionFlag(RegionFlag regionFlag) {
        this.regionFlag = regionFlag;
    }
    public Vector3 getProtectionOrigin() {
        return protectionOrigin;
    }
    public void setProtectionOrigin(Vector3 protectionOrigin) {
        this.protectionOrigin = protectionOrigin;
    }
    public int getProtection_size() {
        return protection_size;
    }
    public void setProtection_size(int protection_size) {
        this.protection_size = protection_size;
    }
    public String getLevelName() {
        return levelName;
    }
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
}
