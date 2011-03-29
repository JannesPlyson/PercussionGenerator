/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

import model.Track;
import gui.view.SimpleViewOptions;
import gui.view.View;
import gui.view.ViewOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import model.Note;
import model.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author jannes
 */
public class DataParser{
    private ArrayList<Track> tracks;
    private String currentElement;
    private int pattern_length;    
    private ArrayList<Note> notes;
    private int startTime;
    private int duration;
    private int pitch;
    private int volume;
    private int grid;
    private Track track;
    private DefaultHandler defaultHandler;
    
    public ArrayList<Track> readFile(File f){
        tracks = new ArrayList<Track>();        
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(f, defaultHandler);
        } catch (Exception ex) {
            System.out.println("error reading xml: " + ex);            
        }
        return tracks;
    }

    public boolean writeFile(File f, ArrayList<Track> tracks){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element elData = doc.createElement("data");
            doc.appendChild(elData);
            for(int i = 0; i < tracks.size(); i++){
                createTrack(doc, elData, tracks.get(i));
            }
            Source source = new DOMSource(doc);
            Result result = new StreamResult(f);
            TransformerFactory tf = TransformerFactory.newInstance();            
            Transformer xformer = tf.newTransformer();
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            xformer.transform(source, result);
            return true;
        }catch(Exception ex){
            System.out.println("error writing xml: " + ex);
            return false;
        }

    }

    private void createTrack(Document doc, Element elData,Track track){
        Element elTrack = doc.createElement("track");
        elData.appendChild(elTrack);
        Element elPattern = doc.createElement("pattern");
        elTrack.appendChild(elPattern);        
        Element elLength = doc.createElement("length");
        elPattern.appendChild(elLength);
        elLength.appendChild(doc.createTextNode("" + track.getPattern().getLength()));
        Element elNotes = doc.createElement("notes");
        elPattern.appendChild(elNotes);
        Iterator<Note> it = track.getIterator();
        while(it.hasNext()){
            Note note = it.next();
            createNote(doc,elNotes,note);
        }
        ArrayList<View> views = track.getViews();
        for(int i = 0; i < views.size(); i++){
            Element elView = doc.createElement("view");
            elTrack.appendChild(elView);
            elView.appendChild(views.get(i).getViewOptions().createDomNodes(doc));
        }
    }

    private void createNote(Document doc, Element elNotes,Note note){
        Element elNote = doc.createElement("note");
        elNotes.appendChild(elNote);
        Element elStart = doc.createElement("start");
        elNote.appendChild(elStart);
        elStart.appendChild(doc.createTextNode("" + note.getStart()));
        Element elDuration = doc.createElement("duration");
        elNote.appendChild(elDuration);
        elDuration.appendChild(doc.createTextNode("" + note.getDuration()));
        Element elPitch = doc.createElement("pitch");
        elNote.appendChild(elPitch);
        elPitch.appendChild(doc.createTextNode("" + note.getPitch()));
        Element elVolume = doc.createElement("volume");
        elNote.appendChild(elVolume);
        elVolume.appendChild(doc.createTextNode("" + note.getVolume()));
    }

    public DataParser(){
        defaultHandler = new DefaultHandler(){
            @Override
            public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
                currentElement = qName;
                System.out.println("start: " + currentElement);
                if(currentElement.equals("notes")){
                    notes = new ArrayList<Note>();
                }
            }

            @Override
            public void endElement(String uri, String localName,String qName)throws SAXException {
                currentElement = "/" + qName;
                if(currentElement.equals("/note")){                    
                    notes.add(new Note(startTime,duration,pitch,volume));
                }else if(currentElement.equals("/notes")){
                    track = new Track(new Pattern(pattern_length,notes));
                    tracks.add(track);
                }else if(currentElement.equals("/simpleView")){                                        
                    SimpleViewOptions svo = (SimpleViewOptions)track.addView(View.SIMPLE_VIEW);
                    svo.grid = grid;
                }
            }

            @Override
            public void characters(char ch[], int start, int length)throws SAXException {
                if(currentElement.equals("length")){
                    pattern_length = Integer.parseInt(new String(ch,start,length));
                }else if(currentElement.equals("start")){
                    startTime = Integer.parseInt(new String(ch,start,length));
                }else if(currentElement.equals("duration")){
                    duration = Integer.parseInt(new String(ch,start,length));
                }else if(currentElement.equals("pitch")){
                    pitch = Integer.parseInt(new String(ch,start,length));
                }else if (currentElement.equals("volume")) {
                    volume = Integer.parseInt(new String(ch,start,length));
                }if(currentElement.equals("grid")){
                    grid = Integer.parseInt(new String(ch,start,length));
                }
            }
        };
    }
    
}
