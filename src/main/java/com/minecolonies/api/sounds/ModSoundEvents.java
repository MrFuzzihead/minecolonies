package com.minecolonies.api.sounds;

import com.minecolonies.api.colony.jobs.ModJobs;
import com.minecolonies.api.entity.mobs.RaiderType;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.api.util.constant.Constants;
// [1.7.10] Registries removed
import net.minecraft.util.ResourceLocation;
import net.minecraft.sounds.SoundEvent; // [1.7.10] SoundEvent shim
// [1.7.10] DeferredRegister removed

import java.util.*;

import static com.minecolonies.core.generation.SoundsJson.createSoundJson;

/**
 * Registering of sound events for our colony.
 * [1.7.10] SoundEvent is now a shim wrapping ResourceLocation; no DeferredRegister.
 */
public final class ModSoundEvents
{
    public static final String CITIZEN_SOUND_EVENT_PREFIX = "citizen.";

    // [1.7.10] No DeferredRegister; sounds referenced by string in sounds.json
    // public static final DeferredRegister<SoundEvent> SOUND_EVENTS = ...

    public static Map<String, Map<EventType, List<Tuple<SoundEvent, SoundEvent>>>> CITIZEN_SOUND_EVENTS = new HashMap<>();

    public static SoundEvent SAW;

    private ModSoundEvents() {}

    static
    {
        final List<ResourceLocation> mainTypes = new ArrayList<>(ModJobs.getJobs());
        mainTypes.remove(ModJobs.placeHolder.getKey());
        mainTypes.add(new ResourceLocation(Constants.MOD_ID, "unemployed"));
        mainTypes.add(new ResourceLocation(Constants.MOD_ID, "visitor"));

        for (final ResourceLocation job : mainTypes)
        {
            final Map<EventType, List<Tuple<SoundEvent, SoundEvent>>> map = new HashMap<>();
            for (final EventType event : EventType.values())
            {
                final List<Tuple<SoundEvent, SoundEvent>> individualSounds = new ArrayList<>();
                for (int i = 1; i <= 4; i++)
                {
                    final SoundEvent maleSoundEvent =
                      ModSoundEvents.getSoundID(CITIZEN_SOUND_EVENT_PREFIX + job.getResourcePath() + ".male" + i + "." + event.getId());
                    final SoundEvent femaleSoundEvent =
                      ModSoundEvents.getSoundID(CITIZEN_SOUND_EVENT_PREFIX + job.getResourcePath() + ".female" + i + "." + event.getId());

                    individualSounds.add(new Tuple<>(maleSoundEvent, femaleSoundEvent));
                }
                map.put(event, individualSounds);
            }
            CITIZEN_SOUND_EVENTS.put(job.getResourcePath(), map);
        }

        final Map<EventType, List<Tuple<SoundEvent, SoundEvent>>> childMap = new HashMap<>();
        for (final EventType event : EventType.values())
        {
            final List<Tuple<SoundEvent, SoundEvent>> individualSounds = new ArrayList<>();
            for (int i = 1; i <= 2; i++)
            {
                final SoundEvent maleSoundEvent =
                        ModSoundEvents.getSoundID(CITIZEN_SOUND_EVENT_PREFIX + "child.male" + i + "." + event.getId());
                final SoundEvent femaleSoundEvent =
                        ModSoundEvents.getSoundID(CITIZEN_SOUND_EVENT_PREFIX + "child.female" + i + "." + event.getId());

                individualSounds.add(new Tuple<>(maleSoundEvent, femaleSoundEvent));
                individualSounds.add(new Tuple<>(maleSoundEvent, femaleSoundEvent));
            }
            childMap.put(event, individualSounds);
        }
        CITIZEN_SOUND_EVENTS.put("child", childMap);

        for (final RaiderType raiderType : RaiderType.values())
        {
            final SoundEvent raiderHurt = ModSoundEvents.getSoundID("EntityCreature." + raiderType.name().toLowerCase(Locale.US) + ".hurt");
            final SoundEvent raiderDeath = ModSoundEvents.getSoundID("EntityCreature." + raiderType.name().toLowerCase(Locale.US) + ".death");
            final SoundEvent raiderSay = ModSoundEvents.getSoundID("EntityCreature." + raiderType.name().toLowerCase(Locale.US) + ".say");

            final Map<RaiderSounds.RaiderSoundTypes, SoundEvent> sounds = new HashMap<>();
            sounds.put(RaiderSounds.RaiderSoundTypes.HURT, raiderHurt);
            sounds.put(RaiderSounds.RaiderSoundTypes.DEATH, raiderDeath);
            sounds.put(RaiderSounds.RaiderSoundTypes.SAY, raiderSay);

            RaiderSounds.raiderSounds.put(raiderType, sounds);
        }

        SAW = ModSoundEvents.getSoundID("tile.sawmill.saw");
    }

    public static SoundEvent getSoundID(final String soundName)
    {
        return SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, soundName));
    }
}
