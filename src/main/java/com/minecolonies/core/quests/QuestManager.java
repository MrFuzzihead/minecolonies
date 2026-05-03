package com.minecolonies.core.quests;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.quests.FinishedQuest;
import com.minecolonies.api.quests.IQuestInstance;
import com.minecolonies.api.quests.IQuestManager;
import com.minecolonies.api.quests.IQuestTemplate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

import static com.minecolonies.api.util.constant.NbtTagConstants.*;

/**
 * Quest manager of each colony.
 */
public class QuestManager implements IQuestManager
{
    /**
     * All quests that have been unlocked.
     */
    private final Map<ResourceLocation, IQuestInstance> availableQuests = new HashMap<>();

    /**
     * All quests that have been finished. And how often.
     */
    private final Map<ResourceLocation, Integer> finishedQuests = new HashMap<>();

    /**
     * All quests in progress.
     */
    private final Map<ResourceLocation, IQuestInstance> inProgressQuests = new HashMap<>();

    /**
     * Unlocked quest requirements.
     */
    private final List<ResourceLocation> unlockedQuests = new ArrayList<>();

    /**
     * Cached mapped results for the finished quests.
     */
    private List<FinishedQuest> finishedQuestsCache = null;

    /**
     * Quest reputation.
     */
    private double questReputation = 0;

    /**
     * This manager's colony
     */
    private final IColony colony;

    /**
     * Tracks if data needs to be sent
     */
    private boolean isDirty = true;

    public QuestManager(final IColony colony)
    {
        this.colony = colony;
    }

    @Override
    public boolean attemptAcceptQuest(final ResourceLocation questID, final Player player)
    {
        final IQuestInstance quest = availableQuests.getOrDefault(questID, null);
        if (quest == null || !quest.isValid(colony))
        {
            return false;
        }
        this.inProgressQuests.put(questID, quest);
        this.availableQuests.remove(questID);
        markDirty();
        return true;
    }

    @Override
    public void alterReputation(final double difference)
    {
        this.questReputation += difference;
        markDirty();
    }

    @Override
    public double getReputation()
    {
        return this.questReputation;
    }

    @Override
    public void completeQuest(final ResourceLocation questId)
    {
        if (inProgressQuests.containsKey(questId))
        {
            inProgressQuests.remove(questId);
            finishedQuests.put(questId, finishedQuests.getOrDefault(questId, 0) + 1);
            markDirty();
        }
        else if (availableQuests.containsKey(questId))
        {
            // When a player short-cut quits a job without accepting it. (E.g. been there, done that options).
            availableQuests.remove(questId);
            finishedQuests.put(questId, finishedQuests.getOrDefault(questId, 0) + 1);
            markDirty();
        }

        finishedQuestsCache = null;
    }

    @Override
    public void onColonyTick()
    {
        for (final Map.Entry<ResourceLocation, IQuestTemplate> quest : GLOBAL_SERVER_QUESTS.entrySet())
        {
            if (availableQuests.containsKey(quest.getKey())
                  || inProgressQuests.containsKey(quest.getKey())
                  || finishedQuests.getOrDefault(quest.getKey(), 0) >= quest.getValue().getMaxOccurrence())
            {
                continue;
            }

            boolean missingParent = false;
            for (final ResourceLocation parent: quest.getValue().getParents())
            {
                if (!finishedQuests.containsKey(parent))
                {
                    missingParent = true;
                    break;
                }
            }

            if (missingParent)
            {
                continue;
            }

            final IQuestInstance colonyQuest = quest.getValue().attemptStart(colony);
            if (colonyQuest != null)
            {
                this.availableQuests.put(quest.getKey(), colonyQuest);
                markDirty();
            }
        }

        for (final Map.Entry<ResourceLocation, IQuestInstance> availableQuest : new ArrayList<>(availableQuests.entrySet()))
        {
            if (!GLOBAL_SERVER_QUESTS.containsKey(availableQuest.getKey()) || !availableQuest.getValue().isValid(colony))
            {
                availableQuest.getValue().onDeletion();
                this.availableQuests.remove(availableQuest.getKey());
                markDirty();
            }
        }

        for (final Map.Entry<ResourceLocation, IQuestInstance> inProgressQuest : new ArrayList<>(inProgressQuests.entrySet()))
        {
            if (!GLOBAL_SERVER_QUESTS.containsKey(inProgressQuest.getKey()) || !inProgressQuest.getValue().isValid(colony))
            {
                inProgressQuest.getValue().onDeletion();
                this.inProgressQuests.remove(inProgressQuest.getKey());
                markDirty();
            }
        }
    }

    @Override
    public void deleteQuest(final ResourceLocation questID)
    {
        this.availableQuests.remove(questID);
        this.inProgressQuests.remove(questID);
        markDirty();
    }

    @Override
    public IQuestInstance getAvailableOrInProgressQuest(final ResourceLocation questId)
    {
        return availableQuests.containsKey(questId) ? availableQuests.get(questId) : inProgressQuests.get(questId);
    }

    @Override
    public void onWorldLoad()
    {
        for (final IQuestInstance colonyQuest : inProgressQuests.values())
        {
            colonyQuest.onWorldLoad();
        }
    }

    @Override
    public void unlockQuest(final ResourceLocation questId)
    {
        this.unlockedQuests.add(questId);
        markDirty();
    }

