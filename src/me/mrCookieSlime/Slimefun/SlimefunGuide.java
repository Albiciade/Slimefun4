package me.mrCookieSlime.Slimefun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.mrCookieSlime.CSCoreLibPlugin.PlayerRunnable;
import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage;
import me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage.HoverAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.CustomBookOverlay;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Math.DoubleHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.GitHub.Contributor;
import me.mrCookieSlime.Slimefun.GitHub.IntegerFormat;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Misc.BookDesign;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.LockedCategory;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SeasonCategory;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunGadget;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunMachine;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AReactor;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.Setup.Messages;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.GuideHandler;
import me.mrCookieSlime.Slimefun.api.Slimefun;

public class SlimefunGuide {
	
	public static Map<UUID, List<Object>> history = new HashMap<>();
	public static int month = 0;
	
	public static List<Contributor> contributors = new ArrayList<Contributor>();
	public static int issues = 0;
	public static int forks = 0;
	/**
	 * Represents the number of stars on the Slimefun4 GitHub repository.
	 * @since 4.1.13
	 */
	public static int stars = 0;
	public static int code_bytes = 0;
	public static Date last_update = new Date();

	static boolean all_recipes = true;
	private static final int category_size = 36;

	@Deprecated
	public static ItemStack getItem() {
		return getItem(BookDesign.CHEST);
	}

	public static ItemStack getItem(BookDesign design) {
		switch (design) {
		case BOOK: {
			return new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&aGuide Slimefun &7(Livre)", "", "&eClic droit &8\u21E8 &7Parcourir les objets", "&eShift + Clic droit &8\u21E8 &7Paramètres / Crédits");
		}
		case CHEAT_SHEET: {
			return new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&cGuide Slimefun &4(Cheat Sheet)", "", "&4&lUtilisable uniquement par les admins", "", "&eClic droit &8\u21E8 &7Parcourir les objets", "&eShift + Clic droit &8\u21E8 &7Paramètres / Crédits");
		}
		case CHEST: {
			return new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&aGuide Slimefun &7(Coffre)", "", "&eClic droit &8\u21E8 &7Parcourir les objets", "&eShift + Clic droit &8\u21E8 &7Paramètres / Crédits");
		}
		default:
			return null;
		}
	}

	@Deprecated
	public static ItemStack getItem(boolean book) {
		return getItem(book ? BookDesign.BOOK: BookDesign.CHEST);
	}
	
	public static ItemStack getDeprecatedItem(boolean book) {
		return new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&eGuide Slimefun &7(Clic droit)", (book ? "": "&2"), "&rCeci est votre guide basique pour Slimefun", "&rVous pouvez voir tous les objets du plugin", "&rou ses addons, incluant leur recettes", "&run peu d'informations et plus");
	}
	
