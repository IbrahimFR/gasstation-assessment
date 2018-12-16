package gasstation.implementation;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;


/**
 * Created by ibrahimfouad on 12/16/2018.
 */
public class StationCustomers implements Runnable {

    private GasType gasType;
    private GasStation favoriteGasStation;
    private String customerName;
    private double lastAmountOfGasPurchased;
    private double amountOfGasPurchased;

    public StationCustomers(GasType gasType, GasStation favoriteGasStation, String customerName) {
        this.gasType = gasType;
        this.favoriteGasStation = favoriteGasStation;
        this.customerName = customerName;
    }

    @SuppressWarnings("SleepWhileInLoop")
    /**
     * Buy gas to a specific gas station
     */
    public void run() {
        boolean keepBuying = true;

        while (keepBuying) {

            try {
                lastAmountOfGasPurchased = amountOfGasPurchased;
                amountOfGasPurchased = favoriteGasStation.buyGas(getGasType(), generateLitersRequested(), generatePriceWillingToPay());
                amountOfGasPurchased = Math.rint(amountOfGasPurchased * 100) / 100;
                System.out.println(toString() + "purchased Gas! ");
                Thread.sleep((int) (Math.random() * 1));

            } catch (NotEnoughGasException ex) {
                keepBuying = false;
                System.out.println(toString() + "Not enough gas! ");
            } catch (GasTooExpensiveException ex) {
                keepBuying = false;
                System.out.println(toString() + "Gas per liter is too expensive! ");
            } catch (Exception e) {
                Logger.getLogger(StationCustomers.class.getName()).log(Level.SEVERE, null, e);
            }

        }
    }

    public double generatePriceWillingToPay() {
        Random random = new Random();
        double value = (3 * random.nextDouble());
        value = Math.rint(value * 100) / 100;
        return value;
    }

    public double generateLitersRequested() {
        double value = (25 * Math.random());
        value = Math.rint(value * 100) / 100;
        return value;
    }

    public GasType getGasType() {
        return gasType;
    }

    public void setGasType(GasType gasType) {
        this.gasType = gasType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public GasStation getFavoriteGasStation() {
        return favoriteGasStation;
    }

    public void setFavoriteGasStation(GasStation favoriteGasStation) {
        this.favoriteGasStation = favoriteGasStation;
    }

    public double getLastAmountOfGasPurchased() {
        return lastAmountOfGasPurchased;
    }

    public void setLastAmountOfGasPurchased(double lastAmountOfGasPurchased) {
        this.lastAmountOfGasPurchased = lastAmountOfGasPurchased;
    }

    public double getAmountOfGasPurchased() {
        return amountOfGasPurchased;
    }

    public void setAmountOfGasPurchased(double amountOfGasPurchased) {
        this.amountOfGasPurchased = amountOfGasPurchased;
    }        

    @Override
    public String toString() {
        return "Customer name:" + customerName + ", Gas type:" + gasType + ", Amount of gas purchased " + amountOfGasPurchased +", Last amount of gas purchased= Euros " + lastAmountOfGasPurchased;
    }

}
