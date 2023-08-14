<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <title>Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>
<form method="post" action="meals" enctype="application/x-www-form-urlencoded">
    <input type="hidden" name="id" value="${meal.id}">
    DateTime:
    <input type="datetime-local" name="dateTime" value="${meal.dateTime}"/><br>

    Description:
    <input type="text" name="description" value="${meal.description}"/><br>

    Calories:
    <input type="text" name="calories" value="${meal.calories}"/><br>
    <button type="submit">Save</button>
    <button type="reset" onclick="window.history.back()">Cancel</button>
</form>
</body>
</html>
