package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.time.LocalDate;
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
//        MealsUtil.mealsUser2.forEach(meal -> save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            return repository.computeIfAbsent(userId, v -> new ConcurrentHashMap<>())
                    .put(meal.getId(), meal);
        }
        Map<Integer, Meal> mealMap = repository.get(userId);
        return mealMap != null ? mealMap.computeIfPresent(meal.getId(), (k, v) -> meal) : null;
    }

    @Override
    public boolean delete(int mealId, int userId) {
        return ValidationUtil
                .checkNotFoundWithId(repository.get(userId), userId)
                .remove(mealId) != null;
    }

    @Override
    public Meal get(int mealId, int userId) {
        return ValidationUtil
                .checkNotFoundWithId(repository.get(userId), userId)
                .get(mealId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return ValidationUtil
                .checkNotFoundWithId(filterByPredicate(userId, meal -> true), userId);
    }

    @Override
    public List<Meal> getAllFilteredByDate(LocalDate startDate, LocalDate endTime, int userId) {
        return ValidationUtil
                .checkNotFoundWithId(filterByPredicate(userId,
                        meal -> isDateBetween(meal.getDate(), startDate, endTime)), userId);
    }

    private List<Meal> filterByPredicate(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> mealMap = repository.get(userId);
        if (mealMap != null) {
            return mealMap.values()
                    .stream()
                    .filter(filter)
                    .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                    .collect(Collectors.toList());
        }
        return null;
    }
}