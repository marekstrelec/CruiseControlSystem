/**
 * You should *not* modify this file!
 * A simple class for retrieving the current state of the engine via
 * its sensor. Simply allows you to determine whether it is on or off.
 */
public class EngineSensor {
	private boolean engine_on = false;
	public void update_state(InputState new_state){
		this.engine_on = new_state.parseBoolean(new_state.engine_status, this.engine_on);
	}
	public boolean is_engine_on(){
		return this.engine_on;
	}
}
