package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO многопоточность
public class MealMapStorage implements Storage {
    private static final MealMapStorage INSTANCE = new MealMapStorage();
    private static int counter;
    private Map<Integer, Meal> storage;

    private MealMapStorage() {
        counter = 0;
        this.storage = new HashMap<>();
    }

    public static MealMapStorage getStorage() {
        return INSTANCE;
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public void update(Meal meal, int id) {
        storage.replace(id, meal);
    }

    @Override
    public void save(Meal meal) {
        synchronized (this) {
            counter++;
        }
        storage.put(meal.getId(), meal);
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
