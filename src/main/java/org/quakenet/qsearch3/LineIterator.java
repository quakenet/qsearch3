package org.quakenet.qsearch3;

import com.google.common.collect.AbstractIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public final class LineIterator extends AbstractIterator<String> {
	private final BufferedReader _reader;

	public LineIterator(Reader reader) {
		_reader = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
	}

	@Override
	protected String computeNext() {
		try {
			String line = _reader.readLine();
			if(line == null)
				return endOfData();

			return line;
		} catch(IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
