package org.chielokacode.dvillabooking.service;

import org.chielokacode.dvillabooking.model.Room;
import org.chielokacode.dvillabooking.payload.dto.RoomResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RoomService {


    Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String roomDesc) throws IOException, SQLException;

    List<String> getAllRoomTypes();

    List<Room> getAllRooms();

    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;

    void deleteRoom(Long roomId);

    Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photo, String roomDesc);

    Optional<Room> getRoomById(Long roomId);
}
