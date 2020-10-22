import java.util.Random;

public class Event implements Comparable<Event> {
	
	private int timeOfDay;
	private int serviceTime;
	private int eventType;
	private static Random rand = new Random();
	
	public Event(int timeDay, int serviceTime, int eventType) {
		this.timeOfDay = timeDay;
		this.serviceTime = serviceTime;
		this.eventType = eventType;
	}

	public static Event generateRandomArrivalEvent(int clock) {
		
		int eventTimeDay = clock + uniformRandom(3, 2, rand);
		int eventService = uniformRandom(8, 3, rand);
		
		return new Event(eventTimeDay, eventService, -1);
		
	}
	
	public static Event generateRandomDepartureEvent(int clock, int serviceTime, int tellerNum) {
		int eventTimeDay = clock + serviceTime;
		
		return new Event(eventTimeDay, serviceTime, tellerNum);
		
	}
	
	private static int uniformRandom(int mean, int variant, Random random) {
		int small = mean - variant;
		int range = 2 * variant + 1;
		return small + random.nextInt(range);
	}
	
	/**
	 * @return the timeOfDay
	 */
	public int getTimeOfDay() {
		return timeOfDay;
	}

	/**
	 * @param timeOfDay the timeOfDay to set
	 */
	public void setTimeOfDay(int timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	/**
	 * @return the serviceTime
	 */
	public int getServiceTime() {
		return serviceTime;
	}

	/**
	 * @param serviceTime the serviceTime to set
	 */
	public void setServiceTime(int serviceTime) {
		this.serviceTime = serviceTime;
	}

	/**
	 * @return the eventType
	 */
	public int getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	@Override
	public int compareTo(Event e) {
		
		if(this.getTimeOfDay() == e.getTimeOfDay()) {
			return 0;
		}
		
		if(this.getTimeOfDay() < e.getTimeOfDay()) {
			return -1;
		}
		
		if(this.getTimeOfDay() > e.getTimeOfDay()) {
			return 1;
		}
		
		return 0;
	}
	
	
	
}
