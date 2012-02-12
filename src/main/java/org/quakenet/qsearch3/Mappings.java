package org.quakenet.qsearch3;

import com.google.common.base.Charsets;
import org.apache.solr.common.SolrInputDocument;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

final public class Mappings {
	private Mappings() {
		/* no */
	}

	public static StringMapping mapString(String ... fieldNames) {
		return new StringMapping(fieldNames);
	}

	public static IntegerMapping mapInteger(String ... fieldNames) {
		return new IntegerMapping(fieldNames);
	}

	public static LongMapping mapLong(String ... fieldNames) {
		return new LongMapping(fieldNames);
	}

	private abstract static class AbstractMapping<T> implements Mapping {
		private final String[] _fieldNames;

		protected AbstractMapping(String ... fieldNames) {
			_fieldNames = fieldNames;
		}

		final public void map(SolrInputDocument document, String value) {
			if(value == null)
				return;
			for(String fieldName: _fieldNames) {
				T converted = convert(value);
				if(converted != null)
					document.setField(fieldName, converted);
			}
		}

		protected abstract T convert(String value);
	}

	public static class StringMapping extends AbstractMapping<String> {
		private final CharsetDecoder _decoder = Charsets.UTF_8.newDecoder();
		{
			_decoder.onMalformedInput(CodingErrorAction.REPORT);
			_decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		}
		
		public StringMapping(String ... fieldNames) {
			super(fieldNames);
		}

		@Override
		protected String convert(String value) {
			byte[] data = value.getBytes(Charsets.ISO_8859_1);
			try {
				return _decoder.decode(ByteBuffer.wrap(data)).toString();
			} catch (CharacterCodingException e) {
				return value;
			}
		}
	}

	public static class IntegerMapping extends AbstractMapping<Integer> {
		public IntegerMapping(String ... fieldNames) {
			super(fieldNames);
		}

		@Override
		protected Integer convert(String value) {
			return Integer.valueOf(value);
		}
	}

	public static class LongMapping extends AbstractMapping<Long> {
		public LongMapping(String ... fieldNames) {
			super(fieldNames);
		}

		@Override
		protected Long convert(String value) {
			return Long.valueOf(value);
		}
	}
}
