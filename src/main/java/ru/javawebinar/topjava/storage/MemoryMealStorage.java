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
        int id = meal.getId();
        storage.replace(id, meal);
        return storage.get(id);
    }

    @Override
    public Meal create(Meal meal) {
        meal.setId(counter.incrementAndGet());
        storage.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public Meal get(int id) {
        return storage.get(id);
    }

    @Override
    public void delete(int id) {
        storage.remove(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }
}
