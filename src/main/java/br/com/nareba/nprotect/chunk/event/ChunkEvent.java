package br.com.nareba.nprotect.chunk.event;

import br.com.nareba.nprotect.NProtect;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.ChunkLoadEvent;

public class ChunkEvent implements Listener {
    private final NProtect plugin;
    public ChunkEvent(NProtect plugin)   {
        this.plugin = plugin;
    }
    public void onChunkLoad(ChunkLoadEvent event)   {

    }
}
