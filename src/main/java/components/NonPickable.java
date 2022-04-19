package components;

public class NonPickable extends Component{
    // This class does nothing  on its own
    // It's only use is to be attached to gameobjects
    // For example, the translate gizmo has this component attached to it,
    // So if the translate gizmo won't try to create another translate gizmo
    // That attaches to itself
    // Since it checks if the nonpickable component is attached before attempting to do so
}
