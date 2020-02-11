package br.com.nareba.nprotect.region;

import br.com.nareba.nprotect.NProtect;
import br.com.nareba.nprotect.region.command.RegionCommand;
import br.com.nareba.nprotect.region.core.Region;
import br.com.nareba.nprotect.region.core.RegionInfo;
import br.com.nareba.nprotect.region.core.RegionStatus;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

public class RegionManager {
    private final NProtect plugin;
    private final RegionCommand command;
    private final String regionFolder = "/regions";
    private Map<String, Region> regions;
    private Map<String, Region> regionSelect;
    public RegionManager(NProtect plugin)   {
        this.plugin = plugin;
        this.command = new RegionCommand(this);
        this.regions = new HashMap<String, Region>();
        this.regionSelect = new HashMap<String, Region>();
        initializeFiles();
        loadRegionFile();
    }
    private boolean hasRegion()   {
        if (this.regions.size() > 0)   {
            return true;
        }
        return false;
    }
    public NProtect getPlugin()   {
        return this.plugin;
    }
    public Optional<RegionStatus> check(Player player, Position position, String flagName, Collection<Region> regions)   {
        if (hasRegion())   {
            for (Region region : regions)   {
                if (region.hasSubRegion())   {
                    Optional<List<String>> optSubRegionListId = region.getSubRegionsId();
                    List<Region> subRegions = new ArrayList<Region>();
                    for (String subRegionId : optSubRegionListId.get())   {
                        if (this.regions.containsKey(subRegionId))   {
                            Region subRegion = this.regions.get(subRegionId);
                            subRegions.add(subRegion);
                        }
                    }
                    Optional<RegionStatus> optStatus = check(player, position, flagName, subRegions);
                    if (optStatus.isPresent())   {
                        if (optStatus.get() != RegionStatus.NONE)   {
                            return optStatus;
                        }
                    }
                }
                // Switch Flag Name
                switch (flagName)   {
                    case "break": case "place": case "use": case "move": case "chest":
                        Optional<RegionStatus> optStatus = region.isInside(position);
                        if (optStatus.isPresent())   {
                            if (optStatus.get() == RegionStatus.INSIDE_BLOCK)   {
                                Optional<Boolean> optFlag = region.getFlag(flagName);
                                Optional<RegionStatus> optOwner = region.isOwner(player);
                                Optional<RegionStatus> optMember = region.isMember(player);
                                if (optFlag.isPresent() && optOwner.isPresent() && optMember.isPresent())   {
                                    if (!optFlag.get() && optOwner.get() == RegionStatus.NONE && optMember.get() == RegionStatus.NONE && !player.isOp() && !player.hasPermission("nprotect.pass"))   {
                                        return Optional.of(RegionStatus.CANT);
                                    }
                                    return Optional.of(RegionStatus.CAN);
                                }
                            }
                        }
                        break;
                    case "pvp": case "waterflow": case "lavaflow": case "waterbucket": case "lavabucket":
                        Optional<RegionStatus> optPvpInside = region.isInside(position);
                        if (optPvpInside.isPresent())   {
                            if (optPvpInside.get() == RegionStatus.INSIDE_BLOCK)   {
                                Optional<Boolean> optFlag = region.getFlag(flagName);
                                if (optFlag.isPresent())   {
                                    if (!optFlag.get()  && !player.isOp() && !player.hasPermission("nprotect.pass"))   {
                                        return Optional.of(RegionStatus.CANT);
                                    }
                                    return Optional.of(RegionStatus.CAN);
                                }
                            }
                        }
                        break;
                    case "protection_block":
                        Optional<RegionStatus> optProtectionBlock = region.isInside(position);
                        if (optProtectionBlock.isPresent())   {
                            if (optProtectionBlock.get() == RegionStatus.PROTECTION_BLOCK)   {
                                Optional<RegionStatus> optIsOwner = region.isOwner(player);
                                if (optIsOwner.isPresent())   {
                                    if (optIsOwner.get() == RegionStatus.IS_OWNER || player.isOp() || player.hasPermission("nprotect.pass"))   {
                                        this.regionSelect.put(player.getUniqueId().toString(), region);
                                        return Optional.of(RegionStatus.CAN);
                                    }
                                    return Optional.of(RegionStatus.CANT);
                                }
                            }
                        }
                        break;
                }
            }
        }
        return Optional.of(RegionStatus.NONE);
    }
    public Optional<RegionStatus> createRegion(Player player, Item protectionItem, Block block)   {
        if (protectionItem.getCustomName().equals("Bloco de Proteção"))   {
            if (protectionItem.getLore().length == 2)   {
                String[] lore = protectionItem.getLore();
                String[] sizeLine = lore[1].split(":");
                if (sizeLine[0].equals("size") && lore[0].equals("cria uma regiao protegida"))   {
                    Integer protection_size = new Integer(sizeLine[1]);
                    if (hasRegion())   {
                        for (Region region : this.regions.values())   {
                            Optional<RegionStatus> optProtectionSource = region.isInside(block);
                            Optional<RegionStatus> optFirstPosInside = region.isInside(new Position((block.x + protection_size), (block.y + protection_size), (block.z + protection_size), block.level));
                            Optional<RegionStatus> optSecondPosInside = region.isInside(new Position((block.x - protection_size), (block.y - protection_size), (block.z - protection_size), block.level));
                            if (optFirstPosInside.isPresent() && optSecondPosInside.isPresent() && optProtectionSource.isPresent())   {
                                if (optFirstPosInside.get() == RegionStatus.NONE && optSecondPosInside.get() == RegionStatus.NONE && optProtectionSource.get() == RegionStatus.NONE)   {
                                    //CREATE REGION
                                    RegionInfo regionInfo = new RegionInfo(player, new Vector3(block.x, block.y, block.z), protection_size, block.level.getName());
                                    Region regionNew = new Region(regionInfo);
                                    this.regions.put(regionNew.getId(), regionNew);
                                    saveRegionFile(regionNew);
                                    player.sendMessage("Região protegida criada com sucesso!");
                                    return Optional.of(RegionStatus.REGION_CREATED);
                                }else if (optFirstPosInside.get() == RegionStatus.INSIDE_BLOCK && optSecondPosInside.get() == RegionStatus.INSIDE_BLOCK && optProtectionSource.get() == RegionStatus.INSIDE_BLOCK)   {
                                    //CREATE SUBREGION
                                    Optional<RegionStatus> optIsOwner = region.isOwner(player);
                                    if (optIsOwner.isPresent())   {
                                        if (optIsOwner.get() == RegionStatus.IS_OWNER)   {
                                            RegionInfo regionInfo = new RegionInfo(player, new Vector3(block.x, block.y, block.z), protection_size, block.level.getName(), region.getId());
                                            Region subRegionNew = new Region(regionInfo);
                                            region.addSubRegionId(subRegionNew);
                                            //subRegionNew.addOwner(player);
                                            this.regions.put(subRegionNew.getId(), subRegionNew);
                                            saveRegionFile(subRegionNew);
                                            player.sendMessage("Subregião protegida criada com sucesso!");
                                            return Optional.of(RegionStatus.SUBREGION_CREATED);
                                        }
                                    }
                                }
                                return Optional.of(RegionStatus.CANT);
                            }
                        }
                    }else   {
                        //CREATE REGION
                        RegionInfo regionInfo = new RegionInfo(player, new Vector3(block.x, block.y, block.z), protection_size, block.level.getName());
                        Region regionNew = new Region(regionInfo);
                        this.regions.put(regionNew.getId(), regionNew);
                        saveRegionFile(regionNew);
                        player.sendMessage("Região protegida criada com sucesso!");
                        return Optional.of(RegionStatus.REGION_CREATED);
                    }
                }
            }
        }
        return Optional.of(RegionStatus.NONE);
    }
    public Optional<RegionStatus> removeRegion(Player player)   {
        if (this.regionSelect.containsKey(player.getUniqueId().toString()))   {
            Region tmpRegion = this.regionSelect.get(player.getUniqueId().toString());
            if (!tmpRegion.hasSubRegion())   {
                if (tmpRegion.hasSuperRegion() && this.regions.containsKey(tmpRegion.getId()))   {
                    Region superRegion = this.regions.get(tmpRegion.getSuperRegionId());
                    superRegion.removeSubRegionId(tmpRegion);
                    plugin.getLogger().info("Subregião '" + tmpRegion.getId() + "' foi retirada da super região '" + superRegion.getId() + "'." );
                }
                removeRegionFile(tmpRegion);
                this.regions.remove(tmpRegion.getId());
                plugin.getLogger().info("Região '" + tmpRegion.getId() + "' foi deletada com sucesso!");
                return Optional.of(RegionStatus.REGION_REMOVED);
            }else   {
                return Optional.of(RegionStatus.HAS_SUBREGION);
            }
        }
        return Optional.of(RegionStatus.CANT);
    }
    public Optional<RegionStatus> addMember(Player player, String targetPlayerName)   {
        if (this.regionSelect.containsKey(player.getUniqueId().toString()))   {
            Region tmpRegion = this.regionSelect.get(player.getUniqueId().toString());
            Optional<RegionStatus> optIsOwner = tmpRegion.isOwner(player);
            if (optIsOwner.isPresent())   {
                if (optIsOwner.get() == RegionStatus.IS_OWNER)   {
                    Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
                    if (targetPlayer != null)   {
                        Optional<String> optPlayerId = tmpRegion.addMember(targetPlayer);
                        if (optPlayerId.isPresent())   {
                            if (optPlayerId.get().equals(targetPlayer.getUniqueId().toString()))   {
                                plugin.getLogger().info("Jogador '" + optPlayerId.get() +"' foi adicionado como membro da região '" + tmpRegion.getId() + "'.");
                                return Optional.of(RegionStatus.ADDED);
                            }
                        }else   {
                            return Optional.of(RegionStatus.IS_MEMBER);
                        }
                    }
                    return Optional.of(RegionStatus.NO_PLAYER_FOUND);
                }
            }
        }
        return Optional.of(RegionStatus.NO_REGION_SELECTED);
    }
    public Optional<RegionStatus> addOwner(Player player, String targetPlayerName)   {
        if (this.regionSelect.containsKey(player.getUniqueId().toString()))   {
            Region tmpRegion = this.regionSelect.get(player.getUniqueId().toString());
            Optional<RegionStatus> optIsOwner = tmpRegion.isOwner(player);
            if (optIsOwner.isPresent())   {
                if (optIsOwner.get() == RegionStatus.IS_OWNER)   {
                    Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
                    if (targetPlayer != null)   {
                        Optional<String> optPlayerId = tmpRegion.addOwner(targetPlayer);
                        if (optPlayerId.isPresent())   {
                            if (optPlayerId.get().equals(targetPlayer.getUniqueId().toString()))   {
                                plugin.getLogger().info("Jogador '" + optPlayerId.get() +"' foi adicionado como dono da região '" + tmpRegion.getId() + "'.");
                                return Optional.of(RegionStatus.ADDED);
                            }
                        }else   {
                            return Optional.of(RegionStatus.IS_OWNER);
                        }
                    }
                    return Optional.of(RegionStatus.NO_PLAYER_FOUND);
                }
            }
        }
        return Optional.of(RegionStatus.NO_REGION_SELECTED);
    }
    public Optional<RegionStatus> removeMember(Player player, String targetPlayerName)   {
        if (this.regionSelect.containsKey(player.getUniqueId().toString()))   {
            Region tmpRegion = this.regionSelect.get(player.getUniqueId().toString());
            Optional<RegionStatus> optIsOwner = tmpRegion.isOwner(player);
            if (optIsOwner.isPresent())   {
                if (optIsOwner.get() == RegionStatus.IS_OWNER)   {
                    Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
                    if (targetPlayer != null)   {
                        Optional<String> optPlayerId = tmpRegion.removeMember(targetPlayer);
                        if (optPlayerId.isPresent())   {
                            if (optPlayerId.get().equals(targetPlayer.getUniqueId().toString()))   {
                                plugin.getLogger().info("Jogador '" + optPlayerId.get() +"' não é mais membro da região '" + tmpRegion.getId() + "'.");
                                return Optional.of(RegionStatus.REMOVED);
                            }
                        }
                        return Optional.of(RegionStatus.NOT_MEMBER);
                    }
                    return Optional.of(RegionStatus.NO_PLAYER_FOUND);
                }
            }
        }
        return Optional.of(RegionStatus.NO_REGION_SELECTED);
    }
    public Optional<RegionStatus> removeOwner(Player player, String targetPlayerName)   {
        if (this.regionSelect.containsKey(player.getUniqueId().toString()))   {
            Region tmpRegion = this.regionSelect.get(player.getUniqueId().toString());
            Optional<RegionStatus> optIsOwner = tmpRegion.isOwner(player);
            if (optIsOwner.isPresent())   {
                if (optIsOwner.get() == RegionStatus.IS_OWNER)   {
                    Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
                    if (targetPlayer != null)   {
                        Optional<String> optPlayerId = tmpRegion.removeOwner(targetPlayer);
                        if (optPlayerId.isPresent())   {
                            if (optPlayerId.get().equals(targetPlayer.getUniqueId().toString()))   {
                                plugin.getLogger().info("Jogador '" + optPlayerId.get() +"' não é mais dono da região '" + tmpRegion.getId() + "'.");
                                return Optional.of(RegionStatus.REMOVED);
                            }
                        }
                        return Optional.of(RegionStatus.NOT_MEMBER);
                    }
                    return Optional.of(RegionStatus.NO_PLAYER_FOUND);
                }
            }
        }
        return Optional.of(RegionStatus.NO_REGION_SELECTED);
    }
    public Optional<RegionStatus> setFlag(Player player,String flagName, Boolean value)   {
        if (this.regionSelect.containsKey(player.getUniqueId().toString()))   {
            Region regionSelected = this.regionSelect.get(player.getUniqueId().toString());
            Optional<RegionStatus> optStatus = regionSelected.setFlag(flagName, value);
            saveRegionFile(regionSelected);
            return optStatus;
        }
        return Optional.of(RegionStatus.NO_REGION_SELECTED);
    }
    public RegionCommand getCommand()   {
        return this.command;
    }
    public Map<String, Region> getRegions()   {
        return this.regions;
    }
    private void initializeFiles()   {
        File regionsFolder = new File(plugin.getDataFolder() + this.regionFolder);
        if (!regionsFolder.exists())   {
            regionsFolder.mkdirs();
        }
    }
    private void saveRegionFile(Region region)   {
        try   {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String regionJson = gson.toJson(region.getRegionInfo());
            File regionFile = new File(this.plugin.getDataFolder() + this.regionFolder, region.getId() + ".json");
            Utils.writeFile(regionFile, regionJson);
            plugin.getLogger().info("Region '" + region.getId() + "' foi salva com sucesso!");
        }catch (Exception e)   {
            e.printStackTrace();
        }
    }
    private void loadRegionFile()   {
        try   {
            File regionFolder = new File(this.plugin.getDataFolder() + this.regionFolder);
            File[] regionFiles = regionFolder.listFiles();
            if (regionFiles.length > 0)   {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type regionType = new TypeToken<RegionInfo>(){}.getType();
                for (File regionFile : regionFiles)   {
                    String regionJson = Utils.readFile(regionFile);
                    RegionInfo tmpRegionInfo = gson.fromJson(regionJson, regionType);
                    Region tmpRegion = new Region(tmpRegionInfo);
                    this.regions.put(tmpRegion.getId(), tmpRegion);
                    this.plugin.getLogger().info("Region '" + tmpRegion.getId() + "' foi carregada com sucesso!");
                }
                this.plugin.getLogger().info("Todas as regiões foram carregadas com sucesso!");
            }else   {
                this.plugin.getLogger().info("Não existe regiões a serem carregadas!");
            }
        }catch (Exception e)   {
            e.printStackTrace();
        }
    }
    private void removeRegionFile(Region region)   {
        try   {
            File fileRegion = new File(plugin.getDataFolder() + this.regionFolder, region.getId() + ".json");
            if (fileRegion.exists())   {
                boolean status = fileRegion.delete();
                if (status)   {
                    plugin.getLogger().info("Arquivo da região '" + region.getId() + "' foi deletado com sucesso!");
                }
            }
        }catch (Exception e)   {
            e.printStackTrace();
        }
    }
}
