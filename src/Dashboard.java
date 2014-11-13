/**
 * You should *not* modify this file!
 * The dashboard class holds the information about each of the
 * buttons on the dashboard of the car. Each button is either in
 * a state of being "depressed" or "pushed-in", which is represented
 * by "true". Or the button is not "depressed/pushed-in" represented
 * by "false".
 * Your implementation of the CCS should make sure to not only
 * interrogate these buttons to determine the *current* state of
 * the car, but also *set* the states of these appropriately. 
 */
public class Dashboard {
	private boolean start_ccs_button = false;
	private boolean stop_ccs_button = false;
	private boolean start_accelerating_button = false;
	private boolean stop_accelerating_button = false;
	private boolean resume_button = false;
	
	public void update_state(InputState new_state){
		this.start_ccs_button = new_state.parseBoolean(new_state.start_ccs_button, this.start_ccs_button);
		this.stop_ccs_button = new_state.parseBoolean(new_state.stop_ccs_button, this.stop_ccs_button);
		this.start_accelerating_button = new_state.parseBoolean(new_state.start_acceleration_button, this.start_accelerating_button);
		this.stop_accelerating_button = new_state.parseBoolean(new_state.stop_acceleration_button, this.stop_accelerating_button);
		this.resume_button = new_state.parseBoolean(new_state.resume_button, this.resume_button);
	}
	
	public boolean get_start_ccs(){
		return this.start_ccs_button;
	}
	public void set_start_ccs(boolean status){
		this.start_ccs_button = status;
	}
	public boolean get_stop_ccs(){
		return this.stop_ccs_button;
	}
	public void set_stop_ccs(boolean status){
		this.stop_ccs_button = status;
	}
	public boolean get_start_accelerating(){
		return this.start_accelerating_button;
	}
	public void set_start_accelerating(boolean status){
		this.start_accelerating_button = status;
	}
	public boolean get_stop_accelerating(){
		return this.stop_accelerating_button;
	}
	public void set_stop_accelerating(boolean status){
		this.stop_accelerating_button = status;
	}
	public boolean get_resume(){
		return this.resume_button;
	}
	public void set_resume(boolean status){
		this.resume_button = status;
	}
}
