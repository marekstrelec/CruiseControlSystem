public class CruiseControlSystem implements ICruiseControlSystem {
    private double last_throttle_value_during_cruising = 0.0;
    private boolean was_accelerating_by_pedal = false;
    private boolean is_ccs_already_on = false;
    private double recorded_throttle_value = 0.0;
    private boolean throttle_value_was_recorded = false;


    /**
     * Sets all buttons on the dashboard to false
     * @param car
     */
    private void setButtonsToFalse(Car car){
        car.dashboard.set_start_ccs(false);
        car.dashboard.set_start_accelerating(false);
        car.dashboard.set_stop_accelerating(false);
        car.dashboard.set_stop_ccs(false);
    }

    /**
     * Starts Cruising Control System
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void startCCS(Car car){
        car.dashboard.set_start_ccs(true);
        car.dashboard.set_resume(false);
        car.throttle.setThrottlePosition(car.speed_sensor.get_speed() / 50.0);
        is_ccs_already_on = true;
        last_throttle_value_during_cruising = car.throttle.getThrottlePosition();
        throttle_value_was_recorded = true;
    }

    /**
     * If the Start_CCS button was pressed and all conditions are met to start the CCS,
     * then the throttle position is set to the current speed / 50.0
     * and the start_ccs button on the dashboard is set to true
     * otherwise: all buttons are set to false and the throttle value is set to
     * the value of the accelerator pedal.
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void checkCCSStartButton(Car car){
        if (car.dashboard.get_start_ccs()
                && car.engine_sensor.is_engine_on()
                && car.speed_sensor.get_speed() >= 40.0
                && !car.brake_pedal.is_brake_on()){
                    startCCS(car);
            }else{
                setButtonsToFalse(car);
                car.throttle.setThrottlePosition(car.accelerator_pedal.get_accelerator());
            }
    }

    /**
     * If the stop_ccs button is pressed then throttle value is set to the value
     * corresponding to the value of the accelerator pedal and all buttons are set to false.
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void checkCCSStopButton(Car car){
        if (car.dashboard.get_stop_ccs()){
            was_accelerating_by_pedal = false;
            last_throttle_value_during_cruising = car.throttle.getThrottlePosition();
            car.throttle.setThrottlePosition(car.accelerator_pedal.get_accelerator());
            setButtonsToFalse(car);
        }
    }

    /**
     * If the brake pedal is pressed while the CCS is on, then the CCS must switch off
     * itself, all buttons must be set to false and the throttle position to 0.0.
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void checkBrakePedalWhileCCSisOn(Car car){
        if (car.brake_pedal.is_brake_on()
            && is_ccs_already_on){
                car.throttle.setThrottlePosition(0.0);
                setButtonsToFalse(car);
        }
    }

    /**
     * If the engine switches off while the CCS is on, then CCS must switch of itself
     * and all buttons must be set to false, and throttle position to 0.0.
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void checkEngineWhileCCSisOn(Car car){
        if (!car.engine_sensor.is_engine_on()
            && is_ccs_already_on ){
                setButtonsToFalse(car);
                car.throttle.setThrottlePosition(0.0);
        }
    }

    /**
     * If the CCS is on and the driver starts accelerating by the pedal then the
     * throttle value should be set to the corresponding accelerator pedal value
     * and the previous accelerator pedal state is set to true so that it is
     * possible to check whether the car has been accelerating before pressing
     * the stop accelerating button.
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void checkAcceleratingByPedalWhileCCSisOn(Car car){
        if (car.accelerator_pedal.is_accelerator_on()
            && !car.dashboard.get_start_accelerating()
            && !was_accelerating_by_pedal){
                was_accelerating_by_pedal = true;
                last_throttle_value_during_cruising = car.throttle.getThrottlePosition();
                car.throttle.setThrottlePosition(car.accelerator_pedal.get_accelerator());
        }
    }

    /**
     * If the driver is accelerating by the pedal and the start accelerating button is pressed
     * then the throttle position is set to the biggest value (corresponding to the speed or to
     * the position of the accelerator pedal).
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void checkStartAcceleratingButtonWhileAcceleratingByPedal(Car car){
        if (car.accelerator_pedal.is_accelerator_on()
            && car.dashboard.get_start_accelerating()){
                car.throttle.setThrottlePosition(Math.max(car.speed_sensor.get_speed() / 50.0, car.accelerator_pedal.get_accelerator()));
                last_throttle_value_during_cruising = car.throttle.getThrottlePosition();
        }
    }

    /**
     * If the CCS is on, and the driver was previously accelerating by pushing the pedal,
     * then the throttle position is set to the previous position before
     * the driver started the acceleration process.
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void setThrottleBackAfterAcceleratingByPedal(Car car){
        if (was_accelerating_by_pedal
            && !car.accelerator_pedal.is_accelerator_on()){
                car.throttle.setThrottlePosition(last_throttle_value_during_cruising);
        }
    }

    /**
     * If the CCS is on and the driver chooses to accelerate by pushing the button,
     * and the stop cruising button is not pressed, then the CCS accelerates the car
     * at manner of 2m/s^2 which corresponds to the value of the throttle.
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void checkAccelerationByButton(Car car){
        if (car.dashboard.get_start_accelerating()){
            car.throttle.setThrottlePosition((car.speed_sensor.get_speed() + 7.2) / 50.0 );
            recorded_throttle_value = car.throttle.getThrottlePosition();
            last_throttle_value_during_cruising = recorded_throttle_value;
        }
    }

    /**
     * If the CCS is on and the driver decides to stop acceleration by
     * pushing the stop acceleration button, then the throttle value
     * is set to the previously achieved value.
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void checkStopAccelerationByButton(Car car){
        if (car.dashboard.get_stop_accelerating()){
            car.throttle.setThrottlePosition(recorded_throttle_value);
            car.dashboard.set_start_accelerating(false);
            car.dashboard.set_stop_accelerating(false);
        }
    }

    /**
     * If driver selects the resume cruising function, then the throttle value is set to
     * the previous value that was selected during the last cruising session.
     * If no value is recorded beforehand, then the throttle position is set
     * to the current speed.
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    private void checkResumeCruising(Car car){
        if(!car.dashboard.get_start_ccs()){
            if (car.dashboard.get_resume()
                && throttle_value_was_recorded){
                    startCCS(car);
                    car.throttle.setThrottlePosition(recorded_throttle_value);
            } else if (car.dashboard.get_resume()){
                startCCS(car);
            }
        }
    }



    /**
     * The pulse method is called for each state of the car. It checks the sensors
     * of the car and manipulates their states accordingly.
     *
     * @param car   the class that manipulates the state of sensors on the car
     */
    public void pulse(Car car){
        this.checkCCSStartButton(car);
        this.checkCCSStopButton(car);
        this.checkBrakePedalWhileCCSisOn(car);
        this.checkEngineWhileCCSisOn(car);
        this.checkAcceleratingByPedalWhileCCSisOn(car);
        this.checkStartAcceleratingButtonWhileAcceleratingByPedal(car);
        this.setThrottleBackAfterAcceleratingByPedal(car);
        this.checkAccelerationByButton(car);
        this.checkStopAccelerationByButton(car);
        this.checkResumeCruising(car);

    }
}
