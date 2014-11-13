/**
 * You should *not* modify this file!
 * A simple class for retrieving the state of the accelerator pedal
 * in each pulse.
 */
public class AcceleratorPedalSensor {
	private double accelerator_position = 0.0;
	public void update_state(InputState new_state){
		this.accelerator_position = new_state.parseDouble(new_state.accelerator_pedal, this.accelerator_position);
	}
	public double get_accelerator(){
		return this.accelerator_position; 
	}
	public boolean is_accelerator_on(){
		return this.accelerator_position > 0.0;
	}
}
