/**
 * You should *not* modify this file!
 * This class simply allows the convenient input to the system. The input
 * consists of lines of input states which are all in the format described
 * in the InputState class. Here you can retrieve the input from a list or
 * array of input strings, or from an input file path.
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class StateInput {
	public static List<InputState> input_states_from_strings(List<String> lines){
		LinkedList<InputState> input_states = new LinkedList<InputState>();
		for (String s : lines){
			input_states.add(new InputState(s));
		}
		
		return input_states;
	}
	public static List<InputState> input_states_from_strings(String[] lines){
		return input_states_from_strings(Arrays.asList(lines));
	}
		
	public static List<InputState>input_states_from_file (Path filename) throws IOException{
		List<String> input_lines = Files.readAllLines(filename, StandardCharsets.UTF_8);
		return input_states_from_strings(input_lines);
	}

}
