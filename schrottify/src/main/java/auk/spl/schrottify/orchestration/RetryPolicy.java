package auk.spl.schrottify.orchestration;

/** Defines retry policies for various operations. */
public class RetryPolicy {

    private final int maxRetries;
    private final long initialDelayMillis;
    private final double backoffFactor;

    private RetryPolicy(Builder builder) {
        this.maxRetries = builder.maxRetries;
        this.initialDelayMillis = builder.initialDelayMillis;
        this.backoffFactor = builder.backoffFactor;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getInitialDelayMillis() {
        return initialDelayMillis;
    }

    public double getBackoffFactor() {
        return backoffFactor;
    }

    /** Builder for creating retry policies. */
    public static class Builder {
        private int maxRetries = 3;
        private long initialDelayMillis = 1000;
        private double backoffFactor = 2.0;

        /** Sets the maximum number of retry attempts. Default is 3. */
        public Builder maxRetries(int maxRetries) {
            if (maxRetries < 0) {
                throw new IllegalArgumentException("maxRetries must be non-negative");
            }
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * Sets the initial delay before the first retry in milliseconds. Default is
         * 1000.
         */
        public Builder initialDelayMillis(long initialDelayMillis) {
            if (initialDelayMillis < 0) {
                throw new IllegalArgumentException("initialDelayMillis must be non-negative");
            }
            this.initialDelayMillis = initialDelayMillis;
            return this;
        }

        /** Sets the exponential backoff factor. Default is 2.0. */
        public Builder backoffFactor(double backoffFactor) {
            if (backoffFactor <= 1.0) {
                throw new IllegalArgumentException("backoffFactor must be greater than 1.0");
            }
            this.backoffFactor = backoffFactor;
            return this;
        }

        /** Builds the retry policy. */
        public RetryPolicy build() {
            return new RetryPolicy(this);
        }
    }

    /** Creates a default retry policy. */
    public static RetryPolicy defaultPolicy() {
        return new Builder().build();
    }
}
