import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.Test;

/**
 * Test that the CCS behaves as expected at least for the basic tests.
 * You should extend this class with your own tests.
 * 
 */
public class BasicTests {

    /**
     * Temporarily captures the output to the standard output stream, then
     * restores the standard output stream once complete.
     * 
     * @param args
     *            arguments to pass to main function of class to be tested
     * @return output result of calling main function of class to be tested
     */
    private String captureOutputOfMain(String args[]) {
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
    public void test_command_line(){
    	String expected = "true 50.000000 0.000000 0.500000 false false false false false 0.500000\n"
    					+ "true 50.000000 0.000000 0.500000 true false false false false 1.000000";
    	String[] arguments = { "test-input-files/simple-input.text" };
    	String actual_output = this.captureOutputOfMain(arguments);
    	assertTrue(expected.equals(actual_output));
    }
    
    @Test
    public void test_start_ccs() {
    	// Create input such that the CCS should be started and such that
    	// the throttle position should be set by the CCS in at least one
    	// pulse.
    	String[] input_lines = { "true 50.0 0.0 0.5 false false false false false",
    							 "- - - - true - - - -"};
    	OutputState final_state = get_final_state(input_lines);
    	assertTrue(final_state.get_throttle_position() == 1.0);
    }
    
    @Test
    public void test_start_accelerating(){
    	String[] input_lines = { "true 50.0 0.0 1.0 false false false false false",
				 			     "- - - - true - - - -",
				 				 "- - - - - - true - -"};
    	OutputState final_state = get_final_state(input_lines);
    	// The speed of the car is 50km/h so we should set the throttle position
    	// to a position which reflects 57.2km/h (because 7.2km/h = 2m/s)
    	assertTrue(final_state.get_throttle_position() == 1.1440000000000001);
    }
    
    @Test
	public void test_resume_cruising(){
		// Give an input which would change throttle position (acceleration) and during 
		// 5th pulse stop cruising
 		// in last pulse resume cruising, and this should return throttle position which 
		// was recorded after 4th pulse input
		String[] input_lines1 = { "true 50.0 0.0 0.0 true false false false false", 	// start cruising
			     				  "- - - - - - true - -",	// start accelerating, throttle_posiyion = 1.144000 
			     				  "- - - - - - - - -",		// still accelerating, throttle_position = 1.288000
								  "- - - - - - - true -" };	// stop accelerating, keeps previous throttle_position
		OutputState final_state1 = get_final_state(input_lines1);
		String[] input_lines2 = { "- - - - - true - - -",	// stop cruising, throttle_position = 0.000000
								  "- - - - - - - - true" };	// resume cruising, keeps last trottle_position
															// achieved during cruising
		
		OutputState final_state2 = get_final_state(input_lines2);
		// If current throttle_position after resuming CCS matches with the position 
		// that was before witching off CCS, then test passes
		assertTrue(final_state1.get_throttle_position() == final_state2.get_throttle_position());
	}
    
    @Test
	public void test_stop_cruising_by_button(){
		String[] input_lines = { "true 50.0 0.0 0.0 false false false false false",
								 "- - - - true - - - -",
								 "- - - - - true - - -" };
		
		OutputState final_state = get_final_state(input_lines);
		// Create input such that the CCS should be turned off by button press 
    	// and such that the throttle position should be set to 0 by the CCS.
		// We need to assume that CCS was turned on before it was switched off. 
		assertTrue(final_state.get_throttle_position() == 0.0000000000000000);
	}
	
	@Test
	public void test_stop_cruising_by_brake_pedal(){
		String[] input_lines = { "true 50.0 0.0 0.0 false false false false false",
								"- - - - true - - - -",
								"- - 0.1 - - - - - -" };
		
		OutputState final_state = get_final_state(input_lines);
		// Create input such that the CCS should be turned off by pressing brake pedal 
    	// and such that the throttle position should be set to 0 by the CCS.
		// We need to assume that CCS had been turned on before brake pedal was pressed. 
		assertTrue(final_state.get_throttle_position() == 0.0000000000000000);
	}
		
	@Test
	public void test_stop_cruising_by_engine(){
		String[] input_lines = { "true 50.0 0.0 0.0 false false false false false",
								"- - - - true - - - -",
								"false - - - - - - - - -" };
		
		OutputState final_state = get_final_state(input_lines);
		// Create input such that the CCS should be turned off when engine switches off 
    	// and such that the throttle position should be set to 0 by the CCS.
		// We need to assume that CCS had been turned on before engine switched off. 
		assertTrue(final_state.get_throttle_position() == 0.0000000000000000);
	}	
	
	@Test
	public void test_stop_accelerating_by_button(){
		String[] input_lines1 = { "true 65.0 0.0 0.0 true false false false false",
								 "- - - - - - true - -",
								 "- - - - - - - - -"};
		OutputState final_state1 = get_final_state(input_lines1);
		// After stopping acceleration, CCS should maintain the speed that was achieved 
		// during acceleration. Speed and throttle_position should remain the same
		String[] input_lines2 = { "- - - - - - - true -"};	
		OutputState final_state2 = get_final_state(input_lines2);
		
		assertTrue(final_state1.get_throttle_position()*50.0 == final_state2.get_throttle_position()*50.0
				   && final_state1.get_throttle_position() == final_state2.get_throttle_position());
	}
	
	
}
