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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.internal.util.Concurrency;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * Default phenotype evaluation strategy. It uses the configured {@link Executor}
 * for the fitness evaluation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 4.2
 */
final class ConcurrentEvaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements FitnessEvaluator<G, C>
{

	private final Function<? super Genotype<G>, ? extends C> _fitness;
	private final Executor _executor;

	ConcurrentEvaluator(
		final Function<? super Genotype<G>, ? extends C> fitness,
		final Executor executor
	) {
		_fitness = requireNonNull(fitness);
		_executor = requireNonNull(executor);
	}

	ConcurrentEvaluator(final Function<? super Genotype<G>, ? extends C> fitness) {
		this(fitness, ForkJoinPool.commonPool());
	}

	ConcurrentEvaluator<G, C> with(final Executor executor) {
		return new ConcurrentEvaluator<>(_fitness, executor);
	}

	@Override
	public ISeq<Phenotype<G, C>> evaluate(final Seq<Phenotype<G, C>> population) {
		final ISeq<PhenotypeFitness<G, C>> evaluate = population.stream()
			.filter(Phenotype::nonEvaluated)
			.map(pt -> new PhenotypeFitness<>(pt, _fitness))
			.collect(ISeq.toISeq());

		final ISeq<Phenotype<G, C>> result;
		if (evaluate.nonEmpty()) {
			try (Concurrency c = Concurrency.with(_executor)) {
				c.execute(evaluate);
			}

			result = evaluate.size() == population.size()
				? evaluate.map(PhenotypeFitness::phenotype)
				: population.stream()
					.filter(Phenotype::isEvaluated)
					.collect(ISeq.toISeq())
					.append(evaluate.map(PhenotypeFitness::phenotype));
		} else {
			result = population.asISeq();
		}

		return result;
	}


	private static final class PhenotypeFitness<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
		implements Runnable
	{
		final Phenotype<G, C> _phenotype;
		final Function<? super Genotype<G>, ? extends C> _function;
		C _fitness;

		PhenotypeFitness(
			final Phenotype<G, C> phenotype,
			final Function<? super Genotype<G>, ? extends C> function
		) {
			_phenotype = phenotype;
			_function = function;
		}

		@Override
		public void run() {
			_fitness = _function.apply(_phenotype.getGenotype());
		}

		Phenotype<G, C> phenotype() {
			return _phenotype.withFitness(_fitness);
		}

	}

}
