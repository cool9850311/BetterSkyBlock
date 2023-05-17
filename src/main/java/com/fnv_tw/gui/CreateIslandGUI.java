package com.fnv_tw.gui;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CreateIslandGUI extends AbstractGUI{
    private final BetterSkyBlock plugin;
    private final MainConfig mainConfig;
    private final Language languageConfig;
    private final String islandName;

    public CreateIslandGUI(Player player, String islandName){
        plugin = BetterSkyBlock.getInstance();
        mainConfig = plugin.getMainConfigConfigManager().getConfig();
        languageConfig = plugin.getLanguageConfigManager().getConfig();
        this.player = player;
        this.size = 27;
        this.guiItems = new ItemStack[this.size];
        this.name = languageConfig.getCreateIslandGUITitle();
        this.islandName = islandName;
        ItemStack normal = new ItemStack(Material.GRASS_BLOCK,1);
        ItemStack nether = new ItemStack(Material.NETHERRACK,1);
        ItemStack theEnd = new ItemStack(Material.END_STONE,1);

        ItemMeta normalItemMeta = normal.getItemMeta();
        normalItemMeta.setDisplayName(ChatColor.GOLD + languageConfig.getNormalCreateIslandGUI());
        normalItemMeta.setCustomModelData(11);
//        normalItemMeta.setLore(loreBuilder(100,10000));
        normal.setItemMeta(normalItemMeta);
        ItemMeta netherItemMeta = nether.getItemMeta();
        netherItemMeta.setDisplayName(ChatColor.GOLD + languageConfig.getNetherCreateIslandGUI());
        netherItemMeta.setCustomModelData(13);
//        netherItemMeta.setLore(loreBuilder(200,20000));
        nether.setItemMeta(netherItemMeta);
        ItemMeta theEndItemMeta = theEnd.getItemMeta();
        theEndItemMeta.setDisplayName(ChatColor.GOLD + languageConfig.getTheEndCreateIslandGUI());
        theEndItemMeta.setCustomModelData(15);
//        theEndItemMeta.setLore(loreBuilder(300,30000));
        theEnd.setItemMeta(theEndItemMeta);

        guiItems[11] = normal;
        guiItems[13] = nether;
        guiItems[15] = theEnd;
    }

    @Override
    public void clickItemOperation(ItemStack item) {
        String title;
        switch (item.getItemMeta().getCustomModelData()) {

            case 11 -> {
                close(true);
                title = languageConfig.getCreateIslandConfirmTitle() + " " + languageConfig.getNormalCreateIslandGUI();
                Consumer<Player> acceptAction = (player) -> {
                    BetterSkyBlock.getInstance().getIslandManager().createWorld(player, islandName, World.Environment.NORMAL);
                };
                Consumer<Player> rejectAction = (player) -> {

                };
                new AcceptRejectGUI(player, title, acceptAction,null).open();
//                BetterSkyBlock.getInstance().getIslandManager().createWorld(player, islandName);
            }
            case 13 -> {
                title = languageConfig.getCreateIslandConfirmTitle() + " " + languageConfig.getNetherCreateIslandGUI();
                close(true);
                Consumer<Player> acceptAction = (player) -> {
                    BetterSkyBlock.getInstance().getIslandManager().createWorld(player, islandName, World.Environment.NETHER);
                };
                Consumer<Player> rejectAction = (player) -> {

                };
                new AcceptRejectGUI(player, title, acceptAction,null).open();
            }
            case 15 -> {
                close(true);
                title = languageConfig.getCreateIslandConfirmTitle() + " " + languageConfig.getTheEndCreateIslandGUI();
                Consumer<Player> acceptAction = (player) -> {
                    BetterSkyBlock.getInstance().getIslandManager().createWorld(player, islandName, World.Environment.THE_END);
                };
                Consumer<Player> rejectAction = (player) -> {

                };
                new AcceptRejectGUI(player, title, acceptAction,null).open();
            }
        }
    }
}
