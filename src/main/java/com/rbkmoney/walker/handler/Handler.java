package com.rbkmoney.walker.handler;


import com.rbkmoney.geck.filter.Filter;

/**
 * Created by tolkonepiu on 03.08.16.
 */
public interface Handler<T> {

    default boolean accept(T value) {
        return getFilter().match(value);
    }

    void handle(T value);

    Filter getFilter();

}
