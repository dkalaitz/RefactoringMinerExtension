/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.api.datastream;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.typeutils.TypeInfoParser;
import org.apache.flink.streaming.api.transformations.CoFeedbackTransformation;
import org.apache.flink.streaming.api.transformations.FeedbackTransformation;
import org.apache.flink.streaming.api.transformations.StreamTransformation;

import java.util.Collection;

/**
 * The iterative data stream represents the start of an iteration in a {@link DataStream}.
 * 
 * @param <T> Type of the elements in this Stream
 */
public class IterativeDataStream<T> extends SingleOutputStreamOperator<T, IterativeDataStream<T>> {

	// We store these so that we can create a co-iteration if we need to
	private DataStream<T> originalInput;
	private long maxWaitTime;
	
	protected IterativeDataStream(DataStream<T> dataStream, long maxWaitTime) {
		super(dataStream.getExecutionEnvironment(),
				new FeedbackTransformation<T>(dataStream.getTransformation(), maxWaitTime));
		this.originalInput = dataStream;
		this.maxWaitTime = maxWaitTime;
		setBufferTimeout(dataStream.environment.getBufferTimeout());
	}

	/**
	 * Closes the iteration. This method defines the end of the iterative
	 * program part that will be fed back to the start of the iteration.
	 *
	 * <p>
	 * A common usage pattern for streaming iterations is to use output
	 * splitting to send a part of the closing data stream to the head. Refer to
	 * {@link DataStream#split(org.apache.flink.streaming.api.collector.selector.OutputSelector)}
	 * for more information.
	 * 
	 * @param feedbackStream
	 *            {@link DataStream} that will be used as input to the iteration
	 *            head.
	 *
	 * @return The feedback stream.
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DataStream<T> closeWith(DataStream<T> feedbackStream) {

		Collection<StreamTransformation<?>> predecessors = feedbackStream.getTransformation().getTransitivePredecessors();

		if (!predecessors.contains(this.transformation)) {
			throw new UnsupportedOperationException(
					"Cannot close an iteration with a feedback DataStream that does not originate from said iteration.");
		}

		((FeedbackTransformation) getTransformation()).addFeedbackEdge(feedbackStream.getTransformation());

		return feedbackStream;
	}

	/**
	 * Changes the feedback type of the iteration and allows the user to apply
	 * co-transformations on the input and feedback stream, as in a
	 * {@link ConnectedDataStream}.
	 *
	 * <p>
	 * For type safety the user needs to define the feedback type
	 * 
	 * @param feedbackTypeString
	 *            String describing the type information of the feedback stream.
	 * @return A {@link ConnectedIterativeDataStream}.
	 */
	public <F> ConnectedIterativeDataStream<T, F> withFeedbackType(String feedbackTypeString) {
		return withFeedbackType(TypeInfoParser.<F> parse(feedbackTypeString));
	}

	/**
	 * Changes the feedback type of the iteration and allows the user to apply
	 * co-transformations on the input and feedback stream, as in a
	 * {@link ConnectedDataStream}.
	 *
	 * <p>
	 * For type safety the user needs to define the feedback type
	 * 
	 * @param feedbackTypeClass
	 *            Class of the elements in the feedback stream.
	 * @return A {@link ConnectedIterativeDataStream}.
	 */
	public <F> ConnectedIterativeDataStream<T, F> withFeedbackType(Class<F> feedbackTypeClass) {
		return withFeedbackType(TypeExtractor.getForClass(feedbackTypeClass));
	}

	/**
	 * Changes the feedback type of the iteration and allows the user to apply
	 * co-transformations on the input and feedback stream, as in a
	 * {@link ConnectedDataStream}.
	 *
	 * <p>
	 * For type safety the user needs to define the feedback type
	 * 
	 * @param feedbackType
	 *            The type information of the feedback stream.
	 * @return A {@link ConnectedIterativeDataStream}.
	 */
	public <F> ConnectedIterativeDataStream<T, F> withFeedbackType(TypeInformation<F> feedbackType) {
		return new ConnectedIterativeDataStream<T, F>(originalInput, feedbackType, maxWaitTime);
	}
	
