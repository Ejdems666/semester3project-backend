package org.cba.model.facades;

import io.ebean.Ebean;
import org.cba.model.entities.Rental;

public class RentalFacade {

    public Rental addRental(String city, String zip, String address, String description, String title, String image, Double latitude, Double longitude) {

            Rental rental = new Rental();
            rental.setCity(city);
            rental.setZip(zip);
            rental.setAddress(address);
            rental.setDescription(description);
            rental.setTitle(title);
            rental.setImageUrl(image);
            rental.setLatitude(latitude);
            rental.setLongitude(longitude);
            Ebean.save(rental);
            return rental;
    }
}