package com.ordering.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeResponse {
    private Long tableId;
    private String tableNumber;
    private String qrCodeData;
    private String qrCodeUrl;
} 