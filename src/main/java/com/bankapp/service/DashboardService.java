package com.bankapp.service;

import com.bankapp.dto.dashboard.DashboardResponse;
import com.bankapp.entity.User;
import com.bankapp.interfaces.UserProjection;
import com.bankapp.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UserRepository userRepository;

    public DashboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public DashboardResponse getDashboard(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserProjection userProjection = userRepository.findProjectedByUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new DashboardResponse(
                userProjection
        );
    }
}
