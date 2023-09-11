package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.Collection;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

public class MealService {

    private MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId) {
        return repository.save(meal, userId);
    }

    public void delete(int mealId, int userId) {
        checkNotFoundWithId(repository.delete(mealId, userId), mealId);
    }

    public Meal get(int mealId, int userId) {
        return checkNotFoundWithId(repository.get(mealId, userId), mealId);
    }

    public Collection<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public void update(Meal meal, int userId) {
        checkNotFoundWithId(repository.save(meal, userId), meal.getId());
    }

}