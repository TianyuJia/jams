/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jams;

import java.io.IOException;
import java.util.Observer;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public interface SystemProperties {
    /**
     * Identifier for libraries value
     */
    String LIBS_IDENTIFIER = "libs";

    /**
     * Identifier for model value
     */
    String MODEL_IDENTIFIER = "model";

    /**
     * Identifier for server account value
     */
    String SERVER_ACCOUNT_IDENTIFIER = "serveraccount";

    /**
     * Identifier for excludes value
     */
    String SERVER_EXCLUDES_IDENTIFIER = "excludes";

    /**
     * Identifier for server value
     */
    String SERVER_IDENTIFIER = "server";

    /**
     * Identifier for server password value
     */
    String SERVER_PASSWORD_IDENTIFIER = "serverpassword";

    /**
     * Identifier for workspace value
     */
    String WORKSPACE_IDENTIFIER = "workspace";

    /**
     * Adds an observer for some property
     * @param key The identifier for the property
     * @param obs The java.util.Observer object
     */
    void addObserver(String key, Observer obs);

    /**
     *
     * @return The default file name for storing JAMS properties
     */
    String getDefaultFilename();

    /**
     * Gets a property value
     * @param key The identifier for the property
     * @return The property value
     */
    String getProperty(String key);

    /**
     * Gets a property value or default value if property does not exist
     * @param key The identifier for the property
     * @param defaultValue The default value
     * @return The property value
     */
    String getProperty(String key, String defaultValue);

    /**
     * Loads properties from a file
     * @param fileName The name of the file to read properties from
     * @throws java.io.IOException
     */
    void load(String fileName) throws IOException;

    /**
     * Saves properties to a file
     * @param fileName The name of the file to save properties to
     * @throws java.io.IOException
     */
    void save(String fileName) throws IOException;

    /**
     * Set the default file name for storing JAMS properties
     * @param defaultFilename The default file name
     */
    void setDefaultFilename(String defaultFilename);

    /**
     * Sets a property value
     * @param key The identifier for the property
     * @param value The value of the property
     */
    void setProperty(String key, String value);

    String toString();

}