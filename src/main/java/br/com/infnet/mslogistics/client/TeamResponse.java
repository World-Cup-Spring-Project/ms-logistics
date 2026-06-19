package br.com.infnet.mslogistics.client;

public record TeamResponse(
        String id,
        String name,
        String federation,
        String groupLetter,
        String flagUrl,
        String worldcupId
) {}
