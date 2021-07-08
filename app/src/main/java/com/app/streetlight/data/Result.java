package com.app.streetlight.data;

import org.jetbrains.annotations.NotNull;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result<LoggedInUser> {
//    private Success<LoggedInUser> success;
//    private Error error;
//
//
//    public Result(Success<LoggedInUser> success) {
//        this.success = success;
//    }
//
//    public Result(Error error) {
//        this.error = error;
//    }

    @NotNull
    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success<LoggedInUser> success = (Result.Success<LoggedInUser>) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return "Error[exception=" + error.getError() + "]";
        }
        return "";
    }

    // Success sub-class
    public final static class Success<LoggedInUser> extends Result<com.app.streetlight.data.model.LoggedInUser> {
        private final LoggedInUser data;

        public Success(LoggedInUser data) {
            this.data = data;
        }

        public LoggedInUser getData() {
            return this.data;
        }
    }

    // Error sub-class
    public final static class Error extends Result<com.app.streetlight.data.model.LoggedInUser> {
        private final String error;

        public Error(String failReason) {
            this.error = failReason;
        }

        public String getError() {
            return this.error;
        }
    }
}