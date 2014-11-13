/**
 * You should *not* modify this file!
 * The main class for a command-line program which reads in
 * a file containing the sequence of state updates, and outputs
 * the resulting states after each pulse. You could use this to
 * check your output, but you should be doing that via your automatic tests.
 */
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class CommandLine {

	/**
	 * The main method that simply takes the command-line arguments as
	 * as the single parameter.
	 * @throws IOException 
	 */
	public static void main(String[] commandLineArgs) throws IOException {
		Path input_path = Paths.get(commandLineArgs[0]);
		List<InputState> input_states = StateInput.input_states_from_file(input_path);
		Timer timer = new Timer(new CruiseControlSystem());
		List<OutputState> output_states = timer.pulse_from_input(input_states);
		for (OutputState s : output_states){
			System.out.println(s.format());
		}
	}
}
