package gasstation.implementation;

/**
 * Created by ibrahimfouad on 12/16/2018.
 */


import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class GasStationImpTest {

    private static final Logger LOG = Logger.getLogger(GasStationImpTest.class.getName());
    private GasStationImp station;
    private final double dieselPrice = 0.86;
    private final double regularPrice = 0.406;
    private final double superPrice = 1;
    private final double defaultLiters = 7000;
    private final double minLiters = 2;

    @Before
    public void setUpGasStation() {
        //Create station
        station = new GasStationImp();
        //Set prices
        station.setPrice(GasType.REGULAR, regularPrice);
        station.setPrice(GasType.SUPER, superPrice);
        station.setPrice(GasType.DIESEL, dieselPrice);
        //Add pumps
        GasPump pumpDiesel1 = new GasPump(GasType.DIESEL, minLiters);
        GasPump pumpDiesel2 = new GasPump(GasType.DIESEL, minLiters);
        //Create regular pumps
        GasPump pumpRegular1 = new GasPump(GasType.REGULAR, defaultLiters);
        GasPump pumpRegular2 = new GasPump(GasType.REGULAR, defaultLiters);
        GasPump pumpRegular3 = new GasPump(GasType.REGULAR, defaultLiters);

        //Create super pumps
        GasPump pumpSuper1 = new GasPump(GasType.SUPER, defaultLiters);
        GasPump pumpSuper2 = new GasPump(GasType.SUPER, defaultLiters);
        GasPump pumpSuper3 = new GasPump(GasType.SUPER, defaultLiters);
        //Add to station
        station.addGasPump(pumpDiesel1);
        station.addGasPump(pumpRegular1);
        station.addGasPump(pumpRegular3);
        station.addGasPump(pumpRegular2);
        station.addGasPump(pumpDiesel2);
        station.addGasPump(pumpSuper1);
        station.addGasPump(pumpSuper2);
        station.addGasPump(pumpSuper3);
        LOG.info("Station created successfully");
    }


    /**
     * Test of setPrice method, of class Station.
     */
    @Test
    public void testPrice() {
        long idThread = Thread.currentThread().getId();
        LOG.info("[testSetPrice] Setting prices for gas types. Current Thread: "+idThread);
        Random random = new Random();
        double rPrice = random.nextDouble();
        double sPrice = random.nextDouble();
        double dPrice = random.nextDouble();
        station.setPrice(GasType.REGULAR, rPrice);
        station.setPrice(GasType.SUPER, sPrice);
        station.setPrice(GasType.DIESEL, dPrice);
        Assert.assertTrue(  station.getPrice(GasType.REGULAR) == rPrice &&
                station.getPrice(GasType.SUPER) == sPrice &&
                station.getPrice(GasType.DIESEL) == dPrice);
    }


    @Test
    public void testGasPumps() {
        long idThread = Thread.currentThread().getId();
        LOG.info("[testGasPumps] Adding gas pumps. Current Thread: "+idThread);
        GasPump pumpDiesel1 = new GasPump(GasType.DIESEL, defaultLiters);
        GasPump pumpRegular1 = new GasPump(GasType.REGULAR, defaultLiters);
        GasPump pumpSuper1 = new GasPump(GasType.SUPER, defaultLiters);
        station.addGasPump(pumpDiesel1);
        station.addGasPump(pumpRegular1);
        station.addGasPump(pumpSuper1);
        //Take into consideration the 3 added here and the 5 added default
        Assert.assertTrue(station.getGasPumps().size() == 11);
    }

    @Test
    public void testBuyGas() throws Exception {
        long idThread = Thread.currentThread().getId();
        LOG.info("[testBuyGas] Buying gas. Current Thread: "+idThread);
        double pricePerLiter = station.getPrice(GasType.SUPER);
        Random random = new Random();
        double liters = random.nextDouble()*10;
        double price = 0;
        double estimatedPrice = liters * pricePerLiter;
        try {
            price = station.buyGas(GasType.SUPER, liters, 1);
            LOG.info("[testBuyGas] Price paid:"+price+". Current Thread: "+idThread);
        } catch (NotEnoughGasException ex) {
            LOG.log(Level.SEVERE,"Not enough gas");
        } catch (GasTooExpensiveException ex) {
            LOG.log(Level.SEVERE,"Gas too expensive");
        }
        Assert.assertTrue(price == estimatedPrice);
    }

    @Test
    public void testBuyGasDepleted() throws Exception {
        long idThread = Thread.currentThread().getId();
        boolean success = false;
        LOG.info("[testBuyGasDepleted] Buying gas. Current Thread: "+idThread);
        try {
            station.buyGas(GasType.DIESEL, 1, 1.1);
            success = true;
        } catch (NotEnoughGasException ex) {
            LOG.log(Level.SEVERE,"Not enough gas");
        } catch (GasTooExpensiveException ex) {
            LOG.log(Level.SEVERE,"Gas too expensive");
        }
        Assert.assertTrue(success);
    }


    @Test
    public void testNoGasException() throws Exception {
        long idThread = Thread.currentThread().getId();
        boolean success = false;
        LOG.info("[testNoGasException] Buying gas. Current Thread: "+idThread);
        try {
            station.buyGas(GasType.DIESEL, 100000, 2);
        } catch (NotEnoughGasException ex) {
            success = true;
            LOG.log(Level.SEVERE,"Not enough gas");
        } catch (GasTooExpensiveException ex) {
            LOG.log(Level.SEVERE,"Gas too expensive");
        }
        Assert.assertTrue(success);
    }


    @Test
    public void testTooExpensiveException() throws Exception {
        long idThread = Thread.currentThread().getId();
        boolean success = false;
        LOG.info("[testTooExpensiveException] Buying gas. Current Thread: "+idThread);
        try {
            station.buyGas(GasType.REGULAR, 1, 0);
        } catch (NotEnoughGasException ex) {
            LOG.log(Level.SEVERE,"Not enough gas");
        } catch (GasTooExpensiveException ex) {
            success = true;
            LOG.log(Level.SEVERE,"Gas too expensive");
        }
        Assert.assertTrue(success);
    }


    @Test
    public void testgetRevenue() throws Exception {
        long idThread = Thread.currentThread().getId();
        LOG.info("[testgetRevenue] current revenue . Current Thread: "+idThread);
        double revenue = station.getRevenue();
        Assert.assertTrue(revenue==0.0);
    }


    @Test
    public void shouldReturnTheLitersOfGasSoldForEachPump() throws Exception {

        double amountInLiters1 = 50d;
        double amountInLiters2 = 60d;
        double maxPricePerLiter1 = 1.5d;
        double maxPricePerLiter2 = 1.6d;

        GasPump pump1 = new GasPump(GasType.DIESEL, amountInLiters1);
        GasPump pump2 = new GasPump(GasType.SUPER, amountInLiters2);

        double expectedResult1 = 75d;
        double expectedResult2 = 96d;
        double expectedLitersOfGasSold1 = 50d;
        double expectedLitersOfGasSold2 = 60d;

        station.addGasPump(pump1);
        station.addGasPump(pump2);
        station.setPrice(GasType.DIESEL, maxPricePerLiter1);
        station.setPrice(GasType.SUPER, maxPricePerLiter2);

        double actualResult1 = station.buyGas(GasType.DIESEL, amountInLiters1, maxPricePerLiter1);
        double actualResult2 = station.buyGas(GasType.SUPER, amountInLiters2, maxPricePerLiter2);
        double actualLitersOfGasSold1 = station.getAmountSold(GasType.DIESEL);
        double actualLitersOfGasSold2 = station.getAmountSold(GasType.SUPER);

        Assert.assertEquals(expectedResult1, actualResult1,0.001);
        Assert.assertEquals(expectedResult2, actualResult2,0.001);
        Assert.assertEquals(expectedLitersOfGasSold1, actualLitersOfGasSold1,0.001);
        Assert.assertEquals(expectedLitersOfGasSold2, actualLitersOfGasSold2,0.001);

    }

    @Test
    public void shouldBeAbleToServeGasSuccessfully() throws Exception {
        double amountInLiters = 50d;
        double maxPricePerLiter = 1.5d;

        GasPump pump1 = new GasPump(GasType.DIESEL, amountInLiters);
        GasPump pump2 = new GasPump(GasType.SUPER, amountInLiters);

        double expectedResult = 75d;

        station.addGasPump(pump1);
        station.addGasPump(pump2);
        station.setPrice(GasType.DIESEL, maxPricePerLiter);

        double actualResult = station.buyGas(GasType.DIESEL, amountInLiters, maxPricePerLiter);

        Assert.assertEquals(expectedResult, actualResult,0.001);

    }
    @Test
    public void shouldReturnTheTotalRevenueEarned() throws Exception {

        double amountInLiters1 = 50d;
        double amountInLiters2 = 60d;
        double maxPricePerLiter1 = 1.5d;
        double maxPricePerLiter2 = 1.6d;

        GasPump pump1 = new GasPump(GasType.DIESEL, amountInLiters1);
        GasPump pump2 = new GasPump(GasType.SUPER, amountInLiters2);

        double expectedResult1 = 75d;
        double expectedResult2 = 96d;
        double expectedRevenueEarned = 171d;

        station .addGasPump(pump1);
        station.addGasPump(pump2);
        station.setPrice(GasType.DIESEL, maxPricePerLiter1);
        station.setPrice(GasType.SUPER, maxPricePerLiter2);

        double actualResult1 = station.buyGas(GasType.DIESEL, amountInLiters1, maxPricePerLiter1);
        double actualResult2 = station.buyGas(GasType.SUPER, amountInLiters2, maxPricePerLiter2);
        double actualRevenueEarned = station.getRevenue();

        Assert.assertEquals(expectedResult1, actualResult1,.001);
        Assert.assertEquals(expectedResult2, actualResult2,.001);
        Assert.assertEquals(expectedRevenueEarned, actualRevenueEarned,.001);

    }


}
