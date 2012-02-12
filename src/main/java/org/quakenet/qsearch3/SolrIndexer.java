package org.quakenet.qsearch3;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

final public class SolrIndexer {
	private final CommonsHttpSolrServer _solr;
	private final URI _swapURI;

	public SolrIndexer(String solrBaseURL) throws MalformedURLException, URIException {
		solrBaseURL = solrBaseURL + (solrBaseURL.endsWith("/") ? "" : "/");
		_solr = new CommonsHttpSolrServer(solrBaseURL + "write");
		_solr.setRequestWriter(new BinaryRequestWriter());
		_swapURI = new URI(solrBaseURL + "admin/cores?action=SWAP&core=write&other=read", false);
	}
	
	public void indexAndSwitch(Iterator<SolrInputDocument> itt) throws IOException, SolrServerException {
		_solr.deleteByQuery("*:*");
		_solr.add(itt);

		_solr.commit(true, true);
		_solr.optimize(true, true);

		HttpClient client = _solr.getHttpClient();
		HttpMethod method = new PostMethod();
		method.setURI(_swapURI);
		client.executeMethod(method);
	}
}
