package net.sokontokoro_factory.lovelive.persistence.master;

import lombok.Getter;

public enum MasterGame {
    HONOCAR     (1, "honocar"),
    SHAKARIN    (2, "shakarin");

    @Getter
    private int id;
    @Getter
    private String value;

    MasterGame(int id, String value){
        this.id = id;
        this.value = value;
    }
}