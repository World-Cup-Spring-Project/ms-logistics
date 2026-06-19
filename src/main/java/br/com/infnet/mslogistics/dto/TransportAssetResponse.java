package br.com.infnet.mslogistics.dto;

import br.com.infnet.mslogistics.model.TransportAsset;
import br.com.infnet.mslogistics.model.TransportType;

import java.util.UUID;

public record TransportAssetResponse(UUID id, TransportType type, String plate, int capacity, boolean available) {
    public static TransportAssetResponse from(TransportAsset a) {
        return new TransportAssetResponse(a.getId(), a.getType(), a.getPlate(), a.getCapacity(), a.isAvailable());
    }
}
