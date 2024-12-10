package org.referix.lotusOffSeasonV2.trader.hoarder;

import java.util.Objects;

public class Holder {
    private final String id;       // Унікальний ідентифікатор
    private String name;           // Ім'я холдера
    private String description;    // Опис холдера

    // Конструктор
    public Holder(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Гетер для ID
    public String getId() {
        return id;
    }

    // Гетер і сетер для name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Гетер і сетер для description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Перевизначення toString для зручного виведення
    @Override
    public String toString() {
        return "Holder{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    // Перевизначення equals для порівняння об'єктів
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holder holder = (Holder) o;
        return id.equals(holder.id);
    }

    // Перевизначення hashCode для використання в хеш-таблицях
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
