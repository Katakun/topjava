package ru.javawebinar.topjava.repository.inmemory;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(SecurityUtil.authUserId());
            repository.put(meal.getId(), meal);
            return meal;
        } else if (isMealBelongToUser(meal.getId())) {
            meal.setUserId(repository.get(meal.getId()).getUserId());
            return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(int id) {
        if (isMealBelongToUser(id)) {
            return repository.remove(id) != null;
        }
        return false;
    }

    @Override
    public Meal get(int id) {
        if (isMealBelongToUser(id)) {
            return repository.get(id);
        }
        return null;
    }


    @Override
    public Collection<Meal> getAll() {
        return repository.values()
                .stream()
                .filter(meal -> isMealBelongToUser(meal.getId()))
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }

    private boolean isMealBelongToUser(int mealId) {
        Integer userId = repository.get(mealId).getUserId();
        if (userId != null) {
            return userId.equals(SecurityUtil.authUserId());
        }
        return false;
    }
}

