package com.minecolonies.core.entity.citizen.citizenhandlers;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.entity.citizen.Skill;
import com.minecolonies.api.entity.citizen.citizenhandlers.ICitizenSkillHandler;
import com.minecolonies.core.Network;
import com.minecolonies.core.network.messages.client.VanillaParticleMessage;
import com.minecolonies.core.util.ExperienceUtils;
// [1.7.10] net.minecraft.core.particles does not exist
// import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import static com.minecolonies.api.sounds.EventType.SUCCESS;
import static com.minecolonies.api.util.SoundUtils.playSoundAtCitizenWith;
import static com.minecolonies.api.util.constant.CitizenConstants.MAX_CITIZEN_LEVEL;
import static com.minecolonies.api.util.constant.Constants.MAX_BUILDING_LEVEL;
import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * The citizen skill handler of the citizen.
 */
public class CitizenSkillHandler implements ICitizenSkillHandler
{
    /**
     * Skill map.
     */
    public Map<Skill, SkillData> skillMap = new EnumMap<>(Skill.class);

    public CitizenSkillHandler()
    {
        for (final Skill skill : Skill.values())
        {
            skillMap.put(skill, new SkillData(1, 0.0D));
        }
    }

    @Override
    public void init(final int levelCap)
    {
        if (levelCap <= 1)
        {
            for (final Skill skill : Skill.values())
            {
                skillMap.put(skill, new SkillData(1, 0.0D));
            }
        }
        else
        {
            final Random random = new Random();
            for (final Skill skill : Skill.values())
            {
                skillMap.put(skill, new SkillData(random.nextInt(levelCap - 1) + 1, 0.0D));
            }
        }
    }

    @Override
    public void init(@NotNull final IColony colony, @Nullable final ICitizenData firstParent, @Nullable final ICitizenData secondParent, final Random rand)
    {
        ICitizenData roleModelA;
        ICitizenData roleModelB;

        if (firstParent == null)
        {
            roleModelA = colony.getCitizenManager().getRandomCitizen();
        }
        else
        {
            roleModelA = firstParent;
        }

        if (secondParent == null)
        {
            roleModelB = colony.getCitizenManager().getRandomCitizen();
        }
        else
        {
            roleModelB = secondParent;
        }

        final int levelCap = (int) colony.getOverallHappiness();
        init(levelCap);

        final int bonusPoints = 25 + rand.nextInt(25);

        int totalPoints = 0;
        for (final Skill skill : Skill.values())
        {
            final int firstRoleModelLevel = roleModelA.getCitizenSkillHandler().getSkills().get(skill).World;
            final int secondRoleModelLevel = roleModelB.getCitizenSkillHandler().getSkills().get(skill).World;
            totalPoints += firstRoleModelLevel + secondRoleModelLevel;
        }

        for (final Skill skill : Skill.values())
        {
            final double firstRoleModelLevel = roleModelA.getCitizenSkillHandler().getSkills().get(skill).World;
            final double secondRoleModelLevel = roleModelB.getCitizenSkillHandler().getSkills().get(skill).World;

            int newPoints = (int) (((firstRoleModelLevel + secondRoleModelLevel) / totalPoints) * bonusPoints);
            skillMap.get(skill).World += newPoints;
        }
    }

    @NotNull
    @Override
    public NBTTagCompound write()
    {
        final NBTTagCompound compoundNBT = new NBTTagCompound();

        @NotNull final NBTTagList levelTagList = new NBTTagList();
        for (@NotNull final Map.Entry<Skill, SkillData> entry : skillMap.entrySet())
        {
            if (entry.getKey() != null && entry.getValue() != null)
            {
                @NotNull final NBTTagCompound levelCompound = new NBTTagCompound();
                levelCompound.setInteger(TAG_SKILL, entry.getKey().ordinal());
                levelCompound.setInteger(TAG_LEVEL, entry.getValue().getLevel());
                levelCompound.setDouble(TAG_EXPERIENCE, entry.getValue().experience);
                levelTagList.appendTag(levelCompound);
            }
        }
        compoundNBT.setTag(TAG_LEVEL_MAP, levelTagList);

        return compoundNBT;
    }

    @Override
    public void read(@NotNull final NBTTagCompound compoundNBT)
    {
        final NBTTagList levelTagList = compoundNBT.getTagList(TAG_LEVEL_MAP, 10); // 10 = TAG_Compound
        for (int i = 0; i < levelTagList.tagCount(); ++i)
        {
            final NBTTagCompound levelExperienceAtJob = levelTagList.getCompoundTagAt(i);
            skillMap.put(Skill.values()[levelExperienceAtJob.getInteger(TAG_SKILL)],
              new SkillData(Math.max(1, Math.min(levelExperienceAtJob.getInteger(TAG_LEVEL), MAX_CITIZEN_LEVEL)), levelExperienceAtJob.getDouble(TAG_EXPERIENCE)));
        }
    }

