package org.chielokacode.dvillabooking.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomType;
    private BigDecimal roomPrice;
    private String roomDesc;

    private boolean isRoomBooked = false;

    //binary data like audio, images
    @Lob
    private Blob photo;

    //one Room can have many Bookings, and when the room is deleted, all bookings is deleted automatically
    @OneToMany(mappedBy = "room",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookedRoom> bookings;


    //initial step bookings is set to an empty array to avoid null pointer exception
    public Room() {
        this.bookings = new ArrayList<>();
    }

    public void addBookings(BookedRoom booking){
        if (bookings == null){
            bookings = new ArrayList<>();
        }
        bookings.add(booking);
        booking.setRoom(this);
        isRoomBooked = true;
        //when a new room is booked, a confirmation code is generated for the guest
        //randomStringUtils generates a random string number of length 10
        String bookingCode = RandomStringUtils.randomNumeric(10);
        booking.setBookingConfirmationCode(bookingCode);
    }
}
