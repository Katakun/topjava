package ru.javawebinar.topjava.storage;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class MealStorage implements MealStorageInterface {
    private static final Logger log = getLogger(Meal.class);
    private static final MealStorage INSTANCE = new MealStorage();
    private static int counter;
    private volatile Map<Integer, Meal> storage;

    private MealStorage() {
        counter = 0;
        this.storage = new HashMap<>();
    }

    public static MealStorage getStorage() {
        return INSTANCE;
    }

    @Override
    public Meal update(Meal meal) {
        storage.replace(meal.getId(), meal);
        return meal;
    }

    @Override
    public Meal create(Meal meal) {
        synchronized (this) {
            meal.setId(counter);
            log.debug("new meal key: " + counter);
            counter++;
        }
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
