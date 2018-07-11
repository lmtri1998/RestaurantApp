package model.interfaces;

/**
 * The Observer class.
 *
 * The Observer will listen to all its Observables and update it self according to the Observer who calls the update.
 * The Observable and the Observer interfaces is implements the Observer design pattern.
 * @see Observable
 */
public interface Observer {

    /**
     * Receives the update notice and update itself
     * @param observable the Observable that notifies this update
     */
    void update(Observable observable);
}
