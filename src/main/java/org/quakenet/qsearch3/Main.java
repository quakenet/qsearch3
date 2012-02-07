package org.quakenet.qsearch3;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Iterator;

/**
 * QSearch3
 *
 * Copyright (C) 2012 Chris Porter.
 *
 * Public domain.
 */
final public class Main {
	public static void main(String[] args) throws IOException, SolrServerException {
		if(args.length != 3) {
			System.err.println("Syntax: ./javastuff <dumpfile.txt> <timestamp file> <solr base url>");
			System.exit(1);
		}
		
		File dumpFile = new File(args[0]);
		File timestampFile = new File(args[1]);
		String solrBaseURL = args[2];

		long timestamp;
		try(CloseableIterator<SolrInputDocument> timestampItt = timestampIterator(dumpFile)) {
			timestamp = (Long)timestampItt.next().get("timestamp").getFirstValue();
			try(Reader reader = new FileReader(timestampFile)) {
				long oldTimestamp = Long.valueOf(new LineIterator(reader).next());
				if(oldTimestamp >= timestamp)
					System.exit(2);
			}
		}

		try(CloseableIterator<SolrInputDocument> channels = channelsIterator(dumpFile);
			CloseableIterator<SolrInputDocument> nicks = nicksIterator(dumpFile)) {
			new SolrIndexer(solrBaseURL).indexAndSwitch(Iterators.concat(channels, nicks));
		}
		
		try(FileWriter fileWriter = new FileWriter(timestampFile)) {
			fileWriter.write(String.valueOf(timestamp));
		}
	}

	private static CloseableIterator<SolrInputDocument> nicksIterator(File dumpFile) throws FileNotFoundException {
		return buildTransformer(dumpFile, 6, 6, "N ", "nick",
			null, Mappings.mapString("id", "nick"), Mappings.mapString("ident"), Mappings.mapString("hostname"), new Mappings.StringMapping("authname") {
			@Override
			protected String convert(String value) {
				if(value.equals("0"))
					return null;

				return super.convert(value);
			}
		}, Mappings.mapString("realname"));
	}

	private static CloseableIterator<SolrInputDocument> channelsIterator(File dumpFile) throws FileNotFoundException {
		return buildTransformer(dumpFile, 3, 4, "C ", "channel",
			null, Mappings.mapString("id", "name"), Mappings.mapInteger("size"), Mappings.mapString("topic"));
	}

	private static CloseableIterator<SolrInputDocument> timestampIterator(File dumpFile) throws FileNotFoundException {
		return buildTransformer(dumpFile, 3, 3, "M T ", null,
		null, null, Mappings.mapLong("timestamp"));
	}

	private static CloseableIterator<SolrInputDocument> buildTransformer(File file, int min, int max, String prefix, final String type, final Mapping ... mappings) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		try {
			Reader reader = new InputStreamReader(fis, Charsets.ISO_8859_1);
			Iterator<String[]> itt = Iterators.transform(Iterators.filter(new LineIterator(reader), new TokenFilter(prefix)), new Tokeniser(min, max));
			Iterator<SolrInputDocument> itt2 = Iterators.transform(itt, new Function<String[], SolrInputDocument>() {
				@Override
				public SolrInputDocument apply(@Nullable String[] input) {
					SolrInputDocument result = new SolrInputDocument();
					result.setField("type", type);
					for(int i=0;i<mappings.length&&i<input.length;i++)
						if(mappings[i] != null)
							mappings[i].map(result, input[i]);

					return result;
				}
			});
			return CloseableIterator.makeCloseable(itt2, reader);
		} catch(Throwable t) {
			try {
				fis.close();
			} catch (IOException e) {
				/* ignore */
			}
			throw t; /* yey java 7 */
		}
	}
}
