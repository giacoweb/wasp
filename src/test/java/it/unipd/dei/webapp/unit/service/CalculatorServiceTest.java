package it.unipd.dei.webapp.unit.service;

import it.unipd.dei.webapp.model.AmortizationRow;
import it.unipd.dei.webapp.model.CompoundResult;
import it.unipd.dei.webapp.service.CalculatorService;
import it.unipd.dei.webapp.service.exception.ValidationException;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorServiceTest {

    private final CalculatorService calculatorService = new CalculatorService();


    //Test calculateloan()

    @Test
    void calculateLoan_ValidData_ReturnsCorrectSchedule() throws ValidationException {

        //Using 10000€, 10% annualRate , 24 month
        List<AmortizationRow> schedule = calculatorService.calculateLoan(1000, 10, 12);

        assertNotNull(schedule);
        assertEquals(12,schedule.size());

    }

    @Test
    void calculateLoan_NegativePrincipal_ThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                        calculatorService.calculateLoan(-1000, 10, 12)
        );
    }

    @Test
    void calculateLoan_NegativeAnnualRate_ThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                calculatorService.calculateLoan(1000, -10, 12)
        );
    }

    @Test
    void calculateLoan_AnnualRateTooHigh_ThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                calculatorService.calculateLoan(1000, 150, 12)
        );
    }

    @Test
    void calculateLoan_NegativeMonths_ThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                calculatorService.calculateLoan(1000, 10, -12)
        );
    }


    //Test calculateCompound()

    @Test
    void calculateCompound_ValidData_ReturnsCorrectProjections() throws ValidationException {

        // Example: 1000€ initialcapital, 0 monthlyContribution, 10% annualRate, 2 years
        List<CompoundResult> results = calculatorService.calculateCompound(1000, 0, 10, 2);
        assertNotNull(results);
        assertEquals(2, results.size());

    }

    @Test
    void calculateCompound_NegativeInitialCapital_ThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                calculatorService.calculateCompound(-1000, 0, 10,2)
        );
    }

    @Test
    void calculateCompound_NegativeMonthlyContribution_ThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                calculatorService.calculateCompound(1000, -100, 10,2)
        );
    }

    @Test
    void calculateCompound_NegativeAnnualRate_ThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                calculatorService.calculateCompound(1000, 0, -10,2)
        );
    }

    @Test
    void calculateCompound_AnnualRateTooHigh_ThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                calculatorService.calculateCompound(1000, 0, 150,2)
        );
    }

    @Test
    void calculateCompound_NegativeYears_ThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                calculatorService.calculateCompound(1000, 0, 10,-2)
        );
    }

}
