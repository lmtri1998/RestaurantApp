package model.interfaces;

/**
 * The Observable interfaces.
 *
 * Classes who implement this interface would act as a Observable. It should be able to attach itself to the
 * observers and whenever an update occurs, the Observable should be able to notify all the Observers that this
 * Observable is attached on. Also, the Observable and the Observer interfaces is implements the Observer design
 * pattern.
 * @see Observer
 */
public interface Observable {

    /**
     * Attach this Observable to the target Observer.
     * @param observer the target Observer
     */
    void attachObserver(Observer observer);

    /**
     * Remove the target Observer from the list.
     * @param observer the target Observer
     */
    void removeObserver(Observer observer);

    /**
     * Update all the Observers that is attached to this Observable
     */
    void update();
}
