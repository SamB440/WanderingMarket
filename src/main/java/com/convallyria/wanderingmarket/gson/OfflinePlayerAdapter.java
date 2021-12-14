package com.convallyria.wanderingmarket.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Type;
import java.util.UUID;

public class OfflinePlayerAdapter implements JsonSerializer<OfflinePlayer>, JsonDeserializer<OfflinePlayer> {

    private final Gson gson;

    public OfflinePlayerAdapter() {
        this.gson = new GsonBuilder().create();
    }

    @Override
    public OfflinePlayer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        UUID uuid = UUID.fromString(jsonElement.getAsString());
        return Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    public JsonElement serialize(OfflinePlayer player, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(player.getUniqueId().toString());
    }
}