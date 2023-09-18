package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.DateTimeUtil.isDateBetween;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {   // TODO remove hardcode
        MealsUtil.mealsUser1.forEach(meal -> save(meal, 1));
        MealsUtil.mealsUser2.forEach(meal -> save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.putIfAbsent(meal.getId(), new ConcurrentHashMap<>());
            repository.get(userId).put(meal.getId(), meal);
            return meal;
        } else if (isMealBelongToUser(meal.getId(), userId)) {
            meal.setUserId(userId);
            return repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(int mealId, int userId) {
        if (isMealBelongToUser(mealId, userId)) {
            return repository.get(userId).remove(mealId) != null;
        }
        return false;
    }

    @Override
    public Meal get(int mealId, int userId) {
        Meal meal = repository.get(userId).get(mealId);
        if (meal != null && meal.getUserId().equals(userId)) {
            return meal;
        }
        return null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return filterByPredicate(repository.get(userId).values(), userId, meal -> true);
    }

    @Override
    public List<Meal> getAllFilteredByDate(LocalDate startDate, LocalDate endTime, int userId) {
        return filterByPredicate(repository.get(userId).values(), userId,
                meal -> isDateBetween(meal.getDate(), startDate, endTime));
    }

    private List<Meal> filterByPredicate(Collection<Meal> meals, int userId, Predicate<Meal> filter) {
        return meals.stream()
                .filter(meal -> meal.getUserId().equals(userId))
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private boolean isMealBelongToUser(int mealId, int userId) {
        Meal meal = repository.get(userId).get(mealId);
        if (meal != null) {
            return meal.getUserId().equals(userId);
        }
        return false;
    }
}