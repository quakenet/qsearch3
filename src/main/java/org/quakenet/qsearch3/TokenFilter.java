package org.quakenet.qsearch3;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

final public class TokenFilter implements Predicate<String> {
	private final String _token;

	public TokenFilter(String token) {
		_token = token;
	}

	@Override
	public boolean apply(@Nullable String input) {
		return input.startsWith(_token);
	}
}
