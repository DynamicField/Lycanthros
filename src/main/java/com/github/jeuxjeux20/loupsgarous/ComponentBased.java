package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.common.collect.Iterables;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A simple interface that allows for easier composition and delegation.
 * <p>
 * Think of it as a better {@code instanceof}.
 */
public interface ComponentBased {
    /**
     * A method to use as a placeholder for no filtering as a {@link Predicate} argument.
     * <p>
     * If you didn't understand this, just keep in mind that this returns {@code true}.
     * <p>
     * <b>Example:</b>
     * <p>
     * Let's say you have a method that takes a {@code Predicate<Blah>}.
     * And you don't want any filtering, so you do:
     *
     * <pre>{@code
     * someMethod(x -> true);
     * }</pre>
     * <p>
     * But then... you realised... "Everything looks so much better with method references"!
     * That's a weird thought, but sure. Maybe that's more descriptive... Anyway, you can do this instead:
     *
     * <pre>{@code
     * someMethod(ComponentBased::noFilter);
     * }</pre>
     * Yay! Method references!
     *
     * @param element the element of the predicate
     * @return {@code true}
     */
    static boolean noFilter(Object element) {
        return true;
    }

    default Iterable<?> getAllComponents() {
        return Collections.emptyList();
    }

    /**
     * Executes {@link #getComponent(Class, Predicate)} but without any filtering.
     *
     * @implSpec The default implementation is equivalent to this code:
     * <pre>return getComponent(clazz, x -> true);</pre>
     * @see #getComponent(Class, Predicate)
     */
    default <T> Optional<T> getComponent(Class<T> clazz) {
        return getComponent(clazz, ComponentBased::noFilter);
    }

    /**
     * Finds one component of the type of the specified class,
     * and filters out components where the {@code ensurePossible} predicate returns {@code false}.
     *
     * @param clazz          the class of the type to search
     * @param ensurePossible a predicate filtering out found elements
     * @param <T>            the type of the class
     * @return an {@link Optional} with the found component, or an empty one if none has been found
     * @throws MultipleComponentsException when multiple (filtered) components have been found
     */
    default <T> Optional<T> getComponent(Class<T> clazz, Predicate<? super T> ensurePossible) {
        List<T> components = getComponents(clazz, ensurePossible);
        if (components.isEmpty()) return Optional.empty();
        if (components.size() == 1) return Optional.of(components.get(0));

        throw new MultipleComponentsException(
                "Multiple components have been found while using getComponent: " +
                components.stream().map(x -> getClass().getName()).collect(Collectors.joining(", ")) + "."
        );
    }

    default <T> Optional<SafeResult<T>> getSafeComponent(Class<T> clazz, Function<? super T, Check> ensurePossible) {
        int[] count = new int[1];
        Check[] actualCheck = new Check[1];

        Optional<T> result = getComponent(clazz, x -> {
            Check check = ensurePossible.apply(x);

            if (!check.isSuccess()) count[0]++;

            if (count[0] == 1) {
                actualCheck[0] = check;
            } else {
                actualCheck[0] = null;
            }

            return check.isSuccess();
        });

        if (result.isPresent()) {
            return result.map(SafeResult::success);
        } else {
            if (actualCheck[0] == null) return Optional.empty();

            return Optional.of(SafeResult.error(actualCheck[0].getErrorMessage()));
        }
    }

    default <T> List<T> getComponents(Class<T> clazz) {
        return getComponents(clazz, ComponentBased::noFilter);
    }

    /**
     * Finds all the components of the type of the specified class,
     * and filters out components where the {@code ensurePossible} predicate returns {@code false}.
     *
     * @param clazz          the class of the type to search
     * @param ensurePossible a predicate filtering out found elements
     * @param <T>            the type of the class
     * @return a list containing all the components found, and filtered
     */
    default <T> List<T> getComponents(Class<T> clazz, Predicate<? super T> ensurePossible) {
        List<T> candidates = new ArrayList<>();

        Iterable<Object> componentsAndThis = Iterables.concat(Collections.singletonList(this), getAllComponents());

        for (Object componentCandidate : componentsAndThis) {
            Objects.requireNonNull(componentCandidate);

            if (clazz.isInstance(componentCandidate)) {
                T component = clazz.cast(componentCandidate);

                if (ensurePossible.test(component))
                    candidates.add(component);
            }

            if (componentCandidate != this && componentCandidate instanceof ComponentBased) {
                ComponentBased componentBased = (ComponentBased) componentCandidate;
                candidates.addAll(componentBased.getComponents(clazz, ensurePossible));
            }
        }

        return candidates;
    }
}
