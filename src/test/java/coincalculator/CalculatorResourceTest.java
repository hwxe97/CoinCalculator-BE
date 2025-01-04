package coincalculator;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import jakarta.ws.rs.BadRequestException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(DropwizardExtensionsSupport.class)
public class CalculatorResourceTest {
    private static final ResourceExtension RESOURCES = ResourceExtension.builder()
            .addResource(new CalculatorResource())
            .build();

    @Test
    void testCalculateCoinsEndpoint_Success() {
        String targetAmount = "7.03";
        String coinDenominations = "0.01,0.5,1.0,5.0,10.0";

        Response response = RESOURCES.target("/coin-calculator")
                .queryParam("targetAmount", targetAmount)
                .queryParam("coinDenominations", coinDenominations)
                .request()
                .get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            List<Double> coins = response.readEntity(List.class);
            assertThat(coins).
                    containsExactly(0.01,0.01,0.01,1.0,1.0,5.0);
        } else {
            fail("Unexpected response status: " + response.getStatus());
        }
    }

    @Test
    void testCalculateCoinsEndpoint_BadRequest() {
        String targetAmount = "103";
        String coinDenominations = "10";

        Response response = RESOURCES.target("/coin-calculator")
                .queryParam("targetAmount", targetAmount)
                .queryParam("coinDenominations", coinDenominations)
                .request()
                .get();

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            String errorMessage = response.readEntity(String.class);
            assertThat(errorMessage).isEqualTo("Unable to reach target amount with provided coin denomination, short of 3.00");
        } else {
            fail("Unexpected response status: " + response.getStatus());
        }
    }

    @Test
    void testCalculateCoinsEndpoint_InvalidInput() {
        String targetAmount = "abc";
        String coinDenominations = "0.1,1.0";

        Response response = RESOURCES.target("/coin-calculator")
                .queryParam("targetAmount", targetAmount)
                .queryParam("coinDenominations", coinDenominations)
                .request()
                .get();

        if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            String errorMessage = response.readEntity(String.class);
            assertThat(errorMessage).isEqualTo("Invalid number format in target amount or coin denominations.");
        } else {
            fail("Unexpected response status: " + response.getStatus());
        }
    }
}