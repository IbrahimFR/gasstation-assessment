package gasstation.implementation;


import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by ibrahimfouad on 12/16/2018.
 */
public class StationCusotmersTest {
    private static final Logger LOG = Logger.getLogger(StationCusotmersTest.class.getName());
    GasStationImp station;
    private final double dieselPrice = 0.86;
    private final double regularPrice = 0.406;
    private final double superPrice = 1;
    private final double defaultLiters = 7000;
    private final double minLiters = 2;
    //Number of customers to be created randomly.
    private static int numCustomers = getRandomInt(100);
    //Number of threads to be created randomly
    private static int numThreads = getRandomInt(10);
    //Number of pumps to be created randomly
    private static int numGasPumps = getRandomInt(6);
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
   @Test
   public void testRunCustomerSchedule(){
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);

        //Get an instace of a gas satation

        StationCustomers[] customers = new StationCustomers[numCustomers + 3];

        //at least one of each type
        customers[0] = new StationCustomers(GasType.DIESEL, station, "Customer[0]");
        customers[1] = new StationCustomers(GasType.REGULAR, station, "Customer[1]");
        customers[2] = new StationCustomers(GasType.SUPER, station, "Customer[2]");

        System.out.println("Customer ... number of customers created = "+customers.length);
        int count = 3;
        for (StationCustomers customer : customers) {
            String name = "Customer[" + (count++) + "]";
            customer = new StationCustomers(getRandomGasType(), station, name);
            pool.execute(customer);
        }

        pool.shutdown();
       Assert.assertTrue(true);
    }

    /**
     * Return a GasType value randomly.
     */
    private static GasType getRandomGasType() {
        Random random = new Random();
        return GasType.values()[random.nextInt(GasType.values().length)];
    }

    private static int getRandomInt(int range) {
        Random random = new Random();
        int number = random.nextInt(range)+1;
        return number;
    }

}
