/**
 * You should *not* modify this file!
 * An output state object is used to record the state of the car
 * after each pulse. An output state object is essentially the
 * same as an input state except that there is an additional field
 * recording the position of the throttle.
 */
public class OutputState extends InputState {
	private double throttle_position = 0.0;
	OutputState(String input_string, double throttle_position) {
		super(input_string);
		this.throttle_position = throttle_position;
	}
	
	public double get_throttle_position(){
		return this.throttle_position;
	}
	
	public String format(){
		return String.format("%s %s %s %s %s %s %s %s %s %f",
							 this.engine_status,
							 this.speed_sensor,
							 this.brake_pedal,
							 this.accelerator_pedal,
							 this.start_ccs_button,
							 this.stop_ccs_button,
							 this.start_acceleration_button,
							 this.stop_acceleration_button,
							 this.resume_button,
							 this.throttle_position);
	}
}
