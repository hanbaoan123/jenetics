/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics;

import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import io.jenetics.internal.util.Equality;
import io.jenetics.internal.util.Hash;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

/**
 * Numeric chromosome implementation which holds 32 bit integer numbers.
 *
 * @see IntegerGene
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz  Wilhelmstötter</a>
 * @since 2.0
 * @version !__version__!
 */
public class IntegerChromosome
	extends AbstractBoundedChromosome<Integer, IntegerGene>
	implements
			NumericChromosome<Integer, IntegerGene>,
			Serializable
{
	private static final long serialVersionUID = 3L;

	/**
	 * Create a new chromosome from the given {@code genes} and the allowed
	 * length range of the chromosome.
	 *
	 * @since 4.0
	 *
	 * @param genes the genes that form the chromosome.
	 * @param lengthRange the allowed length range of the chromosome
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 */
	protected IntegerChromosome(
		final ISeq<IntegerGene> genes,
		final IntRange lengthRange
	) {
		super(genes, lengthRange);
	}

	/**
	 * Create a new random chromosome.
	 *
	 * @since 4.0
	 *
	 * @param min the min value of the {@link IntegerGene}s (inclusively).
	 * @param max the max value of the {@link IntegerGene}s (inclusively).
	 * @param lengthRange the allowed length range of the chromosome.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the length is smaller than one
	 */
	public IntegerChromosome(
		final Integer min,
		final Integer max,
		final IntRange lengthRange
	) {
		this(IntegerGene.seq(min, max, lengthRange), lengthRange);
		_valid = true;
	}

	/**
	 * Create a new random {@code IntegerChromosome}.
	 *
	 * @param min the min value of the {@link IntegerGene}s (inclusively).
	 * @param max the max value of the {@link IntegerGene}s (inclusively).
	 * @param length the length of the chromosome.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public IntegerChromosome(
		final Integer min,
		final Integer max,
		final int length
	) {
		this(min, max, IntRange.of(length));
	}

	/**
	 * Create a new random {@code IntegerChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (inclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public IntegerChromosome(final Integer min, final Integer max) {
		this(min, max, 1);
	}

	/**
	 * Returns an int array containing all of the elements in this chromosome
	 * in proper sequence.  If the chromosome fits in the specified array, it is
	 * returned therein. Otherwise, a new array is allocated with the length of
	 * this chromosome.
	 *
	 * @since 3.0
	 *
	 * @param array the array into which the elements of this chromosomes are to
	 *        be stored, if it is big enough; otherwise, a new array is
	 *        allocated for this purpose.
	 * @return an array containing the elements of this chromosome
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 */
	public int[] toArray(final int[] array) {
		final int[] a = array.length >= length() ? array : new int[length()];
		for (int i = length(); --i >= 0;) {
			a[i] = intValue(i);
		}

		return a;
	}

	/**
	 * Returns an int array containing all of the elements in this chromosome
	 * in proper sequence.
	 *
	 * @since 3.0
	 *
	 * @return an array containing the elements of this chromosome
	 */
	public int[] toArray() {
		return toArray(new int[length()]);
	}

	/**
	 * Create a new {@code IntegerChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty.
	 */
	public static IntegerChromosome of(final IntegerGene... genes) {
		return new IntegerChromosome(ISeq.of(genes), IntRange.of(genes.length));
	}

	/**
	 * Create a new random chromosome.
	 *
	 * @since 4.0
	 *
	 * @param min the min value of the {@link IntegerGene}s (inclusively).
	 * @param max the max value of the {@link IntegerGene}s (inclusively).
	 * @param lengthRange the allowed length range of the chromosome.
	 * @return a new {@code IntegerChromosome} with the given parameter
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 * @throws NullPointerException if the given {@code lengthRange} is
	 *         {@code null}
	 */
	public static IntegerChromosome of(
		final int min,
		final int max,
		final IntRange lengthRange
	) {
		return new IntegerChromosome(min, max, lengthRange);
	}

	/**
	 * Create a new random {@code IntegerChromosome}.
	 *
	 * @param min the min value of the {@link IntegerGene}s (inclusively).
	 * @param max the max value of the {@link IntegerGene}s (inclusively).
	 * @param length the length of the chromosome.
	 * @return a new random {@code IntegerChromosome}
	 * @throws IllegalArgumentException if the length is smaller than one
	 */
	public static IntegerChromosome of(
		final int min,
		final int max,
		final int length
	) {
		return new IntegerChromosome(min, max, length);
	}

	/**
	 * Create a new random chromosome.
	 *
	 * @since 4.0
	 *
	 * @param range the integer range of the chromosome.
	 * @param lengthRange the allowed length range of the chromosome.
	 * @return a new {@code IntegerChromosome} with the given parameter
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 * @throws NullPointerException if the given {@code lengthRange} is
	 *         {@code null}
	 */
	public static IntegerChromosome of(
		final IntRange range,
		final IntRange lengthRange
	) {
		return new IntegerChromosome(range.getMin(), range.getMax(), lengthRange);
	}

	/**
	 * Create a new random {@code IntegerChromosome}.
	 *
	 * @since 3.2
	 *
	 * @param range the integer range of the chromosome.
	 * @param length the length of the chromosome.
	 * @return a new random {@code IntegerChromosome}
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if the length is smaller than one
	 */
	public static IntegerChromosome of(final IntRange range, final int length) {
		return new IntegerChromosome(range.getMin(), range.getMax(), length);
	}

	/**
	 * Create a new random {@code IntegerChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (inclusively).
	 * @return a new random {@code IntegerChromosome} of length one
	 */
	public static IntegerChromosome of(final int min, final int max) {
		return new IntegerChromosome(min, max);
	}

	/**
	 * Create a new random {@code IntegerChromosome} of length one.
	 *
	 * @since 3.2
	 *
	 * @param range the integer range of the chromosome.
	 * @return a new random {@code IntegerChromosome} of length one
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 */
	public static IntegerChromosome of(final IntRange range) {
		return new IntegerChromosome(range.getMin(), range.getMax());
	}

	@Override
	public IntegerChromosome newInstance(final ISeq<IntegerGene> genes) {
		return new IntegerChromosome(genes, lengthRange());
	}

	@Override
	public IntegerChromosome newInstance() {
		return new IntegerChromosome(_min, _max, lengthRange());
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.INTEGER_CHROMOSOME, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeInt(length(), out);
		writeInt(lengthRange().getMin(), out);
		writeInt(lengthRange().getMax(), out);
		writeInt(_min, out);
		writeInt(_max, out);

		for (int i = 0, n = length(); i < n; ++i) {
			writeInt(intValue(i), out);
		}
	}

	static IntegerChromosome read(final DataInput in) throws IOException {
		final int length = readInt(in);
		final IntRange lengthRange = IntRange.of(readInt(in), readInt(in));
		final int min = readInt(in);
		final int max = readInt(in);

		final MSeq<IntegerGene> values = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			values.set(i, IntegerGene.of(readInt(in), min, max));
		}

		return new IntegerChromosome(values.toISeq(), lengthRange);
	}

}
