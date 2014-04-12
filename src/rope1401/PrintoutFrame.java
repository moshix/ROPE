package rope1401;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import static java.lang.Math.abs;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;

public class PrintoutFrame extends ChildFrame implements ActionListener, ChangeListener, CaretListener, CommandWindow
{
	private static final long serialVersionUID = 1L;
	
    BorderLayout borderLayout1 = new BorderLayout();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JPanel controlPanel = new JPanel();
    JButton updateButton = new JButton();
    JCheckBox autoCheckBox = new JCheckBox();
    JCheckBox stripesCheckBox = new JCheckBox();
    JCheckBox barsCheckBox = new JCheckBox();
    JScrollPane scrollPane = new JScrollPane();
    JTextArea printoutArea = new JTextArea();

    private BufferedReader printout;
    private Color barColor = new Color(100, 0, 100);
    private Color stripeColor = new Color(25, 0, 25);

    public PrintoutFrame(RopeFrame parent)
    {
		super(parent);
		
        setSize(940, 400);
   
		try 
		{
            jbInit();
        }
        catch (Exception ex) 
		{
            ex.printStackTrace();
        }

		printoutArea.addCaretListener(this);
	    updateButton.addActionListener(this);
        stripesCheckBox.addChangeListener(this);
        barsCheckBox.addChangeListener(this);

		// Remove automatic key bindings because we want them controlled by menu items
		InputMap im = printoutArea.getInputMap(JComponent.WHEN_FOCUSED);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, RopeHelper.modifierMaks), "none");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, RopeHelper.modifierMaks + InputEvent.SHIFT_MASK), "none");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, RopeHelper.modifierMaks), "none");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, RopeHelper.modifierMaks), "none");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, RopeHelper.modifierMaks), "none");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, RopeHelper.modifierMaks), "none");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, RopeHelper.modifierMaks), "none");
	}

	@Override
    protected void finalize() throws Throwable
    {
		try 
		{
			if (printout != null) 
			{
				try
				{
					printout.close();
				}
				catch(IOException ignore) {}
			}
		}
		finally 
		{
			super.finalize();
		}
    }

    void jbInit() throws Exception
    {
        this.setIconifiable(true);
        this.setMaximizable(true);
        this.setResizable(true);
        this.setTitle("PRINTOUT");
        this.getContentPane().setLayout(borderLayout1);
        this.getContentPane().add(controlPanel, BorderLayout.NORTH);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
				
		printoutArea = new JTextArea() 
		{
			private static final long serialVersionUID = 1L;
			
			@Override
            public void paint(Graphics g)
            {
                super.paint(g);

                boolean doBars = barsCheckBox.isSelected();
                boolean doStripes = stripesCheckBox.isSelected();

                if (doBars || doStripes) 
				{
                    Dimension size = this.getSize();
                    FontMetrics fm = g.getFontMetrics();
                    int charWidth = fm.charWidth('w');
                    int lineHeight = fm.getHeight();
                    int barWidth = 10*charWidth;
                    int barHeight = 3*lineHeight;
                    int skipHeight = 2*barHeight;

                    g.setXORMode(Color.BLACK);

                    if (doBars) 
					{
                        g.setColor(barColor);
						
                        for (int x = barWidth; x < size.width; x += barWidth) 
						{
                            g.drawLine(x, 0, x, size.height);
                        }
                    }

                    if (doStripes) 
					{
                        g.setColor(stripeColor);
						
                        for (int y = barHeight; y < size.height; y += skipHeight) 
						{
                            g.fillRect(0, y, size.width, barHeight);
                        }
                    }

                    g.setPaintMode();
                }
            }
        };
        printoutArea.setFont(new java.awt.Font("Monospaced", 0, 12));
        printoutArea.setDoubleBuffered(true);
        printoutArea.setEditable(false);
        scrollPane.getViewport().add(printoutArea, null);
		
        controlPanel.setLayout(gridBagLayout1);
        updateButton.setText("Update");
        autoCheckBox.setText("Auto update");
        autoCheckBox.setSelected(true);
        stripesCheckBox.setText("Stripes");
        stripesCheckBox.setSelected(true);
        barsCheckBox.setText("Bars");
        barsCheckBox.setSelected(false);
 
		controlPanel.add(updateButton,
                         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 5, 0), 0, 0));
        controlPanel.add(autoCheckBox,
                         new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 5, 5, 0), 0, 0));
        controlPanel.add(stripesCheckBox,
                         new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0,
                                                GridBagConstraints.EAST,
                                                GridBagConstraints.NONE,
                                                new Insets(5, 0, 5, 0), 0, 0));
        controlPanel.add(barsCheckBox,
                         new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
    }

    void initialize()
    {
        try 
		{
            if (printout != null) 
			{
                printout.close();
            }

            printout = new BufferedReader(new FileReader(DataOptions.outputPath));
            printoutArea.setText(null);
        }
        catch(IOException ex) 
		{
            printout = null;
            ex.printStackTrace();
        }
    }

	@Override
    public void execute()
    {
        if (autoCheckBox.isSelected()) 
		{
            update();
        }
        else 
		{
            printoutArea.setEnabled(false);
        }
    }

	@Override
    public void lock()
    {
        updateButton.setEnabled(false);
    }

	@Override
    public void unlock()
    {
        updateButton.setEnabled(true);
    }

	@Override
    public void stateChanged(ChangeEvent event)
    {
        Object source = event.getSource();

        if ((source == stripesCheckBox) || (source == barsCheckBox)) 
		{
            printoutArea.repaint();
        }
    }

	@Override
    public void actionPerformed(ActionEvent event)
    {
        update();
		
        printoutArea.repaint();
    }

    private void update()
    {
        if (printout != null) 
		{
            String line;

            try 
			{
                while ((line = printout.readLine()) != null) 
				{
                    printoutArea.append(line + "\n");
                }
            }
            catch (IOException ex)
			{
                ex.printStackTrace();
            }

            printoutArea.setEnabled(true);
            printoutArea.setCaretPosition(printoutArea.getText().length());
        }
    }
	
	@Override
    public void caretUpdate(CaretEvent event)
    {
  		doCaretUpdate(event.getDot(), event.getMark());
    }

	void doCaretUpdate(int dot, int mark)
	{	
        if (dot == mark) 
		{
            mainFrame.copyItem.setEnabled(false);
 		}   
		else
		{
            mainFrame.copyItem.setEnabled(true);
        }
		
		int length = printoutArea.getText().length();
		if(length == 0 || abs(mark - dot) == length)
		{
			mainFrame.selectAllItem.setEnabled(false);
		}
		else
		{
			mainFrame.selectAllItem.setEnabled(true);
		}
	}

	@Override
	public boolean canCopy()
	{
		return (printoutArea.getText().length() > 0);
	}
	
	public void copyMenuAction(ActionEvent event)
	{
		if(printoutArea != null)
		{
			printoutArea.copy();
		}
	}

	public void selectAllMenuAction(ActionEvent event)
	{
		if(printoutArea != null)
		{
			printoutArea.selectAll();
		}
	}
}