	/**
	 * The {@link ConnectedIterativeDataStream} represent a start of an
	 * iterative part of a streaming program, where the original input of the
	 * iteration and the feedback of the iteration are connected as in a
	 * {@link ConnectedDataStream}.
	 *
	 * <p>
	 * The user can distinguish between the two inputs using co-transformation,
	 * thus eliminating the need for mapping the inputs and outputs to a common
	 * type.
	 * 
	 * @param <I>
	 *            Type of the input of the iteration
	 * @param <F>
	 *            Type of the feedback of the iteration
	 */
	public static class ConnectedIterativeDataStream<I, F> extends ConnectedDataStream<I, F>{

		private CoFeedbackTransformation<F> coFeedbackTransformation;

		public ConnectedIterativeDataStream(DataStream<I> input, TypeInformation<F> feedbackType, long waitTime) {
			super(input.getExecutionEnvironment(),
					input,
					new DataStream<F>(input.getExecutionEnvironment(),
							new CoFeedbackTransformation<F>(input.getParallelism(),
									feedbackType,
									waitTime)));
			this.coFeedbackTransformation = (CoFeedbackTransformation<F>) getSecond().getTransformation();
		}

		/**
		 * Closes the iteration. This method defines the end of the iterative
		 * program part that will be fed back to the start of the iteration as
		 * the second input in the {@link ConnectedDataStream}.
		 * 
		 * @param feedbackStream
		 *            {@link DataStream} that will be used as second input to
		 *            the iteration head.
		 * @return The feedback stream.
		 * 
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public DataStream<F> closeWith(DataStream<F> feedbackStream) {

			Collection<StreamTransformation<?>> predecessors = feedbackStream.getTransformation().getTransitivePredecessors();

			if (!predecessors.contains(this.coFeedbackTransformation)) {
				throw new UnsupportedOperationException(
						"Cannot close an iteration with a feedback DataStream that does not originate from said iteration.");
			}

			coFeedbackTransformation.addFeedbackEdge(feedbackStream.getTransformation());

			return feedbackStream;
		}
		
		private UnsupportedOperationException groupingException = new UnsupportedOperationException(
				"Cannot change the input partitioning of an iteration head directly. Apply the partitioning on the input and feedback streams instead.");
		
		@Override
		public ConnectedDataStream<I, F> groupBy(int keyPosition1, int keyPosition2) {throw groupingException;}
		
		@Override
		public ConnectedDataStream<I, F> groupBy(int[] keyPositions1, int[] keyPositions2) {throw groupingException;}
		
		@Override
		public ConnectedDataStream<I, F> groupBy(String field1, String field2) {throw groupingException;}
		
		@Override
		public ConnectedDataStream<I, F> groupBy(String[] fields1, String[] fields2) {throw groupingException;}
		
		@Override
		public ConnectedDataStream<I, F> groupBy(KeySelector<I, ?> keySelector1,KeySelector<F, ?> keySelector2) {throw groupingException;}
		
		@Override
		public ConnectedDataStream<I, F> partitionByHash(int keyPosition1, int keyPosition2) {throw groupingException;}
		
		@Override
		public ConnectedDataStream<I, F> partitionByHash(int[] keyPositions1, int[] keyPositions2) {throw groupingException;}
		
		@Override
		public ConnectedDataStream<I, F> partitionByHash(String field1, String field2) {throw groupingException;}
		
		@Override
		public ConnectedDataStream<I, F> partitionByHash(String[] fields1, String[] fields2) {throw groupingException;}
		
		@Override
		public ConnectedDataStream<I, F> partitionByHash(KeySelector<I, ?> keySelector1, KeySelector<F, ?> keySelector2) {throw groupingException;}
		
	}
}