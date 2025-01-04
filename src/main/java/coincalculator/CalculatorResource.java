package coincalculator;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/coin-calculator")
public class CalculatorResource {
    @GET
    public Response calculateCoins(@QueryParam("targetAmount") String strTargetAmount,
                                   @QueryParam("coinDenominations") String strCoinDenominations) {

        if (strTargetAmount == null || strTargetAmount.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Target amount is required")
                    .build();
        }

        if (strCoinDenominations == null || strCoinDenominations.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Coin denominations are required.")
                    .build();
        }

        try {
            BigDecimal targetAmount = new BigDecimal(strTargetAmount).setScale(2, RoundingMode.HALF_UP);
            if (targetAmount.compareTo(BigDecimal.ZERO) < 0 || targetAmount.compareTo(new BigDecimal("10000")) > 0)  {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Target amount must be between 0 and 10,000.00")
                        .build();
            }

            List<BigDecimal> coinDenominations = Stream.of(strCoinDenominations.split(","))
                    .map(denomination -> new BigDecimal(denomination).setScale(2, RoundingMode.HALF_UP))
                    .collect(Collectors.toList());


            List<BigDecimal> validDenominations = List.of(new BigDecimal("0.01"), new BigDecimal("0.05"), new BigDecimal("0.10"),
                    new BigDecimal("0.20"), new BigDecimal("0.50"), new BigDecimal("1.00"),
                    new BigDecimal("2.00"), new BigDecimal("5.00"), new BigDecimal("10.00"),
                    new BigDecimal("50.00"), new BigDecimal("100.00"), new BigDecimal("1000.00")
                    );
            for (BigDecimal denomination : coinDenominations) {
                if (!validDenominations.contains(denomination)) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Invalid coin denomination: " + denomination)
                            .build();
                }
            }
            List<BigDecimal> result = calculateMinimumCoins(targetAmount, coinDenominations);
            if (!result.isEmpty() && result.get(0).compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal shortfall = result.get(0).negate();
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Unable to reach target amount with provided coin denomination, short of "+ shortfall)
                        .build();
            }
            return Response.ok(result).build();

        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid number format in target amount or coin denominations.")
                    .build();
        }

    }

    private List<BigDecimal> calculateMinimumCoins(BigDecimal targetAmount, List<BigDecimal> coinDenominations) {
        Collections.sort(coinDenominations, Collections.reverseOrder());
        List<BigDecimal> result = new ArrayList<>();
        BigDecimal originalTargetAmount = targetAmount;

        for (BigDecimal coin : coinDenominations) {
            while (targetAmount.compareTo(coin) >= 0) {
                result.add(coin);
                targetAmount = targetAmount.subtract(coin).setScale(2, RoundingMode.HALF_UP);
            }
        }

        if (targetAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal shortfall = originalTargetAmount.subtract(result.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .setScale(2, RoundingMode.HALF_UP);
            result.clear();
            result.add(shortfall.negate());
        }

        Collections.sort(result);
        return result;
    }
}
