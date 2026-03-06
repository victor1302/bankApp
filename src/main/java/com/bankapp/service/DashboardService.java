package com.bankapp.service;

import com.bankapp.dto.dashboard.DashboardResponse;
import com.bankapp.entity.User;
import com.bankapp.interfaces.AccountProjection;
import com.bankapp.interfaces.CardProjection;
import com.bankapp.interfaces.UserProjection;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.CardRepository;
import com.bankapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    public DashboardService(UserRepository userRepository, CardRepository cardRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }


    public DashboardResponse getDashboard(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();



        UserProjection userProjection = userRepository.findProjectedByUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        CardProjection cardProjection = cardRepository.findProjectionByCardId(user.getUserAccount().getCardAccount().getCardId())
                .orElseThrow(() -> new RuntimeException("Card not found"));



        return new DashboardResponse(
                userProjection,
                cardProjection
        );
    }
}
