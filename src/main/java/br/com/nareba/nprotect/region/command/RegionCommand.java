package br.com.nareba.nprotect.region.command;

import br.com.nareba.nprotect.region.RegionManager;
import br.com.nareba.nprotect.region.core.RegionStatus;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import com.sun.org.apache.xpath.internal.operations.Bool;

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
            if (lowerCmd.equals("nopget") && args.length == 2)   {
                Integer blockType = new Integer(args[0]);
                Integer protectionSize = new Integer(args[1]);
                Item protectionItem = Item.get(blockType + 1);
                protectionItem.setCustomName("Bloco de Proteção");
                protectionItem.setLore("protege " + protectionSize.toString() + " blocos para todos as direções!");
                protectionItem.setCustomBlockData(new CompoundTag("protection"));
                protectionItem.getCustomBlockData().putInt("protection_size", protectionSize);
                player.getInventory().addItem(protectionItem);
                player.sendMessage("Bloco de proteção foi adicionado ao seu inventário!");
            }else if (lowerCmd.equals("nopflag") && args.length == 2)   {
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
            }
        }
    }
}
