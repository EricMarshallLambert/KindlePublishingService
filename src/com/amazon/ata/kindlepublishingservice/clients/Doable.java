package com.amazon.ata.kindlepublishingservice.clients;

/**
 * functional interface with two inputs
 * @param <T>
 * @param <R>
 */
public interface Doable<T, R>{
    R go(T input);

    }
