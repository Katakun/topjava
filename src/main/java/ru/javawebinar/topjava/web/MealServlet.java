package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.storage.Storage;
import ru.javawebinar.topjava.util.MealsData;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MealServlet extends HttpServlet {
    DateTimeFormatter DATETIMEFORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private List<MealTo> mealToList;
    private Storage storage;


    public void init() throws ServletException {
        storage = MealsData.getStorage();
        MealsData.fill();
        mealToList = MealsUtil.filteredByStreams(MealsData.getStorage().getAll(),
                LocalTime.of(0, 0), LocalTime.of(23, 59), MealsData.CALORIES_PER_DAY);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("id"));
        String dateTime = request.getParameter("dateTime");
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));

        Meal editMeal = storage.get(id);
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DATETIMEFORMATTER);
        editMeal.setDateTime(localDateTime);
        editMeal.setDescription(description);
        editMeal.setCalories(calories);
        storage.update(editMeal, id);

        mealToList = MealsUtil.filteredByStreams(MealsData.getStorage().getAll(),
                LocalTime.of(0, 0), LocalTime.of(23, 59), MealsData.CALORIES_PER_DAY);
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mealToList", mealToList);
        String action = request.getParameter("action");
        if (action == null) {
            request.getRequestDispatcher("meals.jsp").forward(request, response);
        } else {
            switch (action) {
                case "delete":
                    int id = Integer.parseInt(request.getParameter("id"));
                    storage.delete(id);
                    mealToList = MealsUtil.filteredByStreams(MealsData.getStorage().getAll(),
                            LocalTime.of(0, 0), LocalTime.of(23, 59), MealsData.CALORIES_PER_DAY);
                    response.sendRedirect("meals");
                    break;
                case "edit":
                    id = Integer.parseInt(request.getParameter("id"));
                    request.setAttribute("meal", storage.get(id));
                    request.setAttribute("action", "Edit");
                    request.getRequestDispatcher("/edit.jsp").forward(request, response);
                    break;
                case "add":
                    Meal meal = new Meal(LocalDateTime.now(), "", 0);
                    storage.save(meal);
                    request.setAttribute("meal", meal);
                    request.setAttribute("action", "Add");
                    request.getRequestDispatcher("edit.jsp").forward(request, response);
                    break;
            }
        }
    }
}
