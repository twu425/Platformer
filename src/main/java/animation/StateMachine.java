package animation;

import animation.AnimationState;
import components.Component;
import components.SpriteRenderer;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StateMachine extends Component {
    // Private class which only StateMachine cares about
    // Holds functions that will only be used here
    private class StateTrigger {
        public String state;
        public String trigger;

        public StateTrigger() {}

        public StateTrigger(String state, String trigger) {
            this.state = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != StateTrigger.class) return false;
            StateTrigger t2 = (StateTrigger)o;
            return t2.trigger.equals(this.trigger) && t2.state.equals(this.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, trigger);
        }
    }

    // StateTrigger is the element containing the state and the trigger, the String is the state it ends in.
    // It is important to make the distinction between states and triggers as they are both Strings
    public HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
    private List<AnimationState> states = new ArrayList<>();
    private transient AnimationState currentState = null;
    private String defaultStateTitle = "";

   public void refreshTextures() {
       for (AnimationState state : states) {
           state.refreshTextures();
       }
   }

    /** Go from this state (from) to another (to) on this trigger (onTrigger)
     * Note that this is simply adding a trigger, not triggering the trigger. */
    public void addStateTrigger(String from, String to, String onTrigger) {
        // stateTransfers is a hashmap, so the StateTrigger and the state to is the key
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    /** Adds a new Animationstate to the list of states. Refer to the class Animationstate for it's parameters. */
    public void addState(AnimationState state) {
        this.states.add(state);
    }

    /** Activates the trigger inputted to change the animation.
     * I'm going to be honest I don't really know what's going on here. */
    public void trigger(String trigger) {
        // Loop through each StateTrigger we have in the hashmap stateTransfers
        for (StateTrigger stateTrigger : stateTransfers.keySet()) {
            // Look through the StateTrigger for two elements:
            // - The state in stateTrigger's title is the (AnimationState) current state's titles are the same
            // - The trigger inputted and the stateTrigger's trigger are the same
            if (stateTrigger.state.equals(currentState.getTitle()) && stateTrigger.trigger.equals(trigger)) {
                // Check to make sure the stateTrigger in the hashmap stateTransfers exists
                if (stateTransfers.get(stateTrigger) != null) {
                    int newStateIndex = 0;
                    int index = 0;
                    for (AnimationState s : states) {
                        if (s.getTitle().equals(stateTransfers.get(stateTrigger))) {
                            newStateIndex = index;
                            break;
                        }
                        index++;
                    }
                    if (newStateIndex > -1) {
                        currentState = states.get(newStateIndex);
                    }
                }
                return;
            }
        }
        System.out.println("Unable to find trigger '" + trigger + "'");
    }

    /** Sets the state the entity should have upon loading the scene */
    public void setDefaultState(String animationTitle) {
        for (AnimationState state : states) {
            if (state.getTitle().equals(animationTitle)) {
                defaultStateTitle = animationTitle;
                if (currentState == null) {
                    currentState = state;
                    return;
                }
            }
        }
        System.out.println("Unable to find default state '" + animationTitle + "'");
    }

    @Override
    public void start() {
        for (AnimationState state : states) {
            // Every time we switch scenes we go back to the defaultState
            if (state.getTitle().equals(defaultStateTitle)) {
                currentState = state;
                break;
            }
        }
    }

    @Override
    public void update(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
            }
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
            if (sprite != null) {
                sprite.setSprite(currentState.getCurrentSprite());
            }
        }
    }

    @Override
    public void imgui() {
        int index = 0;
        for (AnimationState state : states) {
            ImString title = new ImString(state.getTitle());
            ImGui.inputText("State: ", title);
            state.setTitle(title.get());

            ImBoolean doesLoop = new ImBoolean(state.getDoesLoop());
            ImGui.checkbox("Does Loop?: ", doesLoop);
            state.setLoop(doesLoop.get());

            for (Frame frame : state.getFrames()) {
                float[] tmp = new float[1];
                tmp[0] = frame.frameTime;
                ImGui.dragFloat("Frame(" + index + ") Time: ", tmp, 0.01f);
                frame.frameTime = tmp[0];
                index++;
            }
        }
    }
}