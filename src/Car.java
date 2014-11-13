/**
 * You should *not* modify this file!
 * The main class through which you can interrogate the state
 * of the individual sensors on the car. The pulse method of your
 * CCS class will receive a Car object as a parameter. Use this to
 * determine what actions to take within the current pulse.
 */
public class Car {
	public EngineSensor engine_sensor = new EngineSensor();
	public Dashboard dashboard = new Dashboard();
	public SpeedSensor speed_sensor = new SpeedSensor();
	public BrakePedalSensor brake_pedal = new BrakePedalSensor();
	public AcceleratorPedalSensor accelerator_pedal = new AcceleratorPedalSensor();
	public Throttle throttle = new Throttle();
	
	public void update_states(InputState new_state){
		this.engine_sensor.update_state(new_state);
		this.dashboard.update_state(new_state);
		this.speed_sensor.update_state(new_state);
		this.brake_pedal.update_state(new_state);
		this.accelerator_pedal.update_state(new_state);
		this.throttle.update_state(new_state);
	}
	
	public OutputState record_state(){
		String state_string = String.format("%b %f %f %f %b %b %b %b %b",
							 				 this.engine_sensor.is_engine_on(),
											 this.speed_sensor.get_speed(),
											 this.brake_pedal.get_brake(),
											 this.accelerator_pedal.get_accelerator(),
											 this.dashboard.get_start_ccs(),
											 this.dashboard.get_stop_ccs(),
											 this.dashboard.get_start_accelerating(),
											 this.dashboard.get_stop_accelerating(),
											 this.dashboard.get_resume());
		return new OutputState(state_string, this.throttle.getThrottlePosition());
	}
}
