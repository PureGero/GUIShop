package com.pablo67340.guishop.listenable;

import java.util.Objects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;

import org.bukkit.event.*;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.stefvanschie.inventoryframework.shade.mininbt.ItemNBTUtil;
import com.github.stefvanschie.inventoryframework.shade.mininbt.NBTWrappers.NBTTagCompound;

import com.pablo67340.guishop.Main;
import com.pablo67340.guishop.definition.ItemType;
import com.pablo67340.guishop.definition.ShopDef;
import com.pablo67340.guishop.util.Config;
import com.pablo67340.guishop.util.ItemUtil;
import com.pablo67340.guishop.util.XMaterial;

public final class PlayerListener implements Listener {

	/**
	 * An instance of a {@link PlayerListener} that will be used to handle this
	 * specific object reference from other classes, even though methods here will
	 * be static.
	 */
	public static final PlayerListener INSTANCE = new PlayerListener();

	public void openShop(Player player) {
		if (Main.getINSTANCE().getCreatorRefresh()) {
			player.sendMessage("�aGUIShop config was recently edited in creator mode. Reloading before opening...");
			Main.getINSTANCE().reload(player, true);
			Main.getINSTANCE().setCreatorRefresh(false);
		}
		Menu menu = new Menu();
		menu.open(player);
	}

	/**
	 * Print the usage of the plugin to the player.
	 */
	public void printUsage(Player player) {
		player.sendMessage("�dG�9U�8I�3S�dh�9o�8p �3C�do�9m�8m�3a�dn�8d�3s�d:");
		player.sendMessage("�7/guishop �eedit/e �0- �aOpens in Editor Mode");
		player.sendMessage("�7/guishop �eprice/p {price} �0- �aSet item in hand's buy price");
		player.sendMessage("�7/guishop �esell/s {price} �0- �aSet item in hand's sell price");
		player.sendMessage("�7/guishop �eshopname/sn {name} �0- �aSet item in hand's Shop-Name");
		player.sendMessage("�7/guishop �ebuyname/bn {name} �0- �aSet item in hand's Buy-Name");
		player.sendMessage("�7/guishop �eenchant/e {enchants} �0- �aSet item in hand's Enchantments");
		player.sendMessage("�7/guishop �easll {line} �0- �aAdd Shop Lore Line");
		player.sendMessage("�7/guishop �edsll {lineNumber} �0- �aDelete Shop Lore Line. Starts at 0");
		player.sendMessage("�7/guishop �eesll {lineNumber} {line} �0- �aEdit Shop Lore Line. Starts at 0");
		player.sendMessage("�7/guishop �eabll {line} �0- �aAdd Buy Lore Line");
		player.sendMessage("�7/guishop �edbll {lineNumber} �0- �aDelete Buy Lore Line. Starts at 0");
		player.sendMessage("�7/guishop �eebll {lineNumber} {line} �0- �aEdit Buy Lore Line. Starts at 0");
		player.sendMessage("�7/guishop �eac {command} �0- �aAdd Command to item");
		player.sendMessage("�7/guishop �edc {lineNumber} �0- �aDelete Command by line. Starts at 0");
		player.sendMessage("�7/guishop �eec {lineNumber} {cmd} �0- �aEdit Command by line. Starts at 0");
		player.sendMessage("�7/guishop �emt {type} �0- �aSet an item's mob type. Used for Spawners/Eggs.");
		player.sendMessage("�7/guishop �et {type} �0- �aSet an item's type. BLANK, SHOP, COMMAND, DUMMY");
	}

	// When the inventory closes

	// When the player clicks a sign
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Block block = e.getClickedBlock();

		// If the block exists
		if (block != null) {
			// If the block has a state
			block.getState();
			// If the block state is a Sign
			if (block.getState() instanceof Sign) {
				Sign sign = (Sign) block.getState();
				String line1 = ChatColor.translateAlternateColorCodes('&', sign.getLine(0));
				// Check if the sign is a GUIShop sign
				if (line1.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&',
						Objects.requireNonNull(Main.INSTANCE.getMainConfig().getString("sign-title"))))) {
					// If the player has Permission to use sign
					if (player.hasPermission("guishop.use") && player.hasPermission("guishop.sign.use")
							|| player.isOp()) {
						e.setCancelled(true);
						Menu menu = new Menu();
						menu.open(player);
					} else {
						e.setCancelled(true);
						player.sendMessage(Config.getPrefix() + " " + Config.getNoPermission());
					}

				}
			}
		}
	}

	/**
	 * Custom MobSpawner placement method.
	 */
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getItemInHand().getType() == XMaterial.SPAWNER.parseMaterial()) {

			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(Main.getINSTANCE(), () -> {
				ItemStack item = event.getItemInHand();
				NBTTagCompound cmp = ItemNBTUtil.getTag(item);
				if (cmp.hasKey("GUIShopSpawner")) {

					String mobId = cmp.getString("GUIShopSpawner");
					Block block = event.getBlockPlaced();
					CreatureSpawner cs = (CreatureSpawner) block.getState();
					cs.setSpawnedType(Objects.requireNonNull(EntityType.fromName(mobId)));
					cs.update();
				}
			}, 1L);

		}
	}

}
