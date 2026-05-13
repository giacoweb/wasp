package it.unipd.dei.webapp.servlet.api;

import it.unipd.dei.webapp.model.Forecast;
import it.unipd.dei.webapp.model.ForecastScenario;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.ForecastService;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import it.unipd.dei.webapp.servlet.AbstractApiServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ForecastApiServlet", value = "/api/forecast")
public class ForecastApiServlet extends AbstractApiServlet {

    private static final Logger logger = Logger.getLogger(ForecastApiServlet.class.getName());

    private ForecastService forecastService;

    @Override
    public void init() throws ServletException {
        this.forecastService = new ForecastService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        String scenarioParam = request.getParameter("scenario");

        if (scenarioParam == null || scenarioParam.isBlank()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "scenario parameter is required.");
            return;
        }

        try {
            ForecastScenario scenario = ForecastScenario.valueOf(scenarioParam.trim().toUpperCase());
            Forecast forecast = forecastService.findLatest(user.getId(), scenario);

            if (forecast == null) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "No forecast found. Compute one first.");
                return;
            }

            sendJsonResponse(response, HttpServletResponse.SC_OK, toJsonObject(forecast));

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid scenario. Use OPTIMISTIC, CONSERVATIVE, or STRESS_TEST.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in ForecastApiServlet", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        String scenarioParam = request.getParameter("scenario");

        if (scenarioParam == null || scenarioParam.isBlank()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "scenario parameter is required.");
            return;
        }

        try {
            ForecastScenario scenario = ForecastScenario.valueOf(scenarioParam.trim().toUpperCase());
            Forecast forecast = forecastService.compute(user.getId(), scenario);

            sendJsonResponse(response, HttpServletResponse.SC_CREATED, toJsonObject(forecast));

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid scenario. Use OPTIMISTIC, CONSERVATIVE, or STRESS_TEST.");

        } catch (ValidationException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in ForecastApiServlet", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    private User getAuthenticatedUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("authenticatedUser") : null;
        if (user == null) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required.");
        }
        return user;
    }

    private JSONObject toJsonObject(Forecast forecast) {
        JSONObject json = new JSONObject();
        json.put("id", forecast.getId().toString());
        json.put("scenario", forecast.getScenario().name());
        json.put("projection", new JSONArray(forecast.getProjectionJson()));
        json.put("createdAt", forecast.getCreatedAt().toString());
        return json;
    }

    private void sendJsonResponse(HttpServletResponse response, int status, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(data.toString());
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        JSONObject error = new JSONObject();
        error.put("error", message);
        sendJsonResponse(response, status, error);
    }
}
