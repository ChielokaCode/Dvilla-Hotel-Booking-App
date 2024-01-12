package org.chielokacode.dvillabooking.service;

import org.chielokacode.dvillabooking.model.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;


public interface BookingService {
    List<BookedRoom> getAllBookingsByRoomId(Long roomId);
}
