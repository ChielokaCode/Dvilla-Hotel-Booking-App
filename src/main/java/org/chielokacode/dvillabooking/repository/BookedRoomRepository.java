package org.chielokacode.dvillabooking.repository;

import org.chielokacode.dvillabooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookedRoomRepository extends JpaRepository<Room, Long> {
}
