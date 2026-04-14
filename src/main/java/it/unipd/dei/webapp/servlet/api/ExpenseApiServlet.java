package it.unipd.dei.webapp.servlet.api;

import it.unipd.dei.webapp.model.RecurringExpense;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.RecurringExpenseService;
import it.unipd.dei.webapp.service.exception.RecurringExpenseNotFoundException;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "ExpenseApiServlet", value = "/api/expenses/*")
public class ExpenseApiServlet extends HttpServlet {

    private RecurringExpenseService expenseService;

    @Override
    public void init() throws ServletException {
        this.expenseService = new RecurringExpenseService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        try {
            List<RecurringExpense> expenses = expenseService.getExpensesByUser(user.getId());
            sendJsonResponse(response, HttpServletResponse.SC_OK, toJsonArray(expenses));

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.endsWith("/toggle")) {
            handleToggle(request, response, user, pathInfo);
        } else {
            handleCreate(request, response, user);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        String expenseId = extractPathId(request);
        if (expenseId == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Expense ID is required in the URL path.");
            return;
        }

        try {
            UUID id = UUID.fromString(expenseId);
            JSONObject body = readJsonBody(request);

            RecurringExpense updated = expenseService.updateExpense(
                    id,
                    user.getId(),
                    getStringOrNull(body, "label"),
                    getStringOrNull(body, "amount"),
                    getStringOrNull(body, "periodicity"),
                    getStringOrNull(body, "nextDue"));

            sendJsonResponse(response, HttpServletResponse.SC_OK, toJsonObject(updated));

        } catch (JSONException | IOException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid or malformed request body.");

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid expense ID format.");

        } catch (ValidationException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (RecurringExpenseNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (UnauthorizedOperationException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        String expenseId = extractPathId(request);
        if (expenseId == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Expense ID is required in the URL path.");
            return;
        }

        try {
            UUID id = UUID.fromString(expenseId);
            expenseService.deleteExpense(id, user.getId());
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid expense ID format.");

        } catch (RecurringExpenseNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (UnauthorizedOperationException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        try {
            JSONObject body = readJsonBody(request);

            RecurringExpense created = expenseService.createExpense(
                    user.getId(),
                    getStringOrNull(body, "walletId"),
                    getStringOrNull(body, "label"),
                    getStringOrNull(body, "amount"),
                    getStringOrNull(body, "periodicity"),
                    getStringOrNull(body, "nextDue"));

            sendJsonResponse(response, HttpServletResponse.SC_CREATED, toJsonObject(created));

        } catch (JSONException | IOException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Malformed JSON in request body.");

        } catch (ValidationException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (UnauthorizedOperationException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    private void handleToggle(HttpServletRequest request, HttpServletResponse response, User user, String pathInfo)
            throws IOException {
        // pathInfo is like "/{id}/toggle"
        String[] parts = pathInfo.split("/");
        if (parts.length < 3) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid path for toggle.");
            return;
        }

        try {
            UUID id = UUID.fromString(parts[1]);
            RecurringExpense updated = expenseService.toggleExpense(id, user.getId());
            sendJsonResponse(response, HttpServletResponse.SC_OK, toJsonObject(updated));

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid expense ID format.");

        } catch (RecurringExpenseNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());

        } catch (UnauthorizedOperationException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (Exception e) {
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

    private String extractPathId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        String trimmed = pathInfo.substring(1);
        if (trimmed.contains("/")) {
            return null;
        }
        return trimmed;
    }

    private static final int MAX_BODY_SIZE = 8192;

    private JSONObject readJsonBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            char[] buffer = new char[1024];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, charsRead);
                if (sb.length() > MAX_BODY_SIZE) {
                    throw new IOException("Request body too large.");
                }
            }
        }
        String body = sb.toString().trim();
        if (body.isEmpty()) {
            throw new IOException("Request body is empty.");
        }
        return new JSONObject(body);
    }

    private String getStringOrNull(JSONObject json, String key) {
        if (json.isNull(key)) {
            return null;
        }
        String value = json.optString(key, null);
        return value != null && !value.isEmpty() ? value : null;
    }

    private JSONObject toJsonObject(RecurringExpense expense) {
        JSONObject json = new JSONObject();
        json.put("id", expense.getId().toString());
        json.put("walletId", expense.getWalletId().toString());
        json.put("label", expense.getLabel());
        json.put("amount", expense.getAmount());
        json.put("periodicity", expense.getPeriodicity().name());
        json.put("nextDue", expense.getNextDue().toString());
        json.put("active", expense.isActive());
        json.put("createdAt", expense.getCreatedAt().toString());
        return json;
    }

    private JSONArray toJsonArray(List<RecurringExpense> expenses) {
        JSONArray array = new JSONArray();
        for (RecurringExpense expense : expenses) {
            array.put(toJsonObject(expense));
        }
        return array;
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
