/**
 * You should *not* modify this file!
 * A simple Timer class. Given a list of input states this class calls
 * the car's update_states method *before* the CCS is then given control
 * during its pulse. After the CCS returns, the current state of the car
 * is recorded as an OutputState.
 */

import java.util.LinkedList;
import java.util.List;


public class Timer {
	private Car car;
	private ICruiseControlSystem ccs;
	
	Timer(ICruiseControlSystem ccs){
		this.ccs = ccs;
		this.car = new Car();
	}
	
	public List<OutputState> pulse_from_input(List<InputState> input_states){
		LinkedList<OutputState> output_states = new LinkedList<OutputState>();

		for (InputState next_state : input_states){
			this.car.update_states(next_state);
			this.ccs.pulse(this.car);
			output_states.add(this.car.record_state());
		}
		return output_states;
	}
}
