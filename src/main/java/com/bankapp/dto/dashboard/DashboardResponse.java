package com.bankapp.dto.dashboard;
import com.bankapp.interfaces.CardProjection;
import com.bankapp.interfaces.UserProjection;

public record DashboardResponse(
        UserProjection userProjection,
        CardProjection cardProjection
) {
}
