package org.chielokacode.dvillabooking.controller;


import org.apache.tomcat.util.codec.binary.Base64;
import org.chielokacode.dvillabooking.exception.PhotoRetrievalException;
import org.chielokacode.dvillabooking.exception.ResourceNotFoundException;
import org.chielokacode.dvillabooking.model.BookedRoom;
import org.chielokacode.dvillabooking.model.Room;
import org.chielokacode.dvillabooking.payload.dto.RoomResponse;
import org.chielokacode.dvillabooking.serviceImpl.BookingServiceImpl;
import org.chielokacode.dvillabooking.serviceImpl.RoomServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/rooms")
public class RoomController {

    private final BookingServiceImpl bookingService;
    private final RoomServiceImpl roomService;

    @Autowired
    public RoomController(BookingServiceImpl bookingService, RoomServiceImpl roomService){
        this.bookingService = bookingService;
        this.roomService = roomService;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo,
                                                   @RequestParam("roomType") String roomType,
                                                   @RequestParam("roomPrice") BigDecimal roomPrice,
                                                   @RequestParam("roomDesc") String roomDesc) throws SQLException, IOException {
        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice, roomDesc);
        RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice(), savedRoom.getRoomDesc());
        System.out.println(response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //function to get the distinct list of the names of the room type
    @GetMapping("/room/types")
    public ResponseEntity<List<String>> getAllRoomTypesNames(){
        List<String> roomTypes = roomService.getAllRoomTypes();
        return new ResponseEntity<>(roomTypes, HttpStatus.OK);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //function to get the paginated list of rooms to the frontend
    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRoomTypes() throws SQLException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room: rooms){
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if(photoBytes != null && photoBytes.length > 0){
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
        return new ResponseEntity<>(roomResponses, HttpStatus.OK);
    }

    //function to get all room responses from the database( that is what and what we want to display to the user
    private RoomResponse getRoomResponse(Room room) {
        //TO GET THE BOOKING HISTORY TOGETHER WITH THE ROOMS
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
//        List<BookingResponse> bookingInfo = bookings
//                .stream()
//                .map(booking -> new BookingResponse(booking.getBookingId(),
//                        booking.getCheckInDate(),
//                        booking.getCheckOutDate(),
//                        booking.getGuestFullName(),
//                        booking.getBookingConfirmationCode())).toList();
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if(photoBlob != null){
            try{
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            }catch (SQLException e){
                 throw new PhotoRetrievalException("Error Retrieving Photo");
            }
        }
        return new RoomResponse(
                room.getId(),
                room.getRoomType(),
                room.getRoomPrice(),
                room.getRoomDesc(),
                room.isRoomBooked(),
                photoBytes
//                bookingInfo
        );
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long roomId){
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam(required = false) String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice,
                                                   @RequestParam(required = false) String roomDesc,
                                                   @RequestParam(required = false) MultipartFile photo) throws SQLException, IOException {

        byte[] photoBytes = photo != null && !photo.isEmpty() ?
                photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes) : null;
        Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes, roomDesc);
        theRoom.setPhoto(photoBlob);
        RoomResponse roomResponse = getRoomResponse(theRoom);
        return new ResponseEntity<>(roomResponse, HttpStatus.CREATED);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId){
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        return theRoom.map(room -> {
            RoomResponse roomResponse = getRoomResponse(room);
            return new ResponseEntity<>(Optional.of(roomResponse), HttpStatus.OK);
        }).orElseThrow(() -> new ResourceNotFoundException("Room not Found"));

    }


}
