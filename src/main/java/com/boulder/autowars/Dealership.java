package com.boulder.autowars;

import com.boulder.autowars.vehicles.Vehicle;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Dealership {

    // Define properties/methods
    String name;
    BigDecimal balance;
    ArrayList<Vehicle> carLot = new ArrayList<>();
    ArrayList<String> salesHistory = new ArrayList<>();

    // Constructor
    public Dealership(String name, BigDecimal startingBalance) {
        this.name = name;
        this.balance = startingBalance;

        System.out.println(this.name + " is open for business.");
        System.out.println("Starting balance: " + NumberFormat.getCurrencyInstance().format(this.balance));
    }

    // Pick a random vehicle in the dealership's lot
    public Vehicle getRandomVehicle() {
        return this.carLot.get(((int) Math.floor(Math.random() * this.carLot.size())));
    }

    // Purchase vehicles with spending limit
    public Vehicle[] purchaseVehicles(Factory factory, BigDecimal budget) {
        ArrayList<Vehicle> purchased = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Vehicle v : factory.readyToShip) {

            if (budget.subtract(v.getCostToDealership()).compareTo(BigDecimal.valueOf(0)) > 0) {
                purchased.add(v);
                budget = budget.subtract(v.getCostToDealership());
                totalCost = totalCost.add(v.getCostToDealership());
            }

        }

        this.balance = this.balance.subtract(totalCost);

        System.out.println(purchased.size() + " vehicles added to lot. | Total cost: " + totalCost + " | Remaining balance: " + this.balance);

        return purchased.toArray(new Vehicle[0]);
    }

    // Adds vehicles to the lot and returns the number of vehicles added
    public int addToLot(Vehicle[] vehicles) {
        for (Vehicle v : vehicles) {
            this.balance = this.balance.subtract(v.getCostToDealership());
            this.carLot.add(v);
        }

        System.out.println("Vehicles added to lot: " + vehicles.length);

        return vehicles.length;
    }

    // Sells a vehicle by vin and returns the dealership balance after sale
    public BigDecimal sellVehicle(String vin) {
        Vehicle match = null;
        boolean found = false;

        for (Vehicle vehicle : this.carLot) {
            if (!found) {
                if (vehicle.getVin().equals(vin)) {
                    match = vehicle;
                    found = true;
                }
            }
        }

        if (found) {
            System.out.println("Vehicle sold: " + match.getYear() + " " + match.getMake() + " " + match.getModel());
            System.out.println("VIN: " + vin);
            System.out.println("Net profit: " + (match.getPrice().subtract(match.getCostToDealership())));
            System.out.println("Buyer: " + null);

            this.carLot.remove(match);
        } else {
            System.out.println("No VIN match found: " + vin);
        }

        return this.balance;
    }

    // Check vehicle insurance, renew if necessary, return array of vehicles that had expired insurance
    public Vehicle[] checkInsurance() {
        System.out.println("Checking insurance for all vehicles...");

        ArrayList<Vehicle> expired = new ArrayList<>();
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        for (Vehicle v : this.carLot) {
            LocalDate lastInsured = LocalDate.parse(v.getLastInsured().toString(), DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy"));
            int daysAgoLastInsured = Utils.DateDiff.inDays(lastInsured, thirtyDaysAgo);

            if (daysAgoLastInsured > 30) {
                expired.add(v);
                renewInsurance(v);
            }
        }

        Vehicle[] result = new Vehicle[expired.size()];
        result = expired.toArray(result);

        if (result.length == 0) {
            System.out.println("All vehicle insurance is up to date.");
        } else {
            System.out.println("Vehicle insurance policies renewed: " + result.length);
        }

        return result;
    }

    // Renew a vehicle's insurance and return the updated vehicle
    public Vehicle renewInsurance(Vehicle v) {
        v.setLastInsured(new Date());
        return v;
    }

    // Check vehicle maintenance, get vehicle serviced if necessary, return array of vehicles that needed maintenance
    public Vehicle[] checkMaintenance() {
        System.out.println("Checking maintenance for all vehicles...");

        ArrayList<Vehicle> overdue = new ArrayList<>();
        LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90);

        for (Vehicle v : this.carLot) {
            LocalDate lastServiced = LocalDate.parse(v.getLastServiced().toString(), DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy"));
            int daysAgoLastServiced = Utils.DateDiff.inDays(lastServiced, ninetyDaysAgo);

            if (daysAgoLastServiced > 90) {
                overdue.add(v);
                serviceVehicle(v);
            }
        }

        Vehicle[] result = new Vehicle[overdue.size()];
        result = overdue.toArray(result);

        if (result.length == 0) {
            System.out.println("All vehicle maintenance is up to date.");
        } else {
            System.out.println("Vehicles serviced: " + result.length);
        }

        return result;
    }

    // Service a vehicle and return the updated vehicle
    public Vehicle serviceVehicle(Vehicle v) {
        v.setLastServiced(new Date());
        return v;
    }

    // Refuel a vehicle and return the updated vehicle
    public Vehicle refuel(Vehicle v) {
        v.setFuel(v.getFuelCapacity());

        System.out.println("Vehicle refueled: " + v.getVin());

        return v;
    }

    // Give a sales pitch for a vehicle
    public String salesPitch(Vehicle v) {
        return "Come on down to "
                + this.name
                + " n gitchurself a "
                + v.getDescription()
                + " for a stupidly affordable $"
                + v.getPrice()
                + "!";
    }

    // Select a vehicle to test drive
    public Vehicle selectVehicleForTestDrive(int year, String make, String model) {
        Vehicle vehicle = null;
        boolean vehicleFound = false;

        for (Vehicle v : this.carLot) {
            if (!vehicleFound) {
                if (v.getYear() == year && v.getMake().equals(make) && v.getModel().equals(model)) {
                    vehicle = v;
                    vehicleFound = true;
                }
            }
        }

        if (vehicleFound) {
            System.out.println("Vehicle selected for test drive: " + vehicle.getVin());
            if (!vehicle.checkFuel()) {
                this.refuel(vehicle);
            }
            if (!vehicle.isClean()) {
                this.wash(vehicle);
            }
        } else {
            System.out.println("Sorry, we don't have a " + year + " " + make + " " + model + " in stock right now");
        }

        return vehicle;
    }

    // Wash a vehicle
    public Vehicle wash(Vehicle v) {
        v.setClean(true);
        return v;
    }
}
