package com.github.df.loupsgarous;

import java.util.stream.Stream;

public final class CheckStreams {
    private CheckStreams() {}

    public static Check shortCircuitingAnd(Stream<Check> checks) {
        return checks.filter(Check::isError).findFirst().orElse(Check.success());
    }
}
