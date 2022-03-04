package com.convallyria.wanderingmarket.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.lucko.helper.serialize.Serializers;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

/**
 * We serialise ItemStacks to Base64.
 * Bukkit serialisation API does not serialise ItemStacks properly - a lot of the data is lost.
 * We need an exact copy otherwise we will have NBT issues.
 */
public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return Serializers.deserializeItemstack(jsonElement);
    }

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        return Serializers.serializeItemstack(itemStack);
    }
}