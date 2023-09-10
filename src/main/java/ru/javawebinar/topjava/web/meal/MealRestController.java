package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.user.ProfileRestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    ProfileRestController profileRestController = new ProfileRestController();
    private MealService service;

    public List<MealTo> getAll() {
        log.info("getAll");
        return MealsUtil.getTos(service.getAll(), profileRestController.get().getCaloriesPerDay());
    }

    public List<MealTo> getFilteredByDateAndTime(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        log.info("getFilteredByDateAndTime startDate={} startTime={} endDate={} endTime={} ",
                startDate, startTime, endDate, endTime);
        return getAll().stream()
                .filter(mealTo -> DateTimeUtil.isDateBetween(
                        mealTo.getDateTime().toLocalDate(), startDate, endDate))
                .filter(mealTo -> DateTimeUtil.isBetweenHalfOpen(
                        mealTo.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    public Meal get(int id) {
        log.info("get {}", id);
        Meal meal = service.get(id);
        assureIdConsistent(profileRestController.get(), meal.getUserId());
        return meal;
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        assureIdConsistent(profileRestController.get(), service.get(id).getUserId());
        service.delete(id);
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(profileRestController.get(), meal.getUserId());
        service.update(meal);
    }
}