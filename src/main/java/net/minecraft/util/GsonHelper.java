package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.Reader;

/** [1.7.10 bridge] GsonHelper - modern Minecraft utility */
public class GsonHelper
{
    public static <T> T fromJson(final Gson gson, final Reader reader, final Class<T> clazz)
    {
        return gson.fromJson(reader, clazz);
    }

    public static <T> T fromJson(final Gson gson, final String json, final Class<T> clazz)
    {
        return gson.fromJson(json, clazz);
    }
}

