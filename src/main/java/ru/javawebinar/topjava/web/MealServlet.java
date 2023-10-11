package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private MealRestController mealRestController;
    ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");

    @Override
    public void init() {
        mealRestController = appCtx.getBean(MealRestController.class);
    }

    public void destroy() {
        appCtx.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        boolean isFilter = Boolean.parseBoolean(request.getParameter("isFilter"));

        if (isFilter) {
            LocalDate startDate = request.getParameter("startDate").isEmpty() ?
                    LocalDate.MIN : LocalDate.parse(request.getParameter("startDate"));
            LocalDate endDate = request.getParameter("endDate").isEmpty() ?
                    LocalDate.MAX : LocalDate.parse(request.getParameter("endDate"));
            LocalTime startTime = request.getParameter("startTime").isEmpty() ?
                    LocalTime.MIN : LocalTime.parse(request.getParameter("startTime"));
            LocalTime endTime = request.getParameter("endTime").isEmpty() ?
                    LocalTime.MAX : LocalTime.parse(request.getParameter("endTime"));
            request.setAttribute("meals", mealRestController.getFilteredByDateAndTime(
                    startDate, startTime, endDate, endTime));
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
        } else {
            Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                    LocalDateTime.parse(request.getParameter("dateTime")),
                    request.getParameter("description"),
                    Integer.parseInt(request.getParameter("calories")));
            log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
            if (meal.isNew()) {
                mealRestController.create(meal);
            } else {
                mealRestController.update(meal, meal.getId());
            }
            response.sendRedirect("meals");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete id={}", id);
                mealRestController.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        mealRestController.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("meals", mealRestController.getAllMealTo());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
