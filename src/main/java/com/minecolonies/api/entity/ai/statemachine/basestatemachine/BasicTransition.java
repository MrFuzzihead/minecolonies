package com.minecolonies.api.entity.ai.statemachine.basestatemachine;

import com.minecolonies.api.entity.ai.statemachine.states.IState;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.IBooleanConditionSupplier;
import com.minecolonies.api.entity.ai.statemachine.tickratestatemachine.IStateSupplier;
import com.minecolonies.api.entity.ai.statemachine.transitions.IStateMachineTransition;
import com.minecolonies.api.util.Log;
// [1.7.10] EnumChatFormatting / IChatComponent removed; name is plain String
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * Basic Transition class for statemachines. Consists of a state the transition applies in, a statesupplier which determines its next state and a condition which has to be true to
 * transition into the next state.
 */
public class BasicTransition<S extends IState> implements IStateMachineTransition<S>
{
    /**
     * The State we're starting in
     */
    @Nullable
    private final S state;

    /**
     * The condition which needs to be met to transition
     */
    @NotNull
    private final IBooleanConditionSupplier condition;

    /**
     * The next state we transition into
     */
    @NotNull
    private final IStateSupplier<S> nextState;

    /**
     * The name of the transition
     */
    private String name;

    /**
     * Creating a new transition from State A to B under condition C
     *
     * @param state     State A
     * @param condition Condition C
     * @param nextState State B
     */
    public BasicTransition(@NotNull final S state, @NotNull final IBooleanConditionSupplier condition, @NotNull final IStateSupplier<S> nextState)
    {
        this.state = state;
        this.condition = condition;
        this.nextState = nextState;

        // [1.7.10] Component.literal/withStyle not available; use plain string
        name = state.toString() + ":" + getMethodName(condition) + ":" + getMethodName(nextState);
    }

    /**
     * Protected Constructor to allow subclasses without a state
     *
     * @param condition the condition.
     * @param nextState the next state to go to.
     */
    protected BasicTransition(@NotNull final IBooleanConditionSupplier condition, @NotNull final IStateSupplier<S> nextState)
    {
        this.state = null;
        this.condition = condition;
        this.nextState = nextState;

        // [1.7.10] Component.literal/withStyle not available; use plain string
        name = getClass().getSimpleName() + ":" + getMethodName(condition) + ":" + getMethodName(nextState);
    }

    /**
     * Returns the state to apply this transition in
     *
     * @return IAIState
     */
    @Override
    public S getState()
    {
        return state;
    }

    /**
     * Calculate the next state to go into
     *
     * @return next AI state
     */
    @Override
    public S getNextState()
    {
        return nextState.get();
    }

    /**
     * Check if the condition of this transition applies
     */
    @Override
    public boolean checkCondition()
    {
        return condition.getAsBoolean();
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Reflection util for generating a name for the method refs used by transitions, until we properly name them all
     */
    public static String getMethodName(Serializable lambda)
    {
        try
        {
            // Reflectively access the writeReplace method
            Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(lambda);
            String name = serializedLambda.getImplMethodName(); // Method name, e.g., "structureStep"
            if (name.contains("lambda"))
            {
                return "()";
            }

            return name;
        }
        catch (Exception e)
        {
            Log.getLogger().warn("Failed to extract method name from lambda", e);
        }

        return "Unknown";
    }

    @Override
    public BasicTransition<S> withName(final String name)
    {
        this.name = name; // [1.7.10] name is plain String, no Component.literal
        return this;
    }
}


