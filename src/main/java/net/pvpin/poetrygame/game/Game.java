package net.pvpin.poetrygame.game;

import net.pvpin.poetrygame.api.utils.BroadcastUtils;
import net.pvpin.poetrygame.api.utils.Constants;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author William_Shi
 */
public abstract class Game {
    public final GameType type;
    protected final List<UUID> players;
    protected final long createTime;
    public final UUID gameID;
    protected final AtomicInteger status;
    // 0:preparing 1:playing 2:ended

    public Game(GameType type) {
        this.type = type;
        this.players = new ArrayList<>(16);
        this.createTime = System.currentTimeMillis();
        status = new AtomicInteger(0);
        gameID = UUID.randomUUID();
    }

    public synchronized void addOrRemovePlayer(UUID player) {
        if (players.contains(player)) {
            removePlayer(player);
        } else {
            addPlayer(player);
        }
    }

    private void addPlayer(UUID player) {
        if (players.contains(player)) {
            return;
        }
        BroadcastUtils.broadcast(
                Constants.PREFIX + Bukkit.getOfflinePlayer(player).getName() + " 入席。",
                players
        );
        if (players.isEmpty()) {
            BroadcastUtils.send(
                    Constants.PREFIX + "席間尚無他人。",
                    player
            );
        } else {
            StringBuilder builder = new StringBuilder(Constants.PREFIX);
            builder.append("席間列坐者：");
            players.forEach(uuid -> {
                String name = Bukkit.getOfflinePlayer(uuid).getName();
                builder.append(name);
                if (players.indexOf(uuid) < players.size() - 1) {
                    builder.append("、");
                }
            });
            BroadcastUtils.send(
                    builder.toString(),
                    player
            );
        }
        players.add(player);
    }

    private void removePlayer(UUID player) {
        if (!players.contains(player)) {
            return;
        }
        BroadcastUtils.broadcast(
                Constants.PREFIX + Bukkit.getOfflinePlayer(player).getName() + " 離席。",
                players
        );
        players.remove(player);
    }

    public int getStatus() {
        return this.status.intValue();
    }

    public List<UUID> getPlayers() {
        return List.copyOf(this.players);
    }

    public void start() {
        status.incrementAndGet();
    }

    public void end() {
        status.incrementAndGet();
    }
}