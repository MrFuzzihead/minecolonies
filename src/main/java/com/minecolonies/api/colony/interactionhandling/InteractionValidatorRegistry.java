package com.minecolonies.api.colony.interactionhandling;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.requestsystem.token.IToken;
// [1.7.10] int[] -> int x,y,z
import net.minecraft.util.IChatComponent;
// [1.7.10] chat.String replaced by IChatComponent/ChatComponentText

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Utility class to store all validator predicates for the chat handling.
 */
public final class InteractionValidatorRegistry
{
    /**
     * Map of all validator predicates.
     */
    private static Map<String, Predicate<ICitizenData>> map = new HashMap<>();

    /**
     * Map of all pos based validator predicates.
     */
    private static Map<String, BiPredicate<ICitizenData, int[]>> posMap = new HashMap<>();

    /**
     * Map of all IToken based validator predicates.
     */
    private static Map<String, BiPredicate<ICitizenData, IToken<?>>> tokenMap = new HashMap<>();

    /**
     * Get the StandardInteractionValidatorPredicate.
     *
     * @param key the key of it.
     * @return the predicate.
     */
    public static Predicate<ICitizenData> getStandardInteractionValidatorPredicate(final String key)
    {
        return map.get(key);
    }

    /**
     * Get the PosBasedInteractionValidatorPredicate.
     *
     * @param key the key of it.
     * @return the predicate.
     */
    public static BiPredicate<ICitizenData, int[]> getPosBasedInteractionValidatorPredicate(final String key)
    {
        return posMap.get(key);
    }

    /**
     * Get the PosBasedInteractionValidatorPredicate.
     *
     * @param key the key of it.
     * @return the predicate.
     */
    public static BiPredicate<ICitizenData, IToken<?>> getTokenBasedInteractionValidatorPredicate(final String key)
    {
        return tokenMap.get(key);
    }

    /**
     * Add a new StandardInteractionValidatorPredicate.
     *
     * @param key       it's key.
     * @param predicate it's predicate.
     */
    public static void registerStandardPredicate(final String key, final Predicate<ICitizenData> predicate)
    {
        map.put(key, predicate);
    }

    /**
     * Add a new PosBasedInteractionValidatorPredicate.
     *
     * @param key       it's key.
     * @param predicate it's predicate.
     */
    public static void registerPosBasedPredicate(final String key, final BiPredicate<ICitizenData, int[]> predicate)
    {
        posMap.put(key, predicate);
    }

    /**
     * Add a new TokenBasedInteractionValidatorPredicate.
     *
     * @param key       it's key.
     * @param predicate it's predicate.
     */
    public static void registerTokenBasedPredicate(final String key, final BiPredicate<ICitizenData, IToken<?>> predicate)
    {
        tokenMap.put(key, predicate);
    }

    /**
     * Check if there is a validator with a certain key.
     *
     * @param String the key to check.
     * @return true if so.
     */
    public static boolean hasValidator(final String String)
    {
        return map.containsKey(String) || posMap.containsKey(String) || tokenMap.containsKey(String);
    }

    /**
     * Private constructor to hide public one.
     */
    private InteractionValidatorRegistry()
    {
        /*
         * Intentionally left empty.
         */
    }
}



