package br.com.nareba.nprotect.region;

import br.com.nareba.nprotect.NProtect;
import br.com.nareba.nprotect.region.command.RegionCommand;
import br.com.nareba.nprotect.region.core.Region;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public Optional<RegionStatus> can(Player player, Position position, String flagName)   {
        if (hasRegion())   {
            for (Region region : this.regions.values())   {
                if (region.hasSubRegion())   {
                    Optional<List<String>> subRegionListId = region.getSubRegionsId();
                    for (String regionId : subRegionListId.get())   {
                        Optional<RegionStatus> status = can(player, position, flagName, subRegionListId.get());
                        if (status.isPresent())   {
                            if (status.get() != RegionStatus.NONE)   {
                                return status;
                            }
                        }
                    }
                }
                Optional<RegionStatus> optBool = region.isInside(position);
                if (optBool.isPresent())   {
                    if (optBool.get() == RegionStatus.INSIDE_BLOCK)   {
                        Optional<Boolean> optFlag = region.getFlag(flagName);
                        Optional<RegionStatus> optIsOwner = region.isOwner(player);
                        Optional<RegionStatus> optIsMember = region.isMember(player);
                        if (optFlag.isPresent() && optIsOwner.isPresent() && optIsMember.isPresent())   {
                            if (!optFlag.get() && optIsOwner.get() == RegionStatus.NONE && optIsMember.get() == RegionStatus.NONE && !player.hasPermission("nprotect.pass"))   {
                                return Optional.of(RegionStatus.CANT);
                            }else   {
                                return Optional.of(RegionStatus.CAN);
                            }
                        }
                    }else if (optBool.get() == RegionStatus.PROTECTION_BLOCK)   {
                        Optional<RegionStatus> optIsOwner = region.isOwner(player);
                        if (optIsOwner.isPresent())   {
                            if (optIsOwner.get() == RegionStatus.IS_OWNER)   {
                                this.regionSelect.put(player.getUniqueId().toString(), region);
                                player.sendMessage("Você selecionou um bloco de proteção!");
                            }else   {
                                return Optional.of(RegionStatus.CANT);
                            }
                        }
                    }
                }
            }
        }
        return Optional.of(RegionStatus.CAN);
    }
    private Optional<RegionStatus> can(Player player, Position position, String flagName, List<String> regionsId)   {
        if (hasRegion())   {
            for (String subRegionId : regionsId)   {
                Region region = this.regions.get(subRegionId);
                if (region.hasSubRegion())   {
                    Optional<List<String>> subRegionListId = region.getSubRegionsId();
                    for (String regionId : subRegionListId.get())   {
                        Optional<RegionStatus> status = can(player, position, flagName, subRegionListId.get());
                        if (status.isPresent())   {
                            if (status.get() != RegionStatus.NONE)   {
                                return status;
                            }
                        }
                    }
                }
                Optional<RegionStatus> optBool = region.isInside(position);
                if (optBool.isPresent())   {
                    if (optBool.get() == RegionStatus.INSIDE_BLOCK)   {
                        Optional<Boolean> optFlag = region.getFlag(flagName);
                        Optional<RegionStatus> optIsOwner = region.isOwner(player);
                        Optional<RegionStatus> optIsMember = region.isMember(player);
                        if (optFlag.isPresent() && optIsOwner.isPresent() && optIsMember.isPresent())   {
                            if (!optFlag.get() && optIsOwner.get() == RegionStatus.NONE && optIsMember.get() == RegionStatus.NONE && !player.hasPermission("nprotect.pass"))   {
                                return Optional.of(RegionStatus.CANT);
                            }
                            return Optional.of(RegionStatus.CAN);
                        }
                    }else if (optBool.get() == RegionStatus.PROTECTION_BLOCK)   {
                        Optional<RegionStatus> optIsOwner = region.isOwner(player);
                        if (optIsOwner.isPresent())   {
                            if (optIsOwner.get() == RegionStatus.IS_OWNER)   {
                                this.regionSelect.put(player.getUniqueId().toString(), region);
                                player.sendMessage("Você selecionou um bloco de proteção!");
                            }else   {
                                return Optional.of(RegionStatus.CANT);
                            }
                        }
                    }
                }
            }
        }
        return Optional.of(RegionStatus.NONE);
    }
    public Optional<RegionStatus> createRegion(Player player, Item protectionItem, Block block)   {
        if (protectionItem.hasCustomBlockData())   {
            if (protectionItem.getCustomBlockData().contains("protection_size") && protectionItem.getCustomName().equals("Bloco de Proteção"))   {
                int protection_size = protectionItem.getCustomBlockData().getInt("protection_size");
                if (hasRegion())   {
                    for (Region region : this.regions.values())   {
                        Optional<RegionStatus> optFirstPosInside = region.isInside(new Position((block.x + protection_size), (block.y + protection_size), (block.z + protection_size), block.level));
                        Optional<RegionStatus> optSecondPosInside = region.isInside(new Position((block.x - protection_size), (block.y - protection_size), (block.z - protection_size), block.level));
                        if (optFirstPosInside.isPresent() && optSecondPosInside.isPresent())   {
                            if (optFirstPosInside.get() == RegionStatus.NONE && optSecondPosInside.get() == RegionStatus.NONE)   {
                                //CREATE REGION
                                Region regionNew = new Region(player, new Vector3(block.x, block.y, block.z), protection_size, block.level.getName());
                                this.regions.put(regionNew.getId(), regionNew);
                                saveRegionFile(regionNew);
                                player.sendMessage("Região protegida criada com sucesso!");
                                return Optional.of(RegionStatus.REGION_CREATED);
                            }else if (optFirstPosInside.get() == RegionStatus.INSIDE_BLOCK && optSecondPosInside.get() == RegionStatus.INSIDE_BLOCK)   {
                                //CREATE SUBREGION
                                Optional<RegionStatus> optIsOwner = region.isOwner(player);
                                if (optIsOwner.isPresent())   {
                                    if (optIsOwner.get() == RegionStatus.IS_OWNER)   {
                                        Region subRegionNew = new Region(player, new Vector3(block.x, block.y, block.z), protection_size, block.level.getName(), region.getId());
                                        region.addSubRegionId(subRegionNew);
                                        subRegionNew.addOwner(player);
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
                    Region regionNew = new Region(player, new Vector3(block.x, block.y, block.z), protection_size, block.level.getName());
                    this.regions.put(regionNew.getId(), regionNew);
                    saveRegionFile(regionNew);
                    player.sendMessage("Região protegida criada com sucesso!");
                    return Optional.of(RegionStatus.REGION_CREATED);
                }
            }
        }
        return Optional.of(RegionStatus.NONE);
    }
    public RegionCommand getCommand()   {
        return this.command;
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
            String regionJson = gson.toJson(region);
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
                Type regionType = new TypeToken<Region>(){}.getType();
                for (File regionFile : regionFiles)   {
                    String regionJson = Utils.readFile(regionFile);
                    Region tmpRegion = gson.fromJson(regionJson, regionType);
                    this.regions.put(tmpRegion.getId(), tmpRegion);
                    this.plugin.getLogger().info("Region '" + tmpRegion.getId() + "' foi carregada com sucesso!");
                }
                this.plugin.getLogger().info("Todas as regiões foram carregadas com sucesso!");
            }
            this.plugin.getLogger().info("Não existe regiões a serem carregadas!");
        }catch (Exception e)   {
            e.printStackTrace();
        }
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
}
