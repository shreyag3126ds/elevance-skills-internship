package com.travel.reviewsystem;

import com.travel.reviewsystem.model.Flight;
import com.travel.reviewsystem.model.Hotel;
import com.travel.reviewsystem.model.User;
import com.travel.reviewsystem.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds a couple of users, a hotel and a flight on startup so you can
 * try the endpoints right away without creating reference data by hand.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {
        User traveler = new User(null, "priya", "Priya", false);
        User mod = new User(null, "moderator1", "Moderator One", true);
        userRepository.save(traveler);
        userRepository.save(mod);

        Hotel hotel = new Hotel();
        hotel.setName("Sunrise Grand Hotel");
        hotel.setCity("Goa");
        entityManager.persist(hotel);

        Flight flight = new Flight();
        flight.setAirline("SkyJet");
        flight.setFlightNumber("SJ-204");
        flight.setOrigin("DEL");
        flight.setDestination("BOM");
        entityManager.persist(flight);

        System.out.println("Seeded: user id=" + traveler.getId() + ", moderator id=" + mod.getId()
                + ", hotel id=" + hotel.getId() + ", flight id=" + flight.getId());
    }
}
