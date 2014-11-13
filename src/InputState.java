/**
 * You should *not* modify this file!
 * This class defines the input states. These are the values of the
 * system components that you are able to interrogate, however, you
 * should interrogate them through their own defined classes:
 * AcceleratorPedalSensor, SpeedSensor, BrakePedalSensor, EngineSensor,
 * and DashBoard.
 */
public class InputState {
	public String engine_status = "true";
	public String speed_sensor = "50.0";
	public String brake_pedal = "0.0";
	public String accelerator_pedal = "1.0";
	
	public String start_ccs_button = "false";
	public String stop_ccs_button = "false";
	public String start_acceleration_button = "false";
	public String stop_acceleration_button = "false";
	public String resume_button = "false";
	
	// Hence the default state line would look like:
	// "true 50.0 0.0 1.0 false false false false false"
	
	public boolean parseBoolean(String s, boolean current){
		if (s.equals("-")){
			return current;
		} else {
			return Boolean.parseBoolean(s);
		}
	}
	
	public double parseDouble(String s, double current){
		if (s.equals("-")){
			return current;
		} else {
			return Double.parseDouble(s);
		}
	}
	
	InputState(String input_string){
		String[] words = input_string.split(" ");
		this.engine_status = words[0];
		this.speed_sensor = words[1];
		this.brake_pedal = words[2];
		this.accelerator_pedal = words[3];
		
		this.start_ccs_button = words[4];
		this.stop_ccs_button = words[5];
		this.start_acceleration_button = words[6];
		this.stop_acceleration_button = words[7];
		this.resume_button = words[8];
	}
}
