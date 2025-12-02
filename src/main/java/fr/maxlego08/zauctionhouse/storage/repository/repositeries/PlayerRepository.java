package fr.maxlego08.zauctionhouse.storage.repository.repositeries;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.zauctionhouse.api.AuctionPlugin;
import fr.maxlego08.zauctionhouse.api.storage.Repository;
import fr.maxlego08.zauctionhouse.api.storage.Tables;
import fr.maxlego08.zauctionhouse.api.storage.dto.PlayerDTO;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlayerRepository extends Repository {

    public PlayerRepository(AuctionPlugin plugin, DatabaseConnection connection) {
        super(plugin, connection, Tables.PLAYERS);
    }

    public void upsertPlayer(Player player) {
        this.upsert(schema -> {
            schema.uuid("unique_id", player.getUniqueId()).primary();
            schema.string("name", player.getName());
        });
    }

    public List<PlayerDTO> select() {
        return selectAll(PlayerDTO.class);
    }

    public String select(UUID uniqueId) {
        return select(PlayerDTO.class, schema -> schema.where("unique_id", uniqueId.toString())).stream().findFirst().map(PlayerDTO::name).orElse(null);
    }
}
