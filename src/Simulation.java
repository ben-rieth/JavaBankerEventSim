import java.util.PriorityQueue;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Simulation {
	
	private int clock;
	private int totalInterArrivalTime;
	private int totalServiceTime;
	private int numCustomers;
	private int maxCustomerWaitTime;
	private int totalWaitTime;
	
	private Teller[] tellers;
	private PriorityQueue<Event> masterEventQueue;
	
	private File outputFile;
	private FileWriter outputWriter;
	
	public Simulation(int numTellers) {
		this.clock = 0;
		this.totalInterArrivalTime = 0;
		this.totalServiceTime = 0;
		this.numCustomers = 0;
		this.maxCustomerWaitTime = 0;
		this.totalWaitTime = 0;
		
		tellers = new Teller[numTellers];
		initTellers();
		
		masterEventQueue = new PriorityQueue<Event>();

	}
	
	private void initTellers() {
		for(int x = 0; x < tellers.length; x++) {
			tellers[x] = new Teller();
		}
	}
	
	public void initSimulation() {
		masterEventQueue.add(Event.generateRandomArrivalEvent(clock));
		
		totalInterArrivalTime = masterEventQueue.peek().getTimeOfDay() - clock;
		totalServiceTime = masterEventQueue.peek().getServiceTime();
		
		try {
			outputFile = new File("simulationOutput.txt");
			if(outputFile.createNewFile()) {
				System.out.println("Output file created");
			} else {
				System.out.println("Output file already exists");
			}
		} catch (IOException e) {
			System.out.println("An error occured in creating the file");
			e.printStackTrace();
		}
		
		try {
			outputWriter = new FileWriter(outputFile);
			System.out.println("File Writer created successfully");
		} catch (IOException e) {
			System.out.println("Error in creating writer");
			e.printStackTrace();
		}
		
	}
	
	public void runSim(int clockLimit) {
		
		int nextReport = 500; 
		
		Event removedFromMaster;
		int tellerWithSmallestQueue;
		Teller currentTeller;
		int currentCustomerWaitTime;
		Event currentCustomer;
		
		while(clock <= clockLimit) {
			
			//if the clock is greater than the next report time i.e. 500, 1000, 1500, 2000;
			//	a report will be printed
			if(clock >= nextReport) {
				report();
				nextReport += 500;
			}
			
			//removes an event from the master queue
			removedFromMaster = masterEventQueue.remove();
			
			//adds idle time to the tellers if their queue length has been zero
			for(Teller t : tellers) {
				if (t.getQueue().size() == 0) {
					t.addIdleTime(removedFromMaster.getTimeOfDay() - clock);
				}
			}
			
			//the clock jumps forward to the time of the next event
			clock = removedFromMaster.getTimeOfDay();
			
			//this is the conditional for an arrival event
			if(removedFromMaster.getEventType() == -1) {
				
				//this method finds the teller with the smallest queue and sends the 
				//	customer there
				tellerWithSmallestQueue = findSmallestQueue();
				currentTeller = tellers[tellerWithSmallestQueue];
				
				currentTeller.getQueue().add(removedFromMaster);
				
				//if the teller's queue length is greater than the previous maximum queue length,
				//	it is replaced in the object
				if(currentTeller.getQueue().size() > currentTeller.getMaxQueueLength()) {
					currentTeller.setMaxQueueLength(currentTeller.getQueue().size());
				}
				
				//generate departure event if the current teller only has one person in the queue
				if(currentTeller.getQueue().size() == 1) {
					masterEventQueue.add(Event.generateRandomDepartureEvent(
							clock, 
							currentTeller.getQueue().peek().getServiceTime(), 
							tellerWithSmallestQueue));
				}
				
				masterEventQueue.add(Event.generateRandomArrivalEvent(clock));
				totalInterArrivalTime += masterEventQueue.peek().getTimeOfDay() - clock;
				totalServiceTime += masterEventQueue.peek().getServiceTime();
				
			} else {
				numCustomers++;
				currentTeller = tellers[removedFromMaster.getEventType()];
				currentCustomer = currentTeller.getQueue().peek();
				
				currentCustomerWaitTime = clock - (currentCustomer.getTimeOfDay() + currentCustomer.getServiceTime());
				
				if (currentCustomerWaitTime > maxCustomerWaitTime) {
					maxCustomerWaitTime = currentCustomerWaitTime;
				}
				
				totalWaitTime += currentCustomerWaitTime;
				
				currentTeller.getQueue().remove();
				
				if(currentTeller.getQueue().size() != 0) {
					masterEventQueue.add(Event.generateRandomDepartureEvent(
							clock, 
							currentTeller.getQueue().peek().getServiceTime(),
							removedFromMaster.getEventType()
							));
				}
			}
			
		}
		
		endReport();
	}
	
	private int findSmallestQueue() {
		//initializes the min as Teller 0
		int min = 0;
		
		if(tellers.length > 1) {
			
			for(int x = 1; x < tellers.length; x++) {
				
				if (tellers[x].getQueue().size() < tellers[min].getQueue().size()) {
					min = x;
				}
			}
		}
		
		//this is the number of the teller with the minimum value queue
		return min;
		
	}
	
	private void report() {
		try {
			outputWriter.write("Report @ Clock = " + clock + "\n");
			outputWriter.write("----------------------------\n");
			outputWriter.write("Lengths of Teller Queues: \n");
			for(int x = 0; x < tellers.length; x++) {
				outputWriter.write("    Teller " + x + ": " + tellers[x].getQueue().size() + "\n");
			}
			outputWriter.write("----------------------------\n");
			outputWriter.write("Length of Event Queue: " + masterEventQueue.size()+ "\n");
			outputWriter.write("----------------------------\n\n");
		} catch (IOException e) {
			System.out.println("Error with one of the midway reports\n");
			e.printStackTrace();
		}
	}
	
	private void endReport() {
		try {
			outputWriter.write("End Report @ Clock = " + clock + "\n");
			outputWriter.write("----------------------------\n");
			outputWriter.write("Total Customers Processed: " + numCustomers + "\n");
			outputWriter.write("Average Inter Arrival Time: " + ((double) totalInterArrivalTime/numCustomers) + "\n");
			outputWriter.write("Average Service Time: " + ((double) totalServiceTime/numCustomers) + "\n");
			outputWriter.write("Average Wait Time per Customer: " + ((double) totalWaitTime/numCustomers) + "\n");
			outputWriter.write("----------------------------\n");
			outputWriter.write("Cashier Idle Times: \n");
			
			for(int x = 0; x < tellers.length; x++) {
				outputWriter.write("Teller " + x + ": " + ((double) tellers[x].getIdleTime()/clock)*100 + "\n");
			}
			
			outputWriter.write("----------------------------\n");
			outputWriter.write("Maximum Customer Wait Time: " + maxCustomerWaitTime + "\n");
			outputWriter.write("----------------------------\n");
			outputWriter.write("Maximum Teller Queue Lengths: \n");
			
			for(int x = 0; x < tellers.length; x++) {
				outputWriter.write("Teller " + x + ": " + (tellers[x].getMaxQueueLength()) + "\n");
			}
			
			outputWriter.write("----------------------------\n");
			outputWriter.write("Customers Left in the Queues: \n");
			
			for(int x = 0; x < tellers.length; x++) {
				outputWriter.write("Teller " + x + ": " + (tellers[x].getQueue().size()) + "\n");
			}
			
			outputWriter.write("----------------------------\n");
			
			outputWriter.close();
		} catch (IOException e) {
			System.out.println("Error in writing end report");
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the clock
	 */
	public int getClock() {
		return clock;
	}
	
	/**
	 * @param clock the clock to set
	 */
	public void setClock(int clock) {
		this.clock = clock;
	}

	/**
	 * @return the tellers
	 */
	public Teller[] getTellers() {
		return tellers;
	}

	/**
	 * @return the masterEventQueue
	 */
	public PriorityQueue<Event> getQueue() {
		return masterEventQueue;
	}

	/**
	 * @return the totalInterArrivalTime
	 */
	public int getTotalSArrivalTime() {
		return totalInterArrivalTime;
	}

	/**
	 * @param totalInterArrivalTime the totalInterArrivalTime to set
	 */
	public void setTotalArrivalTime(int totalInterArrivalTime) {
		this.totalInterArrivalTime = totalInterArrivalTime;
	}

	/**
	 * @return the totalServiceTime
	 */
	public int getTotalServiceTime() {
		return totalServiceTime;
	}

	/**
	 * @param totalServiceTime the totalServiceTime to set
	 */
	public void setTotalServiceTime(int totalServiceTime) {
		this.totalServiceTime = totalServiceTime;
	}
	
	
	
}
