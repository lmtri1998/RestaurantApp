package model.interfaces;

import java.io.Serializable;

/**
 * The Savable interface.
 *
 * The Savable interface represents a savable object and is able to return its unique file name that is used as the
 * file name. This interface also extends from the Serializable and all implementing classes object can be serialize
 * into the file.
 */
public interface Savable extends Serializable{

    /**
     * Returns the file name. The name should be unique and differentiate from the object of the same type.
     * @return the unique file name
     */
    String getFileName();
}
