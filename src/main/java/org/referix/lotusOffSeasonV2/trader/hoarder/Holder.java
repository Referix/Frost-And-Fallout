package org.referix.lotusOffSeasonV2.trader.hoarder;

import org.referix.lotusOffSeasonV2.LotusOffSeasonV2;

import java.util.Objects;

public class Holder {
    private final String id;       // Уникальный идентификатор
    private String name;           // Имя холдера
    private String description;    // Описание холдера
    private String villagerPreset; // Пресет рецептов для жителя

    // Конструктор
    public Holder(String id, String name, String description, String villagerPreset) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.villagerPreset = villagerPreset;
    }

    // Геттер для ID
    public String getId() {
        return id;
    }

    // Геттер и сеттер для name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Геттер и сеттер для description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Геттер и сеттер для villagerPreset
    public String getVillagerPreset() {
        return villagerPreset;
    }

    public boolean hasPreset() {
        if (villagerPreset == null || villagerPreset.isEmpty()) {
            return false;
        }
        return LotusOffSeasonV2.getInstance().getHorderConfig().hasPreset(villagerPreset);
    }



    public void setVillagerPreset(String villagerPreset) {
        this.villagerPreset = villagerPreset;
    }




    // Переопределение toString для удобного вывода
    @Override
    public String toString() {
        return "Holder{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", villagerPreset='" + villagerPreset + '\'' +
                '}';
    }

    // Переопределение equals для сравнения объектов
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holder holder = (Holder) o;
        return id.equals(holder.id);
    }

    // Переопределение hashCode для использования в хеш-таблицах
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
