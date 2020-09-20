package com.github.jeuxjeux20.loupsgarous.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class WordingUtils {
    private WordingUtils() {
    }

    // https://stackoverflow.com/a/34936891/5816295
    public static Function<List<String>, String> joiningLastDelimiter(
            String delimiter, String lastDelimiter) {
        return list -> {
            int last = list.size() - 1;
            if (last < 1) return String.join(delimiter, list);
            return String.join(lastDelimiter,
                    String.join(delimiter, list.subList(0, last)),
                    list.get(last));
        };
    }

    public static Function<List<String>, String> joiningCommaAnd() {
        return joiningLastDelimiter(", ", " et ");
    }

    public static <T> String joiningCommaAnd(Stream<? extends T> stream, Function<? super T, String> mapFunction) {
        return stream.map(mapFunction)
                .collect(Collectors.collectingAndThen(Collectors.toList(), WordingUtils.joiningCommaAnd()));
    }
}
