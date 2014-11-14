import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.Test;

public class StopCruisingTest extends BasicTests {

	/**
     * Runs the a list of input states defined as strings to produce the
     * list of output states and then simply selects the final output state.
     */
    private OutputState get_final_state(String[] input_lines){
    	List<OutputState> output_states = run_input_states(input_lines);
    	return output_states.get(output_states.size() - 1);
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
    
	@Test
	public void stop_cruising_by_button_test() {
		String[] input_lines = { "true 50.0 0.0 0.0 false false false false false",
								"- - - - true - - - -",
								"- - - - - true - - -" };
		
		OutputState final_state = get_final_state(input_lines);
		// Create input such that the CCS should be turned off by button press 
    	// and such that the throttle position should be set to 0 by the CCS.
		// We need to assume that CCS was turned on before it was switched off. 
		assertTrue(final_state.get_throttle_position() == 0);
	}
	
	@Test
	public void stop_cruising_by_brake_pedal_test() {
		String[] input_lines = { "true 50.0 0.0 0.0 false false false false false",
								"- - - - true - - - -",
								"- - 0.1 - - - - - -" };
		
		OutputState final_state = get_final_state(input_lines);
		// Create input such that the CCS should be turned off by pressing brake pedal 
    	// and such that the throttle position should be set to 0 by the CCS.
		// We need to assume that CCS had been turned on before brake pedal was pressed. 
		assertTrue(final_state.get_throttle_position() == 0);
	}
		
	@Test
	public void stop_cruising_by_engine_test() {
		String[] input_lines = { "true 50.0 0.0 0.0 false false false false false",
								"- - - - true - - - -",
								"false - - - - - - - - -" };
		
		OutputState final_state = get_final_state(input_lines);
		// Create input such that the CCS should be turned off when engine switches off 
    	// and such that the throttle position should be set to 0 by the CCS.
		// We need to assume that CCS had been turned on before engine switched off. 
		assertTrue(final_state.get_throttle_position() == 0);
	}	
			 
}


