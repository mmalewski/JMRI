// TieToolFrame.java

package jmri.jmrix.openlcb.swing.tie;

import jmri.util.davidflanagan.HardcopyWriter;
import jmri.jmrix.maple.SerialTrafficController;
import jmri.jmrix.maple.SerialNode;
import jmri.jmrix.maple.SerialAddress;

import jmri.jmrix.AbstractNode;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.IOException;

import java.util.ResourceBundle;

import javax.swing.border.Border;
import javax.swing.*;
import javax.swing.table.*;

import java.lang.Integer;

/**
 * Frame for running assignment list.
 * @author	 Bob Jacobsen 2008
 * @version	 $Revision: 1.1 $
 * @since 2.3.7
 */
public class TieToolFrame extends jmri.util.JmriJFrame {

    static    ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.openlcb.swing.tie.TieBundle");

    public void initComponents() throws Exception {

        // set the frame's initial state
        setTitle(rb.getString("WindowTitle"));

        Container contentPane = getContentPane();        
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        ProducerTablePane producerPane = new ProducerTablePane();
        producerPane.initComponents();
        Border producerBorder = BorderFactory.createEtchedBorder();
        Border producerTitled = BorderFactory.createTitledBorder(producerBorder,"Producers");
        producerPane.setBorder(producerTitled);
        
        ConsumerTablePane consumerPane = new ConsumerTablePane();
        consumerPane.initComponents();
        Border consumerBorder = BorderFactory.createEtchedBorder();
        Border consumerTitled = BorderFactory.createTitledBorder(consumerBorder,"Consumers");
        consumerPane.setBorder(consumerTitled);
        
        TieTablePane tiePane = new TieTablePane();
        tiePane.initComponents();
        Border tieBorder = BorderFactory.createEtchedBorder();
        Border tieTitled = BorderFactory.createTitledBorder(tieBorder,"Ties");
        tiePane.setBorder(tieTitled);
        
        JSplitPane upperSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, producerPane, consumerPane);
        
        JSplitPane wholeSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperSplit, tiePane);
        
        JPanel p1 = new JPanel();
        p1.add(wholeSplit);
        contentPane.add(p1);
        
        JPanel p2 = new JPanel();
        p2.setLayout(new FlowLayout());
        p2.add(new JButton("Add"));
        p2.add(new JButton("Update"));
        p2.add(new JButton("Delete"));
        contentPane.add(p2);

        // initialize menu bar
        JMenuBar menuBar = new JMenuBar();
		// set up File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        // fileMenu.add(...);
        setJMenuBar(menuBar);

        addHelpMenu("package.jmri.jmrix.maple.assignment.ListFrame", true);

        // pack for display
        pack();
    }

     static org.apache.log4j.Category log = org.apache.log4j.Logger.getLogger(TieToolFrame.class.getName());
	
}

/* @(#)TieToolFrame.java */
