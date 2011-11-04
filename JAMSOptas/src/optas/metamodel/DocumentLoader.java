/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package optas.metamodel;

import jams.tools.XMLTools;
import jams.io.*;
import java.io.File;
import java.io.IOException;
import jams.data.JAMSDocument;
import jams.data.JAMSString;
import jams.model.JAMSVarDescription;
import org.xml.sax.SAXException;
import jams.JAMS;
import jams.data.Attribute;
import jams.tools.FileTools;

/**
 *
 * @author Christian Fischer
 */
public class DocumentLoader {
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "Description"
            )
            public Attribute.String modelFile;
    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "Description"
            )
            public Attribute.String workspaceDir;
    
    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.WRITE, 
            update = JAMSVarDescription.UpdateType.RUN, 
            description = "Collection of hru objects")
    public Attribute.Document modelDoc;
    
    public String init_withResponse(){
        try{
            String info = "";
            String modelFilename = FileTools.createAbsoluteFileName(workspaceDir.toString(),modelFile.toString());
            //check if file exists
            File file = new File(modelFilename);
            if (!file.exists()) {                
                return JAMS.i18n("Model_file_") + modelFilename + JAMS.i18n("_could_not_be_found_-_exiting!");
            }

            // do some search and replace on the input file and create new file if necessary
            String newModelFilename = XMLProcessor.modelDocConverter(modelFilename);
            if (!newModelFilename.equalsIgnoreCase(modelFilename)) {
                info = JAMS.i18n("The_model_definition_in_") + modelFilename + JAMS.i18n("_has_been_adapted_in_order_to_meet_changes_in_the_JAMS_model_specification.The_new_definition_has_been_stored_in_") + newModelFilename + JAMS.i18n("_while_your_original_file_was_left_untouched.");
                modelFilename = newModelFilename;
            }

            String xmlString = FileTools.fileToString(modelFilename);
            String[] args = null;
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    xmlString = xmlString.replaceAll("%" + i, args[i]);
                }
            }

            try {
                modelDoc.setValue(XMLTools.getDocumentFromString(xmlString));
               
            } catch (IOException ioe) {                
                return JAMS.i18n("The_model_definition_file_") + modelFilename + JAMS.i18n("_could_not_be_loaded,_because:_") + ioe.toString();
            } catch (SAXException se) {
                return JAMS.i18n("The_model_definition_file_") + modelFilename + JAMS.i18n("_contained_errors!");
            }                        
        }catch(Exception e){
            e.printStackTrace();
            return JAMS.i18n("Cant_load_model_file,_because_") + e.toString();
        } 
        return null;
    }
    
}