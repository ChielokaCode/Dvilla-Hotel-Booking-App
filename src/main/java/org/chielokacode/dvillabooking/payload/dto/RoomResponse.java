package org.chielokacode.dvillabooking.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.List;


//this class is for viewing the rooms (only what we want to be shown to the user)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private String roomDesc;
    private boolean isRoomBooked;
    private String photo;
    private List<BookingResponse> bookings;

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice, String roomDesc) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.roomDesc = roomDesc;
    }

    public RoomResponse(Long id, String roomType, BigDecimal roomPrice, String roomDesc, boolean isRoomBooked,
                        byte[] photoBytes) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.roomDesc = roomDesc;
        this.isRoomBooked = isRoomBooked;
        this.photo = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;
//        this.bookings = bookings;
    }


}
