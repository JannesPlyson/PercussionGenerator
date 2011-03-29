/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

import model.Track;
import data.midi.MIDIImporter;
import java.io.File;
import java.util.ArrayList;
import javax.xml.validation.Validator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 *
 * @author jannes
 */
public class FileLoader {
    public ArrayList<Track> loadFile(File f){
        if(isDataFile(f)){            
            DataParser dp = new DataParser();
            return dp.readFile(f);
        }else if(isMidiFile(f)){
            MIDIImporter mi = new MIDIImporter();
            return mi.readFile(f);
        }
        return null;
        
    }

    public boolean isMidiFile(File f){                
        return f.getName().endsWith(".mid");
    }

    public boolean isDataFile(File f){        
        if(!f.getName().endsWith(".xml")){            
            return false;
        }
        try{
            String schemaLang = "http://www.w3.org/2001/XMLSchema";
            // get validation driver:
            SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
            // create schema by reading it from an XSD file:
            Schema schema = factory.newSchema(new StreamSource(getClass().getResourceAsStream("/data/data.xsd")));
            Validator validator = schema.newValidator();
            // at last perform validation:
            validator.validate(new StreamSource(f));
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
