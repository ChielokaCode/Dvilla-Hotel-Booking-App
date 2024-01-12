package org.chielokacode.dvillabooking.serviceImpl;

import org.chielokacode.dvillabooking.exception.InternalServerException;
import org.chielokacode.dvillabooking.exception.ResourceNotFoundException;
import org.chielokacode.dvillabooking.model.Room;
import org.chielokacode.dvillabooking.repository.RoomRepository;
import org.chielokacode.dvillabooking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }


    @Override
    public Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String roomDesc) throws IOException, SQLException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        room.setRoomDesc(roomDesc);

        //if photo is present, get the phot bytes and convert it to blob then set to the room photo
        if(!photo.isEmpty()){
            byte[] photoBytes = photo.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);
        }
        return roomRepository.save(room);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findAllDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> theRoom = roomRepository.findById(roomId);

        if(theRoom.isEmpty()){
            throw new ResourceNotFoundException("Sorry, Room not found!");
        }
        Blob roomPhotoBlob = theRoom.get().getPhoto();
        if(roomPhotoBlob != null){
            return roomPhotoBlob.getBytes(1, (int) roomPhotoBlob.length());
        }
        return null;
    }

    @Override
    public void deleteRoom(Long roomId) {
        Optional<Room> theRoom = roomRepository.findById(roomId);

        if(theRoom.isPresent()){
            roomRepository.deleteById(roomId);
        }
    }

    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes, String roomDesc) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not Found"));
        if(roomType != null) room.setRoomType(roomType);
        if(roomPrice != null) room.setRoomPrice(roomPrice);
        if(roomDesc != null) room.setRoomDesc((roomDesc));
        if(photoBytes != null && photoBytes.length > 0){
            try{
                room.setPhoto(new SerialBlob(photoBytes));

            }catch (SQLException e) {
                throw  new InternalServerException("Error updating room");
            }
        }
            return roomRepository.save(room);

    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId).get());
    }

}
