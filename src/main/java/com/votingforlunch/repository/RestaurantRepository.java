package com.votingforlunch.repository;

import com.votingforlunch.model.Restaurant;
import com.votingforlunch.to.RestaurantTo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    @Modifying
    @Transactional
    @Query("delete from Restaurant r where r.id=:id")
    int delete(@Param("id") int id);

    @Query("select new com.votingforlunch.to.RestaurantTo(v.restaurantId, count (v.restaurantId))" +
            "from Vote v group by v.restaurantId order by count (v.restaurantId) desc")
    List<RestaurantTo> getAllWithVotes();
}
