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


    private double previous_throttle_value = 0.0 ;
    private boolean was_accelerating_by_pedal = false;
    private boolean is_ccs_already_on = false;
    private boolean was_accelerating_button_pressed;

    // Method that sets all buttons on the dashboard to false
    private void setButtonsToFalse(Car car){
        car.dashboard.set_start_ccs(false);
        car.dashboard.set_start_accelerating(false);
        car.dashboard.set_stop_accelerating(false);
        car.dashboard.set_stop_ccs(false);
        car.dashboard.set_resume(false);
    }

    // Method that starts cruising
    private void startCCS(Car car){
        car.throttle.setThrottlePosition(car.speed_sensor.get_speed() / 50.0);
        is_ccs_already_on = true;
        previous_throttle_value = car.throttle.getThrottlePosition();
    }
    public void pulse(Car car){

        // if the Start_CCS button was pressed and all conditions are met to start CCS
        // then throttle position is set to the current speed divided by 50.0
        // and button on the dashboard start_ccs is set to true else set all buttons to false
        // and throttle value to set to the value of accelerator pedal
        if (car.dashboard.get_start_ccs()
            && car.engine_sensor.is_engine_on()
            && car.speed_sensor.get_speed() >= 40.0
            && !car.brake_pedal.is_brake_on()){
                startCCS(car);
        } else{
            setButtonsToFalse(car);
            car.throttle.setThrottlePosition(car.accelerator_pedal.get_accelerator());
        }

        // If stop_ccs button is pressed then throttle value is set to the value
        // corresponding to accelerator pedal value and all buttons are set to false
        if (car.dashboard.get_stop_ccs()){
                setButtonsToFalse(car);
                was_accelerating_by_pedal = false;
                previous_throttle_value = car.throttle.getThrottlePosition();
                car.throttle.setThrottlePosition(car.accelerator_pedal.get_accelerator());
        }


        // if the brake pedal is hit while CCS is on, then CCS must switch of
        // itself and all buttons must be set to false, and throttle position to 0.0
        if (car.brake_pedal.is_brake_on()
            && is_ccs_already_on){
                car.throttle.setThrottlePosition(0.0);
                setButtonsToFalse(car);

        }

        // If engine switches off while CCS is on, then CCS must switch of itself
        // and all buttons must be set to false, and throttle position to 0.0
        if (!car.engine_sensor.is_engine_on()
            && is_ccs_already_on ){
                setButtonsToFalse(car);
                car.throttle.setThrottlePosition(0.0);
        }

        // if CCS is on and driver starts accelerating by pedal then throttle
        // value should be set to the corresponding accelerator pedal value
        // and previous accelerator pedal state is set to true so that it would
        // be possible to check whether acceleration function was selected before
        // stop acceleration function was chosen
        if (car.accelerator_pedal.is_accelerator_on()
            && !car.dashboard.get_start_accelerating()
            && !was_accelerating_by_pedal){
                was_accelerating_by_pedal = true;
                previous_throttle_value = car.throttle.getThrottlePosition();
                car.throttle.setThrottlePosition(car.accelerator_pedal.get_accelerator());
        }

        // if driver is accelerating by pedal and start acceleration button is not pressed
        // and he was accelerating by button in the previous pulse
        // then throttle value must correspond to the position of accelerator pedal
        /*if (car.accelerator_pedal.is_accelerator_on()
                && !car.dashboard.get_start_accelerating()){
                    car.throttle.setThrottlePosition(car.accelerator_pedal.get_accelerator());
            }*/

        // if driver is accelerating by pedal and start accelerating button is pressed
        // then throttle position is set to the biggest value (corresponding to speed or to
        // accelerator pedal value
        if (car.accelerator_pedal.is_accelerator_on()
            && car.dashboard.get_start_accelerating()){
                car.throttle.setThrottlePosition(Math.max(car.speed_sensor.get_speed() / 50.0, car.accelerator_pedal.get_accelerator()));
                previous_throttle_value = car.throttle.getThrottlePosition();
        }

        // if CCS is on, and driver was previously was accelerating by pedal
        // then throttle position is set to the previous position before
        // driver started accelerating by pedal
        if (was_accelerating_by_pedal
            && !car.accelerator_pedal.is_accelerator_on()){
                car.throttle.setThrottlePosition(previous_throttle_value);
        }

        // if CCS is on and the driver chooses accelerate by button function,
        // and stop cruising button was not pressed, then CCS accelerates the car
        // at manner of 2m/s^2 which corresponds to the value of the throttle
        if (car.dashboard.get_start_accelerating()){
                car.dashboard.set_stop_accelerating(false);
                was_accelerating_button_pressed = true;
                car.throttle.setThrottlePosition((car.speed_sensor.get_speed() + 7.2) / 50.0 );
                previous_throttle_value = car.throttle.getThrottlePosition();

        }

        // if CCS is on and driver previously accelerated by button
        // and then chooses stop accelerating function, then throttle value
        // is set to the previously achieved value during the acceleration
        if (car.dashboard.get_stop_accelerating()){
            car.throttle.setThrottlePosition(previous_throttle_value);
            car.dashboard.set_start_accelerating(false);
            car.dashboard.set_stop_accelerating(false);
            previous_throttle_value = car.throttle.getThrottlePosition();
        }

        // if driver selects resume cruising function, then throttle value is set to
        // the previous value that was selected during the last cruising session
        // else if no value is recorded before, then throttle position is set
        // to corresponding current speed
        if (car.dashboard.get_resume()){
                car.throttle.setThrottlePosition(previous_throttle_value);
                startCCS(car);
        } else {
            if (car.dashboard.get_resume()
                && previous_throttle_value == 0.0){
                    car.throttle.setThrottlePosition(car.speed_sensor.get_speed() / 50.0);
            }
        }

    }
}
