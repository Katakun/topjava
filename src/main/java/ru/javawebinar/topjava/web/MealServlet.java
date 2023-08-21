package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.storage.MemoryMealStorage;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.slf4j.LoggerFactory.getLogger;


public class MealServlet extends HttpServlet {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final Logger log = getLogger(MealServlet.class);
    private MealStorage storage = new MemoryMealStorage();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime localDateTime = LocalDateTime.parse(request.getParameter("dateTime"), DATE_TIME_FORMATTER);
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        String action = request.getParameter("action");
        switch (action) {
            case "Edit":
                log.debug("doPost/edit");
                Meal editedMeal = new Meal(localDateTime, description, calories);
                editedMeal.setId(Integer.parseInt(request.getParameter("id")));
                storage.update(editedMeal);
                break;
            case "Add":
                log.debug("doPost/add");
                Meal newMeal = new Meal(localDateTime, description, calories);
                storage.create(newMeal);
                break;
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            log.debug("action == null");
            request.setAttribute("mealToList", MealsUtil.filteredByStreams(
                    storage.getAll(), LocalTime.MIN, LocalTime.MAX, MealsUtil.CALORIES_PER_DAY));
            request.getRequestDispatcher("meals.jsp").forward(request, response);
        } else {
            switch (action) {
                case "delete":
                    log.debug("doGet/delete");
                    storage.delete(Integer.parseInt(request.getParameter("id")));
                    request.setAttribute("mealToList", MealsUtil.filteredByStreams(
                            storage.getAll(), LocalTime.MIN, LocalTime.MAX, MealsUtil.CALORIES_PER_DAY));
                    response.sendRedirect("meals");
                    break;
                case "edit":
                    log.debug("doGet/edit");
                    request.setAttribute("meal", storage.get(Integer.parseInt(request.getParameter("id"))));
                    request.setAttribute("action", "Edit");
                    request.getRequestDispatcher("/mealEdit.jsp").forward(request, response);
                    break;
                case "add":
                    log.debug("doGet/add");
                    request.setAttribute("meal", new Meal(
                            LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 0));
                    request.setAttribute("action", "Add");
                    request.getRequestDispatcher("/mealEdit.jsp").forward(request, response);
                    break;
                default:
                    log.debug("doGet/default");
                    request.setAttribute("mealToList", MealsUtil.filteredByStreams(
                            storage.getAll(), LocalTime.MIN, LocalTime.MAX, MealsUtil.CALORIES_PER_DAY));
                    request.getRequestDispatcher("meals.jsp").forward(request, response);
            }
        }
    }
}
