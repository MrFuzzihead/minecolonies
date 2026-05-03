package com.minecolonies.api.quests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minecolonies.api.IMinecoloniesAPI;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.minecolonies.api.quests.QuestParseConstant.*;

/**
 * Dialogue type of objective interface.
 */
public interface IDialogueObjectiveTemplate extends IQuestObjectiveTemplate
{

    /**
     * Getter for the dialogue tree.
     * @return the tree.
     */
    DialogueElement getDialogueTree();


    /**
     * A dialogue element in the dialogue objective.
     */
    class DialogueElement implements IQuestDialogueAnswer
    {
        /**
         * The text the participant says.
         */
        private final String text;

        /**
         * The EntityPlayer options.
         */
        private final List<AnswerElement> answers;

        /**
         * Create a new dialogue element.
         * @param text the participant.
         * @param answers the EntityPlayer answers.
         */
        public DialogueElement(final String text, final List<AnswerElement> answers)
        {
            this.text = text;
            this.answers = answers;
        }

        /**
         * Parse the element from json.
         * @param jsonObject the json to parse it from.
         * @return a new element.
         */
        public static DialogueElement parse(final JsonObject jsonObject)
        {
            final String text = String.translatable(jsonObject.get(TEXT_ID).getAsString());
            final List<AnswerElement> answerElementList = new ArrayList<>();
            for (final JsonElement answerOption : jsonObject.getAsJsonArray(OPTIONS_ID))
            {
                answerElementList.add(AnswerElement.parse(answerOption.getAsJsonObject()));
            }
            return new DialogueElement(text, answerElementList);
        }

        /**
         * Getter for the element text.
         * @return the text.
         */
        public String getText()
        {
            return this.text;
        }

        /**
         * Get all the response options.
         * @return the response option.
         */
        public List<String> getOptions()
        {
            return answers.stream().map(answerElement -> answerElement.text).collect(Collectors.toList());
        }

        /**
         * Get the matching answer result.
         * @param responseId the triggered response to match.
         * @return the next answer.
         */
        @Nullable
        public IQuestDialogueAnswer getOptionResult(final int responseId)
        {
            return responseId < answers.size() ? answers.get(responseId).answerResult : null;
        }
    }

    /**
     * An answer element part of a dialogue path.
     */
    class AnswerElement
    {
        /**
         * The text the EntityPlayer displays.
         */
        private final String text;

        /**
         * The result from the EntityPlayer answer.
         */
        private final IQuestDialogueAnswer answerResult;

        /**
         * Create a new answer element.
         * @param text the text for the player.
         * @param answerResult the result from the choice.
         */
        public AnswerElement(final String text, final IQuestDialogueAnswer answerResult)
        {
            this.text = text;
            this.answerResult = answerResult;
        }

        /**
         * Parse the answer element from json.
         * @param jsonObject the json obj.
         * @return the answer element.
         */
        public static AnswerElement parse(final JsonObject jsonObject)
        {
            final JsonObject resultObj = jsonObject.getAsJsonObject(RESULT_ID);
            return new AnswerElement(String.translatable(jsonObject.get(ANSWER_ID).getAsString()), IMinecoloniesAPI.getInstance().getQuestDialogueAnswerRegistry().getValue(new ResourceLocation(resultObj.get(TYPE_ID).getAsString())).produce(resultObj));
        }
    }
}


