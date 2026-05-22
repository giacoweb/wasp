package it.unipd.dei.webapp.servlet.api;

import it.unipd.dei.webapp.model.Transaction;
import it.unipd.dei.webapp.model.User;
import it.unipd.dei.webapp.service.TransactionService;
import it.unipd.dei.webapp.service.exception.UnauthorizedOperationException;
import it.unipd.dei.webapp.service.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import it.unipd.dei.webapp.servlet.AbstractApiServlet;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "TransactionApiServlet", value = "/api/transactions/*")
public class TransactionApiServlet extends AbstractApiServlet {

    private static final Logger logger = Logger.getLogger(TransactionApiServlet.class.getName());

    private TransactionService transactionService;

    @Override
    public void init() throws ServletException {
        this.transactionService = new TransactionService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        String walletIdParam = request.getParameter("walletId");

        if (walletIdParam == null || walletIdParam.isBlank()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "walletId parameter is required.");
            return;
        }

        try {
            UUID walletId = UUID.fromString(walletIdParam);
            List<Transaction> transactions = transactionService.getTransactionsByWallet(walletId, user.getId());
            sendJsonResponse(response, HttpServletResponse.SC_OK, toJsonArray(transactions));

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid walletId format.");

        } catch (ValidationException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (UnauthorizedOperationException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in TransactionApiServlet", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        try {
            JSONObject body = readJsonBody(request);

            String walletIdStr = getStringOrNull(body, "walletId");
            if (walletIdStr == null) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "walletId is required.");
                return;
            }

            UUID walletId = UUID.fromString(walletIdStr);

            Transaction created = transactionService.createTransaction(
                    walletId,
                    user.getId(),
                    getStringOrNull(body, "amount"),
                    getStringOrNull(body, "category"),
                    getStringOrNull(body, "date"),
                    getStringOrNull(body, "note"));

            sendJsonResponse(response, HttpServletResponse.SC_CREATED, toJsonObject(created));

        } catch (JSONException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Malformed JSON in request body.");

        } catch (IOException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid walletId format.");

        } catch (ValidationException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (UnauthorizedOperationException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in TransactionApiServlet", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        String transactionId = extractPathId(request);
        if (transactionId == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Transaction ID is required in the URL path.");
            return;
        }

        try {
            UUID id = UUID.fromString(transactionId);
            JSONObject body = readJsonBody(request);

            Transaction updated = transactionService.updateTransaction(
                    id,
                    user.getId(),
                    getStringOrNull(body, "walletId"),
                    getStringOrNull(body, "amount"),
                    getStringOrNull(body, "category"),
                    getStringOrNull(body, "date"),
                    getStringOrNull(body, "note"));

            sendJsonResponse(response, HttpServletResponse.SC_OK, toJsonObject(updated));

        } catch (JSONException | IOException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid or malformed request body.");

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid transaction ID format.");

        } catch (ValidationException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (UnauthorizedOperationException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in TransactionApiServlet", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser(request, response);
        if (user == null) {
            return;
        }

        String transactionId = extractPathId(request);
        if (transactionId == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Transaction ID is required in the URL path.");
            return;
        }

        try {
            UUID id = UUID.fromString(transactionId);
            transactionService.deleteTransaction(id, user.getId());
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid transaction ID format.");

        } catch (ValidationException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (UnauthorizedOperationException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error in TransactionApiServlet", e);
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
        String id = pathInfo.substring(1);
        if (id.contains("/")) {
            return null;
        }
        return id;
    }

    private static final int MAX_BODY_SIZE = 8192;

    private String getStringOrNull(JSONObject json, String key) {
        if (json.isNull(key)) {
            return null;
        }
        String value = json.optString(key, null);
        return value != null && !value.isEmpty() ? value : null;
    }

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

    private JSONObject toJsonObject(Transaction transaction) {
        JSONObject json = new JSONObject();
        json.put("id", transaction.getId().toString());
        json.put("walletId", transaction.getWalletId().toString());
        json.put("type", transaction.getType().name());
        json.put("amount", transaction.getAmount());
        json.put("category", transaction.getCategory());
        json.put("date", transaction.getTransactionDate().toString());
        json.put("note", transaction.getNote() != null ? transaction.getNote() : JSONObject.NULL);
        json.put("createdAt", transaction.getCreatedAt().toString());
        return json;
    }

    private JSONArray toJsonArray(List<Transaction> transactions) {
        JSONArray array = new JSONArray();
        for (Transaction transaction : transactions) {
            array.put(toJsonObject(transaction));
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
