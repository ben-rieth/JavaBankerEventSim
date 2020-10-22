This project was done for my Data Structures class and uploaded at a later date. 

This text-based Java project simulates a bank, where customers are placed in a queue to wait to see a teller. The simulation is Event-based not time-based, so time only moves forward when a new event is created and the time 'jumps' to the time fo the new event. In this case, the events are either customers arriving or departing from the teller.

The lines for each of the tellers represents a priority queue, where a customer is placed into the queue based on the time that their event (arriving or departing) occurs. This priorty queue is how the event driven simulation is implemented.
