/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jams.components.aggregate;

import jams.JAMS;
import jams.components.dbf.DBFField;
import jams.components.dbf.DBFReader;
import jams.components.dbf.DBFWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Level;

/**
 *
 * @author christian
 */
public class ShapeFileOutputDataStore {
    DBFWriter writer  = null;
    File template, file;
    
    public ShapeFileOutputDataStore(File template, File file) throws IOException{
        init(template, file);
    }
    
    private void init(File template, File file) throws IOException{
        this.template = template;
        
        file = new File(file, template.getName().replace(".shp", ".dbf"));
        
        this.file = file;
        //copy data .. 
        File directory = template.getParentFile();
        String name = template.getName().replace(".shp", "");
        for (File srcFile : directory.listFiles()) {
            if (srcFile.getName().startsWith(name)) {
                try {
                    Files.copy(Paths.get(srcFile.getAbsolutePath()), Paths.get(file.getParentFile().getAbsolutePath()+"/"+srcFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    throw new IOException("The file " + srcFile.getAbsolutePath() + " could not be copied to the output directy!\n" + ioe.toString());
                }
            }
        }

        /*InputStream inputStream = null;
        DBFReader dbfReader = null;
        try {
            inputStream = new FileInputStream(new File(template.getParentFile(), template.getName().replace(".shp", ".dbf")));
            dbfReader = new DBFReader(inputStream);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            if (inputStream != null){
                inputStream.close();
            }
            throw new IOException("The dbf-file " + template.getAbsolutePath() + " could not be read properly!\n" + ioe.toString());
        }
        
        writer = new DBFWriter();

        DBFField fields[] = null;
        try {
            fields = new DBFField[dbfReader.getFieldCount()];
            for (int j = 0; j < fields.length; j++) {
                fields[j] = dbfReader.getField(j);
            }
        } catch (DBFException dbfe) {
            dbfe.printStackTrace();
            if (inputStream != null){
                inputStream.close();
            }
            throw new IOException("The dbf-file " + template.getAbsolutePath() + " could not be read properly!\n" + dbfe.toString());
        }
        OutputStream out = null;
        try {
            writer.setFields(fields);
            for (int j = 0; j < dbfReader.getRecordCount(); j++) {
                Object objs[] = dbfReader.nextRecord();
                writer.addRecord(objs);
            }
            out = new FileOutputStream(file);
            writer.write(out);            
            out.flush();                  
        } catch (IOException dbfe) {
            dbfe.printStackTrace();                                  
            throw new IOException("The dbf-file " + file.getAbsolutePath() + " could not be written!\n" + dbfe.toString());
        }finally{            
            if (out != null){
                out.close();
            }
            if (inputStream != null){
                inputStream.close();
            }
            if (writer!=null){
                writer.close();
            }
        }*/
    }
    
    public void addDataToShpFiles(SimpleOutputDataStore store) throws IOException{
        InputStream inputStream = null;
        DBFReader dbfReader = null;
        FileOutputStream outStream = null;
        try {
            String fields[] = store.getEntries();
            inputStream = new FileInputStream(new File(template.getParentFile(), template.getName().replace(".shp", ".dbf")));
            dbfReader = new DBFReader(inputStream);

            DBFWriter writer = new DBFWriter();
            
            DBFField dbfFields[] = new DBFField[fields.length+dbfReader.getFieldCount()];
            int k = dbfReader.getFieldCount();
            int m = fields.length;
            int n = k+m;
            int idField = 0;
            
            for (int i=0;i<k;i++){
                dbfFields[i] = dbfReader.getField(i);
            }
            for (int i=0;i<fields.length;i++){
                dbfFields[i+k] = new DBFField();
                dbfFields[i+k].setName(fields[i].replaceAll(" [0-9][0-9]:[0-9][0-9]", ""));
                dbfFields[i+k].setDataType(DBFField.FIELD_TYPE_N);
                dbfFields[i+k].setFieldLength(12);
                dbfFields[i+k].setDecimalCount(5);                
            }
            writer.setFields(dbfFields);
            outStream = new FileOutputStream(file);
            writer.write(outStream);
            outStream.close();
            outStream = null;
            
            writer = new DBFWriter(file);
            
            for (int j=0;j<dbfReader.getRecordCount();j++){
                Object objIn[] = dbfReader.nextRecord();
                Object objOut[] = new Object[n];
                
                double id = Double.parseDouble(objIn[idField].toString());
                int position = store.getPositionOfEntity(id);//this.aggregatedValues.headMap(id).size();
                
                for (int i=0;i<k;i++){
                    objOut[i] = objIn[i];
                }
                
                for (int i=0;i<m;i++){
                    objOut[i+k] = new Double(store.getData(fields[i], position));
                }
                try{
                    writer.addRecord(objOut);
                }catch(jams.components.dbf.DBFException dbfe){
                    System.out.println("Error writing field: " + dbfe.toString());
                    System.out.println("Fields in question are: " + Arrays.toString(objOut));
                }
            }
            writer.write();
            writer = null;
            
        } catch (IOException ioe) {
            //getModel().getRuntime().getLogger().log(Level.SEVERE, MessageFormat.format(JAMS.i18n("The following DBF File was not found: " + dbfFileOriginal), getInstanceName()));
            ioe.printStackTrace();
            throw new IOException("Could not write shape file, because of: " + ioe.toString());
        } finally {
            try {
                if (inputStream!=null)
                    inputStream.close();
                if (outStream!=null)
                    outStream.close();
                if (writer!=null)
                    writer.close();
            } catch (IOException ioe2) {
            }
        }
    }
}