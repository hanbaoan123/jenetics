/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class TestDataIterator implements Closeable {

	public static class Data {
		public final Double number;
		public final Double m1;
		public final Double m2;
		public final Double m3;
		public final Double m4;
		public final Double mean;
		public final Double variance;
		public final Double std;
		public final Double kurtosis;
		public final Double skewness;

		private Data(final Double[] data) {
			number = data[0];
			m1 = data[1];
			m2 = data[2];
			m3 = data[3];
			m4 = data[4];
			mean = data[5];
			variance = data[6];
			std = data[7];
			kurtosis = data[8];
			skewness = data[9];
		}
	}


	private final InputStream _input;
	private final String _separator;

	private BufferedReader _reader = null;
	private String _line = null;

	public TestDataIterator(final InputStream input, final String separator)
		throws IOException
	{
		_input = input;
		_separator = separator;
		init();
	}

	private void init() throws IOException {
		if (_reader == null) {
			_reader = new BufferedReader(new InputStreamReader(_input));
			readLine();
		}
	}

	private void readLine() throws IOException {
		_line = _reader.readLine();
		while (_line != null && _line.startsWith("#")) {
			_line = _reader.readLine();
		}
	}

	public boolean hasNext() {
		return _line != null;
	}

	public TestDataIterator.Data next() throws IOException {
		init();

		Double[] values = new Double[0];
		if (_line != null) {
			final String[] parts = _line.split(_separator);
			values = new Double[parts.length];
			for (int j = 0; j < parts.length; ++j) {
				values[j] = Double.parseDouble(parts[j]);
			}
		}

		readLine();

		return new Data(values);
	}

	@Override
	public void close() throws IOException {
		_reader.close();
	}


}




