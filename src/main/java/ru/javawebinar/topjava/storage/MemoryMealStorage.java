package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryMealStorage implements MealStorage {
    private AtomicInteger counter;
    private Map<Integer, Meal> storage;

    public MemoryMealStorage() {
        counter = new AtomicInteger(0);
        storage = new ConcurrentHashMap<>();
        for (Meal meal : MealsUtil.meals) {
            create(meal);
        }
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

    public void fill(List<Meal> meals) {
        for (Meal meal : meals) {
            create(meal);
        }
    }
}
