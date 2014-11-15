/**
 * This is the main class that you need to implement. You only have
 * a single method to implement, but of course that may be easier if
 * you define some auxiliary methods.
 */
public class CruiseControlSystem implements ICruiseControlSystem {
	/*
	 * Students may add any private fields or methods that they deem
	 * necessary. Public ones should not be necessary (there is no
	 * rule against it, but you should not be changing the support code
	 * and the rest of the code knows only about this).
	 */
	double previous_throttle_value ;
	boolean previous_accelerator_pedal_state = false;
	boolean is_ccs_already_on = false;
	boolean was_accelerator_button_pressed;
	
	public static void main(String[] args) {
		 System.out.print("Cruise Control System started");
	
	}
	
	public void pulse(Car car){
		// if the Start_CCS button was pressed and all conditions are met to start CCS
		// then throttle position is set to the current speed divided by 50.0 
		// and button on the dashboard start_ccs is set to true else set all buttons to false
		// and throttle value to set to the value of accelerator pedal
		if (car.dashboard.get_start_ccs() == true
			&& car.engine_sensor.is_engine_on() == true 
			&& car.speed_sensor.get_speed() >= 50.0
			&& car.brake_pedal.is_brake_on() == false){
				car.throttle.setThrottlePosition(car.speed_sensor.get_speed() / 50.0);
				car.dashboard.set_start_ccs(true);
				car.dashboard.set_stop_ccs(false);
				is_ccs_already_on = car.dashboard.get_start_ccs();
		} else {
			car.dashboard.set_start_ccs(false);
			car.dashboard.set_start_accelerating(false);
			car.dashboard.set_stop_accelerating(false);
			car.dashboard.set_stop_ccs(false);
			car.dashboard.set_resume(false);
			car.throttle.setThrottlePosition(car.accelerator_pedal.get_accelerator());
			
		}
		
		// If CCS is already on, driver is also accelerating by pedal and stop_ccs button is 
		// pressed then throttle value is set to the value corresponding to accelerator pedal
		// and all buttons are set to false, else if driver is not accelerating but chooses stop cruising
		// then all buttons are set to false and throttle value is set to 0.0
		if (car.dashboard.get_stop_ccs() == true
			&& is_ccs_already_on == true	
			&& car.dashboard.get_start_ccs() != false
			&& car.accelerator_pedal.is_accelerator_on() == true ){
				car.dashboard.set_start_ccs(false);
				car.dashboard.set_start_accelerating(false);
				car.dashboard.set_stop_accelerating(false);
				car.dashboard.set_stop_ccs(false);
				car.dashboard.set_resume(false);
				car.throttle.setThrottlePosition(car.accelerator_pedal.get_accelerator());
		} else {
			if (car.dashboard.get_stop_ccs() == true
					&& is_ccs_already_on == true	
					&& car.dashboard.get_start_ccs() != false
					&& car.accelerator_pedal.is_accelerator_on() == false ){
						car.dashboard.set_start_ccs(false);
						car.dashboard.set_start_accelerating(false);
						car.dashboard.set_stop_accelerating(false);
						car.dashboard.set_stop_ccs(false);
						car.dashboard.set_resume(false);
						car.throttle.setThrottlePosition(0.0);
			}			
		}
		
		// if the brake pedal is hit while CCS is on, then CCS must switch of 
		// itself and all buttons must be set to false, and throttle position to 0.0
		if (car.brake_pedal.is_brake_on() == true
			&& is_ccs_already_on == true
			&& car.dashboard.get_start_ccs() != false){
				car.dashboard.set_start_ccs(false);
				car.dashboard.set_start_accelerating(false);
				car.dashboard.set_stop_accelerating(false);
				car.dashboard.set_stop_ccs(false);
				car.dashboard.set_resume(false);
				car.throttle.setThrottlePosition(0.0);
		}
		
		// If engine switches off while CCS is on, then CCS must switch of itself
		// and all buttons must be set to false, and throttle position to 0.0
		if (car.engine_sensor.is_engine_on() == false
			&& is_ccs_already_on == true
			&& car.dashboard.get_start_ccs() != false){
				car.dashboard.set_start_ccs(false);
				car.dashboard.set_start_accelerating(false);
				car.dashboard.set_stop_accelerating(false);
				car.dashboard.set_stop_ccs(false);
				car.dashboard.set_resume(false);
				car.throttle.setThrottlePosition(0.0);
		}		
		
		// if CCS is on and driver starts accelerating by pedal then throttle
		// value should be set to the corresponding accelerator pedal value
		// and previous accelerator pedal state is set to true so that it would
		// be possible to check whether acceleration function was selected before
		// stop acceleration function was chosen
		if (car.accelerator_pedal.is_accelerator_on() == true
			&& is_ccs_already_on == true
			&& car.dashboard.get_start_ccs() != false
			&& car.dashboard.get_stop_ccs() != true){
				previous_accelerator_pedal_state = true;
				previous_throttle_value = car.throttle.getThrottlePosition();
				car.throttle.setThrottlePosition(car.accelerator_pedal.get_accelerator());
		}
		
		// if CCS is on, and driver is not accelerating by pedal anymore 
		// thus if previously acc. pedal position was greater than 0.0 and in the  
		// next pulse it it is 0.0 and CCS is on, then throttle position
		// is set to the previous position
		if (car.accelerator_pedal.is_accelerator_on() == true 
			&& previous_accelerator_pedal_state == true
			&& is_ccs_already_on == true
			&& car.dashboard.get_start_ccs() != false
			&& car.dashboard.get_stop_ccs() != true){
				car.throttle.setThrottlePosition(previous_throttle_value);
		}
		
		// if CCS is on and the driver chooses accelerate by button function,
		// and stop cruising button was not pressed, then CCS accelerates the car
		// at manner of 2m/s^2 which corresponds to the value of the throttle
		if (car.dashboard.get_start_accelerating() == true
			&& is_ccs_already_on == true
			&& car.dashboard.get_start_ccs() != false){
				car.dashboard.set_stop_accelerating(false);
				was_accelerator_button_pressed = true;
				car.throttle.setThrottlePosition((car.speed_sensor.get_speed() + 7.2) / 50.0 );
				previous_throttle_value = car.throttle.getThrottlePosition(); 
				
		}
	
		// if CCS is on and driver previously accelerated by button
		// and then chooses stop accelerating function, then throttle value
		// is set to the previously achieved value during the acceleration
		if (car.dashboard.get_stop_accelerating() == true 
			&& is_ccs_already_on == true
			&& car.dashboard.get_start_ccs() != false
			&& was_accelerator_button_pressed == true){
			car.throttle.setThrottlePosition(previous_throttle_value); 
		}
		
		 
		
	}
}
