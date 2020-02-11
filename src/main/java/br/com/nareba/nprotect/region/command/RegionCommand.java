package br.com.nareba.nprotect.region.command;

import br.com.nareba.nprotect.region.RegionManager;
import br.com.nareba.nprotect.region.core.RegionStatus;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.Optional;

public class RegionCommand {
    private final RegionManager regionManager;
    public RegionCommand(RegionManager regionManager)   {
        this.regionManager = regionManager;
    }
    public void onCommand(CommandSender sender, Command command, String label, String[] args)   {
        String lowerCmd = command.getName().toLowerCase();
        if (sender.isPlayer())   {
            Player player = (Player) sender;
            if (lowerCmd.equals("npget") && args.length == 2)   {
                Integer blockType = new Integer(args[0]);
                Integer protectionSize = new Integer(args[1]);
                Item protectionItem = Item.get(blockType);
                protectionItem.setCustomName("Bloco de Proteção");
                protectionItem.setLore(new String[]{"cria uma regiao protegida", "size:" + protectionSize});
                player.getInventory().addItem(protectionItem);
                player.sendMessage("Bloco de proteção foi adicionado ao seu inventário!");
            }else if (lowerCmd.equals("npflag") && args.length == 2)   {
                String flagName = args[0];
                Boolean value = new Boolean(args[1]);
                if (value.equals(true) || value.equals(false))   {
                    Optional<RegionStatus> optFlagStatus = regionManager.setFlag(player, flagName, value);
                    if (optFlagStatus.isPresent())   {
                        switch (optFlagStatus.get())   {
                            case NONE:
                                player.sendMessage("A flag '" + flagName + "' não existe!");
                                break;
                            case NO_REGION_SELECTED:
                                player.sendMessage("Você precisa selecionar uma região sua primeiro!");
                                break;
                            case SET_FLAG:
                                player.sendMessage("A flag '" + flagName + "' foi alternada para: " + value.toString());
                                break;
                        }
                    }
                }
            }else if (lowerCmd.equals("npremove"))   {
                Optional<RegionStatus> removeStatus = regionManager.removeRegion(player);
                if (removeStatus.isPresent())   {
                    switch(removeStatus.get())   {
                        case REGION_REMOVED:
                            player.sendMessage("Região removida com sucesso!");
                            break;
                        case HAS_SUBREGION:
                            player.sendMessage("Essa região tem sub regiões! Você não pode deletar ela");
                            break;
                        case CANT:
                            player.sendMessage("Você precisa selecionar uma região para usar este comando!");
                            break;
                    }
                }
            }else if (lowerCmd.equals("npaddmember") && args.length == 1)   {
                String targetPlayerName = args[0];
                Optional<RegionStatus> optMemberStatus = regionManager.addMember(player, targetPlayerName);
                if (optMemberStatus.isPresent())   {
                    switch(optMemberStatus.get())   {
                        case ADDED:
                            player.sendMessage("Jogador '" + targetPlayerName + "' foi adicionado como membro!");
                            break;
                        case IS_MEMBER:
                            player.sendMessage("Jogador '" + targetPlayerName + "' já é um membro!");
                            break;
                        case NO_PLAYER_FOUND:
                            player.sendMessage("Jogador '" + targetPlayerName + "' não foi encontrado!");
                            break;
                        case NO_REGION_SELECTED:
                            player.sendMessage("Você precisa selecionar uma região para usar este comando!");
                            break;
                    }
                }
            }else if (lowerCmd.equals("npaddowner"))   {
                String targetPlayerName = args[0];
                Optional<RegionStatus> optMemberStatus = regionManager.addOwner(player, targetPlayerName);
                if (optMemberStatus.isPresent())   {
                    switch(optMemberStatus.get())   {
                        case ADDED:
                            player.sendMessage("Jogador '" + targetPlayerName + "' foi adicionado como dono!");
                            break;
                        case IS_OWNER:
                            player.sendMessage("Jogador '" + targetPlayerName + "' já é um dono!");
                            break;
                        case NO_PLAYER_FOUND:
                            player.sendMessage("Jogador '" + targetPlayerName + "' não foi encontrado!");
                            break;
                        case NO_REGION_SELECTED:
                            player.sendMessage("Você precisa selecionar uma região para usar este comando!");
                            break;
                    }
                }
            }else if (lowerCmd.equals("npdelmember"))   {
                String targetPlayerName = args[0];
                Optional<RegionStatus> optMemberStatus = regionManager.removeMember(player, targetPlayerName);
                if (optMemberStatus.isPresent())   {
                    switch(optMemberStatus.get())   {
                        case REMOVED:
                            player.sendMessage("Jogador '" + targetPlayerName + "' não é mais um membro!");
                            break;
                        case NOT_MEMBER:
                            player.sendMessage("Jogador '" + targetPlayerName + "' não é um membro dessa região!");
                            break;
                        case NO_PLAYER_FOUND:
                            player.sendMessage("Jogador '" + targetPlayerName + "' não foi encontrado!");
                            break;
                        case NO_REGION_SELECTED:
                            player.sendMessage("Você precisa selecionar uma região para usar este comando!");
                            break;
                    }
                }
            }else if (lowerCmd.equals("npdelowner"))   {
                String targetPlayerName = args[0];
                Optional<RegionStatus> optMemberStatus = regionManager.removeOwner(player, targetPlayerName);
                if (optMemberStatus.isPresent())   {
                    switch(optMemberStatus.get())   {
                        case REMOVED:
                            player.sendMessage("Jogador '" + targetPlayerName + "' não é mais um dono!");
                            break;
                        case NOT_MEMBER:
                            player.sendMessage("Jogador '" + targetPlayerName + "' não é um dono dessa região!");
                            break;
                        case NO_PLAYER_FOUND:
                            player.sendMessage("Jogador '" + targetPlayerName + "' não foi encontrado!");
                            break;
                        case NO_REGION_SELECTED:
                            player.sendMessage("Você precisa selecionar uma região para usar este comando!");
                            break;
                    }
                }
            }
        }
    }
}
