package br.com.nareba.nprotect.region.event;

import br.com.nareba.nprotect.region.RegionManager;
import br.com.nareba.nprotect.region.core.RegionStatus;
import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

import java.util.Optional;

public class RegionEvent implements Listener {
    private final RegionManager regionManager;
    public RegionEvent(RegionManager regionManager)   {
        this.regionManager = regionManager;
    }
    @EventHandler //Region Break
    public void onBlockBreak(BlockBreakEvent event)   {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Optional<RegionStatus> optBool = regionManager.check(player, block, "break", regionManager.getRegions().values());
        if (optBool.isPresent())   {
            if (optBool.get() == RegionStatus.CANT)   {
                player.sendMessage("Você não pode quebrar aqui!");
                event.setCancelled();
            }else   {
                Optional<RegionStatus> optProtectionBlock = regionManager.check(player, block, "protection_block", regionManager.getRegions().values());
                if (optProtectionBlock.isPresent())   {
                    if (optProtectionBlock.get() == RegionStatus.CAN || optProtectionBlock.get() == RegionStatus.CANT)   {
                        if (optProtectionBlock.get() == RegionStatus.CAN)   {
                            player.sendMessage("Você precisa deletar essa região para quebrar esse bloco! selecione essa região e use /npremove para isso.");
                        }
                        event.setCancelled();
                    }
                }
            }
        }
    }
    @EventHandler //Region Place
    public void onBlockPlace(BlockPlaceEvent event)   {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Item item = event.getItem();
        Block blockPlaced = event.getBlockAgainst();
        Optional<RegionStatus> optBool = regionManager.check(player, block, "place", regionManager.getRegions().values());
        if (optBool.isPresent())   {
            if (optBool.get() == RegionStatus.CANT)   {
                player.sendMessage("Você não pode colocar blocos aqui!");
                event.setCancelled();
            }
        }
        if (item.getCustomName().equals("Bloco de Proteção"))   {
            Optional<RegionStatus> optStatus = regionManager.createRegion(player, item, block);
            if (optStatus.isPresent())   {
                if (optStatus.get() == RegionStatus.CANT)   {
                    player.sendMessage("Você não pode criar uma região aqui!");
                    event.setCancelled();
                }
            }
        }
    }
    @EventHandler //Region Interact
    public void onPlayerInteract(PlayerInteractEvent event)   {
        Player player = event.getPlayer();
        Vector3 beforePos = new Vector3(player.x, player.y, player.z);
        Block block = event.getBlock();
        if (event.getPlayer().getInventory().getItemInHand().getId() != Item.GOLD_AXE)   {
            if (block.canBeActivated() && block.getId() != Block.DIRT && block.getId() != Block.GRASS)   {
                Optional<RegionStatus> optBool = regionManager.check(player, block, "use", regionManager.getRegions().values());
                if (optBool.isPresent())   {
                    if (optBool.get() == RegionStatus.CANT)   {
                        switch(block.getId())   {
                            case Block.DOOR_BLOCK:
                                BlockDoor door = (BlockDoor) block;
                                door.toggle(player);
                                door.toggle(player);
                                if (!door.isOpen())   {
                                    player.teleport(beforePos);
                                }
                                break;
                            case Block.TRAPDOOR:
                                BlockTrapdoor trapDoor = (BlockTrapdoor) block;
                                if (!trapDoor.isOpen())   {
                                    player.teleport(beforePos);
                                }
                                break;
                            case Block.FENCE_GATE:
                                BlockFenceGate fenceGate = (BlockFenceGate) block;
                                if (!fenceGate.isOpen())   {
                                    player.teleport(beforePos);
                                }
                                break;
                        }
                        player.sendMessage("você não pode usar blocos aqui!");
                        event.setCancelled();
                    }
                }
            }
            if (event.getItem().getId() == Item.BUCKET)   {
                switch (event.getItem().getName())   {
                    case "Water Bucket":
                        Optional<RegionStatus> optWaterStatus = regionManager.check(player, block, "waterbucket", regionManager.getRegions().values());
                        if (optWaterStatus.isPresent())   {
                            if (optWaterStatus.get() == RegionStatus.CANT)   {
                                player.sendMessage("Você não pode colocar água aqui!");
                                event.setCancelled();
                            }
                        }
                        break;
                    case "Lava Bucket":
                        Optional<RegionStatus> optLavaStatus = regionManager.check(player, block, "lavabucket", regionManager.getRegions().values());
                        if (optLavaStatus.isPresent())   {
                            if (optLavaStatus.get() == RegionStatus.CANT)   {
                                player.sendMessage("Você não pode colocar lava aqui!");
                                event.setCancelled();
                            }
                        }
                        break;
                }
            }
        }else   {
            Optional<RegionStatus> optProtectionBlock = regionManager.check(player, block, "protection_block", regionManager.getRegions().values());
            if (optProtectionBlock.isPresent())   {
                if (optProtectionBlock.get() == RegionStatus.CAN)   {
                    player.sendMessage("Você selecionou um bloco de proteção!");
                }
            }
        }
    }
    @EventHandler //Region Move
    public void onPlayerMove(PlayerMoveEvent event)   {
        Player player = event.getPlayer();
        Position playerPos = player.getPosition();
        Optional<RegionStatus> optBool = regionManager.check(player, player.getPosition(), "move", regionManager.getRegions().values());
        if (optBool.isPresent())   {
            if (optBool.get() == RegionStatus.CANT)   {
                player.sendMessage("Você não pode se mover aqui!");
                event.setCancelled();
            }
        }
    }
    @EventHandler //Region PVP
    public void onDamageByEntity(EntityDamageByEntityEvent event)   {
        Entity damager = event.getDamager();
        Entity target = event.getEntity();
        if (damager instanceof EntityHuman && target instanceof EntityHuman)   {
            Optional<RegionStatus> optStatus = regionManager.check((Player)damager, damager.getPosition(), "pvp", regionManager.getRegions().values());
            if (optStatus.isPresent())   {
                if (optStatus.get() == RegionStatus.CANT)   {
                    ((Player) damager).sendMessage("Pvp não é permitido nessa região!");
                    event.setCancelled();
                }
            }
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplosionPrimeEvent event)   {
        event.setBlockBreaking(false);
    }
}
