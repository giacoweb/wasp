package it.unipd.dei.webapp.servlet.page;

import it.unipd.dei.webapp.dao.ReportSessionDAO;
import it.unipd.dei.webapp.model.ReportSession;
import it.unipd.dei.webapp.service.PdfReportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebServlet(name = "PdfExportServlet", value = "/report/export")
public class PdfExportServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(PdfExportServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tokenParam = request.getParameter("token");
        HttpSession session = request.getSession(false);
        PdfReportService reportService = new PdfReportService();


        Object authUser = null;
        if (session != null) {
            authUser = session.getAttribute("authenticatedUser");
        }


        if (tokenParam != null && !tokenParam.isBlank()) {
            try
            {

                UUID requestedToken = UUID.fromString(tokenParam);

                UUID ownerToken = null;
                if (session != null) {
                    ownerToken = (UUID) session.getAttribute("guestSessionToken");
                }

                if (ownerToken == null || !ownerToken.equals(requestedToken))
                {
                    logger.log(Level.WARNING, "Unauthorized PDF export attempt for token {0} from {1}",
                            new Object[]{requestedToken, request.getRemoteAddr()});
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. You do not have permission to download this file.");
                    return;
                }


                byte[] pdfBytes = reportService.generateGuestPdf(requestedToken);
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"fintrack-report.pdf\"");
                response.setContentLength(pdfBytes.length);
                response.getOutputStream().write(pdfBytes);


            }
            catch (IllegalArgumentException e)
            {
                if ("No data to save. The report session is empty.".equals(e.getMessage())) {
                    response.sendRedirect(request.getContextPath() + "/guest/report?error=no_data");
                    return;
                }
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Database error during PDF export", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQL Error.");

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Unexpected error during PDF export", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        else if (authUser != null)
        {
            //Not implemented Authenticated method
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Authenticated export feature is not yet implemented.");

        }
        else
        {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authorized.");
        }


    }


}
