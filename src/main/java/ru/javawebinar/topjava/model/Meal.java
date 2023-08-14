package ru.javawebinar.topjava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Meal {
    private static int counter = 0;
    private final int id;
    private LocalDateTime dateTime;
    private String description;
    private int calories;

    public Meal(LocalDateTime dateTime, String description, int calories) {
        synchronized (Meal.class) {
            id = counter++;
        }
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        System.out.println("My ID: " + id);
    }

    public static int getCounter() {
        return counter;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
