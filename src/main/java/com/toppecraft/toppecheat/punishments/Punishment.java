package com.toppecraft.toppecheat.punishments;

import java.util.UUID;

public class Punishment {

    private PunishmentType type;
    private String reason;
    private UUID uuid;

    public Punishment(UUID uuid, String reason, PunishmentType type) {
        this.uuid = uuid;
        this.reason = reason;
        this.type = type;
    }

    public UUID getPlayerUUID() {
        return uuid;
    }

    public PunishmentType getPunishmentType() {
        return type;
    }

    public String getReason() {
		if (reason == null) {
			return "breaking rules";
		} else {
			return reason;
		}
    }

    @Override
    public String toString() {
        return "Type: " + getPunishmentType() + " | Reason: " + getReason();
    }

    public enum PunishmentType {
        AUTOBAN,
        AUTOMUTE,
        KICK,
    }

}
