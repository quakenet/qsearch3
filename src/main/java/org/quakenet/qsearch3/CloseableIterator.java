package org.quakenet.qsearch3;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

final public class CloseableIterator<E> implements Iterator<E>, Closeable {
	private final Iterator<E> _delegate;
	private final Closeable _closeable;

	private CloseableIterator(Iterator<E> delegate, Closeable closeable) {
		_delegate = delegate;
		_closeable = closeable;
	}

	@Override
	public boolean hasNext() {
		return _delegate.hasNext();
	}

	@Override
	public E next() {
		return _delegate.next();
	}

	@Override
	public void remove() {
		_delegate.remove();
	}

	@Override
	public void close() throws IOException {
		_closeable.close();
	}
	
	public static <E_> CloseableIterator<E_> makeCloseable(Iterator<E_> delegate, Closeable closeable) {
		return new CloseableIterator(delegate, closeable);
	}
}
