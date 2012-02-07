package org.quakenet.qsearch3;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.util.Arrays;

final public class Tokeniser implements Function<String, String[]> {
	private final int _max;
	private final int _min;

	public Tokeniser(int min, int max) {
		_min = min;
		_max = max;
	}

	@Override
	public String[] apply(@Nullable String input) {
		String[] result = input.split(" ", _max);
		if(result.length < _min)
			throw new IllegalStateException("Number of tokens below min (" + _max + "): " + Arrays.toString(result));
		return result;
	}
}
