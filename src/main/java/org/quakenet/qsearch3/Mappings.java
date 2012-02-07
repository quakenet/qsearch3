package org.quakenet.qsearch3;

import org.apache.solr.common.SolrInputDocument;

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
		public StringMapping(String ... fieldNames) {
			super(fieldNames);
		}

		@Override
		protected String convert(String value) {
			return value;
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
