package com.bankapp.dto.dashboard;

import com.bankapp.interfaces.UserProjection;

public record DashboardResponse(
        UserProjection userProjection
) {
}
