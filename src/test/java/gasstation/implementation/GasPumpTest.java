package gasstation.implementation;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GasPumpTest {

    private static final double AVAILABLE_GAS = 50d;
    private static final GasType GAS_TYPE = GasType.DIESEL;

    private GasPump gasPump;

    @Before
    public void setup() {

        gasPump = new GasPump(GAS_TYPE, AVAILABLE_GAS);

    }

    @Test
    public void shouldPumpGasAndHaveTheCorrectRemainingAmountAfterwards() throws Exception {

        // Given
        double expectedAmountAfterPump = 10d;

        // When
        gasPump.pumpGas(40d);

        double amount =  gasPump.getRemainingAmount();

        // Then
       assertEquals(expectedAmountAfterPump,amount,0.001);

    }

    @Test
    public void shouldReturnTheGasTypeThePumpServes() throws Exception {

        // When
        GasType result = gasPump.getGasType();

        // Then
        assertEquals(GAS_TYPE, result);

    }

}