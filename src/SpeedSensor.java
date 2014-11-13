/**
 * You should *not* modify this file!
 * A simple class to report the state of the speed sensor.
 */
public class SpeedSensor {
	private double speed = 0.0;
	public void update_state(InputState new_state){
		this.speed = new_state.parseDouble(new_state.speed_sensor, this.speed);
	}
	public double get_speed(){
		return this.speed;
	}
}
