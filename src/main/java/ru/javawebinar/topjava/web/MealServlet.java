package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

public class MealServlet extends HttpServlet {
    private List<MealTo> mealToList;
    private int calPerDay = MealsUtil.CALORIES_PER_DAY;
    private LocalTime startTime = LocalTime.of(7, 0);
    private LocalTime endTime = LocalTime.of(21, 0);

    public void init() throws ServletException {
        mealToList = MealsUtil.filteredByStreams(MealsUtil.getMealList(),
                startTime, endTime, calPerDay);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("mealToList", mealToList);
        request.getRequestDispatcher("meals.jsp").forward(request, response);
//        response.sendRedirect("meals.jsp");
    }
}
