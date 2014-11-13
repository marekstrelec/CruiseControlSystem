/**
 * You should *not* modify this file!
 * A simple class which can be used to interrogate the status
 * of the brake pedal in each pulse. 
 */
public class BrakePedalSensor {
	private double brake_position = 0.0;
	public void update_state(InputState new_state){
		this.brake_position = new_state.parseDouble(new_state.brake_pedal, this.brake_position);
	}
	public double get_brake(){
		return this.brake_position; 
	}
	public boolean is_brake_on(){
		return this.brake_position > 0.0;
	}
}
