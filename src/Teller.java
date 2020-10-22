
import java.util.PriorityQueue;

public class Teller {
	
	private int idleTime;
	private int maxQueueLength;
	private PriorityQueue<Event> queue;
	
	public Teller() {
		this.idleTime = 0;
		this.setMaxQueueLength(0);
		this.queue = new PriorityQueue<Event>();
	}

	/**
	 * @return the idleTime
	 */
	public int getIdleTime() {
		return idleTime;
	}

	/**
	 * @param idleTime the idleTime to set
	 */
	public void addIdleTime(int additional) {
		this.idleTime += additional;
	}

	/**
	 * @return the queue
	 */
	public PriorityQueue<Event> getQueue() {
		return queue;
	}

	public int getMaxQueueLength() {
		return maxQueueLength;
	}

	public void setMaxQueueLength(int maxQueueLength) {
		this.maxQueueLength = maxQueueLength;
	}
	
	
}
