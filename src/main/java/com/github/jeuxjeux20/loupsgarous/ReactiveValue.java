package com.github.jeuxjeux20.loupsgarous;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class ReactiveValue<T> {
    private final Subject<T> subject = PublishSubject.create();

    public static <T> ReactiveValue<T> accessors(Supplier<T> getter, Consumer<T> setter) {
        return new ReactiveValue<T>() {
            @Override
            public T get() {
                return getter.get();
            }

            @Override
            protected void setNewValue(T value) {
                setter.accept(value);
            }
        };
    }

    public abstract T get();

    public void set(T value) {
        if (value != get()) {
            setNewValue(value);
            subject.onNext(get());
        }
    }

    public void update(UnaryOperator<T> transformer) {
        set(transformer.apply(get()));
    }

    protected abstract void setNewValue(T value);

    public Observable<T> observe() {
        return subject;
    }

    public Observable<T> observeWithCurrent() {
        return new Observable<T>() {
            @Override
            protected void subscribeActual(@NotNull Observer<? super T> observer) {
                observer.onNext(get());
                subject.subscribe(observer);
            }
        };
    }

    protected Subject<T> getSubject() {
        return subject;
    }
}
