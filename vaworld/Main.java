package vaworld;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.util.HashMap;



public class Main
{
    public static void main(String[] args)
    {
        HashMap<String, VacuumAgent> agentList = new HashMap<String, VacuumAgent>();
        agentList.put("Random Vacuum Agent", new RandomVacuumAgent());
        agentList.put("My Vacuum", new DummyVacuum());
        agentList.put("Test Vacuum", new TestAgent());
        try {
            UIManager.setLookAndFeel("MetalLookAndFeel");
        } catch (Exception e) {}

        JFrame f = new JFrame("Agent Floor");

        f.setSize(1054, 750);
        f.setResizable(false);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(new MyPanel(f, agentList) , BorderLayout.NORTH);
        f.setVisible(true);
    }
}

class MyPanel extends JPanel {

    public static final Color buttonColor = MetalLookAndFeel.getTextHighlightColor();
    private JFrame holder;
    public JTabbedPane centerPanel = new JTabbedPane();
    private CreatePanel wcp;
    public MyPanel(JFrame h, HashMap<String, VacuumAgent> agents) {
        holder = h;
        wcp = new CreatePanel(holder, agents);
//        JPanel aboutPanel = createAboutPanel();
        centerPanel.addTab("Agents and Environments", wcp);
//        centerPanel.addTab("About", aboutPanel);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
        holder.getContentPane().add(centerPanel, BorderLayout.CENTER);
    }
//    private JPanel createAboutPanel() {
//        JPanel aboutPanel = new JPanel(new BorderLayout());
//        JTextPane aboutText = new JTextPane();
//        SimpleAttributeSet set = new SimpleAttributeSet();
//        StyleConstants.setAlignment(set,StyleConstants.ALIGN_CENTER);
//        aboutText.setParagraphAttributes(set,true);
//        aboutText.setBackground(MetalLookAndFeel.getTextHighlightColor());
//        aboutText.setEditable(false);
//        aboutPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
//        aboutPanel.add(aboutText, BorderLayout.CENTER);
//        return aboutPanel;
//    }
}