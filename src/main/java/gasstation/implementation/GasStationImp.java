package gasstation.implementation;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ibrahimfouad on 12/16/2018.
 */
public class GasStationImp implements GasStation{
    // Collection of Gas Pumps this station has.
    private CopyOnWriteArrayList<GasPump> pumps = new CopyOnWriteArrayList<GasPump>();

    // Counter of canceled transactions because of expensive gas.
    private AtomicInteger canceledTransactionsExpensiveCounter = new AtomicInteger(0);

     // Counter of canceled transactions because of no gas..
     private final AtomicInteger cancellationsNoGasCounter = new AtomicInteger(0);

     // Map with list of gas types and corresponding prices.
     private ConcurrentMap<GasType, Double> gasPrices = new ConcurrentHashMap<GasType, Double>();

    // Total revenue counter of the station.
    private final AtomicLong revenueCounter = new AtomicLong(0);

    //Total sales of the station.
    private final AtomicInteger salesCounter = new AtomicInteger(0);



    public void addGasPump(GasPump pump) {
        if (pump == null) {
            throw new IllegalArgumentException("Gas Pump Is Null");
        }
        this.pumps.add(pump);
    }

    public Collection<GasPump> getGasPumps() {
       return this.pumps;
    }

    public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter) throws NotEnoughGasException, GasTooExpensiveException {
        try {
            if (type == null) {
                throw new IllegalArgumentException("Gas Type Is Null");
            }
            if (amountInLiters <= 0) {
                throw new IllegalArgumentException("Amount Must Be > 0");
            }

            final double pricePerLiter = getPrice(type);
            double price = 0;

            //price validation per liter . throw exception when the price is max price per leter.
            if (pricePerLiter > maxPricePerLiter) {
                canceledTransactionsExpensiveCounter.getAndIncrement();
                throw new GasTooExpensiveException();
            }
            //check pumps
            for (GasPump pump : pumps) {
                //check pump of it server the type of gas
                if (pump.getGasType().equals(type)) {
                    //lock pump while using it in another thread
                    synchronized (pump) {
                        //This pump has enough fuel to serve
                        if (pump.getRemainingAmount() >= amountInLiters) {
                            pump.pumpGas(amountInLiters);
                            price = amountInLiters * pricePerLiter;
                            System.out.println("PUPM (" + type.name() + ")Amount Remaining in : " + pump.getRemainingAmount());
                            revenueCounter.addAndGet(new Double(price).longValue());
                            salesCounter.getAndIncrement();
                            break;
                        }
                    }
                }
            }
            if (price == 0 && amountInLiters > 0) {
                cancellationsNoGasCounter.addAndGet(1);
                throw new NotEnoughGasException();
            }
            return price;
        }finally {
            System.out.println("Gas Station Status :  " + toString());
        }
    }

    public double getRevenue() {
        return revenueCounter.get();
    }

    public int getNumberOfSales() {
        return salesCounter.get();
    }

    public int getNumberOfCancellationsNoGas() {
        return cancellationsNoGasCounter.get();
    }

    public int getNumberOfCancellationsTooExpensive() {
        return canceledTransactionsExpensiveCounter.get();
    }

    public double getPrice(GasType type) {
        if (type == null) {
            throw new IllegalArgumentException("Gas Type Is Null");
        }
        return gasPrices.get(type);
    }

    public void setPrice(GasType type, double price) {
        if (type == null) {
            throw new IllegalArgumentException("Gas Type Is Null");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price Must Be > 0");
        }
        this.gasPrices.put(type, price);

    }

    @Override
    public String toString() {
        return "Gas pumps:" + pumps.size() + ", Revenue: " + revenueCounter + ", Sales:" + salesCounter + ", Cancellations for No Gas:" + cancellationsNoGasCounter + ", Cancellations for expensive price:" + canceledTransactionsExpensiveCounter ;
    }
}
