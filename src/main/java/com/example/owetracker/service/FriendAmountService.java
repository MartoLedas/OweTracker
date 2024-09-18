
package com.example.owetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.owetracker.repository.FriendAmountRepository;
import java.math.BigDecimal;

@Service
public class FriendAmountService {

    @Autowired
    private FriendAmountRepository friendAmountRepository;

    public BigDecimal getTotalOwedToFriend(Integer userId, Integer friendId) {
        return friendAmountRepository.findTotalOwedToFriend(userId, friendId);
    }

    public BigDecimal getTotalOwedByFriend(Integer userId, Integer friendId) {
        return friendAmountRepository.findTotalOwedByFriend(userId, friendId);
    }
}
