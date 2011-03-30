/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import be.hogent.tarsos.dsp.AudioDispatcher;
import be.hogent.tarsos.dsp.PercussionOnsetDetector;
import be.hogent.tarsos.dsp.PercussionOnsetDetector.PercussionHandler;
import input.InputStartOnly;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import model.Pattern;
import model.Track;
import model.TrackCollection;

/**
 *
 * @author installer
 */
public class MicrophoneTappingPanel extends JPanel{

    private Mixer mixer;
    private ArrayList<Mixer> mixerList;
    private DataLine.Info dataLineInfoLittleEndian;
    private DataLine.Info dataLineInfoBigEndian;
    private float sampleRate;
    private int bufferSize,overlap;    
    private AudioDispatcher dispatcher;
    private double sensitivity;
    private JProgressBar progressBar;
    private int tresholdInterval;    
    private InputStartOnly input;
    private int defaultPitch,defaultVelocity;
    private TrackCollection trackCollection;
    private boolean bigEndian;
    private int wizardCount;
    private Timer progressBarTimer;
    private boolean wizard;
    private JSlider slTreshold;

    public MicrophoneTappingPanel(TrackCollection trackCollection){
        sampleRate = 44100;
        bufferSize = 1024;
        overlap = 0;
        dispatcher = null;
        sensitivity = 10;
        tresholdInterval = 4000;
        defaultPitch = 50;
        defaultVelocity = 60;
        wizardCount = 0;
        //times = new ArrayList<Double>();
        input = new InputStartOnly();
        bigEndian = false;
        mixerList = new ArrayList<Mixer>();

        this.setMinimumSize(new Dimension(300,200));
        this.setPreferredSize(new Dimension(300,200));
        //this.setMaximumSize(new Dimension(250,200));
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Microphone"));

        JPanel mixerPanel = new JPanel(new BorderLayout());        
        this.add(mixerPanel,BorderLayout.NORTH);
        JLabel lblMixers = new JLabel("Available devices:");
        mixerPanel.add(lblMixers,BorderLayout.NORTH);
        final JComboBox cmbMixers = new JComboBox();       
        mixerPanel.add(cmbMixers,BorderLayout.SOUTH);
        cmbMixers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try{
                    mixer = mixerList.get(cmbMixers.getSelectedIndex());                    
                }catch(Exception exc){}
            }
        });        
        //get usable mixers
        final AudioFormat formatLittleEndian = new AudioFormat(sampleRate, 16, 1, true, false);
        dataLineInfoLittleEndian = new DataLine.Info(TargetDataLine.class, formatLittleEndian);
        final AudioFormat formatBigEndian = new AudioFormat(sampleRate, 16, 1, true, true);
        dataLineInfoBigEndian = new DataLine.Info(TargetDataLine.class, formatBigEndian);        
        this.trackCollection = trackCollection;

        for(Mixer.Info mixerInfo : AudioSystem.getMixerInfo()){
            Mixer localMixer = AudioSystem.getMixer(mixerInfo);
            final int index = mixerList.size();
            if(localMixer.isLineSupported(dataLineInfoLittleEndian) || localMixer.isLineSupported(dataLineInfoBigEndian)){
                Action action = new AbstractAction(mixerInfo.toString()) {
                      public void actionPerformed(ActionEvent evt) {
                          mixer = mixerList.get(index);
                      }
                };
                mixerList.add(localMixer);
                cmbMixers.addItem(localMixer.getMixerInfo());
            }            
        }
        if(!mixerList.isEmpty()){
            mixer = mixerList.get(0);
        }


        progressBar = new JProgressBar(JProgressBar.VERTICAL,0,100);
        this.add(progressBar,BorderLayout.EAST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        this.add(centerPanel,BorderLayout.CENTER);
        JPanel tresholdPanel = new JPanel(new BorderLayout());
        JLabel lblTreshold = new JLabel("Treshold: ");
        tresholdPanel.add(lblTreshold,BorderLayout.NORTH);
        slTreshold = new JSlider(0,20);
        slTreshold.setValue(10);
        tresholdPanel.add(slTreshold,BorderLayout.SOUTH);
        centerPanel.add(tresholdPanel,BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        centerPanel.add(buttonPanel,BorderLayout.SOUTH);
        buttonPanel.setLayout(new FlowLayout());
        final JButton btnStart = new JButton(">");
        final JButton btnStop = new JButton("||");
        btnStop.setEnabled(false);
        buttonPanel.add(btnStart);
        buttonPanel.add(btnStop);
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                start();
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop();
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
            }
        });

        JButton btnWizzard = new JButton("Wizzard");
        buttonPanel.add(btnWizzard);
        btnWizzard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "tap 5 times between the start en the end of the progress bar");
                if(result == JOptionPane.OK_OPTION){
                    try{
                        wizard = true;
                        bigEndian = true;                        
                        Thread.sleep(1000);
                        checkTreshold(0, 20);
                    }catch(Exception exc){}
                }
            }
        });
        
    }    

    private void start(){
        input = new InputStartOnly();
        setMixer(mixerList.indexOf(mixer));
        if(progressBarTimer != null){
            progressBarTimer.cancel();
        }
        progressBarTimer = new Timer();
        progressBarTimer.scheduleAtFixedRate(new TimerTask(){
            public void run(){
                if(progressBar.getValue() > 0){
                    progressBar.setValue(progressBar.getValue()-5);
                }
            }
        }, 0, 50);
    }

    private void stop(){        
        Pattern pattern = input.getPattern();
        if(pattern != null){
            trackCollection.addTrack(new Track(pattern));
        }
        if(progressBarTimer != null){
            progressBarTimer.cancel();
        }
        progressBarTimer = null;
        if(dispatcher != null){
            try{
                dispatcher.stop();
            }catch(Exception exc){
                exc.printStackTrace();
            }
        }
    }

    private void checkTreshold(final int min,final int max){
        if(min == max){
            if(!bigEndian){
                JOptionPane.showMessageDialog(null, "Wizard failed, will retry with different settings");
                bigEndian = true;
                setMixer(mixerList.indexOf(mixer));
                checkTreshold(0, 20);
            }else{
                JOptionPane.showMessageDialog(null, "Wizard failed, problems with microphone?");
                wizard = false;                
            }

        }else{
            slTreshold.setValue(max/2);
            setMixer(mixerList.indexOf(mixer));            
            progressBar.setValue(0);            
            try{
                Thread.sleep(1000);
            }catch(Exception exc){
                exc.printStackTrace();
            }            
            wizardCount = 0;
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask(){
                public void run(){
                    progressBar.setValue(progressBar.getValue()+1);
                    if(progressBar.getValue() == 100){
                        timer.cancel();
                        if(wizardCount > 5){
                            checkTreshold(max/2,max);                            
                        }else if(wizardCount < 5){
                            checkTreshold(min,max/2);                            
                        }else{
                            JOptionPane.showMessageDialog(null, "Wizard succesfull completed");
                            wizard = false;                            
                        }
                    }
                }
            }, 0, tresholdInterval/100);            
        }
    }

    private void setMixer(int i){
        if(dispatcher != null){
            try{
                dispatcher.stop();
            }catch(Exception exc){
                exc.printStackTrace();
            }
        }        
        mixer = mixerList.get(i);                
        TargetDataLine line = null;
        try{
            if(bigEndian){
                line = (TargetDataLine) mixer.getLine(dataLineInfoBigEndian);
                final int numberOfSamples = bufferSize;
                line.open(dataLineInfoBigEndian.getFormats()[0], numberOfSamples);
                line.start();
            }else{
                line = (TargetDataLine) mixer.getLine(dataLineInfoLittleEndian);
                final int numberOfSamples = bufferSize;
                line.open(dataLineInfoLittleEndian.getFormats()[0], numberOfSamples);
                line.start();
            }
            AudioInputStream stream = new AudioInputStream(line);
            //create a new dispatcher
            dispatcher = new AudioDispatcher(stream, bufferSize, overlap);

            //add a processor, handle percussion event.            
            dispatcher.addAudioProcessor(new PercussionOnsetDetector(sampleRate, bufferSize,overlap, new PercussionHandler() {
                @Override
                public void handlePercussion(double timestamp) {
                    input.addInput(timestamp);
                    if(wizard){
                        wizardCount++;
                    }else {
                        progressBar.setValue(100);
                    }
                }
            },sensitivity,slTreshold.getValue()));
            //run the dispatcher (on the same thread, use start() to run it on another thread).
            //dispatcher.run();
            new Thread(dispatcher).start();
        }catch(Exception exc){
            exc.printStackTrace();
        }
    }
}
