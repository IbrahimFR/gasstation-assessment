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
    /**
     * Test Station Configuration and Layout
     * - [Diesel pumps]
     *      quantity: 2
     *      individual capacity: 5 liters
     *      total capacity: 10 liters
     *      price per liter: $0.76
     * - [Regular pumps]
     *      quantity: 3
     *      individual capacity: 5000 liters
     *      total capacity: 150000 liters
     *      price per liter: $0.56
     */
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

    /**
     * Test of addGasPump method, of class Station.
     */
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

    /**
     * Test of buyGas method, of class Station. We invoke this test from 1000 different
     * threads simultaneously. We don't test with a higher number yet in order to keep
     * compilation time low (disable test on compile if needed)
     */
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

    /**
     * Force the use of multiple gas pumps of the same type. If for a transaction requested
     * the first found pump of the given type has no capacity to serve (not enough gas), check
     * if there are more pumps available with that gas type that can within the station.
     */
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

    /**
     * Test the NoGas Exception. We will request an amount of fuel that no configured pump
     * has in order to force it.
     * @throws Exception
     */
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

    /**
     * Test for the TooExpensive exception. We will set a max price to pay at zero indicating
     * we want the fuel for free, how cool would that be!
     * @throws Exception
     */
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
        boolean success = false;
        LOG.info("[testgetRevenue] current revenue . Current Thread: "+idThread);
        double revenue = station.getRevenue();
        Assert.assertTrue(revenue==0.0);
    }

}
