import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.Test;

public class ResumeCruisingTest {

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
	public void resume_cruising_test() {
		// Give an input which would change throttle position (acceleration) and during 
		// 5th pulse stop cruising
 		// in last pulse resume cruising, and this should return throttle position which 
		// was recorded after 4th pulse input
		String[] input_lines = { "true 50.0 0.0 0.0 true false false false false", 	// start cruising
			     "- - - - - - true - -",					// start accelerating, throttle_posiyion = 1.144000 
				 "- - - - - - - - -",						// still accelerating, throttle_position = 1.288000
				 "- - - - - - - true - ",					// stop accelerating, keeps previous throttle_position
				 "- - - - - true - - -",					// stop cruising, throttle_position = 0.000000
				 "- - - - - - - - true" };					// resume cruising, keeps last trottle_position
															// achieved during cruising
		
		String actual_output = this.captureOutputOfMain(input_lines);
		String expected = "true 50.000000 0.000000 0.000000 true false false false false 1.000000\n"
						+ "true 57.200000 0.000000 0.000000 true false true false false 1.144000\n"
						+ "true 64.400000 0.000000 0.000000 true false true false false 1.288000\n"
						+ "true 64.400000 0.000000 0.000000 true false false true false 1.288000\n"
						+ "true 64.400000 0.000000 0.000000 false true false false false 0.000000\n"
						+ "true 64.400000 0.000000 0.000000 false false false false true 1.288000\n";
		assertTrue(expected.equals(actual_output));
	}

}
