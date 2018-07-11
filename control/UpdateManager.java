package control;

import model.interfaces.Observable;

/**
 * The UpdateManager interface.
 *
 * This interface represents the update control part of the GUI. It is responsible for attaching all observables
 * to the observers by its type, which the update notification of each observables are passed to the correct observer.
 *
 * The GUI view parts should also implement a UpdateManager which will attach all object in the data base properly
 * to each different parts of views.
 */
public interface UpdateManager {

    /**
     * Attach the observable to the Observers depends on its type.
     * @param observable the observable
     */
    public void hookObserver(Observable observable);
}
