package com.fnv_tw.gui;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import com.fnv_tw.managers.IslandManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

public class AcceptRejectGUI extends AbstractGUI{
    private final BetterSkyBlock plugin;
    private final MainConfig mainConfig;
    private final Language languageConfig;
    private final Consumer<Player> acceptAction;
    private final Consumer<Player> rejectAction;

    public AcceptRejectGUI(Player player, String title, Consumer<Player> acceptAction, Consumer<Player> rejectAction){
        plugin = BetterSkyBlock.getInstance();
        mainConfig = plugin.getMainConfigConfigManager().getConfig();
        languageConfig = plugin.getLanguageConfigManager().getConfig();
        this.player = player;
        this.size = 27;
        this.guiItems = new ItemStack[this.size];
        this.acceptAction = acceptAction;
        this.rejectAction = rejectAction;
        this.name = title;

        ItemStack accept = new ItemStack(Material.LIME_STAINED_GLASS_PANE,1);
        ItemStack deny = new ItemStack(Material.RED_STAINED_GLASS_PANE,1);
        ItemMeta acceptItemMeta = accept.getItemMeta();
        acceptItemMeta.setDisplayName(ChatColor.GREEN+""+ChatColor.BOLD + languageConfig.getAcceptInAcceptRejectGUI());
        acceptItemMeta.setCustomModelData(11);
        accept.setItemMeta(acceptItemMeta);
        ItemMeta denyItemMeta = deny.getItemMeta();
        denyItemMeta.setDisplayName(ChatColor.RED+""+ChatColor.BOLD + languageConfig.getRejectInAcceptRejectGUI());
        denyItemMeta.setCustomModelData(15);
        deny.setItemMeta(denyItemMeta);
        guiItems[11] = accept;
        guiItems[15] = deny;
        //11,15
    }

    @Override
    public void clickItemOperation(ItemStack item) {
        if(item.getItemMeta().getCustomModelData()==11){
            close(true);
            if (acceptAction != null) {
                acceptAction.accept(player);
            }
        }else if(item.getItemMeta().getCustomModelData()==15){
            close(true);
            if (rejectAction != null) {
                rejectAction.accept(player);
            }
        }
    }
}
