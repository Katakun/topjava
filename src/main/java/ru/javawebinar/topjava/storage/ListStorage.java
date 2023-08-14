package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListStorage implements Storage {

    private Map<Integer, Meal> storage = new HashMap<>();

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
    public List<Meal> getALl() {
        return new ArrayList<>(storage.values());
    }
}
