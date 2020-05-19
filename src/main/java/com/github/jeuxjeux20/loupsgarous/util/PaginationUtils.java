package com.github.jeuxjeux20.loupsgarous.util;

import com.google.common.collect.Streams;

import java.util.List;
import java.util.stream.Stream;

public final class PaginationUtils {
    private PaginationUtils() {
    }

    @SuppressWarnings("UnstableApiUsage")
    public static <T> Stream<PaginatedItem<T>> in(List<T> items, int page, int itemsPerPage) {
        int pageCount = getPageCount(items, itemsPerPage);
        if (pageCount > page) page = pageCount;

        int itemsToSkip = (page - 1) * itemsPerPage;

        return Streams.mapWithIndex(
                items.stream().skip(itemsToSkip).limit(itemsPerPage),
                PaginatedItem::new
        );
    }

    public static int getPageCount(List<?> items, int itemsPerPage) {
        int itemCount = items.size();
        return (int) Math.ceil(itemCount / (float) itemsPerPage);
    }

    public static final class PaginatedItem<T> {
        private final long index;
        private final T value;

        private PaginatedItem(T value, long index) {
            this.index = index;
            this.value = value;
        }

        public long getIndex() {
            return index;
        }

        public T getValue() {
            return value;
        }
    }
}
