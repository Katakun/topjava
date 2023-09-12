package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.DateTimeUtil.isDateBetween;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1)); // TODO remove hardcode
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.put(meal.getId(), meal);
            return meal;
        } else if (isMealBelongToUser(meal.getId(), userId)) {
            meal.setUserId(repository.get(meal.getId()).getUserId());
            return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(int mealId, int userId) {
        if (isMealBelongToUser(mealId, userId)) {
            return repository.remove(mealId) != null;
        }
        return false;
    }

    @Override
    public Meal get(int mealId, int userId) {
        if (isMealBelongToUser(mealId, userId)) {
            return repository.get(mealId);
        }
        return null;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return repository.values()
                .stream()
                .filter(meal -> isMealBelongToUser(meal.getId(), userId))
                .sorted(Comparator.comparing(Meal::getDate).reversed())
                .collect(Collectors.toList());
    }

    public Collection<Meal> getFilteredByDate(LocalDate startDate, LocalDate endTime, int userId) {
        return getAll(userId)
                .stream()
                .filter(meal -> isDateBetween(meal.getDate(), startDate, endTime))
                .collect(Collectors.toList());
    }

    private boolean isMealBelongToUser(int mealId, int userId) {
        Meal meal = repository.get(mealId);
        if (meal != null && meal.getUserId() != null) {
            return meal.getUserId().equals(userId);
        }
        return false;
    }
}