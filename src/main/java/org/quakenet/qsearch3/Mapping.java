package org.quakenet.qsearch3;

import org.apache.solr.common.SolrInputDocument;

public interface Mapping<T> {
	void map(SolrInputDocument document, String value);
}
