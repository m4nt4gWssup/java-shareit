package ru.practicum.shareit.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {

    private final int page;
    private final int size;

    // Конструктор с сортировкой
    private CustomPageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
        this.page = page;
        this.size = size;
    }

    // Конструктор без сортировки (использует unsorted для создания PageRequest)
    private CustomPageRequest(int page, int size) {
        super(page, size, Sort.unsorted());
        this.page = page;
        this.size = size;
    }

    // Статический метод создания экземпляра с сортировкой
    public static CustomPageRequest of(int from, int size, Sort sort) {
        return new CustomPageRequest(calculatePage(from, size), size, sort);
    }

    // Статический метод создания экземпляра без сортировки
    public static CustomPageRequest of(int from, int size) {
        return new CustomPageRequest(calculatePage(from, size), size);
    }

    // Вспомогательный метод для расчета страницы
    private static int calculatePage(int from, int size) {
        return from / size;
    }
}
