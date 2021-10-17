package gui;

//java generic imports
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;

//custom classes imports
import office.OfficeCounter;
import office.ServiceType;
import office.Ticket;
import office.Office;


public class Gui {
	private List<ServiceType> serviceList;
	private List<OfficeCounter> counterList;
	private JFrame frame;
	private String[] serviceNames;
	private String selectedServiceName;
	private final int FRAME_WIDTH = 1280;
	private final int FRAME_HEIGHT = 720;
	private Office o;
	
	
	public Gui (List<ServiceType> services, List<OfficeCounter> counterList, Office office) {
		this.serviceList = services;
		this.frame = new JFrame("OfficeQueueManager");
		this.o = office;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

        //creating a tabbed pane for customer, officers and manager
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        //right panel, with the screen having information for clients
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(FRAME_WIDTH*2/5, FRAME_HEIGHT));;

        //getting the serviceNames from the List<ServiceTypes>
        serviceNames = new String[services.size()];
        int i = 0;
        for(ServiceType tmp : services) {
        	serviceNames[i] = tmp.getName();
        	i++;
        }
        
        //CUSTOMER VIEW
        JPanel customerPanel = new JPanel();
        JLabel serviceLabel = new JLabel("Select the desired type of service: ");
        customerPanel.add(serviceLabel);
        JComboBox serviceTypeSelection = new JComboBox(serviceNames);
        customerPanel.add(serviceTypeSelection);
        
        //GET A TICKET BUTTON
        JButton pickTicket = new JButton("Get a ticket for the selected type of service");
        
        //action listener for getting a ticket
        ActionListener ticketActionListener = new ActionListener() {
        	 public void actionPerformed(ActionEvent actionEvent) {
        		 System.out.println(serviceTypeSelection.getSelectedItem());
                 int num = 0;
                 for (int i = 0; i < o.getServiceTypesNumber() ; i++) {
                     if (o.getServiceTypeList().get(i).getName() == serviceTypeSelection.getSelectedItem()) {
                         num = i;
                         break;
                     }
                 }
                 
                 o.getNewTicket(o.getServiceTypeList().get(num));
        		 
        		 //Ticket ticket = o.getNewTicket();
        		 JPanel ticketPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        		 JLabel ticketPanelLable = new JLabel("Your ticket:", SwingConstants.CENTER);
        		 JLabel ticketIdLabel = new JLabel("ID: "/*+ticket.getId()*/);
        		 JLabel ticketServiceLabel = new JLabel("Type of service: "/*+ticket.getServiceType()*/);
        		 ticketPanel.add(ticketPanelLable);
        		 ticketPanel.add(ticketIdLabel);
        		 ticketPanel.add(ticketServiceLabel);
        		 ticketPanel.setPreferredSize(new Dimension(300, 100));
        		 ticketPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        		 customerPanel.add(ticketPanel);
        		 customerPanel.updateUI();
        		 
        		 Timer wipeOutTimer = new Timer(5000, new ActionListener() {
                     public void actionPerformed(ActionEvent event) 
                     {
                         customerPanel.remove(ticketPanel);
                         ticketPanel.validate();
                         ticketPanel.repaint();
                     }
                 });
        		 wipeOutTimer.setRepeats(false);
        		 if (!wipeOutTimer.isRunning()) {
        		     wipeOutTimer.start();
        		 }
        		 else 
        			 wipeOutTimer.restart();
                 
        	 }
    	 };
        pickTicket.addActionListener(ticketActionListener);
        customerPanel.add(pickTicket);
         
        
        
        //OFFICER VIEW
        JPanel officerPanel = new JPanel(new GridLayout(counterList.size(), 1, 0, 10));
        for (OfficeCounter tmp : counterList) {
        	JPanel singleCounter = new JPanel(new GridLayout(1, 2, 10, 0));
        	JLabel counterName = new JLabel("Counter "+tmp.getId());
        	singleCounter.add(counterName);
        	JPanel ticketAndDone = new JPanel(new GridLayout(2, 1, 0, 10));
        	JLabel ticket;
        	if(tmp.getCurrentlyServedTicket() != null)
        		ticket = new JLabel("Ticket being served: "+tmp.getCurrentlyServedTicket().getId());
        	else 
        		ticket = new JLabel("Ticket being served: free");
        	JButton done = new JButton("Done");
        	
        	ActionListener counterActionListener = new ActionListener() {
            	public void actionPerformed(ActionEvent actionEvent) {
            		o.notifyThatCounterIsFree(tmp.getId());
            		rightPanel.updateUI();
            	}
            };
            done.addActionListener(counterActionListener);
        	
        	ticketAndDone.add(ticket);
        	ticketAndDone.add(done);
        	singleCounter.add(ticketAndDone);
        	officerPanel.add(singleCounter);
        }
        
        
        
        
        
        //OTHER VIEWS (Officer, Manager)
        JTextArea jt3 = new JTextArea("Manager view is still in development, sorry");

        tabbedPane.addTab("Customer", null, customerPanel, "See the customer view");
        tabbedPane.addTab("Officer", null, officerPanel, "See the officer view");
        tabbedPane.addTab("Manager", null, jt3, "See the manager view");
        
        
        

        
        
        //Right screen with the information on tickets currently served
        System.out.println(counterList.size()+2);
        JPanel display = new JPanel(new GridLayout(counterList.size()+2, 1, 0, 10));
    	JLabel label = new JLabel("Counters info");
    	display.add(label);
    	label = new JLabel("Which is the last ticket picked from queue?");
    	display.add(label);
    	
        //creating a label for each counter
        for(OfficeCounter tmp : counterList) {
        	if(tmp.getCurrentlyServedTicket() != null)
        		label = new JLabel("Counter "+tmp.getId()+" is serving ticket number "+tmp.getCurrentlyServedTicket().getId());
        	else 
        		label = new JLabel("Counter "+tmp.getId()+" isn't serving any ticket");
        	display.add(label);
        }
        rightPanel.add(display);

        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.CENTER, tabbedPane);
        frame.getContentPane().add(BorderLayout.EAST, rightPanel);
     	}

	public void show() {
        frame.setVisible(true);		
	}

}