import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.Test;

public class BasicTests {

    /**
     * Temporarily captures the output to the standard output stream, then
     * restores the standard output stream once complete.
     *
     * @param args
     *            arguments to pass to main function of class to be tested
     * @return output result of calling main function of class to be tested
     * @throws IOException
     */
    private String captureOutputOfMain(String args[]) throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            CommandLine.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            System.setOut(originalOut);
        }
        return outputStream.toString().trim();
    }

    /**
     * A simple function to run the a list of input states defined as an
     * array of strings to retrieve a list of output states.
     */
    private List<OutputState> run_input_states(String[] input_lines){
        List<InputState> test_input_states = StateInput.input_states_from_strings(input_lines);

        Timer timer = new Timer(new CruiseControlSystem());
        return timer.pulse_from_input(test_input_states);
    }

    /**
     * Runs the a list of input states defined as strings to produce the
     * list of output states and then simply selects the final output state.
     */
    private OutputState get_final_state(String[] input_lines){
        List<OutputState> output_states = run_input_states(input_lines);
        return output_states.get(output_states.size() - 1);
    }

    @Test
    public void test_command_line() throws IOException{
        String expected = "true 50.000000 0.000000 0.500000 false false false false false 0.500000\n"
                        + "true 50.000000 0.000000 0.500000 true false false false false 1.000000";
        String[] arguments = { "test-input-files/simple-input.text" };
        String actual_output = this.captureOutputOfMain(arguments);
        assertTrue(expected.equals(actual_output));
    }



    /* START CCS */

    @Test
    public void test_start_ccs() {
        // Create an input such that the CCS should be started and such that
        // the throttle position should be set by the CCS in at least one
        // pulse.
        String[] input_lines = { "true 50.0 0.0 0.5 false false false false false",
                                 "- - - - true - - - -"};
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_ccs_button));
        assertEquals(1.0, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_start_ccs_after_off() {
        // Create an input such that the CCS should be started and it has been switched off before
        String[] input_lines = { "true 50.0 0.0 0.5 false false false false false",
                                 "- - - - true - - - -",
                                 "- - - - - true - - -",
                                 "- - - - true - - - -"};
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_ccs_button));
        assertEquals(1.0, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_start_ccs_when_engine_is_off() {
        // Create an input such that the CCS should NOT be started because the engine is off
        String[] input_lines = { "false 50.0 0.0 0.5 false false false false false",
                                 "- - - - true - - - -"};
        OutputState final_state = get_final_state(input_lines);

        assertFalse(Boolean.parseBoolean(final_state.start_ccs_button));
        assertEquals(0.5, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_start_ccs_speed_low() {
        // Create an input such that the CCS should NOT be started because the engine is <50 km/h
        String[] input_lines = { "true 39.0 0.0 0.5 false false false false false",
                                 "- - - - true - - - -"};
        OutputState final_state = get_final_state(input_lines);

        assertFalse(Boolean.parseBoolean(final_state.start_ccs_button));
        assertEquals(0.5, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_start_ccs_brakepedal_on() {
        // Create an input such that the CCS should NOT be started because the brake pedal is pressed
        String[] input_lines = { "true 50.0 0.5 0.0 false false false false false",
                                 "- - - - true - - - -"};
        OutputState final_state = get_final_state(input_lines);

        assertFalse(Boolean.parseBoolean(final_state.start_ccs_button));
        assertEquals(0.0, final_state.get_throttle_position(), 0.001);
    }



    /* START ACCELERATING */

    @Test
    public void test_start_accelerating_50kmh(){
        // Create an input such that the current speed of the car is 50km/h and
        // the driver starts the CCS and pushes the acceleration button
        String[] input_lines = { "true 50.0 0.0 1.0 false false false false false",
                                 "- - - - true - - - -",
                                 "- - - - - - true - -" };
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertTrue(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The trottle position 1.144 represents 57.2km/h (7.2km/h = 2m/s)
        assertEquals(1.144, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_start_accelerating_132kmh(){
        // Create an input such that the current speed of the car is 132km/h and
        // the driver starts the CCS and pushes the acceleration button
        String[] input_lines = { "true 132.0 0.0 1.0 false false false false false",
                                 "- - - - true - - - -",
                                 "- - - - - - true - -" };
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertTrue(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The throttle position 2.784 represents 139.2km/h (7.2km/h = 2m/s)
        assertEquals(2.784, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_accelerate_by_pedal(){
        // Create an input such that the CCS is on and the driver is accelerating by pedal.
        // The throttle value must be set to the value of the accelerator pedal
        String[] input_lines = { "true 50.0 0.0 0.0 true false false false false",
                                 "- - - 1.8 - - - - -",
                                 "- - - 1.9 - - - - -"};
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The throttle value of 1.9 corresponds to the value of accelerator pedal
        // which is greater than the value that would correspond to the speed
        assertEquals(1.9, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_accelerate_by_pedal_2(){
        // Create an input such that the CCS is on and the driver is accelerating by pedal.
        // The throttle value must be set to the value of the accelerator pedal
        String[] input_lines = { "true 117.0 0.0 0.0 true false false false false",
                                 "- - - 3.7 - - - - -",
                                 "- - - 2.35 - - - - -"};
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The throttle value of 2.35 corresponds to the value of accelerator pedal
        // which is greater than the value that would correspond to the speed
        assertEquals(2.35, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_accelerate_by_pedal_is_low(){
        // Create an input such that the CCS is on and the driver is accelerating by pedal.
        // The throttle value must be set to the value of the CCS because the pedal position
        // indicates lower throttle value then the CCS
        String[] input_lines = { "true 50.0 0.0 0.0 true false false false false",
                                 "- - - 2.7 - - - - -",
                                 "- - - 0.1 - - - - -"};
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The throttle value of 1.0 corresponds to the speed and this value
        // is greater than the value that would correspond to accelerator pedal
        // in the last pulse
        assertEquals(1.0, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_accelerate_by_pedal_pedal_is_low_2(){
        // Create an input such that the CCS is on and the driver is accelerating by pedal.
        // The throttle value must be set to the value of the CCS because the pedal position
        // indicates lower throttle value then the CCS
        String[] input_lines = { "true 117.0 0.0 0.0 true false false false false",
                                 "- - - 3.7 - - - - -",
                                 "- - - 2.33 - - - - -"};
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The throttle value of 2.34 corresponds to the speed and this value
        // is greater than the value that would correspond to accelerator pedal
        // in the last pulse
        assertEquals(2.34, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_acclerate_by_pedal_while_accelerating_by_button(){
        String[] input_lines = { "true 117.0 0.0 0.0 true false false false false",
                                 "- - - - - - true - -",
                                 "- - - 2.483 - - - - -" };
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertTrue(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The throttle value of 2.484 corresponds to the speed and this value
        // is greater than the value that would correspond to accelerator pedal
        // in the last pulse (we assume that speed hasn't changed)
        assertEquals(2.484, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_acclerate_by_pedal_while_accelerating_by_button_2(){
        String[] input_lines = { "true 117.0 0.0 0.0 true false false false false",
                                 "- - - - - - true - -",
                                 "- - - 2.485 - - - - -" };
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertTrue(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The throttle value of 2.485 corresponds to the position of accelerator
        // pedal which is greater than the value that would correspond to the speed
        // in the last pulse (we assume that speed hasn't changed)
        assertEquals(2.485, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_stop_accelerating_by_button(){
        // After stopping acceleration, CCS should maintain the speed that was achieved
        // during acceleration. Speed and throttle_position should remain the same
        String[] input_lines = { "true 50.0 0.0 0.0 true false false false false",
                                  "- - - - - - true - -",
                                  "- - - - - - - true -" };
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The throttle value of 1.144 corresponds to the speed after acceleration
        // by button
        assertEquals(1.144, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_accelerate_by_pedal_after_previous_acceleration(){
        // If the driver accelerates by pedal after stopping acceleration by button,
        // throttle value should be set to the higher value corresponding to the speed
        // or to the accelerator pedal position
        String[] input_lines = { "true 50.0 0.0 0.0 true false false false false",
                                 "- - - - - - true - -",
                                 "- - - - - - - true -",
                                 "- - - 1.2 - - - - -" };
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The throttle value of 1.2 corresponds to the position of the accelerator
        // pedal because this value is greater than the the throttle value after
        // acceleration by button which is 1.114
        assertEquals(1.2, final_state.get_throttle_position(), 0.001);
    }


    @Test
    public void test_stop_accelerating_by_pedal(){
        // After driver was accelerating by pedal while CCS is on and if he is
        // not accelerating anymore, CCS should set throttle value corresponding
        // to the speed
        String[] input_lines = { "true 50.0 0.0 0.0 true false false false false",
                                  "- - - 1.2 - - - - -",
                                  "- - - 0.0 - - - - -"};
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));

        // The throttle value of 1.0 corresponds to the speed because driver is not
        // accelerating by pedal
        assertEquals(1.0, final_state.get_throttle_position(), 0.001);
    }



    /* RESUME CRUISING */

    @Test
    public void test_resume_cruising_when_CCS_is_on(){
        // Create an input that resumes the CCS when the CCS is switched on
        String[] input_lines = { "true 50.0 0.0 0.0 false false false false false",
                                  "- - - - - - - - true"};
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.resume_button));
        assertEquals(1.0, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_resume_cruising_without_recorded_throttle_value(){
        // Create an input that resumes the CCS when the CCS has not been started yet
        // => no throttle value is recorded
        String[] input_lines = { "true 50.0 0.0 0.0 false false false false false",
                                  "- - - - - - - - true"};
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.resume_button));
        assertEquals(1.0, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_resume_cruising_with_recorded_throttle_value (){
        // Create an input that resumes the CCS when the CCS has been already started
        // => the throttle value is recorded
        String[] input_lines = { "true 50.0 0.0 0.0 true false false false false",
                                  "- - - - - - true - -",
                                  "- - - - - - - true -",
                                  "- - - - - true - - -",
                                  "true 50.0 0.0 0.0 false false false false false",
                                  "- - - - - - - - true"};
        OutputState final_state = get_final_state(input_lines);

        assertTrue(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.resume_button));
        assertEquals(1.144, final_state.get_throttle_position(), 0.001);
    }



    /* STOP CCS */

    @Test
    public void test_stop_cruising_by_button_when_accelerating_by_pedal(){
        // Create an input such that the CCS is turned on, the accelerating pedal is pressed and later,
        // the CCS is turned off by pressing the button
        String[] input_lines = { "true 50.0 0.0 0.0 true false false false false",
                                 "- - - 1.5 - - - - -",
                                 "- - - - - true - - -" };
        OutputState final_state = get_final_state(input_lines);

        assertFalse(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));
        assertEquals(1.5, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_stop_cruising_by_brake_pedal(){
        // Create an input such that the CCS is turned on and the driver pushes the brake pedal
        String[] input_lines = { "true 50.0 0.0 0.0 false false false false false",
                                "- - - - true - - - -",
                                "- - 0.1 - - - - - -" };
        OutputState final_state = get_final_state(input_lines);

        assertFalse(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));
        assertEquals(0.0, final_state.get_throttle_position(), 0.001);
    }

    @Test
    public void test_stop_cruising_by_engine(){
        // Create input such that the CCS is running and the engine switches off.
        String[] input_lines = { "true 50.0 0.0 0.0 false false false false false",
                                "- - - - true - - - -",
                                "- - - - - - - - - ",
                                "false - - - - - - - - -" };
        OutputState final_state = get_final_state(input_lines);

        assertFalse(Boolean.parseBoolean(final_state.engine_status));
        assertFalse(Boolean.parseBoolean(final_state.start_ccs_button));
        assertFalse(Boolean.parseBoolean(final_state.start_acceleration_button));
        assertFalse(Boolean.parseBoolean(final_state.stop_acceleration_button));
        assertEquals(0.0, final_state.get_throttle_position(), 0.001);
    }

}
