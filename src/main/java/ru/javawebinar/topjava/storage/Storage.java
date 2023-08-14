package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface Storage {
    void clear();

    void update(Meal meal, int index);

    void save(Meal meal);

    Meal get(int index);

    void delete(int index);

    List<Meal> getAll();
}
