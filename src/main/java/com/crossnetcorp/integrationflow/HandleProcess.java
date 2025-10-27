package com.crossnetcorp.integrationflow;

/**
 * A functional interface for processing a {@link FlowMessage}.
 *
 * @param <A> The type of the payload.
 */
@FunctionalInterface
public interface HandleProcess<A> {
    /**
     * Processes the given {@link FlowMessage}.
     *
     * @param in The input message.
     * @return The processed message.
     * @throws FlowException If an error occurs during processing.
     */
    FlowMessage<A> process(FlowMessage<A> in) throws FlowException;
}