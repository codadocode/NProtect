package br.com.nareba.nprotect;

import br.com.nareba.nprotect.region.RegionManager;
import br.com.nareba.nprotect.region.event.RegionEvent;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

public class NProtect extends PluginBase {
    private RegionManager regionManager;
    @Override
    public void onEnable() {
        getLogger().info(TextFormat.DARK_GREEN + "Initializing...");
        getLogger().info(TextFormat.DARK_GREEN + "Loading info...");
        this.regionManager = new RegionManager(this);
        this.getServer().getPluginManager().registerEvents(new RegionEvent(this.regionManager), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        regionManager.getCommand().onCommand(sender, command, label, args);
        return true;
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.DARK_GREEN + "Finishing...");
    }
}
