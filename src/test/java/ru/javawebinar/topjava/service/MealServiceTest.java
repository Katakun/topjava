package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(MEAL_ID, USER_ID);
        assertMatch(meal, MealTestData.BREAKFAST_MEAL);
    }

    @Test
    public void getAlienMeal() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NON_EXIST_MEAL_ID, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(MEAL_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID, USER_ID));
    }

    @Test
    public void deleteAlienMeal() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID, ADMIN_ID));
    }

    @Test
    public void deleteNonExistMeal() {
        assertThrows(NotFoundException.class, () -> service.get(NON_EXIST_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 2);
        List<Meal> filteredMeals = service.getBetweenInclusive(start, end, USER_ID);
        assertMatch(filteredMeals, LUNCH_MEAL, BREAKFAST_MEAL);
    }

    @Test
    public void getBetweenNull() {
        List<Meal> filteredMeals = service.getBetweenInclusive(null, null, USER_ID);
        assertMatch(filteredMeals, DINNER_MEAL, LUNCH_MEAL, BREAKFAST_MEAL);
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, DINNER_MEAL, LUNCH_MEAL, BREAKFAST_MEAL);
    }

    @Test
    public void update() {
        Meal updatedMeal = getUpdated();
        service.update(updatedMeal, USER_ID);
        assertMatch(service.get(updatedMeal.getId(), USER_ID), getUpdated());
    }

    @Test
    public void updateAlienMeal() {
        Meal updatedMeal = getUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updatedMeal, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal createdMeal = service.create(getNew(), USER_ID);
        Integer newId = createdMeal.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(createdMeal, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        Meal createdMeal = service.create(getNew(), USER_ID);
        Meal doubleTime = getNew();
        assertThrows(DataAccessException.class, () -> service.create(doubleTime, USER_ID));
    }
}