    @Override
    public boolean isUnlocked(final ResourceLocation questId)
    {
        return this.unlockedQuests.contains(questId);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        final NBTTagCompound managerCompound = new NBTTagCompound();

        final NBTTagList availableListTag = new NBTTagList();
        for (final Map.Entry<ResourceLocation, IQuestInstance> available : availableQuests.entrySet())
        {
            availableListTag.add(available.getValue().serializeNBT());
        }
        managerCompound.put(TAG_AVAILABLE, availableListTag);

        final NBTTagList inProgressListTag = new NBTTagList();
        for (final Map.Entry<ResourceLocation, IQuestInstance> inProgress : inProgressQuests.entrySet())
        {
            inProgressListTag.add(inProgress.getValue().serializeNBT());
        }
        managerCompound.put(TAG_IN_PROGRESS, inProgressListTag);

        final NBTTagList finishedListTag = new NBTTagList();
        for (final Map.Entry<ResourceLocation, Integer> finished : finishedQuests.entrySet())
        {
            final NBTTagCompound finishedTag = new NBTTagCompound();
            finishedTag.putString(TAG_ID, finished.getKey().toString());
            finishedTag.putInt(TAG_QUANTITY, finished.getValue());
            finishedListTag.add(finishedTag);
        }
        managerCompound.put(TAG_FINISHED, finishedListTag);

        final NBTTagList unlockedListTag = new NBTTagList();
        for (final ResourceLocation unlocked : unlockedQuests)
        {
            final NBTTagCompound unlockedTag = new NBTTagCompound();
            unlockedTag.putString(TAG_ID, unlocked.toString());
            unlockedListTag.add(unlockedTag);
        }
        managerCompound.put(TAG_UNLOCKED, unlockedListTag);
        managerCompound.putDouble(TAG_REPUTATION, questReputation);

        return managerCompound;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt)
    {
        final Map<ResourceLocation, IQuestInstance> localAvailableQuests = new HashMap<>();
        final NBTTagList availableListTag = nbt.getList(TAG_AVAILABLE, NBTBase.TAG_COMPOUND);
        for (final NBTBase element : availableListTag)
        {
            final ResourceLocation key = new ResourceLocation(((NBTTagCompound) element).getString(TAG_ID));
            if (GLOBAL_SERVER_QUESTS.containsKey(key))
            {
                final IQuestInstance colonyQuest = availableQuests.containsKey(key) ? availableQuests.get(key) : new QuestInstance(colony);
                colonyQuest.deserializeNBT((NBTTagCompound) element);
                localAvailableQuests.put(colonyQuest.getId(), colonyQuest);
            }
        }

        this.availableQuests.clear();
        this.availableQuests.putAll(localAvailableQuests);

        final Map<ResourceLocation, IQuestInstance> localInProgressQuests = new HashMap<>();
        final NBTTagList inProgressListTag = nbt.getList(TAG_IN_PROGRESS, NBTBase.TAG_COMPOUND);
        for (final NBTBase element : inProgressListTag)
        {
            final ResourceLocation key = new ResourceLocation(((NBTTagCompound) element).getString(TAG_ID));
            if (GLOBAL_SERVER_QUESTS.containsKey(key))
            {
                final IQuestInstance colonyQuest = this.inProgressQuests.containsKey(key) ? this.inProgressQuests.get(key) : new QuestInstance(colony);
                colonyQuest.deserializeNBT((NBTTagCompound) element);
                localInProgressQuests.put(colonyQuest.getId(), colonyQuest);
            }
        }

        this.inProgressQuests.clear();
        this.inProgressQuests.putAll(localInProgressQuests);


        this.finishedQuests.clear();
        final NBTTagList finishedListTag = nbt.getList(TAG_FINISHED, NBTBase.TAG_COMPOUND);
        for (final NBTBase element : finishedListTag)
        {
            this.finishedQuests.put(new ResourceLocation(((NBTTagCompound) element).getString(TAG_ID)), ((NBTTagCompound) element).getInt(TAG_QUANTITY));
        }
        finishedQuestsCache = null;

        this.unlockedQuests.clear();
        final NBTTagList unlockedListTag = nbt.getList(TAG_UNLOCKED, NBTBase.TAG_COMPOUND);
        for (final NBTBase element : unlockedListTag)
        {
            this.unlockedQuests.add(new ResourceLocation(((NBTTagCompound) element).getString(TAG_ID)));
        }
        this.questReputation = nbt.getDouble(TAG_REPUTATION);
    }

    @Override
    public void serialize(final PacketBuffer buf, final boolean hasNewSubscribers)
    {
        buf.writeBoolean(isDirty || hasNewSubscribers);
        if (isDirty || hasNewSubscribers)
        {
            buf.writeNbt(serializeNBT());
        }
        isDirty = false;
    }

    @Override
    public void deserialize(final PacketBuffer buf)
    {
        final boolean hasData = buf.readBoolean();
        if (hasData)
        {
            deserializeNBT(buf.readNbt());
        }
    }

    @Override
    public void markDirty()
    {
        isDirty = true;
    }

    @Override
    public List<IQuestInstance> getAvailableQuests()
    {
        return new ArrayList<>(availableQuests.values());
    }

    @Override
    public List<IQuestInstance> getInProgressQuests()
    {
        return new ArrayList<>(inProgressQuests.values());
    }

    @Override
    public List<FinishedQuest> getFinishedQuests()
    {
        if (finishedQuestsCache == null)
        {
            List<FinishedQuest> data = new ArrayList<>();
            for (Map.Entry<ResourceLocation, Integer> entry : finishedQuests.entrySet())
            {
                IQuestTemplate template = GLOBAL_SERVER_QUESTS.get(entry.getKey());
                if (template != null)
                {
                    data.add(new FinishedQuest(template, entry.getValue()));
                }
            }
            finishedQuestsCache = Collections.unmodifiableList(data);
        }
        return finishedQuestsCache;
    }

    @Override
    public void injectAvailableQuest(final IQuestInstance questInstance)
    {
        this.availableQuests.put(questInstance.getId(), questInstance);
        markDirty();
    }
}