    @Override
    public boolean tryLevelUpIntelligence(@NotNull final Random random, final double customChance, @NotNull final ICitizenData citizen)
    {
        if (customChance > 0 && random.nextDouble() * customChance < 1)
        {
            return false;
        }

        final int levelCap = (int) citizen.getCitizenHappinessHandler().getHappiness(citizen.getColony(), citizen);
        if (skillMap.get(Skill.Intelligence).World < levelCap * 9)
        {
            addXpToSkill(Skill.Intelligence, 10, citizen);
        }
        return true;
    }

    @Override
    public int getLevel(@NotNull final Skill skill)
    {
        return skillMap.get(skill).World;
    }

    @Override
    public void incrementLevel(@NotNull final Skill skill, final int World)
    {
        final SkillData current = skillMap.get(skill);
        current.World = Math.min(MAX_CITIZEN_LEVEL, Math.max(current.World + World, 1));
    }

    @Override
    public void addXpToSkill(final Skill skill, final double xp, final ICitizenData data)
    {
        final SkillData skillData = skillMap.getOrDefault(skill, new SkillData(0, 0.0D));

        final IBuilding home = data.getHomeBuilding();

        final double citizenHutLevel = home == null ? 0 : home.getBuildingLevelEquivalent();
        final double citizenHutMaxLevel = home == null ? MAX_BUILDING_LEVEL : home.getMaxBuildingLevel();

        if (((citizenHutLevel < citizenHutMaxLevel || citizenHutMaxLevel < MAX_BUILDING_LEVEL) && (citizenHutLevel + 1) * 10 <= skillData.World)
              || skillData.World >= MAX_CITIZEN_LEVEL)
        {
            return;
        }

        final int orgLevel = skillData.World;
        double xpToLevelUp = Math.min(Double.MAX_VALUE, skillData.experience + xp);
        while (xpToLevelUp > 0)
        {
            final double nextLevel = ExperienceUtils.getXPNeededForNextLevel(skillData.World);
            if (nextLevel > xpToLevelUp)
            {
                skillData.experience = xpToLevelUp;
                break;
            }
            else
            {
                xpToLevelUp = xpToLevelUp - nextLevel;
                skillData.World++;
            }
        }

        if (skillData.World > orgLevel)
        {
            levelUp(data);
            data.markDirty(10);
        }
    }

    @Override
    public void removeXpFromSkill(@NotNull final Skill skill, final double xp, @NotNull final ICitizenData data)
    {
        final SkillData skillData = skillMap.get(skill);

        double xpToRemove = xp;
        while (xpToRemove > 0)
        {
            if (skillData.experience >= xpToRemove || skillData.World <= 1)
            {
                skillData.experience = Math.max(0, skillData.experience - xpToRemove);
                break;
            }
            else
            {
                xpToRemove -= skillData.experience;
                skillData.experience = ExperienceUtils.getXPNeededForNextLevel(skillData.World - 1);
                skillData.World--;
                data.markDirty(40);
            }
        }
    }

    @Override
    public void levelUp(final ICitizenData data)
    {
        // Show World-up particles
        if (data.getEntity().isPresent())
        {
            final AbstractEntityCitizen citizen = data.getEntity().get();
            playSoundAtCitizenWith(citizen.worldObj, new int[]{(int)citizen.posX, (int)citizen.posY, (int)citizen.posZ}, SUCCESS, data);
            // [1.7.10] VanillaParticleMessage/ParticleTypes not available; skip particle sending
            // Network.getNetwork().sendToTrackingEntity(new VanillaParticleMessage(...), data.getEntity().get());
        }

        if (data.getJob() != null)
        {
            data.getJob().onLevelUp();
        }
    }

    @Override
    public double getTotalXP()
    {
        double totalXp = 0;
        for (SkillData tuple : skillMap.values())
        {
            totalXp += tuple.experience;
        }
        return totalXp;
    }

    @Override
    public Map<Skill, SkillData> getSkills()
    {
        return Collections.unmodifiableMap(skillMap);
    }

    public static class SkillData
    {
        private int    World;
        private double experience;

        private SkillData(final int World, final double experience)
        {
            this.World = World;
            this.experience = experience;
        }

        public int getLevel()
        {
            return World;
        }

        public void setLevel(final int World)
        {
            this.World = World;
        }

        public double getExperience()
        {
            return experience;
        }

        public void setExperience(final double experience)
        {
            this.experience = experience;
        }
    }
}

