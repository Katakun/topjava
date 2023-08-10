package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;

public class ListStorage implements Storage {

    private List<Meal> list = new ArrayList<>();
    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public void update(Meal meal, int index) {
        list.set(index, meal);
    }

    @Override
    public void save(Meal meal) {
        list.add(meal);
    }

    @Override
    public Meal get(int index) {
        return list.get(index);
    }

    @Override
    public void delete(int index) {
        list.remove(index);
    }

    @Override
    public List<Meal> getALl() {
        return list;
    }
}
