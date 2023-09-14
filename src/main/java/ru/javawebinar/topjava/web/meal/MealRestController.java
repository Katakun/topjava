package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.user.ProfileRestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.DateTimeUtil.isDateBetween;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    ProfileRestController profileRestController;
    private final MealService service;
    DateTimeUtil<LocalTime> dtu = new DateTimeUtil<>();

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAllMealTo() {
        log.info("getAll");
        return MealsUtil.getTos(service.getAll(authUserId()), profileRestController.get().getCaloriesPerDay());
    }

    public List<Meal> getAllMeal() {
        log.info("getAll");
        return new ArrayList<>(service.getAll(authUserId()));
    }

    public List<MealTo> getFilteredByDateAndTime(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        log.info("getFilteredByDateAndTime startDate={} startTime={} endDate={} endTime={} ",
                startDate, startTime, endDate, endTime);
        return getAllMealTo()
                .stream()
                .filter(mealTo -> isDateBetween(
                        mealTo.getDateTime().toLocalDate(), startDate, endDate))
                .filter(mealTo -> dtu.isBetweenHalfOpen(
                        mealTo.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    public Meal get(int mealId) {
        log.info("get {}", mealId);
        return service.get(mealId, authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal, authUserId());
    }

    public void delete(int mealId) {
        log.info("delete {}", mealId);
        service.delete(mealId, authUserId());
    }

    public void update(Meal meal, int mealId) {
        log.info("update {} with id={}", meal, mealId);
        assureIdConsistent(meal, mealId);
        service.update(meal, authUserId());
    }
}