	private static final int[] slots = new int[] {0, 2, 3, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
	
	public static void openSettings(Player p, final ItemStack guide) {
		final ChestMenu menu = new ChestMenu("Paramètres / Informations");
		
		menu.setEmptySlotsClickable(false);
		menu.addMenuOpeningHandler(
				pl -> pl.playSound(pl.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 0.7F, 0.7F)
		);
		
		for (int i: slots) {
			menu.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
			menu.addMenuClickHandler(i,
				(pl, slot, item, action) -> false
			);
		}
		
		
		if (SlimefunManager.isItemSimiliar(guide, getItem(BookDesign.CHEST), true)) {
			if (p.hasPermission("slimefun.cheat.items")) {
				menu.addItem(19, new CustomItem(new ItemStack(Material.CHEST), "&7Disposition: &eCoffre", "", "&aCoffre", "&7Livre", "&7Cheat Sheet", "", "&e Clic &8\u21E8 &7Changer la disposition"));
				menu.addMenuClickHandler(19, (pl, slot, item, action) -> {
					pl.getInventory().setItemInMainHand(getItem(BookDesign.BOOK));
					openSettings(pl, pl.getInventory().getItemInMainHand());
					return false;
				});
			}
			else {
				menu.addItem(19, new CustomItem(new ItemStack(Material.CHEST), "&7Disposition: &eCoffre", "", "&aCoffre", "&7Livre", "", "&e Clic &8\u21E8 &7Changer la disposition"));
				menu.addMenuClickHandler(19, (pl, slot, item, action) -> {
					pl.getInventory().setItemInMainHand(getItem(BookDesign.BOOK));
					openSettings(pl, pl.getInventory().getItemInMainHand());
					return false;
				});
			}
		}
		else if (SlimefunManager.isItemSimiliar(guide, getItem(BookDesign.BOOK), true)) {
			if (p.hasPermission("slimefun.cheat.items")) {
				menu.addItem(19, new CustomItem(new ItemStack(Material.CHEST), "&7Disposition: &eLivre", "", "&7Coffre", "&aLivre", "&7Cheat Sheet", "", "&e Clic &8\u21E8 &7Changer la disposition"));
				menu.addMenuClickHandler(19, (pl, slot, item, action) -> {
					pl.getInventory().setItemInMainHand(getItem(BookDesign.CHEAT_SHEET));
					openSettings(pl, pl.getInventory().getItemInMainHand());
					return false;
				});
			}
			else {
				menu.addItem(19, new CustomItem(new ItemStack(Material.CHEST), "&7Disposition: &eLivre", "", "&7Coffre", "&aLivre", "", "&e Clic &8\u21E8 &7Changer la disposition"));
				menu.addMenuClickHandler(19, (pl, slot, item, action) -> {
					pl.getInventory().setItemInMainHand(getItem(BookDesign.CHEST));
					openSettings(pl, pl.getInventory().getItemInMainHand());
					return false;
				});
			}
		}
		else if (SlimefunManager.isItemSimiliar(guide, getItem(BookDesign.CHEAT_SHEET), true)) {
			menu.addItem(19, new CustomItem(new ItemStack(Material.CHEST), "&7Disposition: &eCheat Sheet", "", "&7Coffre", "&7Livre", "&aCheat Sheet", "", "&e Clic &8\u21E8 &7Changer la disposition"));
			menu.addMenuClickHandler(19, (pl, slot, item, action) -> {
				pl.getInventory().setItemInMainHand(getItem(BookDesign.CHEST));
				openSettings(pl, pl.getInventory().getItemInMainHand());
				return false;
			});
		}
		
		menu.addItem(1, new CustomItem(new ItemStack(Material.WRITABLE_BOOK), "&aCrédits", "", "&7Version: &a" + SlimefunStartup.instance.getDescription().getVersion(), "&7Contributeurs: &e" + contributors.size(), "", "&7\u21E8 Cliquez pour voir qui se cache derrière ce plugin"));
		menu.addMenuClickHandler(1, (pl, slot, item, action) -> {
			openCredits(pl, guide);
			return false;
		});
		
		try {
			menu.addItem(4, new CustomItem(new ItemStack(Material.COMPARATOR), "&eCode Source", "", "&7Octets de code: &6" + IntegerFormat.formatBigNumber(code_bytes), "&7Derni�re M�J il y a : &a" + IntegerFormat.timeDelta(last_update), "&7Forks: &e" + forks, "&7Stars: &e" + stars, "", "&7&oSlimefun 4 est un projet communautaire,", "&7&ole code source est disponible sur GitHub", "&7&oet si vous voulez garder ce plugin en vie,", "&7&opendez � y contribuer !", "", "&7\u21E8 Cliquez pour accéder à GitHub"));
			menu.addMenuClickHandler(4, (pl, slot, item, action) -> {
				pl.closeInventory();
				pl.sendMessage("");
				pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&ohttps://github.com/TheBusyBiscuit/Slimefun4"));
				pl.sendMessage("");
				return false;
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		menu.addItem(7, new CustomItem(new ItemStack(Material.REDSTONE), "&4Bug Tracker", "", "&7Probl�mes non r�solus: &a" + issues, "", "&7\u21E8 Cliquez pour acc�der au Bug Tracker"));
		menu.addMenuClickHandler(7, (pl, slot, item, action) -> {
			pl.closeInventory();
			pl.sendMessage("");
			pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&ohttps://github.com/TheBusyBiscuit/Slimefun4/issues"));
			pl.sendMessage("");
			return false;
		});
		
		menu.open(p);
	}

	public static void openCredits(Player p, final ItemStack guide) {
		final ChestMenu menu = new ChestMenu("Credits");
		
		menu.setEmptySlotsClickable(false);
		menu.addMenuOpeningHandler(
			pl -> pl.playSound(pl.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 0.7F, 0.7F)
		);
		
		for (int i = 0; i < 9; i++) {
			if (i != 4) {
				menu.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
				menu.addMenuClickHandler(i,
					(pl, slot, item, action) -> false
				);
			}
			else {
				menu.addItem(4, new CustomItem(new ItemStack(Material.EMERALD), "&7\u21E6 Retour aux paramêtres"));
				menu.addMenuClickHandler(4, (pl, slot, item, action) -> {
					openSettings(pl, guide);
					return false;
				});
			}
		}
		
		int index = 9;
		double total = 0;

		for (Contributor contributor : contributors) {
			total += contributor.getCommits();
		}
		
		for (final Contributor contributor: contributors) {
			ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
			
			try {
				String texture = contributor.getTexture();
				if (texture != null) skull = CustomSkull.getItem(texture);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD + contributor.getName());

			if (contributor.getCommits() > 0) {
				double percentage = DoubleHandler.fixDouble((contributor.getCommits() * 100.0) / total, 2);
				
				meta.setLore(Arrays.asList("", ChatColor.translateAlternateColorCodes('&', "&7R�le: &r" + contributor.getJob()), ChatColor.translateAlternateColorCodes('&', "&7Contributions: &r" + contributor.getCommits() + " commits &7(&r" + percentage + "%&7)"), "", ChatColor.translateAlternateColorCodes('&', "&7\u21E8 Cliquez pour voir mon profil GitHub")));
			}
			else {
				meta.setLore(Arrays.asList("", ChatColor.translateAlternateColorCodes('&', "&7R�le: &r" + contributor.getJob())));
			}

			skull.setItemMeta(meta);
			
			menu.addItem(index, skull);
			menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
				if (contributor.getCommits() > 0) {
					pl.closeInventory();
					pl.sendMessage("");
					pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&o" + contributor.getProfile()));
					pl.sendMessage("");
				}
				return false;
			});
			
			index++;
		}
		
		menu.open(p);
	}

	public static void openCheatMenu(Player p) {
		openMainMenu(p, false, false, 1);
	}
	
	public static void openGuide(Player p, boolean book) {
		if (!SlimefunStartup.getWhitelist().getBoolean(p.getWorld().getName() + ".enabled")) return;
		if (!SlimefunStartup.getWhitelist().getBoolean(p.getWorld().getName() + ".enabled-items.SLIMEFUN_GUIDE")) return;
		if (!history.containsKey(p.getUniqueId())) openMainMenu(p, true, book, 1);
		else {
			Object last = getLastEntry(p, false);
			if (last instanceof Category) openCategory(p, (Category) last, true, 1, book);
			else if (last instanceof SlimefunItem) displayItem(p, ((SlimefunItem) last).getItem(), false, book, 0);
			else if (last instanceof GuideHandler) ((GuideHandler) last).run(p, true, book);
			else displayItem(p, (ItemStack) last, false, book, 0);
		}
	}
	
	public static void openMainMenu(final Player p, final boolean survival, final boolean book, final int selected_page) {
		if (survival)
			clearHistory(p.getUniqueId());
		
		if (book) {
			List<TellRawMessage> pages = new ArrayList<TellRawMessage>();
			List<String> texts = new ArrayList<String>();
			List<String> tooltips = new ArrayList<String>();
			List<PlayerRunnable> actions = new ArrayList<PlayerRunnable>();
			
			int tier = 0;
			
			for (final Category category: Category.list()) {
				
				boolean locked = true;
				
				for (SlimefunItem item: category.getItems()) {
					if (Slimefun.isEnabled(p, item, false)) {
						locked = false;
						break;
					}
				}
				
				if (locked) {
					// Dont display that Category...
				}
				else {
					if (tier < category.getTier()) {
						if (survival) {
							for (final GuideHandler handler: Slimefun.getGuideHandlers(tier)) {
								handler.addEntry(texts, tooltips);
								actions.add(new PlayerRunnable(2) {
									
									@Override
									public void run(Player p) {
										handler.run(p, survival, book);
									}
								});
							}
						}
						tier = category.getTier();
						if (tier > 1) {
							for (int i = 0; i < 10; i++) {
								if (texts.size() % 10 == 0) break;
								texts.add(" ");
								tooltips.add(null);
								actions.add(null);
							}
						}
						texts.add(ChatColor.translateAlternateColorCodes('&', "&8\u21E8 &6Niveau " + tier));
						tooltips.add(null);
						actions.add(null);
					}
					if (category instanceof LockedCategory && !((LockedCategory) category).hasUnlocked(p)) {
						StringBuilder parents = new StringBuilder(ChatColor.translateAlternateColorCodes('&', "&4&lBLOQUE\n\n&7Pour d�bloquer cette cat�gorie,\n&7vous devez d'abord d�bloquer les objets\n&7des cat�gories suivantes:\n"));
						
						for (Category parent: ((LockedCategory) category).getParents()) {
							parents.append(ChatColor.translateAlternateColorCodes('&', "\n&c" + StringUtils.formatItemName(parent.getItem(), false)));
						}
						
						texts.add(ChatColor.translateAlternateColorCodes('&', shorten("&c" , StringUtils.formatItemName(category.getItem(), false))));
						tooltips.add(parents.toString());
						actions.add(null);
					}
					else if (category instanceof SeasonCategory) {
						if (((SeasonCategory) category).isUnlocked()) {
							texts.add(ChatColor.translateAlternateColorCodes('&', shorten("&a", StringUtils.formatItemName(category.getItem(), false))));
							tooltips.add(ChatColor.translateAlternateColorCodes('&', "&eCliquez pour ouvrir la cat�gorie :\n" + StringUtils.formatItemName(category.getItem(), false)));
							actions.add(new PlayerRunnable(1) {
								@Override
								public void run(final Player p) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
										openCategory(p, category, survival, 1, book);
									}, 1L);
								}
							});
						}
					}
					else {
						texts.add(ChatColor.translateAlternateColorCodes('&', shorten("&a", StringUtils.formatItemName(category.getItem(), false))));
						tooltips.add(ChatColor.translateAlternateColorCodes('&', "&e&eCliquez pour ouvrir la cat�gorie :\n" + StringUtils.formatItemName(category.getItem(), false)));
						actions.add(new PlayerRunnable(1) {
							@Override
							public void run(final Player p) {
								Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
									openCategory(p, category, survival, 1, book);
								}, 1L);
							}
						});
					}
				}
			}
			
			if (survival) {
				for (final GuideHandler handler: Slimefun.getGuideHandlers(tier)) {
					handler.addEntry(texts, tooltips);
					actions.add(new PlayerRunnable(2) {
						@Override
						public void run(Player p) {
							handler.run(p, survival, book);
						}
					});
				}
			}
			
			for (int i = 0; i < texts.size(); i = i + 10) {
				TellRawMessage page = new TellRawMessage();
				page.addText(ChatColor.translateAlternateColorCodes('&', "&b&l- Guide Slimefun -\n\n"));
				for (int j = i; j < texts.size() && j < i + 10; j++) {
					page.addText(texts.get(j) + "\n");
					if (tooltips.get(j) != null) page.addHoverEvent(HoverAction.SHOW_TEXT, tooltips.get(j));
					if (actions.get(j) != null) page.addClickEvent(actions.get(j));
				}
//				page.addText("\n");
//				if (i > 0) {
//					page.addText(ChatColor.translateAlternateColorCodes('&', "&c<- Prev"));
//					page.addHoverEvent(HoverAction.SHOW_TEXT, ChatColor.translateAlternateColorCodes('&', "&eGo to Page " + (i)));
//					page.addClickEvent(me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage.ClickAction.CHANGE_PAGE, String.valueOf(i));
//					page.addText("    ");
//				}
//				if (texts.size() > i * 10) {
//					page.addText("    ");
//					page.addText(ChatColor.translateAlternateColorCodes('&', "&cNext ->"));
//					page.addHoverEvent(HoverAction.SHOW_TEXT, ChatColor.translateAlternateColorCodes('&', "&eGo to Page " + (i + 2)));
//					page.addClickEvent(me.mrCookieSlime.CSCoreLibPlugin.general.Chat.TellRawMessage.ClickAction.CHANGE_PAGE, String.valueOf(i + 2));
//				}
				pages.add(page);
			}
			
			new CustomBookOverlay("Guide Slimefun", "mrCookieSlime", pages.toArray(new TellRawMessage[pages.size()])).open(p);
		}
		else {
			final ChestMenu menu = new ChestMenu("Guide Slimefun");
			
			menu.setEmptySlotsClickable(false);
			menu.addMenuOpeningHandler(
				pl -> pl.playSound(pl.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 0.7F)
			);
			
			List<Category> categories = Slimefun.current_categories;
			List<GuideHandler> handlers = Slimefun.guide_handlers2;
			
			int index = 9;
			int pages = 1;
			
			for (int i = 0; i < 9; i++) {
				menu.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
				menu.addMenuClickHandler(i,
					(pl, slot, item, action) -> false
				);
			}
			
			for (int i = 45; i < 54; i++) {
				menu.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
				menu.addMenuClickHandler(i,
					(pl, slot, item, action) -> false
				);
			}
			
			int target = (category_size * (selected_page - 1)) - 1;
			
			while (target < (categories.size() + handlers.size() - 1)) {
				if (index >= category_size + 9) {
					pages++;
					break;
				}
				
				target++;
				
				if (target >= categories.size()) {
					if (!survival) break;
					index = handlers.get(target - categories.size()).next(p, index, menu);
				}
				else {
					final Category category = categories.get(target);
					
					boolean locked = true;
					
					for (SlimefunItem item: category.getItems()) {
						if (Slimefun.isEnabled(p, item, false)) {
							locked = false;
							break;
						}
					}
					
					if (locked) {
						// Dont display that Category...
					}
					else if (!(category instanceof LockedCategory)) {
						if (!(category instanceof SeasonCategory)) {
							menu.addItem(index, category.getItem());
							menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
								openCategory(pl, category, survival, 1, book);
								return false;
							});
							index++;
						}
						else {
							if (((SeasonCategory) category).isUnlocked()) {
								menu.addItem(index, category.getItem());
								menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
									openCategory(pl, category, survival, 1, book);
									return false;
								});
								index++;
							}
						}
					}
					else if (((LockedCategory) category).hasUnlocked(p)) {
						menu.addItem(index, category.getItem());
						menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
							openCategory(pl, category, survival, 1, book);
							return false;
						});
						index++;
					}
					else {
						List<String> parents = new ArrayList<String>();
						parents.add("");
						parents.add(ChatColor.translateAlternateColorCodes('&', "&rVous devez d�bloquer tous les objets"));
						parents.add(ChatColor.translateAlternateColorCodes('&', "&rdes cat�gories suivantes:"));
						parents.add("");
						for (Category parent : ((LockedCategory) category).getParents()) {
							parents.add(parent.getItem().getItemMeta().getDisplayName());
						}
						menu.addItem(index, new CustomItem(Material.BARRIER, "&4BLOQUE &7- &r" + category.getItem().getItemMeta().getDisplayName(), parents.toArray(new String[parents.size()])));
						menu.addMenuClickHandler(index,
							(pl, slot, item, action) -> false
						);
						index++;
					}
				}
			}

			final int finalPages = pages;
			
			menu.addItem(46, new CustomItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE), "&r\u21E6 Page précédente", "", "&7(" + selected_page + " / " + pages + ")"));
			menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
				int next = selected_page - 1;
				if (next < 1) next = finalPages;
				if (next != selected_page) openMainMenu(pl, survival, book, next);
				return false;
			});
			
			menu.addItem(52, new CustomItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE), "&rPage suivante \u21E8", "", "&7(" + selected_page + " / " + pages + ")"));
			menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
				int next = selected_page + 1;
				if (next > finalPages) next = 1;
				if (next != selected_page) openMainMenu(pl, survival, book, next);
				return false;
			});
			
			menu.open(p);
		}
	}

	public static String shorten(String string, String string2) {
		if (ChatColor.stripColor(string + string2).length() > 19) return (string + ChatColor.stripColor(string2)).substring(0, 18) + "...";
		else return (string + ChatColor.stripColor(string2));
	}

	public static void openCategory(final Player p, final Category category, final boolean survival, final int selected_page, final boolean book) {
		if (category == null) return;

		if (book && category.getItems().size() < 250) {
			List<TellRawMessage> pages = new ArrayList<TellRawMessage>();
			List<String> texts = new ArrayList<String>();
			List<String> tooltips = new ArrayList<String>();
			List<PlayerRunnable> actions = new ArrayList<PlayerRunnable>();
			
			for (final SlimefunItem item: category.getItems()) {
				if (Slimefun.hasPermission(p, item, false)) {
					if (Slimefun.isEnabled(p, item, false)) {
						if (survival && !Slimefun.hasUnlocked(p, item, false) && item.getResearch() != null) {
						    final Research research = item.getResearch();
						    
							texts.add(ChatColor.translateAlternateColorCodes('&', shorten("&7", StringUtils.formatItemName(item.getItem(), false))));
							tooltips.add(ChatColor.translateAlternateColorCodes('&', StringUtils.formatItemName(item.getItem(), false) + "\n&c&lBLOQUE\n\n&7Co�t: " + (p.getLevel() >= research.getCost() ? "&b": "&4") + research.getCost() + " Niveaux\n\n&a> Cliquez pour d�bloquer"));
							actions.add(new PlayerRunnable(2) {
								@Override
								public void run(final Player p) {
									if (!Research.isResearching(p)) {
										if (research.canUnlock(p)) {
											if (research.hasUnlocked(p))
												openCategory(p, category, true, selected_page, book);
											else {
												if (!(p.getGameMode() == GameMode.CREATIVE && Research.creative_research)) {
													p.setLevel(p.getLevel() - research.getCost());
												}

												if (p.getGameMode() == GameMode.CREATIVE) {
													research.unlock(p, true);
													Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
														openCategory(p, category, survival, selected_page, book);
													}, 1L);
												} else {
													research.unlock(p, false);
													Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
														openCategory(p, category, survival, selected_page, book);
													}, 103L);
												}
											}
										} else Messages.local.sendTranslation(p, "messages.not-enough-xp", true);
									}
								}
							});
						}
						else {
							texts.add(ChatColor.translateAlternateColorCodes('&', shorten("&a", StringUtils.formatItemName(item.getItem(), false))));
							
							StringBuilder tooltip = new StringBuilder();
							
							tooltip.append(StringUtils.formatItemName(item.getItem(), false));
							
							if (item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasLore()) {
								for (String line : item.getItem().getItemMeta().getLore()) {
									tooltip.append("\n" + line);
								}
							}
							
							tooltip.append(ChatColor.translateAlternateColorCodes('&', "\n\n&e&oCliquez pour plus d'informations"));
							
							tooltips.add(tooltip.toString());
							actions.add(new PlayerRunnable(2) {
								
								@Override
								public void run(Player p) {
									displayItem(p, item.getItem(), true, book, 0);
								}
							});
						}
					}
				}
				else {
					texts.add(ChatColor.translateAlternateColorCodes('&', shorten("&4", StringUtils.formatItemName(item.getItem(), false))));
					tooltips.add(ChatColor.translateAlternateColorCodes('&', "&cPas la permission !"));
					actions.add(null);
				}
			}
			
			for (int i = 0; i < texts.size(); i = i + 10) {
				TellRawMessage page = new TellRawMessage();
				page.addText(ChatColor.translateAlternateColorCodes('&', "&b&l- Guide Slimefun -\n\n"));
				for (int j = i; j < texts.size() && j < i + 10; j++) {
					page.addText(texts.get(j) + "\n");
					if (tooltips.get(j) != null) page.addHoverEvent(HoverAction.SHOW_TEXT, tooltips.get(j));
					if (actions.get(j) != null) page.addClickEvent(actions.get(j));
				}
				page.addText("\n");
				page.addText(ChatColor.translateAlternateColorCodes('&', "&6\u21E6 &lBack"));
				page.addHoverEvent(HoverAction.SHOW_TEXT, ChatColor.translateAlternateColorCodes('&', "&eCliquez pour retourner � l'aper�u des cat�gories"));
				page.addClickEvent(new PlayerRunnable(2) {
					@Override
					public void run(final Player p) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
							openMainMenu(p, survival, true, 1);
						}, 1L);
					}
				});
				pages.add(page);
			}
			
			new CustomBookOverlay("Guide Slimefun", "mrCookieSlime", pages.toArray(new TellRawMessage[pages.size()])).open(p);
		}
		else {
			final ChestMenu menu = new ChestMenu("Guide Slimefun");
			
			menu.setEmptySlotsClickable(false);
			menu.addMenuOpeningHandler(
				pl -> pl.playSound(pl.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 0.7F)
			);
			
			int index = 9;
			final int pages = (category.getItems().size() - 1) / category_size + 1;
			for (int i = 0; i < 4; i++) {
				menu.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
				menu.addMenuClickHandler(i,
					(pl, slot, item, action) -> false
				);
			}
			
			menu.addItem(4, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7\u21E6 Retour"));
			menu.addMenuClickHandler(4, (pl, slot, item, action) -> {
				openMainMenu(pl, survival, book, 1);
				return false;
			});
			
			for (int i = 5; i < 9; i++) {
				menu.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
				menu.addMenuClickHandler(i,
					(pl, slot, item, action) -> false
				);
			}
			
			for (int i = 45; i < 54; i++) {
				menu.addItem(i, new CustomItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
				menu.addMenuClickHandler(i,
					(pl, slot, item, action) -> false
				);
			}
			
			menu.addItem(46, new CustomItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE), "&r\u21E6 Page précédente", "", "&7(" + selected_page + " / " + pages + ")"));
			menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
				int next = selected_page - 1;
				if (next < 1) next = pages;
				if (next != selected_page) openCategory(pl, category, survival, next, book);
				return false;
			});
			
			menu.addItem(52, new CustomItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE), "&rPage suivante \u21E8", "", "&7(" + selected_page + " / " + pages + ")"));
			menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
				int next = selected_page + 1;
				if (next > pages) next = 1;
				if (next != selected_page) openCategory(pl, category, survival, next, book);
				return false;
			});
			
			int category_index = category_size * (selected_page - 1);
			for (int i = 0; i < category_size; i++) {
				int target = category_index + i;
				if (target >= category.getItems().size()) break;
				final SlimefunItem sfitem = category.getItems().get(target);
				if (Slimefun.isEnabled(p, sfitem, false)) {
					if (survival && !Slimefun.hasUnlocked(p, sfitem.getItem(), false) && sfitem.getResearch() != null) {
						if (Slimefun.hasPermission(p, sfitem, false)) {
						    final Research research = sfitem.getResearch();
							menu.addItem(index, new CustomItem(Material.BARRIER, "&r" + StringUtils.formatItemName(sfitem.getItem(), false), "&4&lBLOQUE", "", "&a> Cliquez pour d�bloquer", "", "&7Co�t: &b" + research.getCost() + " Niveaux"));
							menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
								if (!Research.isResearching(pl)) {
									if (research.canUnlock(pl)) {
										if (research.hasUnlocked(pl))
											openCategory(pl, category, true, selected_page, book);
										else {
											if (!(pl.getGameMode() == GameMode.CREATIVE && Research.creative_research)) {
												pl.setLevel(pl.getLevel() - research.getCost());
											}

											if (pl.getGameMode() == GameMode.CREATIVE) {
												research.unlock(pl, Research.creative_research);
												openCategory(pl, category, survival, selected_page, book);
											} else {
												research.unlock(pl, false);
												Bukkit.getScheduler().scheduleSyncDelayedTask(SlimefunStartup.instance, () -> {
													openCategory(pl, category, survival, selected_page, book);
												}, 103L);
											}
										}
									} else Messages.local.sendTranslation(pl, "messages.not-enough-xp", true);
								}
								return false;
							});
							index++;
						}
						else {
							menu.addItem(index, new CustomItem(Material.BARRIER, StringUtils.formatItemName(sfitem.getItem(), false), new String[] {"", "&rVous n'avez pas la permission", "&rd'acc�der � cet objet"}));
							menu.addMenuClickHandler(index,
								(pl, slot, item, action) -> false
							);
							index++;
						}
					}
					else {
						menu.addItem(index, sfitem.getItem());
						menu.addMenuClickHandler(index, (pl, slot, item, action) -> {
							if (survival) displayItem(pl, item, true, book, 0);
							else pl.getInventory().addItem(item);
							return false;
						});
						index++;
					}
				}
			}
			
			menu.open(p);
		}		

		if (survival) {
			addToHistory(p, category);
		}
	}

	public static void addToHistory(Player p, Object obj) {
		List<Object> list = new ArrayList<>();
		if (history.containsKey(p.getUniqueId())) list = history.get(p.getUniqueId());
		list.add(obj);
		history.put(p.getUniqueId(), list);
	}

	private static Object getLastEntry(Player p, boolean remove) {
		List<Object> list = new ArrayList<>();
		if (history.containsKey(p.getUniqueId())) list = history.get(p.getUniqueId());
		if (remove && list.size() >= 1) {
			Object obj = list.get(list.size() - 1);
			list.remove(obj);
		}
		if (list.isEmpty()) history.remove(p.getUniqueId());
		else history.put(p.getUniqueId(), list);
		return list.isEmpty() ? null: list.get(list.size() - 1);
	}

	public static void displayItem(Player p, final ItemStack item, boolean addToHistory, final boolean book, final int page) {
		if (item == null || item.getType() == Material.AIR) return;

		final SlimefunItem sfItem = SlimefunItem.getByItem(item);
		if (sfItem == null) {
			if (!all_recipes) return;
		}
		
		ItemStack[] recipe = new ItemStack[9];
		ItemStack recipeType = null;
		ItemStack recipeOutput = item;
		
		ChestMenu menu = new ChestMenu("Guide Slimefun");
		
		menu.setEmptySlotsClickable(false);
		menu.addMenuOpeningHandler(
			pl -> pl.playSound(pl.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7F, 0.7F)
		);

		if (sfItem != null) {
			recipe = sfItem.getRecipe();
			recipeType = sfItem.getRecipeType().toItem();
			recipeOutput = sfItem.getRecipeOutput() != null ? sfItem.getRecipeOutput(): sfItem.getItem();
		}
		else {
			List<Recipe> recipes = new ArrayList<Recipe>();
			Iterator<Recipe> iterator = Bukkit.recipeIterator();
			while (iterator.hasNext()) {
				Recipe r = iterator.next();
				if (SlimefunManager.isItemSimiliar(new CustomItem(r.getResult(), 1), item, true)) recipes.add(r);
			}
			
			if (recipes.isEmpty()) return;
			 Recipe r = recipes.get(page);
			 
			 if (recipes.size() > page + 1) {
				 menu.addItem(1, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7Suivant \u21E8", "", "&e&l! &rIl y a plusieurs recette pour cet objet"));
					menu.addMenuClickHandler(1, (pl, slot, itemstack, action) -> {
						displayItem(pl, itemstack, false, book, page + 1);
						return false;
					});
			 }
			 
			 if (r instanceof ShapedRecipe) {
					String[] shape = ((ShapedRecipe) r).getShape();
					for (int i = 0; i < shape.length; i++) {
			            for (int j = 0; j < shape[i].length(); j++) {
			            	recipe[i * 3 + j] = ((ShapedRecipe) r).getIngredientMap().get(shape[i].charAt(j));
			            }
			        }
					recipeType = RecipeType.SHAPED_RECIPE.toItem();
					recipeOutput = r.getResult();
				}
				else if (r instanceof ShapelessRecipe) {
			        List<ItemStack> ingredients = ((ShapelessRecipe) r).getIngredientList();
					for (int i = 0; i < ingredients.size(); i++) {
						recipe[i] = ingredients.get(i);
			        }
					recipeType = RecipeType.SHAPELESS_RECIPE.toItem();
					recipeOutput = r.getResult();
				}
				else if (r instanceof FurnaceRecipe) {
					recipe[4] = ((FurnaceRecipe) r).getInput();
					
					recipeType = RecipeType.FURNACE.toItem();
					recipeOutput = r.getResult();
				}
		}
		

		
		if (addToHistory) addToHistory(p, sfItem != null ? sfItem: item);
		
		if (history.containsKey(p.getUniqueId()) && history.get(p.getUniqueId()).size() > 1) {
			menu.addItem(0, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7\u21E6 Retour", "", "&rClic gauche: &7Retour � la page précédente", "&rShift + Clic gauche: &7Retour au menu principal"));
			menu.addMenuClickHandler(0, (pl, slot, itemstack, action) -> {
				if (action.isShiftClicked()) openMainMenu(p, true, book, 1);
				else {
					Object last = getLastEntry(pl, true);
					if (last instanceof Category) openCategory(pl, (Category) last, true, 1, book);
					else if (last instanceof SlimefunItem) displayItem(pl, ((SlimefunItem) last).getItem(), false, book, 0);
					else if (last instanceof GuideHandler) ((GuideHandler) last).run(pl, true, book);
					else displayItem(pl, (ItemStack) last, false, book, 0);
				}
				return false;
			});
		}
		else {
			menu.addItem(0, new CustomItem(new ItemStack(Material.ENCHANTED_BOOK), "&7\u21E6 Retour", "", "&rClic gauche: &7Retour au menu principal"));
			menu.addMenuClickHandler(0, (pl, slot, itemstack, action) -> {
				openMainMenu(pl, true, book, 1);
				return false;
			});
		}
		
		menu.addItem(3, Slimefun.hasUnlocked(p, recipe[0], false) ? recipe[0]: new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[0], false), "&4&lBLOQUE", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[0]), false) ? "&rDoit être débloqué quelque part" : "&rPas la permission"));
		menu.addMenuClickHandler(3, (pl, slot, itemstack, action) -> {
			displayItem(pl, itemstack, true, book, 0);
			return false;
		});
		
		menu.addItem(4, Slimefun.hasUnlocked(p, recipe[1], false) ? recipe[1]: new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[1], false), "&4&lBLOQUE", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[1]), false) ? "&rDoit être débloqué quelque part" : "&rPas la permission"));
		menu.addMenuClickHandler(4, (pl, slot, itemstack, action) -> {
			displayItem(pl, itemstack, true, book, 0);
			return false;
		});
		
		menu.addItem(5, Slimefun.hasUnlocked(p, recipe[2], false) ? recipe[2]: new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[2], false), "&4&lBLOQUE", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[2]), false) ? "&rDoit être débloqué quelque part" : "&rPas la permission"));
		menu.addMenuClickHandler(5, (pl, slot, itemstack, action) -> {
			displayItem(pl, itemstack, true, book, 0);
			return false;
		});
		
		if (sfItem != null) {
			if (Slimefun.getItemConfig().contains(sfItem.getID() + ".wiki")) {
				try {
					menu.addItem(8, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY2OTJmOTljYzZkNzgyNDIzMDQxMTA1NTM1ODk0ODQyOThiMmU0YTAyMzNiNzY3NTNmODg4ZTIwN2VmNSJ9fX0="), "&rVoir cet objet sur le wiki &7(Slimefun Wiki)", "", "&7\u21E8 Cliquez pour ouvrir"));
					menu.addMenuClickHandler(8, (pl, slot, itemstack, action) -> {
						pl.closeInventory();
						pl.sendMessage("");
						pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&o" + Slimefun.getItemConfig().getString(sfItem.getID() + ".wiki")));
						pl.sendMessage("");
						return false;
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (Slimefun.getItemConfig().contains(sfItem.getID() + ".youtube")) {
				try {
					menu.addItem(7, new CustomItem(CustomSkull.getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQzNTNmZDBmODYzMTQzNTM4NzY1ODYwNzViOWJkZjBjNDg0YWFiMDMzMWI4NzJkZjExYmQ1NjRmY2IwMjllZCJ9fX0="), "&rDémonstration Video &7(Youtube)", "", "&7\u21E8 Cliquez pour regarder"));
					menu.addMenuClickHandler(7, (pl, slot, itemstack, action) -> {
						pl.closeInventory();
						pl.sendMessage("");
						pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&o" + Slimefun.getItemConfig().getString(sfItem.getID() + ".youtube")));
						pl.sendMessage("");
						return false;
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		menu.addItem(10, recipeType);
		menu.addMenuClickHandler(10,
			(pl, slot, itemstack, action) -> false
		);
		
		menu.addItem(12, Slimefun.hasUnlocked(p, recipe[3], false) ? recipe[3]: new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[3], false), "&4&lBLOQUE", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[3]), false) ? "&rDoit être débloqué quelque part" : "&rPas la permission"));
		menu.addMenuClickHandler(12, (pl, slot, itemstack, action) -> {
			displayItem(pl, itemstack, true, book, 0);
			return false;
		});
		
		menu.addItem(13, Slimefun.hasUnlocked(p, recipe[4], false) ? recipe[4]: new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[4], false), "&4&lBLOQUE", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[4]), false) ? "&rDoit être débloqué quelque part" : "&rPas la permission"));
		menu.addMenuClickHandler(13, (pl, slot, itemstack, action) -> {
			displayItem(pl, itemstack, true, book, 0);
			return false;
		});
		
		menu.addItem(14, Slimefun.hasUnlocked(p, recipe[5], false) ? recipe[5]: new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[5], false), "&4&lBLOQUE", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[5]), false) ? "&rDoit être débloqué quelque part" : "&rPas la permission"));
		menu.addMenuClickHandler(14, (pl, slot, itemstack, action) -> {
			displayItem(pl, itemstack, true, book, 0);
			return false;
		});
		
		menu.addItem(16, recipeOutput);
		menu.addMenuClickHandler(16,
			(pl, slot, itemstack, action) -> false
		);
		
		menu.addItem(21, Slimefun.hasUnlocked(p, recipe[6], false) ? recipe[6]: new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[6], false), "&4&lBLOQUE", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[6]), false) ? "&rDoit être débloqué quelque part" : "&rPas la permission"));
		menu.addMenuClickHandler(21, (pl, slot, itemstack, action) -> {
			displayItem(pl, itemstack, true, book, 0);
			return false;
		});
		
		menu.addItem(22, Slimefun.hasUnlocked(p, recipe[7], false) ? recipe[7]: new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[7], false), "&4&lBLOQUE", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[7]), false) ? "&rDoit être débloqué quelque part" : "&rPas la permission"));
		menu.addMenuClickHandler(22, (pl, slot, itemstack, action) -> {
			displayItem(pl, itemstack, true, book, 0);
			return false;
		});
		
		menu.addItem(23, Slimefun.hasUnlocked(p, recipe[8], false) ? recipe[8]: new CustomItem(Material.BARRIER, StringUtils.formatItemName(recipe[8], false), "&4&lBLOQUE", "", Slimefun.hasPermission(p, SlimefunItem.getByItem(recipe[8]), false) ? "&rDoit être débloqué quelque part" : "&rPas la permission"));
		menu.addMenuClickHandler(23, (pl, slot, itemstack, action) -> {
			displayItem(pl, itemstack, true, book, 0);
			return false;
		});
		
		if (sfItem != null) {
			if ((sfItem instanceof SlimefunMachine && ((SlimefunMachine) sfItem).getDisplayRecipes().size() > 0) || (sfItem instanceof SlimefunGadget && ((SlimefunGadget) sfItem).getRecipes().size() > 0)) {
				for (int i = 27; i < 36; i++) {
					menu.addItem(i, new CustomItem(Material.LIME_STAINED_GLASS_PANE, SlimefunItem.getByItem(item) instanceof SlimefunMachine ? "&7\u21E9 Recettes faites dans cette Machine \u21E9": " "));
					menu.addMenuClickHandler(i,
						(pl, slot, itemstack, action) -> false
					);
				}
				
				List<ItemStack> recipes = SlimefunItem.getByItem(item) instanceof SlimefunMachine ? ((SlimefunMachine) SlimefunItem.getByItem(item)).getDisplayRecipes() : ((SlimefunGadget) SlimefunItem.getByItem(item)).getDisplayRecipes();
				int recipe_size = recipes.size();
				if (recipe_size > 18) recipe_size = 18;
				int inputs = -1, outputs = -1;
				
				for (int i = 0; i < recipe_size; i++) {
					int slot = 36;
					if (i % 2 == 1) {
						slot = slot + 9;
						outputs++;
					}
					else inputs++;
					
					int addition = (i % 2 == 0 ? inputs: outputs);
					
					menu.addItem(slot + addition, recipes.get(i));
					menu.addMenuClickHandler(slot + addition, (pl, slotn, itemstack, action) -> {
						displayItem(pl, itemstack, true, book, 0);
						return false;
					});
				}
			}
			else if (sfItem instanceof AGenerator) {
				int slot = 27;
				for (MachineFuel fuel: ((AGenerator) sfItem).getFuelTypes()) {
					if (slot >= 54) break;
					ItemStack fItem = fuel.getInput().clone();
					ItemMeta im = fItem.getItemMeta();
					List<String> lore = new ArrayList<String>();
					lore.add(ChatColor.translateAlternateColorCodes('&', "&8\u21E8 &7Dure " + getTimeLeft(fuel.getTicks() / 2)));
					lore.add(ChatColor.translateAlternateColorCodes('&', "&8\u21E8 &e\u26A1 &7" + (((AGenerator) sfItem).getEnergyProduction() * 2) + " J/s"));
					lore.add(ChatColor.translateAlternateColorCodes('&', "&8\u21E8 &e\u26A1 &7" + DoubleHandler.getFancyDouble(fuel.getTicks() * ((AGenerator) sfItem).getEnergyProduction()) + " J au total"));
					im.setLore(lore);
					fItem.setItemMeta(im);
					menu.addItem(slot, fItem);
					menu.addMenuClickHandler(slot,
						(pl, slotn, itemstack, action) -> false
					);
					slot++;
				}
			}
			else if (sfItem instanceof AReactor) {
				int slot = 27;
				for (MachineFuel fuel: ((AReactor) sfItem).getFuelTypes()) {
					if (slot >= 54) break;
					ItemStack fItem = fuel.getInput().clone();
					ItemMeta im = fItem.getItemMeta();
					List<String> lore = new ArrayList<String>();
					lore.add(ChatColor.translateAlternateColorCodes('&', "&8\u21E8 &7Dure " + getTimeLeft(fuel.getTicks() / 2)));
					lore.add(ChatColor.translateAlternateColorCodes('&', "&8\u21E8 &e\u26A1 &7" + (((AReactor) sfItem).getEnergyProduction() * 2) + " J/s"));
					lore.add(ChatColor.translateAlternateColorCodes('&', "&8\u21E8 &e\u26A1 &7" + DoubleHandler.getFancyDouble(fuel.getTicks() * ((AReactor) sfItem).getEnergyProduction()) + " J au total"));
					im.setLore(lore);
					fItem.setItemMeta(im);
					menu.addItem(slot, fItem);
					menu.addMenuClickHandler(slot, (pl, slotn, itemstack, action) -> {
						return false;
					});
					slot++;
				}
			}
		}
		
		menu.open(p);
	}
	
	public static void clearHistory(UUID uuid) {
		history.remove(uuid);
	}
	
	private static String getTimeLeft(int l) {
		String timeleft = "";
        final int minutes = (int) (l / 60L);
        if (minutes > 0) {
            timeleft = String.valueOf(timeleft) + minutes + "m ";
        }
        l -= minutes * 60;
        final int seconds = (int)l;
        timeleft = String.valueOf(timeleft) + seconds + "s";
        return "&7" + timeleft;
	}

}
