package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealStorage implements MealStorageInterface {
    private AtomicInteger counter;
    private Map<Integer, Meal> storage;

    public MealStorage() {
        counter = new AtomicInteger(0);
        storage = new ConcurrentHashMap<>();
    }

    @Override
    public Meal update(Meal meal) {
        storage.replace(meal.getId(), meal);
        return meal;
    }

    @Override
    public Meal create(Meal meal) {
        counter.incrementAndGet();
        meal.setId(counter.get());
        storage.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public Meal get(int key) {
        return storage.get(key);
    }

    @Override
    public void delete(int key) {
        storage.remove(key);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }
}
