package org.chielokacode.dvillabooking.serviceImpl;

import org.chielokacode.dvillabooking.model.BookedRoom;
import org.chielokacode.dvillabooking.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return null;
    }
}
