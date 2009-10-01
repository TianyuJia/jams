/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jams.workspace;

import jams.runtime.JAMSRuntime;
import jams.workspace.stores.InputDataStore;
import jams.workspace.stores.OutputDataStore;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public interface Workspace extends Serializable {
    /**
     * Comment string used to mark dump files of input datastores
     */
    String DUMP_MARKER = "#JAMSdatadump";

    /**
     * Comment string used to mark dump files of input datastores
     */
    String OUTPUT_FILE_ENDING = ".dat";

    /**
     * Comment string used to mark dump files of input datastores
     */
    String SPREADSHEET_FILE_ENDING = ".sdat";

    /**
     * Checks if this workspace is valid
     * @param readonly If readonly is false, the workspace can be fixed (e.g.
     * missing directories will be created), otherwise not
     * @throws jams.workspace.JAMSWorkspace.InvalidWorkspaceException
     */
    void checkValidity(boolean readonly) throws InvalidWorkspaceException;

    /**
     * Closes the workspace, i.e. closes all datastores
     */
    void close();

    /**
     * Creates a string dump of an input datastore
     * @param dsTitle The name of the datastore to be dumped
     * @return The string representation of the datastore
     * @throws java.io.IOException
     */
    String dataStoreToString(String dsTitle) throws IOException;

    /**
     * Creates a string dump of an input datastore
     * @param store The datastore to be dumped
     * @return The string representation of the datastore
     * @throws java.io.IOException
     */
    String dataStoreToString(InputDataStore store) throws IOException;

    /**
     *
     * @return The classloader that this workspace uses
     */
    ClassLoader getClassLoader();

    /**
     * Get the IDs of all datastores of a given type
     * @param type The type to look for
     * @return A String array containg the datastores IDs
     */
    String[] getDataStoreIDs(String type);

    /**
     *
     * @return The workspace description
     */
    String getDescription();

    /**
     *
     * @return The directory of the workspace
     */
    File getDirectory();

    /**
     *
     * @param dsTitle The name of the datastore to be returned
     * @return An input datastore named by dsTitle
     */
    InputDataStore getInputDataStore(String dsTitle);

    /**
     *
     * @return A set of the names of all input datastores
     */
    Set<String> getInputDataStoreIDs();

    /**
     *
     * @return The input directory, i.e. the directory where the input
     * datastores are defined
     */
    File getInputDirectory();

    /**
     *
     * @return The directory where input datastore dump files are stored
     */
    File getLocalDumpDirectory();

    /**
     *
     * @return The directory where local input files are stored
     */
    File getLocalInputDirectory();

    /**
     *
     * @return The default model path of the workspace
     */
    String getModelFilename();

    /**
     *
     * @return All existing data output directories
     */
    File[] getOutputDataDirectories();

    /**
     *
     * @return The current data output directory
     */
    File getOutputDataDirectory();

    /**
     *
     * @param outputDataDirectory An output data directory
     * @return All existing data files from a given output data directory
     */
    File[] getOutputDataFiles(File outputDataDirectory);

    /**
     *
     * @return A set of the names of all output datastores
     */
    Set<String> getOutputDataStoreIDs();

    /**
     *
     * @param contextName The instance name of a context component
     * @return All output datastores defined for the given context
     */
    OutputDataStore[] getOutputDataStores(String contextName);

    /**
     *
     * @return The JAMSRuntime object that this workspace uses
     */
    JAMSRuntime getRuntime();

    /**
     *
     * @return The temp directory
     */
    File getTempDirectory();

    /**
     *
     * @return The workspace title
     */
    String getTitle();

    /**
     * Creates a file dump of an input datastore
     * @param store The datastore to be dumped
     * @throws java.io.IOException
     */
    void inputDataStoreToFile(InputDataStore store) throws IOException;

    /**
     * Creates file dumps of all input datastores
     * @throws java.io.IOException
     */
    void inputDataStoreToFile() throws IOException;

    /**
     *
     * @return If the data output directory will be overwritten or not
     */
    boolean isPersistent();

    /**
     * Loads the workspace config from the config file in the root of the
     * workspace directory
     */
    void loadConfig();

    /**
     * Removes a datastore from the list of datastores
     * @param store The datastore to be removed
     */
    void removeDataStore(InputDataStore store);

    /**
     * Saves the workspace config in the config file in the root of the
     * workspace directory
     */
    void saveConfig();

    /**
     * Sets the workspace description
     * @param description The description
     */
    void setDescription(String description);

    /**
     * Creates an individual class loader
     * @param libs Array of libs that the new classloader will be based on
     */
    void setLibs(String[] libs);

    /**
     * Sets the default model path of the workspace
     * @param path The path
     */
    void setModelFile(String path);

    /**
     * Defines if the data output directory is overwritten or not
     * @param persistent If persistent is true, a new data output directory will be created
     * for model output, otherwise output will be directed to standard data
     * output directory ("current")
     */
    void setPersistent(boolean persistent);

    /**
     * Sets the workspace title
     * @param title The title
     */
    void setTitle(String title);

}