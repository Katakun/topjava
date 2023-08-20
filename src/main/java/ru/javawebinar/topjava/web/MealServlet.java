package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.storage.MemoryMealStorage;
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

import static org.slf4j.LoggerFactory.getLogger;


public class MealServlet extends HttpServlet {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final org.slf4j.Logger log = getLogger(MealServlet.class);
    private MealStorage storage;

    public void init() throws ServletException {
        storage = new MemoryMealStorage(MealsData.meals);
        MealsUtil.filteredByStreams(storage.getAll(),
                LocalTime.MIN, LocalTime.MAX, MealsData.CALORIES_PER_DAY);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime localDateTime = LocalDateTime.parse(request.getParameter("dateTime"), DATE_TIME_FORMATTER);
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        String action = request.getParameter("action");
        switch (action) {
            case "Edit":
                Meal editedMeal = new Meal(localDateTime, description, calories);
                editedMeal.setId(Integer.parseInt(request.getParameter("id")));
                storage.update(editedMeal);
                log.debug("doPost/edit");
                break;
            case "Add":
                Meal newMeal = new Meal(localDateTime, description, calories);
                storage.create(newMeal);
                log.debug("doPost/add");
                break;
        }
        MealsUtil.filteredByStreams(storage.getAll(),
                LocalTime.MIN, LocalTime.MAX, MealsData.CALORIES_PER_DAY);
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("mealToList", MealsUtil.filteredByStreams(storage.getAll(), LocalTime.MIN, LocalTime.MAX, MealsData.CALORIES_PER_DAY));
            request.getRequestDispatcher("meals.jsp").forward(request, response);
            log.debug("action == null");
        } else {
            switch (action) {
                case "delete":
                    storage.delete(Integer.parseInt(request.getParameter("id")));
                    request.setAttribute("mealToList", MealsUtil.filteredByStreams(storage.getAll(), LocalTime.MIN, LocalTime.MAX, MealsData.CALORIES_PER_DAY));
                    response.sendRedirect("meals");
                    log.debug("doGet/delete");
                    break;
                case "edit":
                    request.setAttribute("meal", storage.get(Integer.parseInt(request.getParameter("id"))));
                    request.setAttribute("action", "Edit");
                    request.getRequestDispatcher("/edit.jsp").forward(request, response);
                    log.debug("doGet/edit");
                    break;
                case "add":
                    request.setAttribute("meal", new Meal(LocalDateTime.now(), "", 0));
                    request.setAttribute("action", "Add");
                    request.getRequestDispatcher("/edit.jsp").forward(request, response);
                    log.debug("doGet/add");
                    break;
                default:
                    request.setAttribute("mealToList", MealsUtil.filteredByStreams(storage.getAll(), LocalTime.MIN, LocalTime.MAX, MealsData.CALORIES_PER_DAY));
                    request.getRequestDispatcher("meals.jsp").forward(request, response);
                    log.debug("doGet/default");
            }
        }
    }
}
