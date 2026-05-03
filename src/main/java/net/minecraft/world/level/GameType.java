package net.minecraft.world.level;
/** [1.7.10 stub] GameType */
public enum GameType { SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR; public static GameType byId(int id) { return values()[id % 4]; } }