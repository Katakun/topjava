package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorageInterface {
    Meal update(Meal meal);

    Meal create(Meal meal);

    Meal get(int index);

    void delete(int index);

    List<Meal> getAll();
}
