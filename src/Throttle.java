/**
 * You should *not* modify this file!
 * Stores the value that the throttle is set to for output after
 * each pulse. Before each pulse the throttle is set to whatever
 * value the accelerator pedal is set to. The CCS system may then
 * set the throttle position to a higher position during its run
 * within the pulse.
 */
public class Throttle {
	private double throttlePosition = 0.0;
	// We have to maintain a memory of what the previous accelerator position
	// was because it might be set to '-' in the state line, meaning whatever
	// was previously.
	private double acceleratorPosition = 0.0;
	
	public double getThrottlePosition(){
		return this.throttlePosition;
	}
	
	/**
	 * Sets the current throttle position, however, note that this will only
	 * have an affect if the new_position provided is higher than the current
	 * position.
	 */
	public void setThrottlePosition(double new_position){
		// We only update the throttle position if it is attempting to set it
		// to a higher position than the current position.
		this.throttlePosition = Math.max(this.throttlePosition, new_position);
	}
	
	/**
	 * Before the CCS is given control via its pulse, the position of the throttle
	 * is set to whatever position the accelerator pedal is set to. This may of
	 * course be zero. If the CCS determines that it should be higher than this,
	 * then it should do so via `setThrottlePosition`. Note that this means the
	 * CCS must set the throttle position in *every* pulse for which it is on.
	 */
	public void update_state(InputState new_state){
		this.acceleratorPosition = new_state.parseDouble(new_state.accelerator_pedal, this.acceleratorPosition);
		this.throttlePosition = this.acceleratorPosition;
	}
}
