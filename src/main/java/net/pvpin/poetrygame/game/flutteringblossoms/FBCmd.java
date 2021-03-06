package net.pvpin.poetrygame.game.flutteringblossoms;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.pvpin.poetrygame.api.Main;
import net.pvpin.poetrygame.api.events.common.AsyncPlayerTipEvent;
import net.pvpin.poetrygame.api.utils.BroadcastUtils;
import net.pvpin.poetrygame.api.utils.Constants;
import net.pvpin.poetrygame.api.poetry.PoetryUtils;
import net.pvpin.poetrygame.game.GameManager;
import net.pvpin.poetrygame.game.GameType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author William_Shi
 */
public class FBCmd implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "join": {
                    BroadcastUtils.send(
                            Constants.PREFIX + "請尋飛花雅集之所。戴侍中之席。",
                            ((Player) sender).getUniqueId()
                    );
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(Main.class), () -> {
                        boolean result = GameManager.join(((Player) sender).getUniqueId(), GameType.FLUTTERING_BLOSSOMS);
                        if (result) {
                            // ...
                        } else {
                            BroadcastUtils.send(
                                    Constants.PREFIX + "尋之不得。足下少安毋躁。",
                                    ((Player) sender).getUniqueId()
                            );
                        }
                    }, 3L);
                    break;
                }
                case "quit": {
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(Main.class), () -> {
                        boolean result = GameManager.quit(((Player) sender).getUniqueId(), GameType.FLUTTERING_BLOSSOMS);
                        if (result) {
                            BroadcastUtils.send(
                                    Constants.PREFIX + "足下離席矣。",
                                    ((Player) sender).getUniqueId()
                            );
                        } else {
                            BroadcastUtils.send(
                                    Constants.PREFIX + "雖慾離席。未之能也。",
                                    ((Player) sender).getUniqueId()
                            );
                        }
                    });
                    break;
                }
                case "tip": {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(Main.class), () -> {
                        FBGame game = (FBGame) GameManager.getGame(((Player) sender).getUniqueId(), GameType.FLUTTERING_BLOSSOMS);
                        if (game == null) {
                            BroadcastUtils.send(
                                    Constants.PREFIX + "足下尚未入席。",
                                    ((Player) sender).getUniqueId()
                            );
                        }
                        if (game.getStatus() != 1) {
                            BroadcastUtils.send(
                                    Constants.PREFIX + "無從提示足下也。",
                                    ((Player) sender).getUniqueId()
                            );
                        }
                        String keyWord = game.task.keyWord;
                        Map<String, UUID> map = PoetryUtils.PresetManager.PRESETS_CACHE.get(keyWord);
                        List<String> available = new ArrayList<>(map.keySet());
                        String tip = available.get(ThreadLocalRandom.current().nextInt(available.size()));
                        BaseComponent component = new TextComponent(Constants.PREFIX);
                        TextComponent tipComponent = new TextComponent(tip);
                        tipComponent.setUnderlined(true);
                        tipComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "行令 " + tip));
                        component.addExtra(tipComponent);
                        AsyncPlayerTipEvent event = new AsyncPlayerTipEvent(game, (Player) sender, component);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            component = event.getTip();
                            BroadcastUtils.send(component, ((Player) sender).getUniqueId());
                        }
                    }, 1L);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return List.of("join", "quit", "tip");
    }
}
