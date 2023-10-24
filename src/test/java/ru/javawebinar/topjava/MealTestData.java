package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int MEAL_ID = START_SEQ + 3;
    public static final int NON_EXIST_MEAL_ID = 333;

    public static final Meal userMeal1 = new Meal(MEAL_ID,
            LocalDateTime.of(2023, 1, 1, 8, 0),
            "Завтрак 500 ккал", 500);

    public static final Meal userMeal2 = new Meal(MEAL_ID + 1,
            LocalDateTime.of(2023, 1, 2, 13, 30),
            "Обед 1000 ккал", 1000);

    public static final Meal userMeal3 = new Meal(MEAL_ID + 2,
            LocalDateTime.of(2023, 1, 3, 20, 50),
            "Ужин 500  ккал", 500);

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2023, 7, 17, 17, 57),
                "mealTestData.getNew() created", 777);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeal1);
        updated.setDateTime(LocalDateTime.of(2023, 1, 9, 9, 59));
        updated.setDescription("mealTestData.getUpdates() updated");
        updated.setCalories(999);
        return updated;
    }
}
